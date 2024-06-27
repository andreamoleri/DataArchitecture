




## Creating MongoDB Transactions in Java Applications

In this section, we demonstrate how to create a multi-document transaction in MongoDB using Java. A multi-document 
transaction ensures the atomicity of reads and/or writes across multiple documents. Specifically, a transaction is 
a sequence of operations executed on a database that represents a single unit of work. Once committed, all write 
operations within the transaction are persisted. If a transaction is aborted or fails to complete successfully, 
all associated write operations are rolled back. Therefore, all operations within a transaction either succeed or 
fail together. This property is known as atomicity. Transactions also ensure the consistency, isolation, and 
durability of operations. These qualities—Atomicity, Consistency, Isolation, and Durability—are collectively 
referred to as ACID compliance.

## Implementation Example

To initiate a transaction in MongoDB using Java, we utilize the `WithTransaction()` method of a session object.
Below are the steps involved in completing a multi-document transaction, followed by the corresponding code snippet:

1. **Session Initialization and Transaction Start**: Begin by establishing a new session and starting a transaction 
2. using the `WithTransaction()` method on the session object.

2. **Transaction Operations**: Define the operations to be performed within the transaction. This typically includes 
3. fetching necessary data, performing updates, and inserting documents.

3. **Transaction Commit**: After executing all operations successfully, commit the transaction to persist the changes.

4. **Handling Timeouts and Resource Closure**: MongoDB automatically cancels any multi-document transaction that 
5. exceeds 60 seconds. Additionally, ensure proper closure of resources utilized by the transaction.

### Example Code

```java
final MongoClient client = MongoClients.create(connectionString);
final ClientSession clientSession = client.startSession();

TransactionBody txnBody = new TransactionBody<String>() {
    public String execute() {
        MongoCollection<Document> bankingCollection = client.getDatabase("bank").getCollection("accounts");

        Bson fromAccountFilter = eq("account_id", "MDB310054629");
        Bson withdrawalUpdate = Updates.inc("balance", -200);

        Bson toAccountFilter = eq("account_id", "MDB643731035");
        Bson depositUpdate = Updates.inc("balance", 200);

        System.out.println("Withdrawing from Account " + fromAccountFilter.toBsonDocument().toJson() + ": " + withdrawalUpdate.toBsonDocument().toJson());
        System.out.println("Depositing to Account " + toAccountFilter.toBsonDocument().toJson() + ": " + depositUpdate.toBsonDocument().toJson());

        bankingCollection.updateOne(clientSession, fromAccountFilter, withdrawalUpdate);
        bankingCollection.updateOne(clientSession, toAccountFilter, depositUpdate);

        return "Transferred funds from John Doe to Mary Doe";
    }
};

try {
    clientSession.withTransaction(txnBody);
} catch (RuntimeException e) {
    System.out.println("Transaction aborted: " + e.getMessage());
} finally {
    clientSession.close();
}
```

This Java code snippet exemplifies the process described. It begins by initializing a MongoDB client and starting a 
session. Within the `execute()` method of the `TransactionBody`, two updates are performed atomically on specified
accounts. If all operations succeed, the transaction commits; otherwise, it rolls back automatically. Finally, the 
session is closed to release associated resources.

By following these steps and utilizing MongoDB's transaction capabilities in Java, developers can ensure reliable 
and consistent data operations across multiple documents within a MongoDB database. What follows is another example of
a real-world scenario in which we would use the method.

```java
// DemoApp.java
public class DemoApp {
    public static void main(final String[] args) {
        Logger root = (Logger) LoggerFactory.getLogger("org.mongodb.driver");
        // Available levels are: OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL
        root.setLevel(Level.WARN);

        String connectionString = System.getenv("MONGODB_URI");
        try (MongoClient client = MongoClients.create(connectionString)) {
            //Transaction
            Transaction txn = new Transaction(client);
            var senderAccountFilter = "MDB310054629";
            var receiverAccountFilter = "MDB643731035";
            double transferAmount = 200;
            txn.transferMoney(senderAccountFilter, transferAmount, receiverAccountFilter);
        }
    }
}

// Transaction.java
public class Transaction {
    private final MongoClient client;

    public Transaction(MongoClient client) {
        this.client = client;
    }

    public void transferMoney(String accountIdOfSender, double transactionAmount, String accountIdOfReceiver) {
    try (ClientSession session = client.startSession()) {
        UUID transfer = UUID.randomUUID();
        String transferId = transfer.toString();
        try {
            session.withTransaction(() -> {
                MongoCollection<Document> accountsCollection = client.getDatabase("bank").getCollection("accounts");
                MongoCollection<Document> transfersCollection = client.getDatabase("bank").getCollection("transfers");


                Bson senderAccountFilter = eq("account_id", accountIdOfSender);
                Bson debitUpdate = Updates.combine(inc("balance", -1 * transactionAmount),push("transfers_complete", transferId));

                Bson receiverAccountId = eq("account_id", accountIdOfReceiver);
                Bson credit = Updates.combine(inc("balance", transactionAmount), push("transfers_complete", transferId));

                transfersCollection.insertOne(session, new Document("_id", new ObjectId()).append("transfer_id", transferId).append("to_account", accountIdOfReceiver).append("from_account", accountIdOfSender).append("amount", transactionAmount).append("last_updated", new Date()));
                accountsCollection.updateOne(session, senderAccountFilter, debitUpdate);
                accountsCollection.updateOne(session, receiverAccountId, credit);
                return null;
            });
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
}
```

