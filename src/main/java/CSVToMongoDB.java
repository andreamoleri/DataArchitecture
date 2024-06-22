import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.quickstart.Connection;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
                String[] fields = line.split(";", -1);

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
                Document doc = new Document();

                // Loop through fields array and add non-null fields to Document
                for (int i = 0; i < fields.length && i < headers.length; i++) {
                    String fieldName = headers[i].trim();
                    String fieldValue = fields[i].trim().replaceAll("\"\"", "\"");
                    if (!fieldName.isEmpty() && !fieldValue.isEmpty()) {
                        // Exclude "Edit_in_OSM" and "other_tags" fields
                        if (!fieldName.equals("Edit_in_OSM") && !fieldName.equals("other_tags")) {
                            doc.append(fieldName, fieldValue);
                        }
                    }
                }

                // Insert the Document into MongoDB collection
                collection.insertOne(doc);
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
}
