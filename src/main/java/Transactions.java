import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiRenderer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
            Document targetFlight = null;
            for (Document f : flights) {
                if (flightID.equals(f.getString("ID"))) {
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

            List<Document> seats = targetFlight.getList("Seats", Document.class);
            Document seat = null;
            for (Document s : seats) {
                if (seatID.equals(s.getString("ID")) && "Vacant".equals(s.getString("Status"))) {
                    seat = s;
                    break;
                }
            }

            if (seat == null) {
                return false;
            }

            if (person.getBalance() < seatPrice) {
                return false;
            }

            seat.put("Status", "Booked");
            seat.put("Name", person.getName());
            seat.put("Surname", person.getSurname());
            seat.put("Document_Info", person.getDocumentInfo());
            seat.put("Date_of_Birth", person.getDateOfBirth());
            seat.put("Balance", person.getBalance());

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
                            Arrays.asList(
                                    Filters.eq("flight.ID", flightID),
                                    Filters.eq("seat.ID", seatID)
                            )
                    )
            );

            person.setBalance(person.getBalance() - seatPrice);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        mongoClient.close();
    }

    public static void main(String[] args) {
        // Abilita ANSI su console
        AnsiConsole.systemInstall();

        try {
            // Setup file logging without timestamps
            FileHandler fh = new FileHandler("booking.log");
            fh.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(java.util.logging.LogRecord record) {
                    return record.getMessage() + System.lineSeparator();
                }
            });
            logger.addHandler(fh);
            logger.setLevel(Level.INFO); // Set logging level to INFO

            // Aggiungi un handler per la console che supporta ANSI
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(java.util.logging.LogRecord record) {
                    String message = super.formatMessage(record);
                    if (record.getLevel() == Level.SEVERE) {
                        return AnsiRenderer.render("@|red " + message + "|@");
                    } else {
                        return message;
                    }
                }
            });
            consoleHandler.setLevel(Level.ALL); // Puoi impostare il livello di log desiderato
            logger.addHandler(consoleHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // MongoDB connection details
        String connectionString = "mongodb+srv://admin:admin@learningmongodb.hikoksa.mongodb.net/?retryWrites=true&w=majority&appName=LearningMongoDB";
        String dbName = "Airports";
        String collectionName = "airportCollection";

        // Initialize MongoDB client and Transactions instance
        MongoClient mongoClient = MongoClients.create(connectionString);
        Transactions transactions = new Transactions(mongoClient, dbName, collectionName);

        // Example usage of Transactions methods
        String departureAirportCode = "MXP";
        String arrivalAirportCode = "CDG";

        Map<String, Map<String, String>> flightsFromBGY = transactions.getFlightsFromAirport(departureAirportCode);

        logger.info("├─ TESTING FLIGHT RETRIEVAL");
        logger.info(String.format("│---├─ Retrieving flights departing from the specified airport (%s)", departureAirportCode));
        for (Map.Entry<String, Map<String, String>> entry : flightsFromBGY.entrySet()) {
            String iataCode = entry.getKey();
            Map<String, String> flightDetails = entry.getValue();
            logger.info(String.format("│---├─ Flight to %s: %s", iataCode, flightDetails));
        }
        logger.info("");

        if (flightsFromBGY.containsKey(arrivalAirportCode)) {
            String flightID = flightsFromBGY.get(arrivalAirportCode).get("ID");

            logger.info("├─ TESTING SEATS RETRIEVAL");
            List<String> availableSeatsDetails = transactions.getAvailableSeats(departureAirportCode, arrivalAirportCode);
            logger.info(String.format("│---├─ The user chose to depart from %s to %s", departureAirportCode, arrivalAirportCode));
            logger.info(String.format("│---├─ Retrieving available seats for the specified flight (%s -> %s)", departureAirportCode, arrivalAirportCode));
            logger.info(String.format("│---├─ Number of available seats: %d", availableSeatsDetails.size()));
            logger.info(String.format("│---├─ Available seats are: %s", availableSeatsDetails));
            logger.info("");

            if (!availableSeatsDetails.isEmpty()) {
                // Shuffle the list to randomize the seat selection
                Collections.shuffle(                availableSeatsDetails);
                String seatID = availableSeatsDetails.get(0);

                PeopleGenerator generator = new PeopleGenerator();
                List<PeopleGenerator.Person> people = generator.generatePeople(2);
                PeopleGenerator.Person person1 = people.get(0);
                PeopleGenerator.Person person2 = people.get(1);

                logger.info("├─ TESTING CONCURRENT TRANSACTIONS");
                logger.info(String.format("│---├─ Testing concurrent booking for the same seat (%s) on flight %s (%s -> %s)", seatID, flightID, departureAirportCode, arrivalAirportCode));

                boolean bookingResult1 = transactions.bookFlight(flightID, seatID, person1);
                boolean bookingResult2 = transactions.bookFlight(flightID, seatID, person2);

                logger.info(String.format("│---├─ Booking result for person 1: %s", bookingResult1));
                logger.info(String.format("│---├─ Booking result for person 2: %s", bookingResult2));

                if (bookingResult1) {
                    logger.info(String.format("│---├─ Person 1 booked the seat first and successfully: %s", person1));
                } else {
                    logger.info(String.format("│---├─ Person 1 consequently failed to book the same seat: %s", person1));
                }

                if (bookingResult2) {
                    logger.info(String.format("│---├─ Person 2 booked the seat first and successfully: %s", person2));
                } else {
                    logger.info(String.format("│---├─ Person 2 consequently failed to book the same seat: %s", person2));
                }

                logger.info("");

                // Aggiungi un caso di test per la prenotazione non concorrente
                if (availableSeatsDetails.size() > 1) {
                    // Shuffle the remaining seats to choose one randomly
                    Collections.shuffle(availableSeatsDetails);
                    String newSeatID = availableSeatsDetails.get(0); // Prendi un posto disponibile casuale

                    PeopleGenerator.Person newPerson = generator.generatePeople(1).get(0); // Genera una nuova persona

                    boolean nonConcurrentBookingResult = transactions.bookFlight(flightID, newSeatID, newPerson);

                    logger.info("├─ TESTING NON-CONCURRENT TRANSACTION");
                    logger.info(String.format("│---├─ A new user chose to book the %s seat, without any concurrency from other users", newSeatID));
                    logger.info(String.format("│---├─ Booking result for new person: %s", nonConcurrentBookingResult));

                    if (nonConcurrentBookingResult) {
                        logger.info(String.format("│---├─ New person booked the seat successfully: %s", newPerson));
                    } else {
                        logger.info(String.format("│---├─ New person failed to book the seat: %s", newPerson));
                    }
                } else {
                    logger.warning(String.format("│---├─ No additional available seats from %s to %s", departureAirportCode, arrivalAirportCode));
                }

                logger.info("");

                // Aggiungi un caso di test per la prenotazione di un posto già occupato
                if (bookingResult1) {
                    String occupiedSeatID = seatID; // Utilizza il posto prenotato da person1

                    PeopleGenerator.Person anotherPerson = generator.generatePeople(1).get(0); // Genera un'altra persona

                    boolean bookingOccupiedSeatResult = transactions.bookFlight(flightID, occupiedSeatID, anotherPerson);

                    logger.info("├─ TESTING BOOKING FOR AN ALREADY OCCUPIED SEAT");
                    logger.info(String.format("│---├─ Booking result for another person trying to book an occupied seat (%s): %s", occupiedSeatID, bookingOccupiedSeatResult));

                    if (bookingOccupiedSeatResult) {
                        logger.severe(String.format("│---├─ Another person booked the already occupied seat (this should not happen): %s", anotherPerson));
                    } else {
                        logger.info(String.format("│---├─ Another person failed to book the already occupied seat (expected): %s", anotherPerson));
                    }
                } else {
                    logger.warning("│---├─ Skipping occupied seat booking test because initial booking was unsuccessful.");
                }

            } else {
                logger.warning(String.format("│---├─ No available seats from %s to %s", departureAirportCode, arrivalAirportCode));
            }
        } else {
            logger.warning(String.format("│---├─ No flights found from %s to %s", departureAirportCode, arrivalAirportCode));
        }

        transactions.close();
    }
}
