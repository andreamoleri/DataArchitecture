/**
 * This Java application reads data from a CSV file containing airport information,
 * parses and filters the data, and stores it in a MongoDB database. It also generates
 * flights for each airport based on its size and stores them in the same database.
 *
 * The main method initiates the process:
 * - Connects to MongoDB using a provided connection string.
 * - Reads airport data from a specified CSV file.
 * - Parses and filters the data into MongoDB Documents, excluding specific fields.
 * - Stores each airport Document in a MongoDB collection.
 * - Generates flights for each airport based on its size and available seats.
 * - Stores generated flight information in MongoDB, associating them with respective airports.
 *
 * The class utilizes external libraries such as MongoDB Java Driver and handles various
 * exceptions including I/O errors and data parsing issues.
 *
 * @author Andrea Moleri
 * @version 1.0
 * @since 2024-06-23
 */
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CSVToMongoDB {

    /**
     * Main method that orchestrates the import of airport data from a CSV file into MongoDB.
     * It establishes a connection, processes the CSV data, and generates flights for each airport.
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        String csvFile = "Data/Airports.csv"; // Path to the CSV file
        String dbName = "Airports"; // MongoDB database name
        String collectionName = "airportCollection"; // MongoDB collection name

        String connectionString = "mongodb+srv://admin:admin@learningmongodb.hikoksa.mongodb.net/?retryWrites=true&w=majority&appName=LearningMongoDB";

        try {
            // Obtain MongoClient and MongoDB/MongoCollection
            MongoClient mongoClient = Connection.getInstance(connectionString).getMongoClient();
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;
            boolean isFirstLine = true; // Flag to check if it's the first line
            String[] headers = null; // Array to hold column headers

            List<Document> airports = new ArrayList<>(); // List to hold all airports for flight generation

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);

                if (isFirstLine) {
                    isFirstLine = false;
                    // Save headers and replace spaces with underscores
                    headers = new String[fields.length];
                    for (int i = 0; i < fields.length; i++) {
                        headers[i] = fields[i].trim().replace(" ", "_");
                    }
                    continue; // Skip header line
                }

                // Create a new Document to store in MongoDB
                Document airportDoc = new Document();

                // Loop through the fields array and add non-null fields to the Document
                for (int i = 0; i < fields.length && i < headers.length; i++) {
                    String fieldName = headers[i].trim();
                    String fieldValue = fields[i].trim().replaceAll("\"\"", "\"");
                    if (!fieldName.isEmpty() && !fieldValue.isEmpty()) {
                        // Exclude "Edit_in_OSM" and "other_tags" fields
                        if (!fieldName.equals("Edit_in_OSM") && !fieldName.equals("other_tags")) {
                            if (fieldName.equals("Size")) {
                                try {
                                    airportDoc.append(fieldName, Integer.parseInt(fieldValue));
                                } catch (NumberFormatException e) {
                                    System.err.println("Error parsing 'Size' field to integer: " + fieldValue);
                                }
                            } else {
                                airportDoc.append(fieldName, fieldValue);
                            }
                        }
                    }
                }

                // Insert Document into MongoDB
                collection.insertOne(airportDoc);

                // Add airportDoc to the list for generating flights later
                airports.add(airportDoc);
            }

            // After inserting all airports, generate flights for each airport
            for (Document airportDoc : airports) {
                generateAndInsertFlights(airportDoc, collection, airports);
            }

            System.out.println("Data imported successfully into MongoDB");

            // Close resources
            reader.close();
            mongoClient.close(); // Close MongoDB connection

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates flights for an airport based on its size and inserts them into the database.
     * @param airportDoc Document representing the airport for which flights are generated
     * @param collection MongoDB collection where flights are stored
     * @param airports List of all airports for generating destination airports
     */
    private static void generateAndInsertFlights(Document airportDoc, MongoCollection<Document> collection, List<Document> airports) {
        int airportSize = airportDoc.getInteger("Size", 0); // Airport size
        List<Document> flights = generateFlights(airportSize, airports, airportDoc);

        // Calculate max flights based on airport size
        int maxFlights = airportSize / 100;
        int totalSeats = 0;

        // Calculate total seats from existing flights
        for (Document flight : flights) {
            totalSeats += flight.getInteger("Number_of_Seats", 0);
        }

        // Reduce flights if total seats exceed airport capacity
        if (totalSeats > airportSize) {
            // Remove flights until total seats fit within airport capacity
            while (totalSeats > airportSize && flights.size() > 0) {
                Document lastFlight = flights.remove(flights.size() - 1);
                totalSeats -= lastFlight.getInteger("Number_of_Seats", 0);
            }
        }

        // Update flights array in airportDoc
        airportDoc.put("Flights", flights);

        // Replace the document in MongoDB collection
        collection.replaceOne(new Document("_id", airportDoc.getObjectId("_id")), airportDoc);
    }

    /**
     * Generates a list of flights for an airport based on its size.
     * @param airportSize Size of the airport
     * @param airports List of all airports for selecting destination airports
     * @param currentAirport Document representing the current airport
     * @return List of generated flights
     */
    private static List<Document> generateFlights(int airportSize, List<Document> airports, Document currentAirport) {
        List<Document> flights = new ArrayList<>();
        Random random = new Random();

        // Generate flights
        for (int i = 0; i < 5; i++) { // Generate 5 flights per airport (adjust as needed)
            // Select a random destination airport (ensure it's not the same as current airport)
            Document destinationAirport = getRandomDestinationAirport(airports, currentAirport);

            // Generate random future date between 1 and 10 days from today
            LocalDate currentDate = LocalDate.now();
            int daysToAdd = random.nextInt(10) + 1;
            LocalDate futureDate = currentDate.plusDays(daysToAdd);
            String dayString = futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Generate random time
            LocalTime randomTime = LocalTime.of(random.nextInt(24), random.nextInt(60));
            String hourString = randomTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            // Generate random duration between 1 and 14 hours
            int durationHours = random.nextInt(14) + 1;
            String durationString = durationHours + " hours";

            // Array of actual operators
            String[] operators = {"Ryanair", "Lufthansa", "EasyJet", "British Airways", "Air France"};

            // Generate random operator
            String operator = operators[random.nextInt(operators.length)];

            // Generate random price per person between 39 and 499
            int pricePerPerson = random.nextInt(461) + 39; // 39 to 499

            Document flight = new Document();
            flight.append("ID", new ObjectId().toString())
                    .append("Number_of_Seats", 100)
                    .append("Day", dayString)
                    .append("Hour", hourString)
                    .append("Operator", operator)
                    .append("Duration", durationString)
                    .append("Price_per_Person", pricePerPerson);

            // Add destination reference as ObjectId
            flight.append("Destination", destinationAirport.getObjectId("_id"));

            // Generate seats for this flight
            List<Document> seats = generateSeats(100); // Assume 100 seats per flight
            flight.append("Seats", seats);

            flights.add(flight);
        }

        return flights;
    }

    /**
     * Retrieves a random destination airport document from the list, ensuring it's different from the current airport.
     * @param airports List of all airports
     * @param currentAirport Document representing the current airport
     * @return Random destination airport Document
     * @throws IllegalStateException If the airports list is empty
     */
    private static Document getRandomDestinationAirport(List<Document> airports, Document currentAirport) {
        Random random = new Random();
        Document destinationAirport = null;

        // Keep trying until a different airport than currentAirport is found
        while (true) {
            if (airports.isEmpty()) {
                throw new IllegalStateException("Airport list is empty, cannot select destination.");
            }
            int index = random.nextInt(airports.size());
            destinationAirport = airports.get(index);
            if (!destinationAirport.getObjectId("_id").equals(currentAirport.getObjectId("_id"))) {
                break;
            }
        }

        return destinationAirport;
    }

    /**
     * Generates a list of seat documents for a flight.
     * @param numberOfSeats Number of seats to generate
     * @return List of generated seat documents
     */
    private static List<Document> generateSeats(int numberOfSeats) {
        List<Document> seats = new ArrayList<>();
        String[] seatLetters = {"A", "B", "C", "D", "E", "F"};
        int seatNumber = 1;

        for (int i = 0; i < numberOfSeats; i++) {
            String seatID = seatNumber + seatLetters[i % 6]; // Cycle through A to F
            Document seat = new Document();
            seat.append("Status", "Vacant")
                    .append("ID", seatID)
                    .append("Name", "")
                    .append("Surname", "")
                    .append("Document_Info", "")
                    .append("Date_of_Birth", "")
                    .append("Balance", 0);

            seats.add(seat);
            if ((i + 1) % 6 == 0) { // Move to the next row of seat numbers (1A, 2A, ...)
                seatNumber++;
            }
        }

        return seats;
    }
}
