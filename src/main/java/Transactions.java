import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.quickstart.Connection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Transactions {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    // Constructor to initialize MongoDB connection
    public Transactions(MongoClient mongoClient, String dbName, String collectionName) {
        this.mongoClient = mongoClient;
        this.database = mongoClient.getDatabase(dbName);
        this.collection = database.getCollection(collectionName);
    }

    // Method to retrieve flights from a given airport code (IATA_code)
    public List<Document> getFlightsFromAirport(String airportCode) {
        List<Document> flights = new ArrayList<>();

        // Query MongoDB collection for flights from the specified airport code
        FindIterable<Document> iterable = collection.find(Filters.eq("IATA_code", airportCode));

        for (Document airportDoc : iterable) {
            List<Document> flightsFromAirport = (List<Document>) airportDoc.get("Flights");
            for (Document flight : flightsFromAirport) {
                Document destinationAirport = getAirportById(flight.getObjectId("Destination"));
                if (destinationAirport != null) {
                    flight.append("Destination_Details", destinationAirport.getString("IATA_code") + " (" + destinationAirport.getString("Name") + ")");
                    flights.add(flight);
                }
            }
        }

        return flights;
    }

    // Method to retrieve airport details by ObjectId
    private Document getAirportById(ObjectId airportId) {
        return collection.find(Filters.eq("_id", airportId)).first();
    }

    // Close MongoDB connection
    public void close() {
        mongoClient.close();
    }

    // Example usage
    public static void main(String[] args) {
        String connectionString = "mongodb+srv://admin:admin@learningmongodb.hikoksa.mongodb.net/?retryWrites=true&w=majority&appName=LearningMongoDB";
        String dbName = "Airports";
        String collectionName = "airportCollection";

        MongoClient mongoClient = Connection.getInstance(connectionString).getMongoClient();
        Transactions transactions = new Transactions(mongoClient, dbName, collectionName);

        // Example usage with airport code "BGY"
        List<Document> flightsFromBGY = transactions.getFlightsFromAirport("BGY");

        // Print out the flights
        for (Document flight : flightsFromBGY) {
            System.out.println("Flight ID: " + flight.getString("ID"));
            System.out.println("Operator: " + flight.getString("Operator"));
            System.out.println("Day: " + flight.getString("Day"));
            System.out.println("Hour: " + flight.getString("Hour"));
            System.out.println("Destination Airport: " + flight.getObjectId("Destination"));
            System.out.println("Destination Details: " + flight.getString("Destination_Details"));
            System.out.println("Price per Person: " + flight.getInteger("Price_per_Person"));
            System.out.println();
        }

        // Close MongoDB connection
        transactions.close();
    }
}
