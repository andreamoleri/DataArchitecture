# Data Architecture

## Team Members
- 902011, Moleri Andrea, a.moleri@campus.unimib.it
- 865939, Armani Filippo, f.armani1@campus.unimib.it

## Abstract

This report presents a comparison between MongoDB and Cassandra, two of the most popular non-relational databases currently in use. The primary objective is to analyze the distinctive features of each system and highlight what sets them apart.

The discussion will begin with an analysis of the architecture, examining how each database is structured and organized internally. Next, the language used by each system to interact with the data will be explored, highlighting the main peculiarities and differences.

We will then consider the data model and design choices, comparing how MongoDB and Cassandra handle data modeling and the approaches they follow to optimize performance and scalability. Particular attention will be given to the management of queries and transactions, illustrating how each database addresses the complexity of read and write operations and how it ensures data integrity and consistency.

Finally, the topic of handling large volumes of data will be addressed, analyzing the capabilities of each system to scale horizontally and maintain efficient performance even with large datasets.

To concretely illustrate the practical differences and implications of design choices, query and transaction management, and the ability to handle large volumes of data, a specific data model designed by us will be used. This practical example will serve to highlight the strengths and potential limitations of MongoDB and Cassandra in real-world scenarios.

## Architecture

### MongoDB Architecture

### Cassandra Architecture

## Language

### MongoDB Syntax

### Cassandra Query Language (CQL)

Unlike MongoDB, Cassandra uses a communication language that is very similar to SQL, typical of relational systems. The most basic way to interact with Apache Cassandra is by using the CQL shell, cqlsh. With cqlsh, you can create keyspaces and tables, insert data into tables, query tables, and much more. This makes it easier for those familiar with SQL to transition to Cassandra and leverage its capabilities for managing large-scale, distributed datasets. 

In the following subparagraphs the main possible operations of the CQL language will be illustrated.

#### Creating a Keyspace

In Cassandra, a keyspace is the highest level of abstraction for organizing data. It is, basically, a top-level namespace. When creating a keyspace, two parameters must be specified:
- the replication factor: the number of copies of the data maintained within the cluster;
- the strategy of replication: how the data are replicated. The choice of strategy depends on specific performance, fault tolerance, and data compaction needs. The main options are:

  - "SimpleStrategy": used only for a single datacenter and one rack. It places the first replica on a node determined by the partitioner. Additional replicas are placed on the next nodes clockwise in the ring without considering topology.
  - "NetworkTopologyStrategy": used when you have multiple data centers available. This strategy specifies how many replicas you want in each data center.
    NetworkTopologyStrategy places replicas in the same data center by traveling the ring clockwise until it reaches the first node in another rack. NetworkTopologyStrategy tries to place replicas on distinct racks because nodes in the same rack often fail at the same time.

Less important alternatives include the "LeveledCompactionStrategy" for efficient data compaction, and the "DateTieredCompactionStrategy," ideal for long-term storage of time-based data.

```cql
CREATE  KEYSPACE [IF NOT EXISTS] keyspace_name 
   WITH REPLICATION = { 
      'class' : 'SimpleStrategy', 'replication_factor' : N } 
     | 'class' : 'NetworkTopologyStrategy', 
       'dc1_name' : N [, ...] 
   }
   [AND DURABLE_WRITES =  true|false] ;
```
An optional parameter is DURABLE_WRITES and if it is set to false it allows the commit log to be ignored when writing to the keyspace by disabling durable writes. The default value is true.

#### Selecting the Keyspace

After creating a keyspace, it must be selected before performing operations on tables within that keyspace.

```cql
USE keyspace_name;
```

#### Creating a Table

As mentioned in the previous chapter, data in Cassandra can be viewed as contents within tables composed of rows and columns. Through the CQL language, developers have a rather abstract understanding of the database architecture; they do not need to have full knowledge of nodes, racks, and datacenters, but only basic information.

Tables in Cassandra are organized around a primary key, which can consist of one or more columns, and clustering keys. It is essential to design primary keys correctly based on data access and performance requirements. The main (not complete) structure of the instruction to create a table involves:

```cql
CREATE TABLE [IF NOT EXISTS] [keyspace_name.]table_name ( 
   column_name column_type [, ...]
   PRIMARY KEY (column_name [, column_name ...])
[WITH CLUSTERING ORDER BY (clustering_column_name order])]
```
The PRIMARY KEY clause identifies the set of columns that form the primary key of the table. This clause must be unique within the table. It can consist of a single column or a combination of columns. If the primary key consists of multiple columns, the first specified column is the partition key, while the subsequent columns are clustering keys that determine the order of rows within each partition.

