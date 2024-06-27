# Air Travel Reservation Modeling Using MongoDB and Cassandra

## Team Members
- 902011, Moleri Andrea, a.moleri@campus.unimib.it
- 865939, Armani Filippo, f.armani1@campus.unimib.it

## Introduction
In the report you are about to read, we will explore NoSQL solutions and how they can be used to model real-world
application problems. We will focus on MongoDB and Cassandra, two widely-used non-relational databases, and 
examine their features and differences. By doing so, we will demonstrate how each database can be utilized to 
model an airline reservation system.

The study will cover several important aspects, such as managing concurrent entries, designing various types of 
transactions, handling large volume read operations, and dealing with system malfunctions. Key database structures
will be highlighted to give a clear understanding of their functionalities.

Additionally, we will analyze the entire data management process, from modeling to query execution. This comprehensive 
approach will provide a detailed look at how MongoDB and Cassandra perform in real-world scenarios, offering insights 
into their strengths and limitations in managing complex data systems.

## Architecture
### MongoDB Architecture

#### Introducing Atlas, MongoDB's DBaaS

MongoDB is a NoSQL database management system renowned for its flexibility and scalability,
including the ability to manage huge amounts of data. A crucial aspect of MongoDB's ecosystem is Atlas,
MongoDB's Multi-Cloud Developer Data Platform. Understanding how MongoDB Atlas stores and hosts data through
Atlas Clusters is fundamental to using its capabilities effectively.

Atlas serves as a comprehensive developer data platform, with its core offering being a Database as a Service (DBaaS).
This implies that utilizing Atlas alleviates the burden of manual MongoDB management,
as Atlas takes care of all lifecycle details. Deployments on Atlas benefit from built-in replication,
referred to as Replica Sets by MongoDB, ensuring data redundancy and availability even in the event of server failures.

Atlas offers two primary types of Database Deployments: Serverless and Clusters.
Serverless instances scale on-demand, charging users only for the resources utilized, making them ideal
for applications with highly variable workloads. On the other hand, Clusters consist of multiple MongoDB servers
working together. Shared Clusters, suitable for initial exploration, include a free tier, while Dedicated Clusters
offer enhanced resources and customization options tailored to specific project requirements, making them optimal
for production workloads. Atlas allows deployment across major cloud providers such as AWS, Azure, and Google Cloud.

Global deployment across multiple regions and clouds is facilitated by Atlas, ensuring flexibility to adapt
to changing needs. Tier adjustments can be made seamlessly as products scale, without causing any downtime.
Operational insights, backups with Point-In-Time Restoration, and features like built-in data tiering with online
archiving contribute to Atlas's appeal across various stages of the development cycle. MongoDB serves as
the underlying data engine powering Atlas's data management and analysis capabilities. Contrary to a common
misconception, MongoDB is not simply the locally deployed version of Atlas.

#### Creating and Deploying an Atlas Cluster

To effectively utilize MongoDB Atlas, the initial step involves creating an account. Upon account creation,
selecting the Database M0 free sandbox tier, which provides 512MB of Storage, Shared RAM, and vCPU,
initiates the process. Subsequently, a Dashboard is presented, housing various functionalities.
Within this interface, the Organizations feature facilitates the grouping and management of users and projects.
Projects, in turn, enable the organization of resources such as Database Clustering, allowing for the creation
of distinct projects tailored for Development, Testing, and Production Environments. Notably, by default,
databases are generated devoid of users and external IP addresses with access privileges. Consequently, creating
an Admin user and an Access Point for the designated IP Address becomes important. Navigating to the
Security Quickstart section enables the authentication of connections through username and password protocols,
thereby establishing a new administrative user. Given MongoDB's default restriction on IP access to all addresses
except those designated by Atlas, configuring access from the local machine necessitates inclusion of the local IP
address within the Access List. This can be achieved by selecting "Add My Current IP Address" and, if required,
additional addresses can be incorporated via the menu before finalizing with the "Finish and Close" button.

#### MongoDB and the Document Model

