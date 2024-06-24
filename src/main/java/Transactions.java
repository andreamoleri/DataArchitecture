import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Transactions {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private Lock lock = new ReentrantLock();

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

    // Method to book a flight
    public boolean bookFlight(String flightID, String seatID, PeopleGenerator.Person person) {
        lock.lock();
        try {
            // Query the flight document
            Document flightDocument = collection.find(Filters.eq("Flights.ID", flightID)).first();
            if (flightDocument == null) {
                return false;
            }

            List<Document> flights = (List<Document>) flightDocument.get("Flights");
            Document targetFlight = null;
            for (Document f : flights) {
                if (f.getString("ID").equals(flightID)) {
                    targetFlight = f;
                    break;
                }
            }

            if (targetFlight == null) {
                return false;
            }

            // Retrieve the price from the flight document
            Number seatPriceNumber = targetFlight.get("Price_per_Person", Number.class);
            if (seatPriceNumber == null) {
                return false;
            }
            Double seatPrice = seatPriceNumber.doubleValue();

            // Find the seat document
            List<Document> seats = (List<Document>) targetFlight.get("Seats");
            Document seat = null;
            for (Document s : seats) {
                if (s.getString("ID").equals(seatID) && s.getString("Status").equals("Vacant")) {
                    seat = s;
                    break;
                }
            }

            if (seat == null) {
                return false;
            }

            // Check if the person has sufficient balance
            if (person.getBalance() < seatPrice) {
                return false;
            }

            // Update the seat status to "Booked"
            seat.put("Status", "Booked");
            seat.put("Name", person.getName());
            seat.put("Surname", person.getSurname());
            seat.put("Document_Info", person.getDocumentInfo());
            seat.put("Date_of_Birth", person.getDateOfBirth());
            seat.put("Balance", person.getBalance());

            // Update the flight document in the database
            collection.updateOne(
                    Filters.and(
                            Filters.eq("Flights.ID", flightID),
                            Filters.eq("Flights.Seats.ID", seatID)),
                    Updates.combine(
                            Updates.set("Flights.$[flight].Seats.$[seat].Status", "Booked"),
                            Updates.set("Flights.$[flight].Seats.$[seat].Name", person.getName()),
                            Updates.set("Flights.$[flight].Seats.$[seat].Surname", person.getSurname()),
                            Updates.set("Flights.$[flight].Seats.$[seat].Document_Info", person.getDocumentInfo()),
                            Updates.set("Flights.$[flight].Seats.$[seat].Date_of_Birth", person.getDateOfBirth()),
                            Updates.set("Flights.$[flight].Seats.$[seat].Balance", person.getBalance())
                    ),
                    new com.mongodb.client.model.UpdateOptions().arrayFilters(
                            java.util.Arrays.asList(
                                    Filters.eq("flight.ID", flightID),
                                    Filters.eq("seat.ID", seatID)
                            )
                    )
            );

            // Deduct the seat price from the person's balance
            person.setBalance(person.getBalance() - seatPrice);
            return true;
        } finally {
            lock.unlock();
        }
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

        if (flightsFromBGY.containsKey(arrivalAirportCode)) {
            String flightID = flightsFromBGY.get(arrivalAirportCode);

            // Get available seats details from BGY to BLL
            List<String> availableSeatsDetails = transactions.getAvailableSeats(departureAirportCode, arrivalAirportCode);
            System.out.println("Available seats from " + departureAirportCode + " to " + arrivalAirportCode + ": " + availableSeatsDetails);

            if (!availableSeatsDetails.isEmpty()) {
                String seatID = availableSeatsDetails.get(0);

                // Generate some people
                PeopleGenerator generator = new PeopleGenerator();
                List<PeopleGenerator.Person> people = generator.generatePeople(2);

                PeopleGenerator.Person person1 = people.get(0);
                PeopleGenerator.Person person2 = people.get(1);

                // Try to book the same seat for both people
                boolean bookingResult1 = transactions.bookFlight(flightID, seatID, person1);
                boolean bookingResult2 = transactions.bookFlight(flightID, seatID, person2);

                System.out.println("Booking result for person 1: " + bookingResult1);
                System.out.println("Booking result for person 2: " + bookingResult2);

                if (bookingResult1) {
                    System.out.println("Person 1 booked the seat successfully: " + person1);
                } else {
                    System.out.println("Person 1 failed to book the seat: " + person1);
                }

                if (bookingResult2) {
                    System.out.println("Person 2 booked the seat successfully: " + person2);
                } else {
                    System.out.println("Person 2 failed to book the seat: " + person2);
                }
            }
        }

        // Close MongoDB connection
        transactions.close();
    }
}
