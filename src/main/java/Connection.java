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
     * Getter method to return the MongoClient instance.
     *
     * @return MongoClient instance
     */
    public MongoClient getMongoClient() {
        return this.mongoClient;
    }
}