After introducing Atlas, it is now time to dive into the Document Model of MongoDB. In essence,
we aim to comprehend how MongoDB stores data, how it is utilized, and its behavior in relation to Atlas.
MongoDB serves as a General Purpose Document Database, structuring data in the form of Documents, akin to JSON Objects.
This is very different from Relational Databases, which organize data into rows and columns within tables.
Documents offer a flexible and developer-friendly approach to working with data.
Consider the following code snippet as a simple example of a MongoDB Document:

```json
{
    "_id": 1,
    "name": {
        "first": "Michael",
        "last": "Jackson"
    },
    "title": "Thriller",
    "interests": ["singing", "dancing"]
}
```

Documents correspond to Objects in code, rendering them intuitive to manage.
This simplifies the planning of how Application Data correlates with data stored in the Database.
Furthermore, Documents can be utilized in a highly developer-friendly manner to model data of any shape or structure:
Key-Value Pairs, Text, Geospatial Data, Time-Series, Graph Data, and much more can be modeled using documents.
The flexibility of documents allows us to employ a format for modeling and querying data for any application.
MongoDB provides drivers in all major programming languages, facilitating the connection of a MongoDB Database 
to our application, regardless of the programming language used. 

#### Key Terminology
_In the realm of MongoDB, several key terms are essential to comprehend its architecture and functionality._

1. **Document:** the fundamental unit of data within MongoDB is referred to as a "Document."
   Each document encapsulates a set of key-value pairs representing a single entity.
   Unlike traditional relational databases, MongoDB's document model allows for flexible and dynamic schemas,
   enabling developers to store heterogeneous data structures within a collection.

2. **Collection:** a "Collection" in MongoDB is a grouping of documents.
   Documents within a collection typically share a similar structure, although MongoDB's flexible schema model permits
   variations in document structure within the same collection. Collections serve as logical containers for organizing
   related documents and are analogous to tables in relational databases.

3. **Database:** a "Database" in MongoDB serves as a container for collections.
   It provides a logical separation and management unit for organizing and accessing data.
   Multiple collections, each containing distinct sets of documents, can reside within a single database.
   MongoDB's architecture allows for the creation of multiple databases within a MongoDB deployment,
   facilitating data segregation and management at scale.

#### The Document Model

As anticipated, MongoDB stores data in structures known as documents which resemble JSON objects.
Below is an example of a document used to store product data in a store. We can observe that the document has
five fields, including a "colors" field containing an array of strings and an "available" field holding a boolean value.

```json
{
	"_id": 1,
	"name": "iPhone 14 Pro Max",
	"colors" : ["space black", "silver", "gold", "deep purple"],
	"price" : 1500,
	"available" : true
}
```

While documents are presented in JSON format, they are stored in the database in a format called BSON, which stands for
Binary JSON, an extension of JSON providing additional features that MongoDB can leverage.
BSON also adds support for additional data types that would not otherwise be present in standard JSON.
Thanks to this choice, the database can support a vast range of data types, including all JSON data types
(strings, objects, arrays, booleans, null), as well as dates, numbers, Object IDs, and more.
Particularly, ObjectID is a special data type used in MongoDB to create unique identifiers.
In the database, each document requires a field `"_id"` serving as the primary key.
If a document does not include this field, MongoDB will automatically add it, generating a specific ObjectID value
for the document in question. By default, MongoDB supports a flexible schema model and polymorphic data.
This enables us to store documents with different structures in the same collection.
Documents can contain various fields, and the fields can hold different data types from one document to another.
This is a significant distinction from relational databases, where declaring a table schema is necessary before
inserting data. MongoDB's flexible schema allows us to iterate rapidly and evolve as our requirements change.
Here's a practical example of this flexible schema. Suppose we have an online furniture store with a catalog of items.
When we start developing our application, we decide to include an `"_id"`, a `"name"`, and a `"price"` for each item.

```json
{
	"_id": ObjectId("abcdef"),
	"name": "iPhone",
	"price": 1500.00
}
```

In the future, we might decide to add another field, such as a `"description"` field. With a relational database,
we would encounter a complicated chain of dependencies to modify, risking downtime and significant time losses,
even for a simple change like this. Instead, to achieve the same with MongoDB, we simply update the classes to
include the new fields, and we can start inserting new documents with the new schema. This is facilitated by MongoDB's
flexible schema model, which means that documents in the same collection are not required to share a common
structure of fields and value types by default.

