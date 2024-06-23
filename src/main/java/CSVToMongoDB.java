import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.quickstart.Connection;
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

    public static void main(String[] args) {
        String csvFile = "Data/Airports.csv"; // Path to CSV file
        String dbName = "Airports"; // MongoDB database name
        String collectionName = "airportCollection"; // MongoDB collection name

        String connectionString = "mongodb+srv://admin:admin@learningmongodb.hikoksa.mongodb.net/?retryWrites=true&w=majority&appName=LearningMongoDB";

        try {
            // Get MongoClient and MongoDB/MongoCollection
            MongoClient mongoClient = Connection.getInstance(connectionString).getMongoClient();
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;
            boolean isFirstLine = true; // Flag to check if it's the first line
            String[] headers = null; // Array to hold the header names

            List<Document> airports = new ArrayList<>(); // List to hold all airports for generating flights

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);

                if (isFirstLine) {
                    isFirstLine = false;
                    // Save the headers and replace spaces with underscores
                    headers = new String[fields.length];
                    for (int i = 0; i < fields.length; i++) {
                        headers[i] = fields[i].trim().replace(" ", "_");
                    }
                    continue; // Skip the header line
                }

                // Create a new Document to store in MongoDB
                Document airportDoc = new Document();

                // Loop through fields array and add non-null fields to Document
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

                // Insert the Document into MongoDB collection
                collection.insertOne(airportDoc);

                // Add airportDoc to list for generating flights later
                airports.add(airportDoc);
            }

            // After all airports are inserted, generate flights for each airport
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

    private static void generateAndInsertFlights(Document airportDoc, MongoCollection<Document> collection, List<Document> airports) {
        int airportSize = airportDoc.getInteger("Size", 0); // Size of the airport
        List<Document> flights = generateFlights(airportSize, airports, airportDoc);

        // Calculate maximum number of flights based on airport size
        int maxFlights = airportSize / 100;
        int totalSeats = 0;

        // Calculate total number of seats from existing flights
        for (Document flight : flights) {
            totalSeats += flight.getInteger("Number_of_Seats", 0);
        }

        // Trim flights if total seats exceed airport capacity
        if (totalSeats > airportSize) {
            // Remove flights until the total seats are within airport capacity
            while (totalSeats > airportSize && flights.size() > 0) {
                Document lastFlight = flights.remove(flights.size() - 1);
                totalSeats -= lastFlight.getInteger("Number_of_Seats", 0);
            }
        }

        // Update the flights array in airportDoc
        airportDoc.put("Flights", flights);

        // Replace the document in MongoDB collection
        collection.replaceOne(new Document("_id", airportDoc.getObjectId("_id")), airportDoc);
    }

    private static List<Document> generateFlights(int airportSize, List<Document> airports, Document currentAirport) {
        List<Document> flights = new ArrayList<>();
        Random random = new Random();

        // Example: generate flights
        for (int i = 0; i < 5; i++) { // Generate 5 flights per airport (adjust as needed)
            // Randomly select a destination airport (ensure it's not the same as current airport)
            Document destinationAirport = getRandomDestinationAirport(airports, currentAirport);

            // Generate random future date within 1 to 10 days from now
            LocalDate currentDate = LocalDate.now();
            int daysToAdd = random.nextInt(10) + 1;
            LocalDate futureDate = currentDate.plusDays(daysToAdd);
            String dayString = futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Generate random hour
            LocalTime randomTime = LocalTime.of(random.nextInt(24), random.nextInt(60));
            String hourString = randomTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            // Generate random duration between 1 to 14 hours
            int durationHours = random.nextInt(14) + 1;
            String durationString = durationHours + " hours";

            // List of real airline operators
            String[] operators = {"Ryanair", "Lufthansa", "EasyJet", "British Airways", "Air France"};

            // Generate random operator
            String operator = operators[random.nextInt(operators.length)];

            // Generate random price per person between 39 to 499
            int pricePerPerson = random.nextInt(461) + 39; // 39 to 499

            Document flight = new Document();
            flight.append("ID", new ObjectId().toString())
                    .append("Number_of_Seats", 100)
                    .append("Day", dayString)
                    .append("Hour", hourString)
                    .append("Operator", operator)
                    .append("Duration", durationString)
                    .append("Price_per_Person", pricePerPerson);

            // Add destination reference as ObjectID
            flight.append("Destination", destinationAirport.getObjectId("_id"));

            // Generate seats for this flight
            List<Document> seats = generateSeats(100); // Assume 100 seats per flight
            flight.append("Seats", seats);

            flights.add(flight);
        }

        return flights;
    }

    private static Document getRandomDestinationAirport(List<Document> airports, Document currentAirport) {
        Random random = new Random();
        Document destinationAirport = null;

        // Keep trying until we find a different airport than the current one
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

    private static List<Document> generateSeats(int numberOfSeats) {
        List<Document> seats = new ArrayList<>();

        for (int i = 0; i < numberOfSeats; i++) {
            Document seat = new Document();
            seat.append("Status", "Vacant")
                    .append("ID", "")
                    .append("Name", "")
                    .append("Surname", "")
                    .append("Document_Info", "")
                    .append("Date_of_Birth", "")
                    .append("Balance", 0);

            seats.add(seat);
        }

        return seats;
    }
}
