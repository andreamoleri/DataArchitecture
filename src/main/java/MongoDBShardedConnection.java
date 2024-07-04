package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code MongoDBShardedConnection} class provides methods for reading airport data from a JSON file and
 * inserting it into a MongoDB collection. This class demonstrates the process of parsing complex JSON data,
 * constructing MongoDB documents, and managing connections to a MongoDB sharded cluster.
 *
 * <p> The primary functionalities include:
 * <ul>
 * <li>Reading and parsing a JSON file containing airport data.
 * <li>Constructing MongoDB documents from the parsed JSON data.
 * <li>Connecting to a MongoDB database and inserting the documents into a specified collection.
 * </ul>
 *
 * <p>The JSON structure includes nested arrays representing flights and seats, which are also parsed and included
 * in the corresponding MongoDB documents.
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * {@code
 * public static void main(String[] args) {
 *     String connectionString = "mongodb://localhost:27017";
 *     try (MongoClient mongoClient = MongoClients.create(connectionString)) {
 *         MongoDatabase database = mongoClient.getDatabase("myDatabase");
 *         System.out.println("Connected to database: " + database.getName());
 *
 *         MongoCollection<Document> collection = database.getCollection("myCollection");
 *         List<Document> airports = getData();
 *         collection.insertMany(airports);
 *
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }
 * }
 * </pre>
 */
public class MongoDBShardedConnection {

    /**
     * Reads airport data from a JSON file and constructs a list of MongoDB documents.
     * The JSON file is expected to contain an array of airport objects, each of which may
     * contain nested arrays for flights and seats.
     *
     * @return a list of {@link Document} objects representing the airport data.
     */
    public static List<Document> getData() {
        List<Document> airportDocuments = new ArrayList<>();

        try (FileReader reader = new FileReader("Data/Airports Modeling Export.json")) {
            // Create a JSONTokener from the FileReader
            JSONTokener tokener = new JSONTokener(reader);

            // Create a JSONArray from the tokener
            JSONArray jsonArray = new JSONArray(tokener);

            // Iterate through the JSON array elements
            for (int i = 0; i < jsonArray.length(); i++) {
                // Get the current JSON object
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Extract desired fields for the airport document
                String geoPoint = jsonObject.optString("Geo_Point", "");
                String name = jsonObject.optString("Name", "");
                String nameEn = jsonObject.optString("Name_(en)", "");
                String nameFr = jsonObject.optString("Name_(fr)", "");
                String iataCode = jsonObject.optString("IATA_code", "");
                String icaoCode = jsonObject.optString("ICAO_code", "");
                String operator = jsonObject.optString("Operator", "");
                String country = jsonObject.optString("Country", "");
                String countryCode = jsonObject.optString("Country_code", "");
                int size = jsonObject.optInt("Size", 0);

                // Extract the array of flights for the airport
                JSONArray flightsArray = jsonObject.getJSONArray("Flights");
                List<Flight> flights = new ArrayList<>();

                // Iterate through the flights array elements
                for (int j = 0; j < flightsArray.length(); j++) {
                    JSONObject flightObject = flightsArray.getJSONObject(j);

                    // Extract desired fields for the flight
                    String flightId = flightObject.optString("ID", "");
                    int numberOfSeats = flightObject.optInt("Number_of_Seats", 0);
                    String day = flightObject.optString("Day", "");
                    String hour = flightObject.optString("Hour", "");
                    String flightOperator = flightObject.optString("Operator", "");
                    String duration = flightObject.optString("Duration", "");
                    int pricePerPerson = flightObject.optInt("Price_per_Person", 0);

                    // Extract the array of seats for the flight
                    JSONArray seatsArray = flightObject.getJSONArray("Seats");
                    List<Seat> seats = new ArrayList<>();

                    // Iterate through the seats array elements
                    for (int k = 0; k < seatsArray.length(); k++) {
                        JSONObject seatObject = seatsArray.getJSONObject(k);

                        // Extract desired fields for the seat
                        String status = seatObject.optString("Status", "");
                        String seatId = seatObject.optString("ID", "");
                        String nameOnTicket = seatObject.optString("Name", "");
                        String surnameOnTicket = seatObject.optString("Surname", "");
                        String documentInfo = seatObject.optString("Document_Info", "");
                        String dateOfBirth = seatObject.optString("Date_of_Birth", "");
                        int balance = seatObject.optInt("Balance", 0);

                        // Create a Seat object and add it to the list of seats
                        Seat seat = new Seat(status, seatId, nameOnTicket, surnameOnTicket, documentInfo, dateOfBirth, balance);
                        seats.add(seat);
                    }

                    // Create a Flight object and add it to the list of flights
                    Flight flight = new Flight(flightId, numberOfSeats, day, hour, flightOperator, duration, pricePerPerson, seats);
                    flights.add(flight);
                }

                // Create an Airport object and add it to the list of documents
                Airport document = new Airport(geoPoint, name, nameEn, nameFr, iataCode, icaoCode, operator, country, countryCode, size, flights);
                airportDocuments.add(document.toDocument());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return airportDocuments;
    }

    /**
     * Main method to establish a connection to the MongoDB sharded cluster, retrieve airport data,
     * and insert it into a specified MongoDB collection.
     *
     * @param args the command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Connection to the mongos router
        String connectionString = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("myDatabase");
            System.out.println("Connected to database: " + database.getName());

            MongoCollection<Document> collection = database.getCollection("myCollection");
            List<Document> airports = getData();
            collection.insertMany(airports);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
