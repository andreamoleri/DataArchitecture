# Data Architecture

## Team Members
- 902011, Moleri Andrea, a.moleri@campus.unimib.it
- 865939, Armani Filippo, f.armani1@campus.unimib.it

## Introducing Atlas, MongoDB's DBaaS

MongoDB is a leading NoSQL database management system renowned for its flexibility and scalability, 
including the ability to manage huge amounts of data. A crucial aspect of MongoDB's ecosystem is Atlas, 
MongoDB's Multi-Cloud Developer Data Platform. Understanding how MongoDB Atlas stores and hosts data through 
Atlas Clusters is fundamental to harnessing its capabilities effectively.

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
archiving contribute to Atlas's appeal across various stages of the development cycle.

Moreover, Atlas provides specialized services such as Atlas Search, enabling cloud-based data search experiences 
without the need for separate search engines, and Atlas Vector Search, leveraging MongoDB's document model to store 
vector embeddings and facilitate semantic searches. Additional functionalities like Atlas Device Sync, Atlas Data Lake, 
Atlas Data Federation, Atlas Charts, and Atlas App Service further enrich the platform, 
empowering developers to build robust, intelligent applications efficiently.

### Creating and Deploying an Atlas Cluster

To effectively utilize MongoDB Atlas, the initial step involves creating an account. Upon account creation, 
selecting the Database M0 free sandbox tier, which provides 512MB of Storage, Shared RAM, and vCPU, 
initiates the process. Subsequently, a Dashboard is presented, housing various functionalities. 
Within this interface, the Organizations feature facilitates the grouping and management of users and projects. 
Projects, in turn, enable the organization of resources such as Database Clustering, allowing for the creation 
of distinct projects tailored for Development, Testing, and Production Environments. Notably, by default, 
databases are generated devoid of users and external IP addresses with access privileges. Consequently, creating 
an Admin user and an Access Point for the designated IP Address becomes imperative. Navigating to the 
Security Quickstart section enables the authentication of connections through username and password protocols, 
thereby establishing a new administrative user. Given MongoDB's default restriction on IP access to all addresses 
except those designated by Atlas, configuring access from the local machine necessitates inclusion of the local IP 
address within the Access List. This can be achieved by selecting "Add My Current IP Address" and, if required, 
additional addresses can be incorporated via the menu before finalizing with the "Finish and Close" button.

### Populating the Database

Initially devoid of data, the database requires population. This can be achieved by selecting "Load Sample Dataset", 
prompting MongoDB to provide a suitable dataset for utilization. Once loaded, access to the data is facilitated through 
the Data Explorer accessible by selecting "Browse Collections" from the Overview Page. The Atlas Data Explorer 
interface provides functionalities for viewing, filtering, and modifying data. A listing of all available databases 
within the cluster is presented on the left. For instance, selecting the sample_analytics database reveals all 
collections within it. Further exploration can be conducted by directly clicking on the name of a specific collection 
to view its documents. Utilizing the Filter Bar enables the specification of queries or the retrieval of specific documents. 
For example, entering { account_id: 794875 } in the filter bar yields documents containing the specified account ID.

## MongoDB and the Document Model

After introducing Atlas, it is pertinent to delve into the Document Model of MongoDB. In essence, 
we aim to comprehend how MongoDB stores data, how it is utilized, and its behavior in relation to Atlas. 
MongoDB serves as a General Purpose Document Database, structuring data in the form of Documents, akin to JSON Objects.
his is markedly distinct from Relational Databases, which organize data into rows and columns within tables.
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
Consequently, development becomes much simpler and more productive. MongoDB provides drivers in all major programming 
languages, facilitating the connection of a MongoDB Database to our application, regardless of the programming language used.

MongoDB encompasses a wide array of use cases, suitable for educational projects, startups, and even mission-critical 
enterprise applications, including e-commerce, content management, IoT, trading, payments, gaming, mobile applications, 
and real-time analytics. Some of the features that render MongoDB so popular include its scalability, resilience, 
rapid development speed, and high levels of privacy and security.