## Introduction to MongoDB Aggregation

In the realm of databases, aggregation involves the analysis and summary of data, where an aggregation stage represents 
an operation performed on data without permanently altering the source data. MongoDB facilitates the creation of 
aggregation pipelines, where developers specify aggregation operations sequentially. What distinguishes MongoDB 
aggregations is the ability to chain these operations into a pipeline, consisting of stages where data can be filtered, 
sorted, grouped, and transformed. Documents output from one stage become the input for the next. In MongoDB Atlas, 
developers can access the Aggregation tab to add stages one by one and view results for each stage. Similarly, this 
can be accomplished using MongoDB CLI or MongoDB Language Drivers. Below is an example of aggregation syntax using the 
CLI, starting with `db.collection.aggregate` followed by stage names and their contained expressions. Each stage 
represents a discrete data operation, commonly including `$match` for filtering data, `$group` for grouping documents, 
and `$sort` for ordering documents based on specified criteria. The use of `$` prefix signifies a field path,
referencing the value in that field, useful for operations like concatenation (`$concat: ["$first_name", "$last_name"]`).

```bash
db.collection.aggregate([
    {
        $stage1: {
            { expression1 },
            { expression2 }...
        },
        $stage2: {
            { expression1 }...
        }
    }
])
```

## Using $match and $group Stages in a MongoDB Aggregation Pipeline

The `$match` stage filters documents that match specified conditions, as illustrated in the example below. The `$group`
stage groups documents based on a specified group key. These stages are commonly used together in an aggregation 
pipeline. In the example, the aggregation pipeline identifies documents with a "state" field matching "CA" and then 
groups these documents by the "$city" group key to count the total number of zip codes in California. Placing `$match` 
early in the pipeline optimizes performance by utilizing indexes to reduce the number of documents processed. 
Conversely, the output of `$group` is a document for each unique value of the group key. Note that `$group` includes 
`_id` as the group key and an accumulator field, specifying how to aggregate information for each group. For instance, 
grouping by city and using `count` as an accumulator determines the count of ZIP Codes per city.

```bash
# Example of Match Stage
{
    $match: {
        "field_name": "value"
    }
}

# Example of Group Stage
{
    $group:
    {
        _id: <expression>, // Group key
        <field>: { <accumulator> : <expression> }
    }
}
 
# Example Using Both
db.zips.aggregate([
    { $match: { state: "CA" } },
    {
        $group: {
            _id: "$city",
            totalZips: { $count : { } }
        }
    }
])
```

## Using $sort and $limit Stages in a MongoDB Aggregation Pipeline

Next, the `$sort` and `$limit` stages in MongoDB aggregation pipelines are discussed. The `$sort` stage arranges all 
input documents in a specified order, using `1` for ascending and `-1` for descending order. The `$limit` stage restricts 
output to a specified number of documents. These stages can be combined, such as in the third example where documents 
are sorted in descending order by population (`pop`), and only the top five documents are returned. `$sort` and `$limit`
stages are essential for quickly identifying top or bottom values in a dataset. Order of stages is crucial; arranging 
`$sort` before `$limit` yields different results compared to the reverse order.

```bash
# Example of Sort Stage
{
    $sort: {
        "field_name": 1
    }
}

# Example of Limit Stage
{
    $limit: 5
}

# Example Using Both
db.zips.aggregate([
    { $sort: { pop: -1 } },
    { $limit: 5 }
])
```

## Using $project, $count, and $set Stages in a MongoDB Aggregation Pipeline

