# Cassandra

Apache Cassandra is a distributed and open-source NoSQL database designed to handle large volumes of data distributed across many servers. Originally created by Facebook to improve their ability to manage massive amounts of data, Cassandra has become one of the most widely used databases for applications that require high availability, scalability, and performance.

## Architecture

The Key Components in Cassandra are:
- node: any system running Cassandra, such as a physical host, cloud instance, or Docker container.
- rack: a group of nearby nodes, either on the same network switch or in the same cloud availability zone.
- data center: a collection of racks connected by a reliable network, corresponding to a physical building or a cloud region (e.g., AWS's us-west-1).

The set of nodes that defines the database, regardless of how many racks and data centers you have, is called a cluster. To organize components at a logical level, the concept of a ring is often used. The ring consists of nodes belonging to the same data center, and each node is assigned a token value range. Typically, two adjacent nodes along the ring do not belong to the same rack. The token value range assigned to each node is designed so that the right/left endpoint of a range is adjacent to the endpoint of the range of the adjacent node to the right/left.

Cassandra typically stores copies of data in various rack/data centers to ensure availability. However, when a node handles a query, it prefers to route it to other nodes within the same data center to optimize performance. To achieve this, Cassandra must know the system's state and uses two internal protocols to manage data placement based on the cluster topology:
- Gossip protocol: allows each node to monitor the state of other nodes in the cluster. A "Gossiper" in each Cassandra instance randomly selects up to three nodes every second to initiate a gossip session. During these sessions, nodes exchange mutual information about their past states, enabling all nodes to quickly learn the overall cluster state. Using this information, Cassandra determines whether a node is active or inactive, thus helping to route requests optimally within the cluster.
- Snitch component: informs each node about its proximity to other nodes. This information is crucial for deciding from which nodes to read and on which to write, and for efficiently distributing replicas to maximize availability in case of node, rack, or entire data center failures.

### Node-Level Architecture

The architecture of a Cassandra node is structured according to the following hierarchical layout:

- Cassandra Daemon (JVM): Cassandra is written in Java, so each node runs a JVM that interprets the Java code.
  - Memtables: In-memory data structures used to temporarily store write operations before they are written to disk.
  - Key Caches: The key cache stores the positions of row keys within SSTables. This allows Cassandra to quickly locate the data on disk without having to read the index files for every read operation.
  - Row Caches: Data structures that cache frequently accessed rows to further improve read performance.

- Disk:
  - Commit Logs: Files that ensure data durability and consistency. Each write operation is first recorded in the commit log before being applied to memory. These logs are crucial for data recovery in case of a failure. The main information stored in the commit logs includes:
    - Timestamp: The date and time of the operation.
    - Keyspace: The database to which the write operation belongs.
    - Table: The affected table.
    - Partition Key: The partition key.
    - Row Key: The row key.
    - Column Values: The values of the written or updated columns.
    - Operation Type: The type of operation (INSERT, UPDATE, DELETE).
    - TTL (Time to Live): The lifespan of the data, if applicable.
    - Consistency Level: The required consistency level.
    - Checksum: To ensure data integrity.
  - SSTables: Immutable files on disk where the data from memtables is periodically written for persistence.
  - Hints: A mechanism used to improve data availability and fault tolerance. When a target node is unavailable to receive a write, Cassandra records a hint on the source node. This hint contains information similar to that in the commit logs, necessary to apply the write once the target node becomes available. The most important information is the identifier of the node that couldn't receive the write and the data to be written.

### Consistency
When a user connects to the database, they are assigned a coordinator node. This node is responsible for coordinating interactions with other nodes, sending requests, collecting results, and returning the query result to the client.

To ensure data availability in case of node failures or unreachability, Cassandra redundantly stores data across multiple nodes known as replica nodes, based on the replication factor.

Ensuring consistency may require time, but in some cases, it may not be necessary. Cassandra offers a feature that allows users to manage the balance between data consistency and time. Consistency can be managed globally or selectively for individual read and write operations. Users can set preferences for data consistency using the CONSISTENCY command or via client drivers. Consistency refers to the number of replicas required to be involved. The parameter can be specified as a numerical value or with keywords like ALL, ANY, and QUORUM ($|replica nodes|/2 +1$).

These options provide application developers with flexibility to balance data availability, consistency, and application performance.

### Writing Data
When the coordinator node receives a write request, it determines which nodes in the cluster are responsible for replicating the data for the specified key in the write request (based on configurations: not necessarily all nodes) . The coordinator node then forwards the write request to these replica nodes. After that, it performs the following steps:
1. Commit Log: When data is written to a node, it's initially stored in the commit log, ensuring the recovery of the write in case of node failure.
2. Memtable: A copy of the data is then stored in the memtable, making it accessible for future reads or updates without accessing the disk.
3. Row Cache: If the row cache is active and contains a previous version of the row, it's invalidated and replaced with the new data.
4. SSTables: Cassandra monitors the size of the memtable. When it reaches a predefined threshold, the data is written to SSTables (on disk). When different columns of the same row are updated in separate write operations, then multiple SSTable files are created.
5. Hinted Handoff: If the coordinator detects that a node is unresponsive during a write, the failed write operation is recorded as a "hint" on the coordinator. When the Gossip protocol detects that the failed node is back online, the coordinator replays the hints for the failed operations and removes them from the coordinator. Hints expire after a configurable period to prevent buildup.

A coordinator node considers a write completed as soon as the data is recorded in the commit log, while other operations occur asynchronously.

### Reading Data
The read operation begins when a client connects to a coordinator node. When this happen the coordinator determinate which nodes need to be involved because they hold replicas of data and send the request. The following operations essentially take place on each replication node:
1. Initial Search: When querying a replica, the first step is to check the row cache. If the necessary data is present, it can be promptly retrieved.
2. Key Cache Check: Following that, Cassandra verifies the key cache (if active). If the partition key is stored in the key cache, Cassandra utilizes it to determine the data's location via an in-memory compressed offset map.
3. Memtable Examination: Subsequently, Cassandra inspects the memtable to ascertain the presence of the required data.
4. SSTable Retrieval: If the data isn't in the memtable, Cassandra fetches it from the SSTables on disk and combines it with any existing memtable data to construct an up-to-date view of the queried data.
5. Row Cache Update: Finally, if row caching is enabled, Cassandra caches the data for future use and promptly returns the results to the coordinator node.

### Scaling in Cassandra

Database administrators frequently encounter the need to scale their databases due to various factors, such as increasing data volumes, higher transaction loads, and changing data access patterns. As data grows, partitions on cluster nodes may fill up, necessitating additional capacity. Rising transaction volumes might require distributing transactions across more nodes to enhance performance. Moreover, launching services in new regions, like Asia, could require extending clusters to new data centers or cloud regions.

Cassandra simplifies scaling through straightforward procedures. Administrators can seamlessly add or remove nodes, migrate nodes to different availability zones, or adjust the size of data centers and cloud regions. Cassandra automatically accommodates these changes and rebalances tokens while continuing to handle requests.

This ability to scale in multiple dimensions offers numerous advantages. Unlike relational databases that require advance planning for future growth, Cassandra databases can start small and expand as needed. This flexibility allows technology investments to grow alongside the business.

## Data Modeling
### Data representations
In Apache Cassandra, data is organized using a column-based and row-based structure, which differs from relational database conventions. The primary data storage unit is the Column Family, which can be likened to a table in a relational database, but with the distinctive feature that each row can contain a variable number of columns. Each Column Family is a set of key-value pairs, akin to a table, with the key-value pairs conceptually corresponding to rows.

Rows, in turn, consist of a collection of columns, each identified by a name and a key used for data distribution and partitioning among cluster nodes. Each column, composing the rows, includes a name, a value, and a timestamp. The column name identifies the data, the value contains the actual information, while the timestamp is used to resolve conflicts during write operations.

SuperColumns are columns that contain other columns, allowing the representation of more complex relationships. However, in newer versions of Cassandra, the preference is for the use of collections such as maps, lists, and sets rather than SuperColumns. Data is partitioned based on the row key, allowing Cassandra to scale linearly by distributing rows among the various cluster nodes. In addition to the partition key, rows can have clustering columns that determine the order of rows within the same partition.

Data is stored in SSTables (Sorted String Tables), disk files containing data sorted by row key. Each SSTable is immutable once written. To facilitate quick data retrieval, each SSTable has a primary index mapping row keys to positions in the file. Cassandra also supports wide-row tables, where a single row key can have a very large number of columns, useful for representing data such as social media timelines or event logs.

To manage data relationships, Cassandra encourages data denormalization. Instead of using complex joins as in relational databases, data is designed to be read together in the same row or partition, thus reducing the need for costly joins. In fact, for Cassandra, it is simpler to handle a high number of write operations to ensure consistency among the different copies of data distributed across nodes. This is because such operations are nearly instantaneous, while the operation of retrieving data external to the main query table, which may not be present on the same node, takes much longer. This approach is particularly suitable for efficient read and write operations in a distributed environment like Cassandra.

Finally, data in Cassandra is serialized into an efficient binary format for writing to disk and transmitting between nodes. This flexible structure allows Cassandra to effectively handle large volumes of data and adapt to different access patterns, while maintaining high performance in reading and writing.
### Data storage
Table rows are allocated within the cluster based on a partition key, which is transformed through hash functions, also known as partition functions, into a 64-bit token identifying the Cassandra node where the data is stored.
Queries that require a particular record, having the partition key, are extremely fast because Cassandra can immediately determine the node containing the record by providing the partition key to the partition functions and querying the nodes whose token matches those generated. This approach allows Cassandra to handle multiple simultaneous queries, distributing queries and data among the cluster nodes, even with a large number of nodes.
Partition keys can consist of a single column, multiple columns, or clustering columns, which regulate how data is grouped and organized within each partition.

### Design choices
In the context of Cassandra, maximizing database efficiency requires an approach to data model design that is primarily focused on the anticipated queries. This involves designing the data structure to facilitate and optimize query operations, at the expense of potentially increased write operations, which are nevertheless nearly instantaneous. This typically entails creating tables that directly reflect the types of queries expected, with an even distribution of data across cluster nodes to prevent overloads and ensure effective scalability.

To successfully implement this approach, it is crucial to follow a series of detailed steps:

1. Define the application flow:
  - Analyze the operational flow of the application, identifying the main operations involving the database.
  - Understand the data paths and interactions among various application components.
  - Clearly specify the performance and scalability requirements of the application.
2. Design the necessary queries:
  - Carefully examine the read and write operations required by the application on the database.
  - Identify search criteria, aggregations, and filtering operations necessary to meet functional requirements.
  - Evaluate query optimization strategies to ensure fast response times and maximum performance.
3. Create table structure:
  - Design the database tables based on identified queries, considering data relationships and access frequency.
  - Equally distribute data across cluster nodes for uniform load distribution.
  - Evaluate data denormalization to improve query performance.
4. Properly define primary keys:
  - Carefully define primary keys for each table, considering how data is accessed and distributed among nodes.
  - Use composite keys if necessary to support complex queries.
5. Optimize data type usage:
  - Select the most suitable data types to represent information in the database, considering performance and query needs.
  - Use Cassandra's native data types to improve storage efficiency.
  - Consider using collection data types for efficient modeling of complex data.

By carefully following these steps, it is possible to design an efficient and performance-optimized data model within Cassandra, ensuring effective data management and an optimal user experience for the application.

### Cassandra Query Language (CQL)
Cassandra Query Language (CQL) is a query language used to communicate with Apache Cassandra, a highly scalable distributed database designed for managing large amounts of data across server clusters. CQL offers SQL-like syntax but is optimized for Cassandra's distributed characteristics and storage structure.

#### Creating a Keyspace

In Cassandra, a keyspace is the highest level of abstraction for organizing data. It is conceptually equivalent to a database in other database management systems. When creating a keyspace, the replication factor is specified, determining the number of copies of the data maintained within the cluster.

```cql
CREATE KEYSPACE keyspace_name
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': number};
```

The "SimpleStrategy" replication strategy is the most commonly used, replicating data across nodes linearly, suitable for development environments or clusters with a single data center. Alternatives include the "NetworkTopologyStrategy," optimized for multi-data center environments, where the replication_factor for each data center must be specified (`'data_center1': 'replication_number_dc1', 'data_center2': 'replication_number_dc2', ...`), the "LeveledCompactionStrategy" for efficient data compaction, and the "DateTieredCompactionStrategy," ideal for long-term storage of time-based data. The choice of strategy depends on specific performance, fault tolerance, and data compaction needs.

#### Selecting the Keyspace

After creating a keyspace, it must be selected before performing operations on tables within that keyspace.

```cql
USE keyspace_name;
```

#### Creating a Table

Tables in Cassandra are organized around a primary key, which can consist of one or more columns. It is essential to design primary keys correctly based on data access and performance requirements.

```cql
CREATE TABLE table_name (
    column_name1 data_type1,
    column_name2 data_type2,
    ...
    PRIMARY KEY (primary_column_name, ...)
) WITH CLUSTERING ORDER BY (clustering_column_name1, ...);
```

The main data types in Cassandra are:

- Primitive Data Types: `int` (32-bit integers), `bigint` (64-bit integers), `smallint` (16-bit integers), `tinyint` (8-bit integers), `varint` (variable-length integers), `float`, `double`, `boolean`, `ascii`, `text`, `blob` (arbitrary binary data).
- Collection Data Types:
  - `list`: Ordered list of values of the same type (e.g., `list<int>`).
  - `set`: Unordered set of unique values of the same type (e.g., `set<int>`).
  - `map`: Map of key-value pairs with arbitrary types (e.g., `map<int, text>`).
- Temporal Data Types:
  - `timestamp`: Timestamp, represented as the number of milliseconds since `1970-01-01`.
  - `date`: Date without timezone, in the format `YYYY-MM-DD`.
  - `time`: Time of day, represented as the number of milliseconds since midnight.
  - `datetime`: Combination of date and time.
- Special Data Types:
  - `uuid`: Universally unique identifier (UUID), used to generate unique primary keys.
  - `timeuuid`: Version 1 UUID, incorporating a timestamp, useful for ordering events based on generation time.

It is also possible to define new types using the following syntax:

```cql
CREATE TYPE type_name ( 
    type_field1 predefined_type1,
    type_field2 predefined_type2, 
    ...
);
```

When defining a collection data type, it is necessary to define the parameter indicating the type with `frozen<type_name>`. The `frozen` keyword forces Cassandra to consider the data as a single value. Example: `list<frozen<type_name>>`.

Primary keys can be defined using multiple columns. The `WITH CLUSTERING ORDER BY` clause (optional) in Cassandra is used to define the order of data within a partition based on clustering columns. This clause is typically used when defining the primary key of a table and specifying the order of data within a partition. An alternative to using `WITH CLUSTERING ORDER BY` is:

```cql
PRIMARY KEY ((primary_column_name, ...), clustering_column_name1, ...)
```

#### Inserting Data

To insert data into a table, values must be specified for all mandatory columns, according to the structure defined when the table was created.

```cql
INSERT INTO table_name (column_name1, column_name2, ...)
VALUES (value1, value2, ...);
```

#### Selecting Data

Selecting data is a fundamental operation in any query language. All columns can be selected, or a specific list of columns can be specified. Filter clauses can also be added to retrieve only data that meets certain conditions.

```cql
SELECT * FROM table_name WHERE conditions;
```

#### Updating Data

Data in a table can be updated using the `UPDATE` clause. Columns to be updated and the conditions that must be met for the update to occur must be specified. The current value is referred to using the column name.

```cql
UPDATE table_name
SET column_name = new_value
WHERE condition;
```

In the `WHERE` clause, the condition can be expressed using mathematical operators (`+`, `-`, `*`, `/`), comparison operators (`<`, `>`, `<=`, `>=`, `=`, `!=`, `<>`), specific functions like `MOD()`, and logical operators:

- `AND`: Returns results that satisfy both conditions.

  ```cql
  SELECT * FROM table_name WHERE condition1 AND condition2;
  ```

- `OR`: Returns results that satisfy at least one condition.

  ```cql
  SELECT * FROM table_name WHERE condition1 OR condition2;
  ```

- `NOT`: Returns results that do not meet the specified condition.

  ```cql
  SELECT * FROM table_name WHERE NOT condition;
  ```

- `IN`: Checks if a value is present in a specified list of values.

  ```cql
  SELECT * FROM table_name WHERE column IN (value1, value2, value3);
  ```

- `CONTAINS`: Checks if a set contains a specific value (used primarily with `set` or `map` columns).

  ```cql
  SELECT * FROM table_name WHERE column CONTAINS value;
  ```

- `CONTAINS KEY`: Checks if a map contains a specific key (used with `map` columns).

  ```cql
  SELECT * FROM table_name WHERE column CONTAINS KEY key;
  ```

- `CONTAINS ENTRY`: Checks if a map contains a specific key/value pair (used with `map` columns).

  ```cql
  SELECT * FROM table_name WHERE column CONTAINS ENTRY (key, value);
  ```

#### Deleting Data

The `DELETE` clause is used to remove data from a table. All data or a specific part of the data can be deleted based on conditions.

```cql
DELETE column_to_delete, ...
FROM table_name
WHERE condition;
```

The `WHERE` clause follows the same rules listed in the data update section.

#### Dropping a Table

To completely remove a table from the keyspace, the `DROP TABLE` command can be used.

```cql
DROP TABLE table_name;
```

#### Secondary Indexes

Secondary indexes are data structures that allow access to table data using columns other than the primary key. In Cassandra, the primary key is fundamental for data partitioning and physical organization on disk. However, there are situations where querying other columns not part of the primary key may be useful. Secondary indexes help in these cases.

When a secondary index is created on a column, Cassandra creates a separate data structure that maps the values of the indexed column to their respective row identifiers. This mapping allows for quickly retrieving all rows containing a specific value in the indexed column.

```cql
CREATE INDEX index_name ON table_name (column_name);
```

Secondary indexes in Cassandra allow for querying non-key columns without modifying the table structure, offering flexibility in query management. However, they can degrade performance on large tables or with high write throughput, as every modification must also update the index. Additionally, they may not be suitable for large clusters or queries returning many results, requiring querying every node. In conclusion, secondary indexes are useful for extending query capabilities but must be used cautiously to avoid performance and scalability issues.

#### Adding Columns

The structure of a table can be modified by adding new columns using the `ALTER TABLE ADD` command.

```cql
ALTER TABLE table_name ADD column_name data_type;
```

#### Materialized Views
Materialized Views are pre-calculated views of the data present in tables, providing faster access to query results. Automatically updated by the system based on changes to the underlying data, they ensure data consistency between views and base tables. These views optimize query performance, offering flexibility in database design and supporting complex queries. 

```cql
CREATE MATERIALIZED VIEW name_m_view AS
QUERY
PRIMARY KEY (name_key1, ...);
```

#### Batches
Batches in Cassandra allow executing multiple write or modification operations in a single atomic transaction, ensuring that either all operations are executed or none. They can involve one or more tables and can be configured as "unlogged" (write operations are not logged in the commit log, posing higher risks in case of data loss) or "logged" (all write operations are logged in the commit log before being applied to the actual data on disk), with significant differences in performance and data durability. Batches are useful for reducing the number of network calls and improving overall system performance, but it's important to balance data consistency and scalability needs when deciding to use them.