```json
{
	"_id": ObjectId("abcdef"),
	"name": "iPhone",
	"price": 1500.00,
	"description": "the all new iPhone!"
}
```

Should we desire more control over the structure and contents of the database, we can add optional
Schema Validation Rule to impose constraints on the structure of documents in the collection.
Nonetheless, the basic syntax of the Document Model remains as indicated in the following code:

```json
{
	"key": "value",
	"key": "value",
	"key": "value"
}
```

#### Clusters in MongoDB

MongoDB can also be configured as a cluster, a powerful setup that involves multiple servers or nodes working together. 
A cluster ensures data availability and reliability by distributing data and tasks across multiple nodes. 
This architecture provides several key benefits:

1. **Scalability**: clusters can handle increasing amounts of data by adding more nodes, allowing horizontal scaling.
2. **High Availability**: by distributing data across multiple nodes, clusters ensure that the system remains operational even if one or more nodes fail.
3. **Load Balancing**: tasks and queries are distributed across the nodes, balancing the load and improving performance.

In MongoDB Atlas, clusters can be easily set up and managed through the Atlas user interface. Clusters can be deployed
across multiple cloud providers and regions, allowing for global distribution of data and applications.

#### Sharding in MongoDB

Sharding is a technique used to distribute data across multiple machines. In MongoDB, sharding involves partitioning 
data into smaller, more manageable pieces called shards. Each shard is a subset of the data and can reside on a 
separate node within the cluster. This method enables horizontal scaling, making it possible to handle very large 
datasets and high-throughput operations efficiently. Key concepts in MongoDB sharding include:

1. **Sharded Clusters**: composed of multiple shards, each storing a portion of the data. A sharded cluster includes three main components: shards, query routers, and config servers.
    - **Shards**: store the actual data. Each shard can be a replica set to provide high availability and data redundancy.
    - **Query Routers (mongos)**: route client requests to the appropriate shard(s). They handle the distribution of queries and aggregation of results.
    - **Config Servers**: maintain metadata and configuration settings for the cluster. They store information about the sharding structure and the location of data.

2. **Shard Keys**: a shard key is a field or a combination of fields that determines how data is distributed across shards. Choosing an appropriate shard key is crucial for balanced data distribution and query performance.

In MongoDB Atlas, sharding can be enabled and configured through the Atlas console, 
providing an easy-to-use interface for managing sharded clusters.

#### Replica Sets in MongoDB

A replica set in MongoDB is a group of MongoDB instances that maintain the same dataset. 
Replica sets provide redundancy and high availability, ensuring that data is replicated across multiple nodes. 
Key features and benefits of replica sets include:

1. **Data Redundancy**: data is replicated to multiple nodes, protecting against data loss in case of hardware failure or other issues.
2. **Automatic Failover**: if the primary node fails, an eligible secondary node is automatically elected as the new primary, ensuring continuous availability.
3. **Read Scalability**: read operations can be distributed across multiple nodes, improving read performance and balancing the load.

A typical replica set consists of:
- **Primary Node**: handles all write operations and coordinates replication to secondary nodes.
- **Secondary Nodes**: maintain copies of the data from the primary node. They can be configured to handle read operations, providing load balancing and improved read performance.
- **Arbiter Nodes**: participate in elections but do not store data. They are used to ensure a quorum in elections when there are an even number of data-bearing nodes.

In MongoDB Atlas, deploying a replica set is straightforward, and the platform provides tools for managing and monitoring 
the health of the replica set. Automatic backups, point-in-time recovery, and monitoring tools are available to ensure the 
reliability and performance of the replica set. By leveraging clusters, sharding, and replica sets, MongoDB Atlas offers 
a robust and scalable infrastructure that can handle the demands of modern applications, ensuring data availability, 
reliability, and performance. In summary, MongoDB's architecture is designed to be flexible, scalable and reliable, 
allowing to manage a wide range of applications and workloads from the simplest to the most complex.

### Architettura Cassandra
_PLACEHOLDER PER FILIPPO_










### MongoDB Data Modeling