Moving on to `$project`, `$set`, and `$count` stages in MongoDB aggregation pipelines. The `$project` stage specifies 
output document fields, including (`1` for inclusion, `0` for exclusion), and optionally assigns new values to fields.
This stage is typically the final one to format output. The `$set` stage creates new fields or modifies existing ones 
within documents, facilitating changes or additions for subsequent pipeline stages. The `$count` stage generates a 
document indicating the count of documents at that stage in the pipeline. `$set` is useful for field modifications, 
while `$project` controls output field visibility and value transformations. `$count` provides a count of documents 
in the aggregation pipeline stage.

```bash
# Example of Project Stage
{
    $project: {
        state: 1, 
        zip: 1,
        population: "$pop",
        _id: 0
    }
}

# Example of Set Stage
{
    $set: {
        place: {
            $concat: ["$city", ",", "$state"]
        },
        pop: 10000
    }
}

# Example of Count Stage
{
    $count: "total_zips"
}
```

## Using the $out Stage in a MongoDB Aggregation Pipeline

The `$out` stage facilitates the creation of a new collection from the output of an aggregation pipeline. It writes 
documents returned by the pipeline into a specified collection. This stage must be the last one in the pipeline. 
Note that `$out` creates a new collection if one does not already exist. If the collection exists, `$out` overwrites 
it with new data. Therefore, careful consideration of the collection name is advised to avoid unintentionally 
overwriting existing data. The `$out` stage expects the database name in the `db` field and the collection name in 
the `coll` field. Alternatively, providing just the collection name directly is also valid. Executing `$out` does not 
produce command-line output; instead, results of the aggregation pipeline are written to a new collection, confirmed 
by `show collections` command in the terminal.

```bash
# Mode 1
$out: {
    db: "<db>",
    coll: "<newcollection>"
}

# Mode 2
{ $out: "<newcollection>" }

# Example
db.sightings.aggregate([
    {
        $match: {
            date: {
                $gte: ISODate('2022-01-01T00:00:00.0Z'),
                $lt: ISODate('2023-01-01T00:00:00.0Z')
            }
        }
    },
    {
        $out: 'sightings_2022'
    }
])
db.sightings_2022.findOne()
```

## Building a MongoDB Aggregation Pipeline in Java Applications

When using the MongoDB Aggregation Framework to construct queries, one must conceptualize these queries as composed of 
discrete stages, where each stage produces an output document that serves as input to the next stage. This aggregation 
pipeline simplifies debugging and maintenance of individual stages, facilitating query rewriting and optimization. 
The expression operators used within this framework function akin to functions, offering a broad spectrum including 
arithmetic, trigonometric, date, and boolean operators. Once assembled, the aggregation pipeline can be validated using 
tools such as MongoShell, Atlas Aggregation Builder, and Compass before integration into the chosen programming language.

## Using MongoDB Aggregation Stages with Java: $match and $group

In the following Java examples, the `Aggregates` builder class is employed to configure `$match` and `$group` stages 
within MongoDB aggregation pipelines. Each example demonstrates how to utilize these stages effectively to manipulate 
and aggregate data.

### Example 1: Using $match

```java
public static void main(String[] args) {
    String connectionString = System.getProperty("mongodb.uri");
    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
        MongoDatabase db = mongoClient.getDatabase("bank");
        MongoCollection<Document> accounts = db.getCollection("accounts");
        matchStage(accounts);
    }
}

private static void matchStage(MongoCollection<Document> accounts){
    Bson matchStage = Aggregates.match(Filters.eq("account_id", "MDB310054629"));
    System.out.println("Display aggregation results");
    accounts.aggregate(Arrays.asList(matchStage)).forEach(document -> System.out.print(document.toJson()));
}
```

### Example 2: Using $match and $group

```java
public static void main(String[] args) {
    String connectionString = System.getProperty("mongodb.uri");
    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
        MongoDatabase db = mongoClient.getDatabase("bank");
        MongoCollection<Document> accounts = db.getCollection("accounts");
        matchAndGroupStages(accounts);
    }
}

private static void matchAndGroupStages(MongoCollection<Document> accounts){
    Bson matchStage = Aggregates.match(Filters.eq("account_id", "MDB310054629"));
    Bson groupStage = Aggregates.group("$account_type", sum("total_balance", "$balance"), avg("average_balance", "$balance"));
    System.out.println("Display aggregation results");
    accounts.aggregate(Arrays.asList(matchStage, groupStage)).forEach(document -> System.out.print(document.toJson()));
}
```

## Using MongoDB Aggregation Stages with Java: $sort and $project

