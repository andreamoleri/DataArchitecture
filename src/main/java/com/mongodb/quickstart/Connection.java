package com.mongodb.quickstart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Connection {
    private static Connection instance;
    private final MongoClient mongoClient;

    /**
     * Constructs a new Connection instance with the specified connection string.
     *
     * @param connectionString the MongoDB connection string
     */
    private Connection(String connectionString) {
        this.mongoClient = MongoClients.create(connectionString);
    }

    /**
     * Returns the singleton instance of the Connection class, creating it if necessary.
     *
     * @param connectionString the MongoDB connection string
     * @return the singleton instance of Connection
     */
    public static synchronized Connection getInstance(String connectionString) {
        if (instance == null) {
            instance = new Connection(connectionString);
        }
        return instance;
    }

    /**
     * Retrieves and prints the list of databases available on the connected MongoDB instance.
     */
    public void listDatabases() {
        // Retrieve the list of databases
        List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());

        // Print information about each database
        databases.forEach(db -> System.out.println(db.toJson()));
    }

    /**
     * Main method to establish a connection to MongoDB and list available databases.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Imposta la connection string direttamente qui
        String connectionString = "mongodb+srv://admin:admin@learningmongodb.hikoksa.mongodb.net/?retryWrites=true&w=majority&appName=LearningMongoDB";

        // Verifica che la connection string non sia vuota
        if (connectionString == null || connectionString.isEmpty()) {
            System.err.println("MongoDB URI not specified. Use -Dmongodb.uri=<connection_string>");
            return;
        }

        // Establish a connection to the MongoDB instance
        Connection connection = Connection.getInstance(connectionString);
        connection.listDatabases();
    }
}