Data modeling is a fundamental aspect of database design, as it serves the purpose of the structuring of data storage
and the delineation of relationships among various entities within the data. It serves as a blueprint for organizing
information within a database. We refer to the organization of data inside a database as a 'Schema'. When data modeling
with MongoDB, it is advisable to conceptualize the application itself: its functionalities, the data it will handle,
user data access patterns, and the data elements critical to the project's objectives. Addressing these questions aids
in understanding the form of the data, its relationships, and the necessary tools for implementation. A robust data
model offers several benefits, facilitating data management, enhancing query efficiency, optimizing resource usage,
and reducing operational costs. As a guiding principle within MongoDB, the mantra _"data that is accessed together, should be stored together"_
underscores the great importance of structuring data in a manner conducive to operational efficiency.
MongoDB employs a flexible document data model, in which collections do not impose any default document structure.
As a consequence, documents may exhibit diverse structures, thanks to a concept called polymorphism, as exemplified below:

**Document I**
```json
{
	"name": "Andrea"
	"major": "CS"
	"course": "Architetture Dati"
	"amount": 1000
	"tuition_payment": True
}
```

**Document II**
```json
{
	"name": "Filippo"
	"major": "CS"
	"course": "Qualità del Software"
	"year": 2024
}
```

It is important to clarify that while MongoDB's Document Model is flexible, it is not entirely schema-less but rather
schema-flexible. This flexibility extends to employing Schema Validation and accommodating diverse data types within
MongoDB. Additionally, MongoDB supports nested or embedded documents, enabling the construction of complex data relationships.
Normalization of data is achievable through database references. The complication lies in aligning data modeling
decisions with application requirements, contrasting with the traditional approach of modeling data in relational databases.

Unlike the standard procedure of gathering data requirements, modeling data, and then handing over the data to developers,
MongoDB's methodology commences with understanding application requirements, user interactions, and subsequently
tailoring data modeling accordingly. MongoDB's versatility enables various data storage approaches, including normalization,
embedding related data for cohesive access, or employing hybrid methods as dictated by application needs. The final goal
of data modeling is to optimize storage, querying, and resource utilization, enhancing application performance and reducing
database costs as a consequence. Once the foundational data modeling framework is established, attention can be directed
towards modeling data relationships. A well-crafted data model simplifies data management, enhances query efficiency,
minimizes resource consumption, and mitigates database operational costs.

#### Types of Data Relationships

When discussing data relationships, it is crucial to delineate between various types: One-To-One, One-To-Many,
and Many-To-Many. Additionally, we will dive into the two primary methods for modeling these relationships:
Embedding and Referencing. As we already said, it is important to structure our data to align with the querying and
updating patterns of our application. In that regard, understanding common relationship types in databases is extremely important.

##### One-To-One Relationship
The One-To-One relationship is characterized by one Data Entity in a Set being connected to precisely one Data Entity in
another set. In traditional relational databases, this relationship might be implemented using a JOIN operation.
In MongoDB, a One-To-One Relationship can be represented succinctly within a single document, as exemplified below.
In the example, a document representing a film encompasses not only the title but also the director's information.

```json
{
	"_id": ObjectId("573a1390f29313caabcd413b"),
	"title": "Her",
	"director": "Spike Jonze",
	"runtime": 126
}
```

##### One-To-Many Relationship
The One-To-Many relationship is characterized by one Data Entity in a set being associated with multiple Data Entities
in another set. For instance, a film may feature several cast members. MongoDB facilitates the representation of this
relationship within a single document using features like Nested Arrays, which are advantageous for modeling One-To-Many
Relationships. The "cast" field in the code shown below exemplifies such a structure.

```json
{
	"_id": ObjectId("573a1390f29313caabcd413b"),
	"title": "Her",
	"director": "Spike Jonze",
	"runtime": 126,
	"cast": [
		{"actor": "Joaquin Phoenix", "character": "Theodore"},
		{"actor": "Scarlett Johansson", "character": "Samantha"},
		{"actor": "Rooney Mara", "character": "Catherine"}
	]
}
```

##### Many-To-Many Relationship
The Many-To-Many relationship represents a scenario where any number of Data Entities in one set are connected to any
number of Data Entities in another set. As previously mentioned, the primary methods for modeling relationships in
MongoDB are Embedding and Referencing. Embedding involves incorporating related data within the document, while
Referencing entails referring to documents in another collection within the document. The following examples illustrate
Embedding and Referencing respectively. In the Embedding example, Actor documents are embedded within Movie documents
using Nested Arrays. On the other hand, in the Referencing example, Filming Locations are referenced inside the document
via their respective ObjectIDs.