This example illustrates the use of `$sort` and `$project` stages within MongoDB aggregation pipelines, emphasizing 
sorting and projecting fields from queried documents.

```java
public static void main(String[] args) {
    String connectionString = System.getProperty("mongodb.uri");
    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
        MongoDatabase db = mongoClient.getDatabase("bank");
        MongoCollection<Document> accounts = db.getCollection("accounts");
        matchSortAndProjectStages(accounts);
    }
}

private static void matchSortAndProjectStages(MongoCollection<Document> accounts){
    Bson matchStage =
            Aggregates.match(Filters.and(Filters.gt("balance", 1500), Filters.eq("account_type", "checking")));
    Bson sortStage = Aggregates.sort(Sorts.orderBy(descending("balance")));
    Bson projectStage = Aggregates.project(
            Projections.fields(
                    Projections.include("account_id", "account_type", "balance"),
                    Projections.computed("euro_balance", new Document("$divide", Arrays.asList("$balance", 1.20F))),
                    Projections.excludeId()
            )
    );
    System.out.println("Display aggregation results");
    accounts.aggregate(Arrays.asList(matchStage, sortStage, projectStage)).forEach(document -> System.out.print(document.toJson()));
}
```

These examples demonstrate the structured use of MongoDB aggregation stages in Java applications, showcasing the 
flexibility and power of the MongoDB Aggregation Framework for data analysis and manipulation. Each stage—`$match`, 
`$group`, `$sort`, and `$project`—plays a crucial role in shaping and refining the results of queries executed against 
MongoDB databases.

## Using MongoDB Indexes in Collections

Indexes in MongoDB are specialized data structures that store a subset of the collection's data in a sorted format, 
facilitating efficient traversal and search operations. They significantly enhance query performance by allowing quick 
lookup, access, and updates of data. MongoDB utilizes indexes to accelerate queries, reduce disk I/O, and optimize 
resource utilization. Indexes support various query operations such as equality matches, range-based queries, and 
sorted results. Without indexes, MongoDB performs a Collection Scan, reading every document in the collection, 
potentially followed by in-memory sorting if required by the query. When utilizing indexes, MongoDB fetches only the 
documents identified by the index relevant to the query, avoiding unnecessary document reads. By default, MongoDB 
creates a single index per collection that includes the `_id` field. Additional indexes can be created to cover specific 
query patterns. However, indexes also impact write performance as they require updates whenever documents are inserted 
or updated. Over-indexing can further degrade performance, necessitating periodic review and removal of redundant indexes.

The most common index types in MongoDB include Single Field Indexes, which index a single field, and Compound Indexes, 
which involve multiple fields in the index definition. Both types can also function as Multikey Indexes if they index arrays within documents.

## Creating a Single Field Index in MongoDB

To create a Single Field Index in MongoDB, the `createIndex()` method is utilized. This method specifies the field and 
optionally its sorting order within the index definition. For instance, the following command creates an ascending index 
on the `birthdate` field:

```bash
db.customers.createIndex({
  birthdate: 1
})
```

If searching customers by their email addresses is a common operation, creating an index on the `email` field can 
improve query performance significantly. Adding `{unique:true}` as an additional parameter ensures that the index 
enforces uniqueness on email values, preventing duplicates in the collection:

```bash
db.customers.createIndex({
  email: 1
},
{
  unique:true
})
```

Indexes can also be managed and viewed through MongoDB Atlas. By navigating to the Database > Collections > Indexes section, 
users can monitor index usage, performance metrics, and create or delete indexes as needed. Additionally, the `explain()`
method can be employed to analyze query execution plans and index usage, providing insights into query optimization.

To list all indexes created on a collection, including default and user-defined indexes, the `getIndexes()` method can be used:

```bash
db.customers.getIndexes()
```

This command displays comprehensive information about each index present in the `customers` collection, aiding in index
management and optimization efforts.

## Creating a Multikey Index in MongoDB

To create a multikey index in MongoDB, the `createIndex()` method is utilized with an object parameter specifying the 
array field and sort order. For instance, to index the `accounts` array field in ascending order:

```bash
db.customers.createIndex({
  accounts: 1
})
```

MongoDB imposes a constraint where only one array field can be indexed per index. If multiple fields are indexed, 
only one of them can be an array.

## Query Optimization with `explain()`

