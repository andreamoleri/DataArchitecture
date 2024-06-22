package com.mongodb.quickstart;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.bson.Document;

import java.io.FileReader;
import java.io.IOException;



public class CsvToMongoDB {

    public static void main(String[] args) {
        // Connessione al database MongoDB locale
        try (var mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("mydatabase");
            MongoCollection<Document> collection = database.getCollection("airports");

            // Percorso del file CSV da importare
            String csvFile = "Data/Airports.csv";

            // Lettura del file CSV
            try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
                String[] headers = reader.readNext(); // Lettura delle intestazioni

                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    Document doc = new Document();
                    for (int i = 0; i < headers.length; i++) {
                        doc.append(headers[i], nextLine[i]);
                    }
                    collection.insertOne(doc);
                }
                System.out.println("Importazione completata.");
            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
            }
        }
    }
}
