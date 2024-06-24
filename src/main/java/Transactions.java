import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, String> getFlightsFromAirport(String airportCode) {
        Map<String, String> flightsMap = new HashMap<>();

        // Query MongoDB collection for flights from the specified airport code
        FindIterable<Document> iterable = collection.find(Filters.eq("IATA_code", airportCode));

        for (Document airportDoc : iterable) {
            List<Document> flightsFromAirport = (List<Document>) airportDoc.get("Flights");
            for (Document flight : flightsFromAirport) {
                ObjectId destinationId = flight.get("Destination", ObjectId.class);
                Document destinationAirport = getAirportById(destinationId);
                if (destinationAirport != null) {
                    flightsMap.put(destinationAirport.getString("IATA_code"), flight.getString("ID"));
                }
            }
        }

        return flightsMap;
    }

    // Method to retrieve airport details by ObjectId
    private Document getAirportById(ObjectId airportId) {
        return collection.find(Filters.eq("_id", airportId)).first();
    }

    // Method to get available seats on a specific flight route
    public List<String> getAvailableSeats(String departureAirportCode, String arrivalAirportCode) {
        List<String> availableSeatsList = new ArrayList<>();

        // Query MongoDB for departure airport
        Document departureAirport = collection.find(Filters.eq("IATA_code", departureAirportCode)).first();

        if (departureAirport != null) {
            // Find the flight from departure to arrival
            List<Document> flightsFromDeparture = (List<Document>) departureAirport.get("Flights");
            for (Document flight : flightsFromDeparture) {
                ObjectId destinationId = flight.get("Destination", ObjectId.class);
                Document destinationAirport = getAirportById(destinationId);
                if (destinationAirport != null && destinationAirport.getString("IATA_code").equals(arrivalAirportCode)) {
                    List<Document> seats = (List<Document>) flight.get("Seats");
                    for (Document seat : seats) {
                        if (seat.getString("Status").equals("Vacant")) {
                            availableSeatsList.add(seat.getString("ID"));
                        }
                    }
                    break; // Stop after finding the correct flight
                }
            }
        }

        return availableSeatsList;
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

        // Example usage with airport codes "BGY" (Bergamo) and "BLL" (Billund)
        String departureAirportCode = "BGY";
        String arrivalAirportCode = "BLL";

        // Get flights from BGY (Bergamo)
        Map<String, String> flightsFromBGY = transactions.getFlightsFromAirport(departureAirportCode);
        System.out.println("Flights from " + departureAirportCode + ": " + flightsFromBGY);

        // Get available seats details from BGY to BLL
        List<String> availableSeatsDetails = transactions.getAvailableSeats(departureAirportCode, arrivalAirportCode);
        System.out.println("Available seats from " + departureAirportCode + " to " + arrivalAirportCode + ": " + availableSeatsDetails);

        // Close MongoDB connection
        transactions.close();
    }
}
