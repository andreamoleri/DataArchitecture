import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.quickstart.Connection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVToMongoDB {

    public static void main(String[] args) {
        String csvFile = "Data/Airports.csv"; // Path to CSV file
        String dbName = "Airports"; // MongoDB database name
        String collectionName = "airportCollection"; // MongoDB collection name

        String connectionString = "mongodb+srv://admin:admin@learningmongodb.hikoksa.mongodb.net/?retryWrites=true&w=majority&appName=LearningMongoDB";

        try {
            // Get Connection instance
            Connection connection = Connection.getInstance(connectionString);

            // Get MongoClient and MongoDB/MongoCollection
            MongoClient mongoClient = connection.getMongoClient();
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;
            boolean isFirstLine = true; // Flag to check if it's the first line
            String[] headers = null; // Array to hold the header names

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

                // Generate flights for this airport
                List<Document> flights = generateFlights();
                airportDoc.append("Flights", flights);

                // Insert the Document into MongoDB collection
                collection.insertOne(airportDoc);
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

    private static List<Document> generateFlights() {
        List<Document> flights = new ArrayList<>();

        // Example: generate 5 flights per airport
        for (int i = 0; i < 5; i++) {
            Document flight = new Document();
            flight.append("ID", new ObjectId().toString())
                    .append("Number_of_Seats", 100)
                    .append("Day", "2024-06-30")
                    .append("Hour", "12:00")
                    .append("Operator", "Airline " + i)
                    .append("Duration", "2 hours")
                    .append("Price_per_Person", 100);

            // Generate seats for this flight
            List<Document> seats = generateSeats(100); // Assume 100 seats per flight
            flight.append("Seats", seats);

            flights.add(flight);
        }

        return flights;
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
