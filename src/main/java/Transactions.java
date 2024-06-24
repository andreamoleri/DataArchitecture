import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;

class Transactions {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private Lock lock = new ReentrantLock();
    private static final Logger logger = Logger.getLogger(Transactions.class.getName());

    public Transactions(MongoClient mongoClient, String dbName, String collectionName) {
        this.mongoClient = mongoClient;
        this.database = mongoClient.getDatabase(dbName);
        this.collection = database.getCollection(collectionName);
    }

    public Map<String, Map<String, String>> getFlightsFromAirport(String airportCode) {
        Map<String, Map<String, String>> flightsMap = new HashMap<>();

        FindIterable<Document> iterable = collection.find(Filters.eq("IATA_code", airportCode));

        for (Document airportDoc : iterable) {
            List<Document> flightsFromAirport = airportDoc.getList("Flights", Document.class);
            for (Document flight : flightsFromAirport) {
                ObjectId destinationId = flight.getObjectId("Destination");
                Document destinationAirport = getAirportById(destinationId);
                if (destinationAirport != null) {
                    Map<String, String> flightDetails = new HashMap<>();
                    flightDetails.put("ID", flight.getString("ID"));
                    flightDetails.put("Name", destinationAirport.getString("Name"));
                    flightDetails.put("IATA_code", destinationAirport.getString("IATA_code"));
                    flightDetails.put("Country", destinationAirport.getString("Country"));
                    flightsMap.put(destinationAirport.getString("IATA_code"), flightDetails);
                }
            }
        }

        return flightsMap;
    }

    private Document getAirportById(ObjectId airportId) {
        return collection.find(Filters.eq("_id", airportId)).first();
    }

    public List<String> getAvailableSeats(String departureAirportCode, String arrivalAirportCode) {
        List<String> availableSeatsList = new ArrayList<>();

        Document departureAirport = collection.find(Filters.eq("IATA_code", departureAirportCode)).first();

        if (departureAirport != null) {
            List<Document> flightsFromDeparture = departureAirport.getList("Flights", Document.class);
            for (Document flight : flightsFromDeparture) {
                ObjectId destinationId = flight.getObjectId("Destination");
                Document destinationAirport = getAirportById(destinationId);
                if (destinationAirport != null && destinationAirport.getString("IATA_code").equals(arrivalAirportCode)) {
                    List<Document> seats = flight.getList("Seats", Document.class);
                    for (Document seat : seats) {
                        if ("Vacant".equals(seat.getString("Status"))) {
                            availableSeatsList.add(seat.getString("ID"));
                        }
                    }
                    break;
                }
            }
        }

        return availableSeatsList;
    }

    public boolean bookFlight(String flightID, String seatID, PeopleGenerator.Person person) {
        lock.lock();
        try {
            Document flightDocument = collection.find(Filters.eq("Flights.ID", flightID)).first();
            if (flightDocument == null) {
                return false;
            }

            List<Document> flights = flightDocument.getList("Flights", Document.class);
            Document targetFlight = flights.stream()
                    .filter(f -> flightID.equals(f.getString("ID")))
                    .findFirst()
                    .orElse(null);

            if (targetFlight == null) {
                return false;
            }

            Object priceObject = targetFlight.get("Price_per_Person");
            if (!(priceObject instanceof Number)) {
                return false; // Gestione se il prezzo non è un numero valido
            }
            double seatPrice = ((Number) priceObject).doubleValue(); // Converti il prezzo del posto in double

            List<Document> seats = targetFlight.getList("Seats", Document.class);
            Document seat = seats.stream()
                    .filter(s -> seatID.equals(s.getString("ID")) && "Vacant".equals(s.getString("Status")))
                    .findFirst()
                    .orElse(null);

            if (seat == null) {
                return false;
            }

            if (person.getBalance() < seatPrice) {
                return false;
            }

            // Store old balance and difference in Person object
            person.setOldBalance(person.getBalance());
            person.setDifference(seatPrice);

            // Update seat status and person details
            seat.put("Status", "Booked");
            seat.put("Name", person.getName());
            seat.put("Surname", person.getSurname());
            seat.put("Document_Info", person.getDocumentInfo());
            seat.put("Date_of_Birth", person.getDateOfBirth());
            seat.put("Balance", person.getBalance() - seatPrice);

            // Update MongoDB document atomically
            UpdateResult result = collection.updateOne(
                    Filters.and(
                            Filters.eq("Flights.ID", flightID),
                            Filters.eq("Flights.Seats.ID", seatID),
                            Filters.eq("Flights.Seats.Status", "Vacant")
                    ),
                    Updates.combine(
                            Updates.set("Flights.$[flight].Seats.$[seat].Status", "Booked"),
                            Updates.set("Flights.$[flight].Seats.$[seat].Name", person.getName()),
                            Updates.set("Flights.$[flight].Seats.$[seat].Surname", person.getSurname()),
                            Updates.set("Flights.$[flight].Seats.$[seat].Document_Info", person.getDocumentInfo()),
                            Updates.set("Flights.$[flight].Seats.$[seat].Date_of_Birth", person.getDateOfBirth()),
                            Updates.set("Flights.$[flight].Seats.$[seat].Balance", person.getBalance() - seatPrice)
                    ),
                    new UpdateOptions().arrayFilters(Arrays.asList(
                            Filters.eq("flight.ID", flightID),
                            Filters.eq("seat.ID", seatID)
                    ))
            );

            if (result.getModifiedCount() == 1) {
                // Deduct seat price from person's balance
                person.setBalance(person.getBalance() - seatPrice);
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }



    public void close() {
        mongoClient.close();
    }

}