The WITH CLUSTERING ORDER BY clause specifies the order in which rows with the same partition key are stored and retrieved. You use the name of one of the clustering keys defined in the primary key, followed by ASC (ascending) or DESC (descending), to establish the clustering order of rows.

#### Data Types
The main data types in Cassandra are:

- Primitive Data Types:
    - `tinyint` (8-bit integers), `smallint` (16-bit integers), `int` (32-bit integers), `bigint` (64-bit integers),
    - `varint` (variable-length integers),
    - `float` (7 decimal digits of precision for a number), `double`, (15 decimal digits of precision for a number)
    - `boolean`, 
    - `text`, 
    - `blob` (intended for storing large data in binary format not directly interpretable by the database)
    - ...
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

#### Inserting Data

To insert data into a table, values must be specified for all mandatory columns, according to the structure defined when the table was created.

```cql
INSERT INTO table_name (column_name1, column_name2, ...)
VALUES (value1, value2, ...);
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

Alternatively, you can change a single variable by using an `UPDATE` statement and setting the value to a default one.

#### Adding Columns

The structure of a table can be modified by adding new columns using the `ALTER TABLE ADD` command.

```cql
ALTER TABLE table_name ADD column_name data_type;
```
Since Cassandra follows a dynamic schema and not all columns are required, it is not necessary to set any value of this column for rows previously added to the database.

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

#### Selecting Data

SELECT queries in Cassandra are used to retrieve data from tables within a cluster. Since Cassandra is a distributed NoSQL database, its SELECT queries have some differences compared to those in traditional SQL databases.

The basic syntax of a SELECT query in Cassandra is as follows:

```cql
SELECT column1, column2, ... 
FROM table_name 
WHERE conditions
[LIMIT number];
```

Where:
- **SELECT clause**: Specifies the columns to be returned.
- **FROM clause**: Specifies the table from which to retrieve the data.
- **WHERE clause**: Defines the conditions for filtering the data. The logic of using this clause has some particularities:
  - **Partition key**: Every query using a WHERE clause must include at least the partition key to ensure optimal performance. This allows Cassandra to quickly locate the node that holds the data using hash functions and hash tables.
  - **Clustering keys**: In addition to the partition key, clustering keys can also be used to allow Cassandra to retrieve data more quickly.
  - **Secondary indexes**: If you need to narrow the scope of a query based on a field that is not part of the primary key, you can create secondary indexes to allow WHERE clause queries on those columns. However, using secondary indexes can negatively impact performance, so they should be used with caution.
- **LIMIT clause**: Limits the number of rows returned. Using this clause is useful to prevent retrieving too much data, which could be inefficient and resource-intensive.

It is essential to design the data model with query requirements in mind to ensure high performance and efficient query execution.

#### Other operations
- **Materialized views**: they are pre-calculated views of the data present in tables, providing faster access to query results. Automatically updated by the system based on changes to the underlying data, they ensure data consistency between views and base tables. These views optimize query performance, offering flexibility in database design and supporting complex queries.
```cql
CREATE MATERIALIZED VIEW name_m_view AS
QUERY
PRIMARY KEY (name_key1, ...);
```
- **Batch statements**: they in Cassandra allow executing multiple write or modification operations in a single atomic transaction, ensuring that either all operations are executed or none. They can involve one or more tables and can be configured as "unlogged" (write operations are not logged in the commit log, posing higher risks in case of data loss) or "logged" (all write operations are logged in the commit log before being applied to the actual data on disk), with significant differences in performance and data durability. Batches are useful for reducing the number of network calls and improving overall system performance, but it's important to balance data consistency and scalability needs when deciding to use them.
```cql
BEGIN [UNLOGGED | LOGGED] BATCH
[USING TIMESTAMP [epoch_microseconds]]
  dml_statement [USING TIMESTAMP [epoch_microseconds]];
  [dml_statement; ...]
APPLY BATCH;
```
- **LWT**: they can be used for operations that require strong consistency. You perform an LTW when using the IF command for conditionals and CAS (Compare and Set). These commands are primarily added to INSERT and SELECT operations. LWTs guarantee serializable isolation but can have a performance cost.
- **Configurable consistency levels**: determine the number of replicas that must agree on the response before an operation is considered complete. Common consistency levels include ONE (completed when at least one replica responds), QUORUM (completed when a majority of replicas respond), ALL, EACH_QUORUM, etc. 

Especially the last 3 structures/operations will be explored in depth in the specific chapter.

## Data Model

### MongoDB Data Model

### Cassandra Data Model

## Queries and Transactions

### Queries and Transactions in Mongo

### Queries and Transactions in Cassandra

## Management of Large Volumes

## Conclusions