### Key Terminology
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

### Integration With Atlas
MongoDB and Atlas share a symbiotic relationship within the Developer Data Platform ecosystem. 
Atlas, MongoDB's cloud-hosted database service, leverages MongoDB as its core technology to provide developers with a 
comprehensive suite of data management tools and services. MongoDB Atlas extends the capabilities of MongoDB by 
offering additional features such as Full Text Search and Data Visualization. These advanced functionalities are 
built on top of data stored within MongoDB databases deployed on the Atlas platform. As a result, MongoDB serves as 
the underlying data engine powering Atlas's data management and analysis capabilities. Contrary to a common 
misconception, MongoDB is not simply the locally deployed version of Atlas.

### The Document Model

As anticipated, MongoDB stores data in structures known as documents which resemble JSON objects.
Below is an example of a document used to store product data in a store. We can observe that the document comprises 
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
inary JSON, an extension of JSON providing additional features that MongoDB can leverage. 
BSON also adds support for additional data types that would not otherwise be present in standard JSON. 
Thanks to this choice, the database can support a vast range of data types, including all JSON data types
(strings, objects, arrays, booleans, null), as well as dates, numbers, Object IDs, and more. 
Particularly, ObjectID is a special data type used in MongoDB to create unique identifiers. 
In the database, each document requires a field "_id" serving as the primary key. 
If a document does not include this field, MongoDB will automatically add it, generating a specific ObjectID value
for the document in question. By default, MongoDB supports a flexible schema model and polymorphic data. 
This enables us to store documents with different structures in the same collection.
Documents can contain various fields, and the fields can hold different data types from one document to another.
This is a significant distinction from relational databases, where declaring a table schema is necessary before 
inserting data. MongoDB's flexible schema allows us to iterate rapidly and evolve as our requirements change.

Here's a practical example of this flexible schema. Suppose we have an online furniture store with a catalog of items.
When we start developing our application, we decide to include an "_id", a "name", and a "price" for each item.

```json
{
	"_id": ObjectId("abcdef"),
	"name": "iPhone",
	"price": 1500.00
}
```

In the future, we might decide to add an additional field, such as a "description" field. With a relational database, 
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

### Managing Databases, Collections, and Documents in Atlas Data Explorer

We can manage data in MongoDB using the Atlas Data Explorer, a tool that provides a graphical interface for data management. 
Although developers often interact with MongoDB instances using the command line, many opt to use the Data Explorer to 
view and manage data in both Development and Production Databases. In the following sections, we will explore how to 
utilize the Data Explorer to create and view databases, collections, and documents within an Atlas Database Deployment.

#### Accessing Data Explorer