**Embedding**
```json
{
	"_id": ObjectId("573a1390f29313caabcd413b"),
	"title": "Her",
	"director": "Spike Jonze",
	"runtime": 126,
	"cast": [
		{"actor": "Joaquin Phoenix", "character": "Theodore"},
		{"actor": "Scarlett Johansson", "character": "Samantha"},
		{"actor": "Rooney Mara", "character": "Catherine"}
	]
}
```

**Referencing**
```json
{
	"_id": ObjectId("573a1390f29313caabcd413b"),
	"title": "Her",
	"director": "Spike Jonze",
	"runtime": 126,
	"filming_locations": [
		ObjectID("654a1420f29313fggbcd718"),
		ObjectID("654a1420f29313fggbcd719")
	]
}
```

#### Modeling Data Relationships

In this section, we provide an example of data modeling based on a practical scenario illustrated in the code below.
When a student enrolls at a university, they fill out a form on a web application that creates their profile,
which is then stored in a database. Upon examining the following code, there emerges a need to gather more information
about the student, such as the courses taken and their grades. Furthermore, certain aspects of the code are not optimally structured.

```json
{
	"student": "Andrea Moleri",
	"student_id": "902011",
	"age": "23",
	"home_phone": "2125550000",
	"cell_phone": "2125550001",
	"email": "andreamoleri@gmail.com",
	"grade_level": "master's degree",
	"street": "Viale della Vittoria 6",
	"city": "Milano",
	"state": "MI",
	"zip": "12345",
	"emergency_contact_name": "Filippo Armani",
	"emergency_contact_number": "212550002",
	"emergency_contact_relation": "Friend"
}
```
An initial observation reveals the presence of three phone numbers at different locations within the code,
resulting in not-so-clean code. To address this issue, reorganization is proposed instead of treating them as
separate elements indicating a One-To-One Relationship. This reorganization involves transforming it into a One-To-Many
Relationship through the use of a Nested Array.

```json
{
	"student": "Andrea Moleri",
	"student_id": "902011",
	"age": "23",
	"email": "andreamoleri@gmail.com",
	"grade_level": "master's degree",
	"street": "Viale della Vittoria 6",
	"city": "Milano",
	"state": "MI",
	"zip": "12345",
	"emergency_contact_name": "Filippo Armani",
	"emergency_contact_relation": "Friend",
	"contact_number": [
		{"number": "2125550000", "type": "home"},
		{"number": "2125550001", "type": "cell"},
		{"number": "212550002", "type": "emergency"}
	]
}
```

In the scenario where additional data regarding the student is available, such as considering the courses taken along
with their respective grades, a different data modeling approach may be considered. Here, references to Course ID and
Course Name are added within the Student Document.

```json
{
	"student": "Andrea Moleri",
	"student_id": "902011",
	"age": "23",
	"contact_number": [
		{"number": "2125550000", "type": "home"},
		{"number": "2125550001", "type": "cell"},
		{"number": "212550002", "type": "emergency"}
	],
	"email": "andreamoleri@gmail.com",
	"grade_level": "master's degree",
	"gpa": "4.0",
	"street": "Viale della Vittoria 6",
	"city": "Milano",
	"state": "MI",
	"zip": "12345",
	"emergency_contact_name": "Filippo",
	"emergency_contact_relation": "Friend",
	"courses": [
		{"course_id": "2324-1-F1801Q159", "course_name": "Architetture Dati"},
		{"course_id": "2324-1-F1801Q115", "course_name": "Qualità del Software"}
	]
}
```

Additionally, a dedicated Collection for Courses can be established, wherein the courses inserted within the Student
Document are represented in a separate document as demonstrated below. In the provided data modeling scenario,
the Student Document represents individual student profiles, containing comprehensive information such as student details,
contact information, emergency contacts, enrolled courses, and other relevant data. Within the Student Document,
a nested array named "courses" is included, which holds references to the courses taken by the student.
Each course reference consists of a CourseID and Course Name.

