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

    // Method to get available seats on a specific flight route
    public int getAvailableSeats(String departureAirportCode, String arrivalAirportCode) {
        int availableSeats = 0;

        // Query MongoDB for departure airport
        Document departureAirport = collection.find(Filters.eq("IATA_code", departureAirportCode)).first();

        if (departureAirport != null) {
            // Find the flight from departure to arrival
            List<Document> flightsFromDeparture = (List<Document>) departureAirport.get("Flights");
            for (Document flight : flightsFromDeparture) {
                Document destinationAirport = getAirportById(flight.getObjectId("Destination"));
                if (destinationAirport != null && destinationAirport.getString("IATA_code").equals(arrivalAirportCode)) {
                    List<Document> seats = (List<Document>) flight.get("Seats");
                    availableSeats = seats.size();
                    break;
                }
            }
        }

        return availableSeats;
    }

    public String getAvailableSeatsDetails(String departureAirportCode, String arrivalAirportCode) {
        StringBuilder sb = new StringBuilder();
        boolean firstSeat = true;
        int seatsCount = 0;
        int vacantSeatsCount = 0;
        final int SEATS_PER_LINE = 6; // Number of seats per line

        // Query MongoDB for departure airport
        Document departureAirport = collection.find(Filters.eq("IATA_code", departureAirportCode)).first();

        if (departureAirport != null) {
            // Find the flight from departure to arrival
            List<Document> flightsFromDeparture = (List<Document>) departureAirport.get("Flights");
            for (Document flight : flightsFromDeparture) {
                Document destinationAirport = getAirportById(flight.getObjectId("Destination"));
                if (destinationAirport != null && destinationAirport.getString("IATA_code").equals(arrivalAirportCode)) {
                    List<Document> seats = (List<Document>) flight.get("Seats");
                    for (Document seat : seats) {
                        if (seat.getString("Status").equals("Vacant")) { // Only include vacant seats
                            // Append comma and line break only if it's not the first seat
                            if (!firstSeat) {
                                sb.append(", ");
                            }
                            // Get seat ID and prepend '0' if necessary
                            String seatId = seat.getString("ID");
                            if (seatId.length() == 2 && Character.isDigit(seatId.charAt(0))) {
                                seatId = "0" + seatId;
                            }
                            sb.append(seatId);
                            firstSeat = false;
                            seatsCount++;

                            // Add comma and line break after every SEATS_PER_LINE seats
                            if (seatsCount % SEATS_PER_LINE == 0) {
                                sb.append(",\n");
                                firstSeat = true; // Reset firstSeat flag for the next line
                            }

                            vacantSeatsCount++; // Increment vacant seats count
                        }
                    }
                    break; // Stop after finding the correct flight
                }
            }
        }

        // Remove trailing comma and newline if exists
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
            sb.deleteCharAt(sb.length() - 1);
        }

        // Construct the result string with the number of available seats
        String result = sb.toString();
        result += "\nNumber of Seats Available: " + vacantSeatsCount;

        return result;
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

        // Example usage with airport codes "BGY" (Bergamo) and "JFK" (New York)
        String departureAirportCode = "BGY";
        String arrivalAirportCode = "JFK";

        // Print flights from BGY (Bergamo)
        List<Document> flightsFromBGY = transactions.getFlightsFromAirport(departureAirportCode);
        System.out.println("Flights from " + departureAirportCode + ":");
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

        // Get available seats details from BGY to JFK
        String availableSeatsDetails = transactions.getAvailableSeatsDetails(departureAirportCode, arrivalAirportCode);
        System.out.println("Details of available seats:");
        System.out.println(availableSeatsDetails);

        // Close MongoDB connection
        transactions.close();
    }
}