To begin, we navigate to the [appropriate web page](https://cloud.mongodb.com/) and access our Database Deployment. From there, we proceed to 
the **Database** section and then select **Collections** to enter the Data Explorer interface. As previously mentioned, 
on the left side of the interface, all our databases are listed, and beneath each, we can access the collections 
contained within them via dropdown menus. Clicking on a collection alters the view, allowing us to see individual 
documents within the collection, along with details such as the Storage Size and total number of documents.

#### Creating Databases and Collections

To create a new database, we click on the "Create Database" button and fill in the required details, such as
atabase Name and Collection Name. Upon clicking the Create button, the database is added to the Database Deployment. 
Alternatively, if we wish to add a collection to an existing database, we can simply press the "+" button next to the
respective database. This action opens the "Create Collection" menu, where we can specify the new collection to be added.

#### Inserting Documents

To insert a document within a collection, for instance, a new document under the "posts" collection, we navigate to the 
desired collection and click on the "Insert Document" button. This action opens the "Insert Document" menu, 
facilitating the entry of documents in a pseudo-JSON format. We can then input the necessary key-value pairs for the 
document. This approach provides a guided mode for document insertion, requiring less code and allowing us to select 
data types from dropdown menus for the key-value pairs. Upon completion, we press the Insert button to add the document
to the collection. The following JSON snippet represents an example document that can be inserted into a collection using 
the Data Explorer's guided insertion functionality, and as such it has not the same structure of a hand-typed JSON file.

```json
_id: ObjectId('6636644d93e845efd9692af4')
"name" : "Andrea",
"age" : 23
```

## MongoDB Data Modeling

Data modeling is a fundamental aspect of database design, as it serves the purpose of the structuring of data storage 
and the delineation of relationships among various entities within the data. It serves as a blueprint for organizing 
information within a database. We refer to the organization of data inside a database as a 'Schema'. When data modeling 
with MongoDB, it is advisable to conceptualize the application itself: its functionalities, the data it will handle, 
user data access patterns, and the data elements critical to the project's objectives. Addressing these questions aids 
in understanding the form of the data, its relationships, and the necessary tools for implementation. A robust data 
model offers several benefits, facilitating data management, enhancing query efficiency, optimizing resource usage, 
and reducing operational costs.

As a guiding principle within MongoDB, the phrase _"data that is accessed together, should be stored together"_ 
underscores the great importance of structuring data in a manner conducive to operational efficiency. 
MongoDB employs a flexible document data model, in which collections do not impose any default document structure.
As a consequence, documents may exhibit diverse structures, thanks to a concept called polymorphism, as exemplified below:

**Document I**
```json

{
	"name": "Andrea"
	"major": "CS"
	"course": "Architetture Dati"
	"amount": 1000,
	"paid": "Yes"
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

### Types of Data Relationships

When discussing data relationships, it is crucial to delineate between various types: One-To-One, One-To-Many, 
and Many-To-Many. Additionally, we will delve into the two primary methods for modeling these relationships: 
Embedding and Referencing. As we already said, it is important to structure our data to align with the querying and 
updating patterns of our application. In that regard, understanding common relationship types in databases is extremely important. 

#### One-To-One Relationship
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

#### One-To-Many Relationship
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

#### Many-To-Many Relationship
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

### Modeling Data Relationships

In this section, we provide an example of data modeling based on a practical scenario illustrated in the code below. 
When a student enrolls at a university, they fill out a form on a web application that creates their profile, 
which is then stored in a database. Upon examining the following code, there emerges a need to gather more information 
about the student, such as the courses taken and their grades. Furthermore, certain aspects of the code are not optimally structured.

```json
{
	"student": "John Smith",
	"student_id": "001",
	"age": "18",
	"home_phone": "2125550000",
	"cell_phone": "2125550001",
	"email": "johnsmith@mongodb.edu",
	"grade_level": "freshman",
	"street": "3771 McClintock Ave",
	"city": "Los Angeles",
	"state": "CA",
	"zip": "90089",
	"emergency_contact_name": "Mary Smith",
	"emergency_contact_number": "212550002",
	"emergency_contact_relation": "Mother"
}
```
An initial observation reveals the presence of three phone numbers at different locations within the code, 
resulting in not-so-clean code. To address this issue, reorganization is proposed instead of treating them as 
separate elements indicating a One-To-One Relationship. This reorganization involves transforming it into a One-To-Many 
Relationship through the use of a Nested Array.

```json
{
	"student": "John Smith",
	"student_id": "001",
	"age": "18",
	"email": "johnsmith@mongodb.edu",
	"grade_level": "freshman",
	"street": "3771 McClintock Ave",
	"city": "Los Angeles",
	"state": "CA",
	"zip": "90089",
	"emergency_contact_name": "Mary Smith",
	"emergency_contact_relation": "Mother",
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
	"student": "John Smith",
	"student_id": "001",
	"age": "18",
	"contact_number": [
		{"number": "2125550000", "type": "home"},
		{"number": "2125550001", "type": "cell"},
		{"number": "212550002", "type": "emergency"}
	],
	"email": "johnsmith@mongodb.edu",
	"grade_level": "freshman",
	"gpa": "4.0",
	"street": "3771 McClintock Ave",
	"city": "Los Angeles",
	"state": "CA",
	"zip": "90089",
	"emergency_contact_name": "Mary Smith",
	"emergency_contact_relation": "Mother",
	"courses": [
		{"course_id": "CS150", "course_name": "MongoDB101"},
		{"course_id": "CS177", "course_name": "Introduction to Programming in Python"}
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
		"course_id": "CS100",
		"course_name": "An Introduction to Computer Science",
		"professor": "Sarah Lambert",
		"offered": "Spring, Summer, Fall, Winter"
	},
	
	{
		"course_id": "CS150",
		"course_name": "MongoDB101",
		"professor": "Bernie Hacker",
		"offered": "Fall, Spring"
	},
	
	{
		"course_id": "CS177",
		"course_name": "Introduction to Programming in Python",
		"professor": "Bernie Hacker",
		"offered": "Winter"
	}
]
```
## Embedding Data in Documents

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
    "name": {"firstName": "Sarah", "lastName": "Davis"},
    "job": "professor",
    "address": {
        "mailingAddress": {
            "street": "402 Maple",
            "city": "Chicago",
            "zipcode": "81442"
        },
        "secondaryAddress": {
            "street": "318 University Blvd",
            "city": "Chicago",
            "zipcode": "81445"
        },
        "emergencyAddress": {
            "name": "Kerri Davis",
            "street": "42 Wallaby Way",
            "city": "Sydney",
            "zipcode": "78 AUZ90",
            "country": "Australia"
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

## Referencing Data in Documents

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
	"student": "John Smith",
	"student_id": "001",
	"age": "18",
	"contact_number": [
		{"number": "2125550000", "type": "home"},
		{"number": "2125550001", "type": "cell"},
		{"number": "212550002", "type": "emergency"}
	],
	"email": "johnsmith@mongodb.edu",
	"grade_level": "freshman",
	"gpa": "4.0",
	"street": "3771 McClintock Ave",
	"city": "Los Angeles",
	"state": "CA",
	"zip": "90089",
	"emergency_contact_name": "Mary Smith",
	"emergency_contact_relation": "Mother",
	"courses": [
		{"course_id": "CS150", "course_name": "MongoDB101"},
		{"course_id": "CS177", "course_name": "Introduction to Programming in Python"}
	]
}
```

To summarize the advantages and disadvantages, we employ embedding when we want to use a single query to retrieve 
data and when performing individual operations for data updates or deletions. However, this approach carries the risk 
of data duplication and the creation of substantial documents. Regarding referencing, this technique enables us to avoid 
duplicates, resulting in smaller and more manageable documents. However, this technique introduces 
the need of data joins from disparate documents.

Another realistic example illustrates the utilization of referencing, where `user_id` in the first collection acts as 
a reference to a document in the `users` collection, thereby establishing a linkage between the two documents through referencing.

**Collection I**
```json
{
    "author": "Aileen Long",
    "title": "Learn MongoDB in 30 Mins",
    "published_date": ISODate("2020-05-18T14:10:30Z"),
    "tags": ["mongodb", "introductory", "database", "nosql"],
    "comments": [
        {
            "comment_id": "LM001",
            "user_id": "AL001",
            "comment_date": ISODate("2020-05-19T14:22:00Z"),
            "comment": "Great read!"
        },
        {
            "comment_id": "LM002",
            "user_id": "AL002",
            "comment_date": ISODate("2020-06-01T08:00:00Z"),
            "comment": "So easy to understand - thanks!"
        }
    ]
}
```

**Collection II**
```json
...
{
    "id": "AL001",
    "name": "Ella Richardson"
},
{
    "id": "AL002",
    "name": "Jackie Thomas"
},
{
    "id": "AL003",
    "name": "Justin McDonald"
},
...  
```

## Scaling a Data Model

Creating non-scalable Data Models is a common issue that has serious consequences. The principle of "data that is 
accessed together should be stored together" is not merely a mantra; it is based on the notion that the way we 
access our data should align with the data model to achieve optimal efficiency in query result times, memory usage, 
CPU usage, and storage. When designing a Data Model, we aim to avoid unbounded documents, which are documents whose 
size can grow indefinitely. This can occur with Document Embedding. 

Consider the following example in the code snippet below, where we have the structure of a Blog Post and its comments. 
Currently, all comments on a single blog post are within an array in the Blog Post Document. However, what happens if 
we have thousands of comments on a single post? There could be issues related to the growth of the comments array, 
including the fact that the document will occupy increasingly more memory space, potentially leading to write performance
issues as, with each comment addition, the entire document is rewritten in the MongoDB Data Storage. Additionally, 
pagination of comments will be complex. Comments cannot be easily filtered in this manner, so we would need to retrieve 
them all and potentially filter them in the application. Furthermore, we must not overlook the maximum BSON document 
size of 16MB, avoiding which can lead to storage problems. The benefits of the model shown are that we can retrieve 
all documents in a single Read, but this is not a feature we require, so the folliwing code certainly has more drawbacks than advantages.

```json
{
	"title": "Basics of MongoDB",
	"url": "https://www.mongodbbasics.com",
	"text": "Let's learn the basics of MongoDB!",
	"comments": [{
		"name": "John Smith",
		"created_on": "2022-07-21T11:00:00Z",
 		"comment": "I learned a lot!"
	}, {
		"name": "Jane Doe",
		"created_on": "2022-07-22T11:00:00Z",
		"comment": "Looks great"
	}
	]
}
```

To resolve the issue, we avoid using Embeddings and partition our data into multiple Collections, using References 
to keep frequently accessed data together, effectively creating two different collections: one called `blog_post` 
and another called `comments`, as illustrated below. We can use the `blog_entry_id` field as a reference between the two collections.

**Blog Post Collection**
```json
{
	"_id": 1,
	"title": "Basics of MongoDB",
	"url": "https://www.mongodbbasics.com",
	"text": "Let's learn the basics of MongoDB!"
}
```

**Comments Collection**
```json
{
	"blog_entry_id": 1,
	"name": "John Smith",
	"created_on": "2022-07-21T11:00:00Z",
	"comment": "I learned a lot!"
},
{
	"blog_entry_id": 1,
	"name": "Jane Doe",
	"created_on": "2022-07-22T11:00:00Z",
	"comment": "Looks great"
}
```

### Using Atlas Tools for Schema Help

Schema Design Patterns are guidelines that assist developers in planning, organizing, and modeling data. 
When applications are developed without adhering to best practices, suboptimal performance and unscalable solutions, 
that we know are known as Schema Anti-Patterns, may arise. Some of the most common Anti-Patterns include massive arrays, 
a massive number of collections, bloated documents, unnecessary indexes, queries without indexes, and data accessed together 
but stored in different collections. Recognizing these Anti-Patterns is not always straightforward, but some of the tools 
available in Atlas can aid in their identification. Specifically, we have Data Explorer and Performance Advisor.

Data Explorer is accessible in the free tier of Atlas and serves as a valuable tool for schema analysis. By selecting a 
collection, developers can access useful information such as Storage Size, Logical Data Size, Total Documents, and Indexes
Total Size. Furthermore, by navigating to the Indexes tab within a selected collection, developers can gain insights into
the indexes associated with the collection. This feature helps identify unnecessary indexes that can be eliminated, 
such as those rarely utilized. If an index is deemed unnecessary, developers can seamlessly remove it by selecting 
"Drop Index". Additionally, under the Collection section, developers can explore Schema Anti-Patterns, which assist
in identifying potential issues. By clicking "Learn How to Fix This Issue", developers are directed to detailed 
documentation explaining how to address the identified Anti-Pattern.

Another valuable tool within Atlas is the Performance Advisor, located under Database. This tool aids in identifying 
redundant indexes. Particularly beneficial for users operating within a paid tier from Cluster Tier M10 and above, 
the Performance Advisor offers recommendations for enhancing the performance of active collections and queries that execute slowly. 

## Connecting to a MongoDB Database Using Connection Strings

### Introduction

The MongoDB Connection String allows us to connect to the cluster and work with the data. It describes the host we will 
use and the options for connecting to a MongoDB database. For example, the Connection String can be used to connect 
from the Mongo Shell, MongoDB Compass, or any other application. MongoDB offers two formats for the Connection String: 
the Standard Format and the DNS Seed List Format.

- **Standard Format**: This is used to connect to standalone clusters, replica sets, or sharded clusters.
- **DNS Seed List Format**: This format allows us to provide a DNS server list in our connection string. It offers 
flexibility in deployment and the ability to rotate servers without reconfiguring clients.

### Finding Your Connection String

Is it possible to find the Connection String on Atlas by navigating to the "Database" section and pressing the "Connect" button 
for the cluster you wish to connect to. This will open a menu that provides options for connecting to the database via 
MongoDB Shell, Application, or Compass. For now, select "Connect Your Application". This will open step-by-step instructions for connecting to the MongoDB instance. 
You will be given a Connection String to copy and paste, which you will use to connect to MongoDB.

### Structure of the Connection String

The connection string begins with the required prefix `mongodb+srv` which identifies it as a MongoDB Connection String.

```json
mongodb+srv://<username>:<password>@cluster0.usqsf.mongodb.net/?retryWrites=true&w=majority
```

- **srv**: Automatically sets the TLS Security Options to true and instructs MongoDB to use the DNS Seedlist.
- **username and password**: These are created for the database in the Atlas Dashboard.
- **Host and optional port number**: If the port number is not specified, MongoDB defaults to port 27017.
- **Additional options**: These include Connection Timeout, TLS, SSL, Connection Pooling, and Read & Write Concerns. 
In this connection string, `retryWrites` is set to true, instructing MongoDB Drivers to automatically 
retry certain types of operations when they fail.











## Connecting to a MongoDB Atlas Cluster with The Shell

### Step-by-Step Connection Process

To connect to the MongoDB Shell, follow these steps:

1. **Login to Atlas**: Start by logging into your MongoDB Atlas account. 
Navigate to the `Databases` section and click on `Connect` for the desired cluster.

2. **Select Connection Method**: Choose the option `Connect with the MongoDB Shell`. 
This will provide step-by-step instructions for connecting via the shell.

3. **Confirm Shell Installation**: Click on `I Have the MongoDB Shell Installed`. 
Then, copy the provided connection string.

4. **Execute in Terminal**: Open your terminal, paste the copied connection string, and press Enter. 
You will be prompted to enter the Admin Password. After doing so, you will be connected to the cluster.


**Note**: Ensure that the MongoDB Shell is installed before proceeding. 
On macOS, you can install it using the following command:

```sh
brew install mongosh
```

Example connection command:

```sh
mongosh "mongodb+srv://learningmongodb.hikoksa.mongodb.net/" --apiVersion 1 --username admin
```

### Post-Login Information and Shell Capabilities

Upon a successful login, you will receive a prompt displaying various details, including the MongoShell Log ID, 
the connected server, and the versions of MongoDB and MongoShell in use.

The MongoDB Shell functions as a Node.js REPL (Read-Eval-Print Loop) environment, offering access to JavaScript 
variables, functions, conditionals, loops, and control flow statements.

For example, you can create a variable containing an array of strings:

```javascript
const greetingArray = ["hello", "world", "welcome"];
```

Press Enter, and the variable will be stored for future use. Additionally, you can define a function to iterate over this array:

```javascript
const loopArray = (array) => array.forEach(el => console.log(el));
```

This function will take an array as input and use the `forEach` method to log each element to the console. You can then invoke this function with the previously defined array:

```javascript
loopArray(greetingArray);
```

Executing the above will print each element of `greetingArray` to the console. The MongoDB Shell thus allows extensive use of JavaScript elements within its environment.