On the other hand, the separate Course Collection stores detailed information about all available courses offered
by the university. Each document within the Course Collection represents a distinct course, featuring attributes like
Course ID, Course Name, Professor, and Offered Term(s). The interconnection between these two pieces of code lies in
the referencing mechanism established within the Student Document. When a student enrolls in a course, instead of
duplicating course information within each student profile, a reference to the corresponding course is included within
the "courses" array. This reference includes the Course ID, allowing easy retrieval of detailed course information from
the Course Collection when and if needed.

```json
"courses": [
	{
		"course_id": "2324-1-F1801Q159",
		"course_name": "Architetture Dati",
		"professors": "Andrea Maurino, Marco Cremaschi, Fabio d'Adda",
		"offered": "Spring, Summer, Fall, Winter"
	},
	
	{
		"course_id": "2324-1-F1801Q115",
		"course_name": "Qualità del Software",
		"professors": "Giovanni Denaro, Luca Guglielmo, Elson Kurian",
		"offered": "Fall, Spring"
	}
]
```
#### Embedding Data in Documents

In the realm of database management, understanding how to model data using embedding is really important.
Embedding is frequently employed in scenarios involving One-To-Many or Many-To-Many Relationships within stored data.
MongoDB's documentation advocates for embedding to streamline queries and enhance performance. Embedded Documents are
also known as Nested Documents, that is documents that encapsulate another document within them.

To better understand this concept, let us consider the following document, which contains two embedded subdocuments
for both name and address. The client possesses only one name, embedded as First and Last Name. Regarding addresses,
the client has three addresses, constituting a One-To-Many Relationship. Documents structured in this manner facilitate
the retrieval of complete address information for a client, aligning with the principle "data that is accessed together
should be stored together." Embedding enables the consolidation of various related pieces of information into a single document,
potentially simplifying and reducing the number of required queries. One-To-One Relationships and One-To-Many Relationships
are the relationships that are most commonly utilizing embedding.

```json
{
    "name": {"firstName": "Walt", "lastName": "Disney"},
    "job": "entrepreneur",
    "address": {
        "europeanAddress": {
            "street": "Bd de Parc, Coupvray",
            "city": "Coupvray (FR)",
            "zipcode": "77700"
        },
        "americanAddress": {
            "street": "Epcot Center Dr, Lake Buena Vista",
            "city": "Florida (USA)",
            "zipcode": "32830"
        },
        "asianAddress": {
            "name": "Tokyo DisneySea",
            "street": "1-13 Maihama, Urayasu",
            "city": "Chiba (JP)",
            "zipcode": "279-0031",
            "country": "Japan"
        }
    }
}
```

