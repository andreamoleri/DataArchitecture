/**
 * The Connection class provides functionality to connect to a MongoDB instance and list all available databases.
 */
package com.mongodb.quickstart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Connection {

    /**
     * The main method establishes a connection to the MongoDB instance specified by the URI provided
     * as a system property and lists all available databases.
     *
     * @param args the command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Retrieve the MongoDB URI from the system properties
        String connectionString = System.getProperty("mongodb.uri");

        // Establish a connection to the MongoDB instance
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            // Retrieve the list of databases
            List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());

            // Print information about each database
            databases.forEach(db -> System.out.println(db.toJson()));
        }
    }
}