To verify whether an index is being utilized by a query, the `explain()` method is employed. By executing `explain()` 
on a query against a collection, MongoDB provides an execution plan detailing various stages such as `IXSCAN`, 
`COLLSCAN`, `FETCH`, and `SORT`. These stages indicate how the query is executed, including whether an index is 
utilized (`IXSCAN`), or if a collection scan occurs (`COLLSCAN`), among others. Multikey indexes enhance query 
efficiency by creating separate index keys for each element in an array field. This optimization allows MongoDB to 
search for specific index keys rather than scanning entire arrays, resulting in significant performance improvements.

```bash
db.customers.explain().find({
  accounts: 627788
})
```

## Working with Compound Indexes

Compound indexes in MongoDB involve indexing multiple fields within a document. Using the `createIndex()` method, 
a compound index is defined with an object containing two or more fields and their respective sort orders. Here's 
an example where the fields `active`, `birthdate`, and `name` are indexed with varying sort orders:

```bash
db.customers.createIndex({
  active: 1, 
  birthdate: -1,
  name: 1
})
```

The sequence of fields in a compound index impacts query optimization. MongoDB recommends organizing fields in the 
order of Equality, Sort, and Range operations. For instance, queries that match on equality (`active: true`), sort by 
a field (`birthdate`), and apply range conditions benefit from such indexing. The optimal order of indexed fields 
ensures efficient query execution by leveraging the index's structure.

```bash
db.customers.find({
  birthdate: {
    $gte: ISODate("1977-01-01")
  },
  active: true
}).sort({
  birthdate: -1, 
  name: 1
})
```

## Index Utilization and Projections

Indexes in MongoDB can cover queries entirely when all necessary data is contained within the index itself, without 
requiring data retrieval from memory. Projections specify which fields to return in query results. By including only 
indexed fields in projections, MongoDB can efficiently cover queries. For example, projecting 
`{name: 1, birthdate: 1, _id: 0}` ensures that MongoDB returns only the specified fields directly from the index.

```bash
db.customers.explain().find({
  birthdate: {
    $gte: ISODate("1977-01-01")
  },
  active: true
},
{
  name: 1,
  birthdate: 1,
  _id: 0
}).sort({
  birthdate: -1,
  name: 1
})
```

By following these indexing and querying strategies, MongoDB optimizes query performance and enhances database 
operations efficiency. Understanding how indexes and query execution plans interact is crucial for maximizing 
MongoDB's capabilities in handling large datasets and complex queries.

## Deleting an Index

In MongoDB, managing indexes is crucial for optimizing query performance and minimizing operational costs associated
with write operations. Indexes in MongoDB are automatically created for the `_id` field in every collection and are 
integral to MongoDB's internal operations; hence, they cannot be deleted.

To view all indexes associated with a collection, the `getIndexes()` method can be utilized. For example, executing 
`db.customers.getIndexes()` provides a comprehensive list of indexes. Conversely, the `dropIndex()` function facilitates 
the removal of specific indexes from a collection. This function accepts either an index key object or the name of the 
index as a string within its parentheses.

The decision to delete indexes should be made with careful consideration of their usage and impact on system performance. 
While indexes enhance query performance by reducing the number of database accesses required, they also impose overhead
on write operations. Consequently, eliminating unused or redundant indexes can mitigate performance degradation in 
MongoDB collections.

Before deleting an index, it is imperative to ensure that the index is not actively supporting any queries. Deleting
the sole index supporting a particular query can severely impair query performance. In production environments, a 
recommended approach involves initially hiding an index using `db.collection.hideIndex(<index>)` before completely 
removing it. This precautionary measure allows for the temporary concealment of an index without immediate deletion, 
thus minimizing disruption to query performance.

```bash
# Example: Deleting an index by name
db.customers.dropIndex('active_1_birthdate_-1_name_1')

# Example: Deleting an index by key
db.customers.dropIndex({
  active: 1,
  birthdate: -1,
  name: 1
})
```

## Deleting Multiple Indexes

In scenarios where multiple indexes need to be deleted simultaneously, MongoDB offers the `dropIndexes()` method. 
This command removes all non-default indexes from a collection, preserving the mandatory index on `_id`. Alternatively, 
`dropIndexes()` can be supplied with an array of index names to selectively delete specific indexes.

The process of index management can also be facilitated through MongoDB Atlas's graphical user interface (GUI), which
provides a user-friendly interface for executing operations such as index deletion.

```bash
# Example: Deleting multiple indexes by names
db.collection.dropIndexes([
  'index1name', 'index2name', 'index3name'
])
```

In conclusion, while indexes play a pivotal role in enhancing query performance in MongoDB, judicious 
management—including periodic review, deletion of redundant indexes, and careful consideration of operational 
implications—is essential to maintain optimal database performance and efficiency.