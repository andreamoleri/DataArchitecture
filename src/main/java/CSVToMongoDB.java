import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.quickstart.Connection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CSVToMongoDB {

    public static void main(String[] args) {
        String csvFile = "Data/Airports.csv"; // Percorso del file CSV
        String dbName = "Airports"; // Nome del database MongoDB
        String collectionName = "airportCollection"; // Nome della collezione MongoDB

        String connectionString = "mongodb+srv://admin:admin@learningmongodb.hikoksa.mongodb.net/?retryWrites=true&w=majority&appName=LearningMongoDB";

        try {
            // Ottieni MongoClient e MongoDB/MongoCollection
            MongoClient mongoClient = Connection.getInstance(connectionString).getMongoClient();
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;
            boolean isFirstLine = true; // Flag per controllare se è la prima riga
            String[] headers = null; // Array per contenere i nomi delle colonne

            List<Document> airports = new ArrayList<>(); // Lista per contenere tutti gli aeroporti per la generazione dei voli

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);

                if (isFirstLine) {
                    isFirstLine = false;
                    // Salva gli header e sostituisci gli spazi con underscore
                    headers = new String[fields.length];
                    for (int i = 0; i < fields.length; i++) {
                        headers[i] = fields[i].trim().replace(" ", "_");
                    }
                    continue; // Salta la riga degli header
                }

                // Crea un nuovo Document da memorizzare in MongoDB
                Document airportDoc = new Document();

                // Loop attraverso l'array di campi e aggiungi i campi non nulli al Document
                for (int i = 0; i < fields.length && i < headers.length; i++) {
                    String fieldName = headers[i].trim();
                    String fieldValue = fields[i].trim().replaceAll("\"\"", "\"");
                    if (!fieldName.isEmpty() && !fieldValue.isEmpty()) {
                        // Escludi i campi "Edit_in_OSM" e "other_tags"
                        if (!fieldName.equals("Edit_in_OSM") && !fieldName.equals("other_tags")) {
                            if (fieldName.equals("Size")) {
                                try {
                                    airportDoc.append(fieldName, Integer.parseInt(fieldValue));
                                } catch (NumberFormatException e) {
                                    System.err.println("Errore nel parsing del campo 'Size' a intero: " + fieldValue);
                                }
                            } else {
                                airportDoc.append(fieldName, fieldValue);
                            }
                        }
                    }
                }

                // Inserisci il Document in MongoDB
                collection.insertOne(airportDoc);

                // Aggiungi airportDoc alla lista per generare i voli successivamente
                airports.add(airportDoc);
            }

            // Dopo l'inserimento di tutti gli aeroporti, genera i voli per ciascun aeroporto
            for (Document airportDoc : airports) {
                generateAndInsertFlights(airportDoc, collection, airports);
            }

            System.out.println("Dati importati con successo in MongoDB");

            // Chiudi le risorse
            reader.close();
            mongoClient.close(); // Chiudi la connessione a MongoDB

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateAndInsertFlights(Document airportDoc, MongoCollection<Document> collection, List<Document> airports) {
        int airportSize = airportDoc.getInteger("Size", 0); // Dimensione dell'aeroporto
        List<Document> flights = generateFlights(airportSize, airports, airportDoc);

        // Calcola il numero massimo di voli in base alla dimensione dell'aeroporto
        int maxFlights = airportSize / 100;
        int totalSeats = 0;

        // Calcola il numero totale di posti dai voli esistenti
        for (Document flight : flights) {
            totalSeats += flight.getInteger("Number_of_Seats", 0);
        }

        // Riduci i voli se il numero totale di posti supera la capacità dell'aeroporto
        if (totalSeats > airportSize) {
            // Rimuovi i voli finché il numero totale di posti non rientra nella capacità dell'aeroporto
            while (totalSeats > airportSize && flights.size() > 0) {
                Document lastFlight = flights.remove(flights.size() - 1);
                totalSeats -= lastFlight.getInteger("Number_of_Seats", 0);
            }
        }

        // Aggiorna l'array dei voli in airportDoc
        airportDoc.put("Flights", flights);

        // Sostituisci il documento nella collezione MongoDB
        collection.replaceOne(new Document("_id", airportDoc.getObjectId("_id")), airportDoc);
    }

    private static List<Document> generateFlights(int airportSize, List<Document> airports, Document currentAirport) {
        List<Document> flights = new ArrayList<>();
        Random random = new Random();

        // Esempio: genera voli
        for (int i = 0; i < 5; i++) { // Genera 5 voli per aeroporto (modificare secondo necessità)
            // Seleziona casualmente un aeroporto di destinazione (assicurati che non sia lo stesso aeroporto corrente)
            Document destinationAirport = getRandomDestinationAirport(airports, currentAirport);

            // Genera data futura casuale tra 1 e 10 giorni da oggi
            LocalDate currentDate = LocalDate.now();
            int daysToAdd = random.nextInt(10) + 1;
            LocalDate futureDate = currentDate.plusDays(daysToAdd);
            String dayString = futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Genera ora casuale
            LocalTime randomTime = LocalTime.of(random.nextInt(24), random.nextInt(60));
            String hourString = randomTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            // Genera durata casuale tra 1 e 14 ore
            int durationHours = random.nextInt(14) + 1;
            String durationString = durationHours + " hours";

            // Array di operatori reali
            String[] operators = {"Ryanair", "Lufthansa", "EasyJet", "British Airways", "Air France"};

            // Genera operatore casuale
            String operator = operators[random.nextInt(operators.length)];

            // Genera prezzo casuale per persona tra 39 e 499
            int pricePerPerson = random.nextInt(461) + 39; // 39 to 499

            Document flight = new Document();
            flight.append("ID", new ObjectId().toString())
                    .append("Number_of_Seats", 100)
                    .append("Day", dayString)
                    .append("Hour", hourString)
                    .append("Operator", operator)
                    .append("Duration", durationString)
                    .append("Price_per_Person", pricePerPerson);

            // Aggiungi riferimento della destinazione come ObjectID
            flight.append("Destination", destinationAirport.getObjectId("_id"));

            // Genera posti per questo volo
            List<Document> seats = generateSeats(100); // Assume 100 posti per volo
            flight.append("Seats", seats);

            flights.add(flight);
        }

        return flights;
    }

    private static Document getRandomDestinationAirport(List<Document> airports, Document currentAirport) {
        Random random = new Random();
        Document destinationAirport = null;

        // Continua a provare finché non trovi un aeroporto diverso dall'aeroporto corrente
        while (true) {
            if (airports.isEmpty()) {
                throw new IllegalStateException("La lista degli aeroporti è vuota, impossibile selezionare la destinazione.");
            }
            int index = random.nextInt(airports.size());
            destinationAirport = airports.get(index);
            if (!destinationAirport.getObjectId("_id").equals(currentAirport.getObjectId("_id"))) {
                break;
            }
        }

        return destinationAirport;
    }

    private static List<Document> generateSeats(int numberOfSeats) {
        List<Document> seats = new ArrayList<>();
        String[] seatLetters = {"A", "B", "C", "D", "E", "F"};
        int seatNumber = 1;

        for (int i = 0; i < numberOfSeats; i++) {
            String seatID = seatNumber + seatLetters[i % 6]; // Cicla tra A e F
            Document seat = new Document();
            seat.append("Status", "Vacant")
                    .append("ID", seatID)
                    .append("Name", "")
                    .append("Surname", "")
                    .append("Document_Info", "")
                    .append("Date_of_Birth", "")
                    .append("Balance", 0);

            seats.add(seat);
            if ((i + 1) % 6 == 0) { // Passa alla riga successiva di numeri dei posti (1A, 2A, ...)
                seatNumber++;
            }
        }

        return seats;
    }
}
