// Importa le classi necessarie
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            // Configurazione del FileHandler con un Formatter personalizzato
            FileHandler fileHandler = new FileHandler("app.log");
            fileHandler.setFormatter(new SimpleLogFormatter());
            logger.addHandler(fileHandler);

            // Dettagli della connessione a MongoDB
            String connectionString = "mongodb+srv://admin:admin@learningmongodb.hikoksa.mongodb.net/?retryWrites=true&w=majority&appName=LearningMongoDB";
            String dbName = "Airports";
            String collectionName = "airportCollection";

            // Inizializzazione del client MongoDB e istanza Transactions
            MongoClient mongoClient = MongoClients.create(connectionString);
            Transactions transactions = new Transactions(mongoClient, dbName, collectionName);

            // Esempio di utilizzo dei metodi di Transactions
            String departureAirportCode = "MXP";
            String arrivalAirportCode = "PMV";

            Map<String, Map<String, String>> flightsFromBGY = transactions.getFlightsFromAirport(departureAirportCode);

            logger.info("├─ TESTING FLIGHT RETRIEVAL");
            logger.info("│---├─ Retrieving flights departing from the specified airport (" + departureAirportCode + ")");
            for (Map.Entry<String, Map<String, String>> entry : flightsFromBGY.entrySet()) {
                String iataCode = entry.getKey();
                Map<String, String> flightDetails = entry.getValue();
                logger.info("│---├─ Flight to " + iataCode + ": " + flightDetails);
            }
            logger.info("");

            PeopleGenerator generator = null;
            String flightID = null;
            List<String> availableSeatsDetails = null;
            if (flightsFromBGY.containsKey(arrivalAirportCode)) {
                flightID = flightsFromBGY.get(arrivalAirportCode).get("ID");

                logger.info("├─ TESTING SEATS RETRIEVAL");
                logger.info("│---├─ The user chose to depart from " + departureAirportCode + " to " + arrivalAirportCode);
                logger.info("│---├─ Retrieving available seats for the specified flight (" + departureAirportCode + " -> " + arrivalAirportCode + ")");
                availableSeatsDetails = transactions.getAvailableSeats(departureAirportCode, arrivalAirportCode);
                logger.info("│---├─ Number of available seats: " + availableSeatsDetails.size());
                logger.info("│---├─ Available seats are: " + availableSeatsDetails);
                logger.info("");

                if (!availableSeatsDetails.isEmpty()) {
                    // Shuffle the list to randomize the seat selection
                    Collections.shuffle(availableSeatsDetails);

                    // TESTING CONCURRENT TRANSACTIONS
                    String seatID = availableSeatsDetails.get(0);
                    generator = new PeopleGenerator();
                    List<PeopleGenerator.Person> people = generator.generatePeople(2);
                    PeopleGenerator.Person person1 = people.get(0);
                    PeopleGenerator.Person person2 = people.get(1);

                    logger.info("├─ TESTING CONCURRENT TRANSACTIONS");
                    logger.info("│---├─ Testing concurrent booking for the same seat (" + seatID + ") on flight " + flightID + " (" + departureAirportCode + " -> " + arrivalAirportCode + ")");
                    logger.info("│---├─ Booking result for person 1: " + transactions.bookFlight(flightID, seatID, person1));
                    logger.info("│---├─ Booking result for person 2: " + transactions.bookFlight(flightID, seatID, person2));
                    logger.info("│---├─ Person 1 booked the seat first and successfully: " + person1);
                    logger.info("│---├─ Person 2 consequently failed to book the same seat: " + person2);
                    logBalanceChange(logger, person1);
                    logger.info("");

                    // TESTING NON-CONCURRENT TRANSACTION
                    String newSeatID = availableSeatsDetails.get(1);
                    PeopleGenerator.Person newPerson = generator.generatePeople(1).get(0);

                    logger.info("├─ TESTING NON-CONCURRENT TRANSACTION");
                    logger.info("│---├─ A new user chose to book the " + newSeatID + " seat, without any concurrency from other users");
                    logger.info("│---├─ Booking result for new person: " + transactions.bookFlight(flightID, newSeatID, newPerson));
                    logger.info("│---├─ New person booked the seat successfully: " + newPerson);
                    logBalanceChange(logger, newPerson);
                    logger.info("");
                } else {
                    logger.info("│---├─ No available seats found for the flight " + flightID + " (" + departureAirportCode + " -> " + arrivalAirportCode + ")");
                }
            } else {
                logger.info("│---├─ No flights found departing from " + departureAirportCode);
            }

            // Testing per la persona "povera" che prova a prenotare un posto libero
            availableSeatsDetails = transactions.getAvailableSeats(departureAirportCode, arrivalAirportCode);
            PeopleGenerator.Person poorPerson = generator.generatePoorPerson();
            String poorPersonSeatID = availableSeatsDetails.get(1);

            logger.info("├─ TESTING INSUFFICIENT FUNDS BEHAVIOUR");
            logger.info("│---├─ " + poorPerson.getName() + " " + poorPerson.getSurname() + " with balance " + poorPerson.getBalance() + "$ is attempting to book seat " + poorPersonSeatID);
            logger.info("│---├─ Booking result for person with insufficient funds: " + transactions.bookFlight(flightID, poorPersonSeatID, poorPerson));
            logger.info("│---├─ The booking failed to to having insufficient balance to complete the transaction");
            logger.info("");

            // Chiudi il client MongoDB
            transactions.close();

            // Chiudi il FileHandler dopo l'utilizzo
            fileHandler.close();

        } catch (IOException e) {
            logger.severe("│---├─ Error during logger configuration: " + e.getMessage());
        }
    }

    // Formatter personalizzato per escludere la data, l'ora, il nome della classe e il livello di log
    static class SimpleLogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return record.getMessage() + System.lineSeparator();
        }
    }

    private static void logBalanceChange(Logger logger, PeopleGenerator.Person person) {
        logger.info(String.format("│---├─ The balance of %s %s before booking the flight was %.2f$",
                person.getName(), person.getSurname(), person.getOldBalance()));
        logger.info(String.format("│---├─ The cost of the flight was %.2f$", person.getDifference()));
        logger.info(String.format("│---├─ This means the new balance of %s %s is %.2f$",
                person.getName(), person.getSurname(), person.getBalance()));
    }
}
