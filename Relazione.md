# Data Architecture

## Team Members
- 902011, Moleri Andrea, a.moleri@campus.unimib.it
- 865939, Armani Filippo, f.armani1@campus.unimib.it

## Introduction


## Architecture

### MongoDB Architecture
I'm not sure if there's already something in the README file.

This is what came out asking ChatGPT about MongoDB architecture, obviously to be expanded and detailed:

```
MongoDB is a NoSQL database that utilizes a distributed and scalable architecture, designed to handle large volumes of data. Here are the main components of a MongoDB database architecture:

1. **Database**: MongoDB organizes data into databases, which can contain multiple collections of documents. A database is a logical storage unit with its own data sets.

2. **Collection**: A collection in MongoDB is a group of documents. Conceptually, it is equivalent to a table in a relational database but does not require a rigid schema. Collections can contain documents with different structures.

3. **Document**: A document is the basic unit of data in MongoDB, similar to a record or row in a relational database. Documents are represented in BSON (Binary JSON), which is an extended binary format of JSON.

4. **Cluster**: MongoDB can be configured as a cluster, which is a set of nodes working together to manage data and ensure the system's availability and reliability. MongoDB clusters can include different types of nodes:

    - **Data Nodes**: Nodes that store the actual data.
    - **Query Nodes**: Nodes that accept queries from users and forward them to data nodes.
    - **Config Nodes**: Nodes that maintain the cluster's configuration and metadata.
    - **Router Nodes**: Nodes that direct client requests to other nodes in the cluster.

5. **Sharding**: MongoDB supports sharding, a technique for distributing data across multiple machines. It involves partitioning data and distributing it among multiple nodes in a cluster (called shards). Sharding allows MongoDB to scale horizontally to handle very large data volumes and intense workloads.

6. **Replica set**: A replica set is a group of MongoDB nodes that contain the same set of data. It provides redundancy and high availability, allowing the database to survive node failures or service interruptions.

In summary, MongoDB's architecture is designed to be flexible, scalable, and reliable, enabling it to handle a wide range of applications and workloads, from simple to complex.
```

### Cassandra Architecture
I'll take care of it.

## Language
### MongoDB Syntax

Main commands like Insert documents, query, delete (only Mongo's language, no Java parts).

### Cassandra Query Language (CQL)

I'll take care of it.

## Data Model

Presentation of our case study (airport), citing the data source, how we structured it, and the process of generating the missing data.
Also, various assumptions (like no login).

### MongoDB Data Model

How you created the data model from the case study and the available data.

### Cassandra Data Model

I'll take care of it.

## Queries and Transactions
Explanation of what a transaction is and why it is important for our case study (simply, I can't have two people in the same seat).

### Queries and Transactions in Mongo
Explain how you modeled the transactions and how they are managed on the backend side (i.e., how the DB handles them).

### Queries and Transactions in Cassandra

I'll take care of it.

## Management of Large Volumes

I still need to understand what exactly we need to do here, but it will also be divided into Mongo and Cassandra.

## Conclusions

Let's summarize: what are the main differences and when it is better to use one over the other.

Summarize like these 3 sites:
https://aws.amazon.com/it/compare/the-difference-between-cassandra-and-mongodb/#:~:text=Riepilogo%20delle%20differenze%3A%20Cassandra%20e%20MongoDB,-Apache%20Cassandra&text=Documenti%20JSON%20serializzati.&text=Cassandra%20supporta%20indici%20secondari%20e,offre%20diverse%20opzioni%20di%20indicizzazione.
https://www.mongodb.com/resources/compare/cassandra-vs-mongodb
https://www.ionos.it/digitalguide/server/know-how/mongodb-e-cassandra/

This part is just to answer the question "so when do you use one over the other?"

## Thank You, Goodbye, and Give Us 30L