Incorporating embedding mitigates the necessity of application joins, thereby minimizing queries and enhancing read
operation performance. Furthermore, it enables developers to update related data in a single write operation.
However, employing Embedded Data Models entails certain risks as swell. Primarily, embedding data within a single
document can lead to excessively large documents, potentially causing latency issues. Large documents must be entirely
read into memory, which may result in reduced performance for end-users. Additionally, during the embedding process,
there's a risk of inadvertently structuring documents in a manner where data is continually added without restraint,
leading to Unbounded Documents. These documents pose a risk of exceeding the maximum BSON document threshold of 16MB.
Both Large Documents and Unbounded Documents are recognized as [Schema Anti-Patterns](https://www.mongodb.com/developer/products/mongodb/schema-design-anti-pattern-summary/), and as such, they should be avoided.

#### Referencing Data in Documents

There may be scenarios where it becomes necessary to store related information in separate documents or even
in distinct collections. When there is a need to store data across different collections while ensuring clarity
regarding their relational nature, References come into play. Working with references is simple, and it is only
a matter of saving the identifier field of one document within another document to establish a link between the two.
The utilization of references is often referred to as Linking or Data Normalization. Let's revisit the example previously
discussed, wherein we have a university student who has taken various university courses. In the following code snippet,
the `course_id` serves as our reference. Referencing enables us to circumvent data duplication, leading to smaller
documents. However, this approach may necessitate querying multiple documents, potentially incurring higher read times and costs.

```json
{
   "student": "Andrea Moleri",
   "student_id": "902011",
   "age": "23",
   "contact_number": [
      {"number": "2125550000", "type": "home"},
      {"number": "2125550001", "type": "cell"},
      {"number": "212550002", "type": "emergency"}
   ],
   "email": "andreamoleri@gmail.com",
   "grade_level": "master's degree",
   "gpa": "4.0",
   "street": "Viale della Vittoria 6",
   "city": "Milano",
   "state": "MI",
   "zip": "12345",
   "emergency_contact_name": "Filippo",
   "emergency_contact_relation": "Friend",
   "courses": [
      {"course_id": "2324-1-F1801Q159", "course_name": "Architetture Dati"},
      {"course_id": "2324-1-F1801Q115", "course_name": "Qualità del Software"}
   ]
}
```

To summarize the advantages and disadvantages, we employ embedding when we want to use a single query to retrieve
data and when performing individual operations for data updates or deletions. However, this approach carries the risk
of data duplication and the creation of substantial documents. Regarding referencing, this technique enables us to avoid
duplicates, resulting in smaller and more manageable documents. However, this technique introduces
the need of data joins from disparate documents. Another realistic example illustrates the utilization of referencing,
where `user_id` in the first collection acts as a reference to a document in the `users` collection, thereby 
establishing a linkage between the two documents through referencing.

**Collection I**
```json
{
    "author": "Aileen Long",
    "title": "Learn The Basics of MongoDB in 90 Minutes",
    "published_date": ISODate("2024-05-18T14:10:30Z"),
    "tags": ["mongodb", "basics", "database", "nosql"],
    "comments": [
        {
            "comment_id": "LM001",
            "user_id": "AL001",
            "comment_date": ISODate("2024-05-19T14:22:00Z"),
            "comment": "Great read!"
        },
        {
            "comment_id": "LM002",
            "user_id": "AL002",
            "comment_date": ISODate("2024-06-01T08:00:00Z"),
            "comment": "So easy to understand - thank you for posting this!"
        }
    ]
}
```

**Collection II**
```json
...
{
    "id": "AL001",
    "name": "Andrea Moleri"
},
{
    "id": "AL002",
    "name": "Filippo Armani"
},
{
    "id": "AL003",
    "name": "Claudio Finazzi"
},
...  
```


































### Cassandra Data Modeling
_PLACEHOLDER PER FILIPPO_






## Sintassi Linguaggi
### Sintassi di MongoDB

Principali comandi tipo Inserimento documenti, query, delete (solo linguaggio di Mongo, niente parti di Java)

### Sintassi di Cassandra
_PLACEHOLDER PER FILIPPO_

## Modello dati

Presentazione del nostro caso di studio (aereoporto), citiamo la fonte dei dati, come li abbiamo strutturati e il processo di generazione di quelli mancanti
Anche le assunzioni varie (tipo no login)

### Modello dati MongoDB

Come a partire dal caso di studio e dei dati a tua disposizione hai creato il modello dati

### Modello dati Cassandra
_PLACEHOLDER PER FILIPPO_

## Transazioni
Spiegazione su cosa è una transazione e perchè è importante per il nostro caso di studio (semplicemnte non posso avere due persone sullo stesso posto)

### Transazioni in Mongo
spieghi come hai modellato le transazioni e come lato backend vengono gestite (quindi come le gestisce il db)

### Transazioni in Cassandra
_PLACEHOLDER PER FILIPPO_

## Gestione su larghi volumi

Devo ancora capire bene cazzo dobbiamo fare qua ma poi anch'esso sarà diviso in Mongo e Cassandra

## Conclusioni

Tiriamo un po' le somme: quali sono le principalissime differenze e quando è meglio usare uno al posto di un altro

Riassumiamo tipo questi 3 siti:
https://aws.amazon.com/it/compare/the-difference-between-cassandra-and-mongodb/#:~:text=Riepilogo%20delle%20differenze%3A%20Cassandra%20e%20MongoDB,-Apache%20Cassandra&text=Documenti%20JSON%20serializzati.&text=Cassandra%20supporta%20indici%20secondari%20e,offre%20diverse%20opzioni%20di%20indicizzazione.
https://www.mongodb.com/resources/compare/cassandra-vs-mongodb
https://www.ionos.it/digitalguide/server/know-how/mongodb-e-cassandra/

questa parte è giusto per rispondere alla domanda "e quindi quando usate uno al posto dell'altro?"

## Grazie, Arrivederci e dacci 30L
