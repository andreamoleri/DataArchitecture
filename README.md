# Data Architecture

## Team Members
- 902011, Moleri Andrea, a.moleri@campus.unimib.it
- 865939, Armani Filippo, f.armani1@campus.unimib.it



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

## Connecting to a MongoDB Atlas Cluster with Compass

In this section, we will explore how to use the Connection String with MongoDB Compass to connect to a Cluster. 
MongoDB Compass is a GUI that allows us to query and analyze our data, as well as compose aggregation pipelines.
Once logged into Atlas, navigate to Databases and then Connect, selecting the MongoDB Compass option. 
This will open a familiar screen, providing the usual connection string to copy and paste. The difference, 
however, is that we can download MongoDB Compass for our operating system. After downloading MongoDB Compass, 
we can go to New Connection, enable the Edit Connection String switch, and then replace the text in the URI Box 
with the Connection String from Atlas. We will notice that the password is not yet inserted in this Connection String: 
we need to replace `<password>` with our password, and once done, we can click Save & Connect.

We will be prompted to name the connection. Once connected, we will notice a list of databases and collections on the
left menu. Additionally, we will find tabs for My Queries to save future aggregations and queries we write, 
a Databases tab that provides metadata about the available databases in the Cluster, and a Performance tab, 
which allows us to monitor the Cluster's performance metrics. In the left-hand sidebar, we can select one of the 
databases, and the collections within the database will be displayed. We can also individually click on one of the 
collections to view the documents within those collections. Similar to Atlas, we can also use the Filter tab,
and there are several other tabs for each collection:

- **Documents**: view the documents in the collection.
- **Aggregations**: compose aggregation statements to execute on the collection.
- **Schema**: analyze the structure of the documents, which can help in optimizing the schema.
- **Explain Plan**: understand the performance of specific queries run in the database.
- **Indexes**: view existing indexes on specific collections and understand the performance of specific queries.
- **Validation**: create rules to enforce the data structure of documents on update and insert statements.

## Connecting to a MongoDB Atlas Cluster from an Application

It is time to introduce the concept of MongoDB Drivers. MongoDB Drivers connect our application to our database 
using a Connection String and through the use of a programming language of our choice. In other words, MongoDB 
drivers provide a way to connect our database with our application.

To find a list of languages supported by MongoDB, it is possible to visit the
[official MongoDB's drivers list website](http://mongodb.com/docs/drivers). 
Numerous languages are supported, and there is also a section called Community Supported Libraries, 
which contains drivers for languages not officially supported but maintained by the community. 
On the aforementioned link, we can choose a language and click on it. The MongoDB Documentation contains 
resources including a Quick Start section to quickly configure the Drivers. We also have a Quick Reference 
section that contains the syntax of common commands, and sections for Usage Examples and Fundamentals.

Since Java is the language we will be using in this report, is it possible to refer to the following documentation page: 
[MongoDB Java Driver Quick Start](https://www.mongodb.com/docs/drivers/java/sync/current/quick-start/). 
It is also worth noting the existence of the MongoDB Developer Center, which contains tutorials, videos,
and examples of applications built in various programming languages.

Always remember that if we encounter issues while following one of the connection procedures mentioned, 
it is often because we have not granted permissions to the IP address of our local machine in Atlas. 
If necessary, add these permissions by navigating to `Security > Network Access > Add IP Address`. 
If this does not resolve the issue, the correct spelling of usernames and passwords needs to be checked.

## Connecting to MongoDB in Java: Spring Boot & Maven

### Introduction

When building a Java application and connecting it with MongoDB, the application requires a series of libraries 
to interact with the MongoDB deployment. Collectively, these libraries are referred to as "Drivers." 
MongoDB maintains official Java Drivers for both synchronous and asynchronous applications. For this report, 
the synchronous version will be used. The MongoDB drivers simplify the connection and interaction between 
applications and the database, establish secure connections to MongoDB clusters, and execute database operations 
on behalf of client applications. Additionally, the drivers allow specifying connection options such as security 
settings, write durability, read isolation, and more. The official drivers adhere to language best practices 
and enable the use of the full functionality of the MongoDB deployment.

### Using Maven with Spring Boot

To start with the practical part using Maven, a Java Maven project can be created with Spring Boot. 
It is possible to use Spring Boot to create a Java Maven project even if there is no intention to use the s
pecific functionalities of Spring. Spring Boot offers many conventions and tools for project configuration, 
greatly simplifying development and dependency management. Even if the functionalities of Spring, 
such as dependency injection or the MVC framework, are not needed, Spring Boot can still provide benefits 
like integration with embedded servers, convention-based automatic configuration, and tools for dependency management. 
The specific features of Spring can always be utilized or ignored based on the project's needs. 
This is why this method of project creation is chosen for the report.

### Creating a Java Maven Project with Spring Boot

To create a Java Maven project using Spring Boot, one can use a tool called 
[Spring Initializr](https://start.spring.io). This tool provides an intuitive web interface that allows 
configuring and generating a customized Spring Boot project quickly.

First, visit the [Spring Initializr](https://start.spring.io) website. Here, the project characteristics 
can be specified. We will choose Java as the language, Maven as the project type, and version 3.2.5 of Spring Boot.
The project will be named `quickstart`, belonging to the group `com.mongodb`. 
The packaging will be executed in `Jar`, and the Java version will be set to 17.

After configuring the project on Spring Initializr, click the generation button to download the project as a zip file. 
Once downloaded, extract the zip file's contents into a directory on your computer. 
This project can then be imported into the preferred integrated development environment (IDE), 
such as IntelliJ IDEA or Eclipse, using the import function to bring in the newly created project.

Once imported, a basic structure will be available to begin application development. Many of the fundamental 
components of a Spring Boot application, such as support for Spring annotations and dependency management through 
Maven, will already be configured. Spring Boot significantly simplifies the development process, reducing the time 
needed for initial project setup and allowing the focus to be on developing the features that make the application unique.

## Connecting to MongoDB in Java: Pom

This section will outline the steps required to connect to MongoDB using Java and Maven. The process involves updating 
the `pom.xml` file to include the necessary MongoDB driver dependencies and writing a Java class to establish and 
manage the connection to a MongoDB Atlas cluster.

### Updating the `pom.xml` File

First, open the Java Maven project and locate the `pom.xml` file, that should look like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>quickstart</artifactId>
    <version>1.0-SNAPSHOT</version>


    <properties>
        <maven.compiler.source>18</maven.compiler.source>
        <maven.compiler.target>18</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>18</source>
                    <target>18</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

To add the MongoDB driver to the project's dependencies, the `pom.xml` file should be updated as follows.
Ensure the latest version is used, which can be found in the
[MongoDB documentation](https://www.mongodb.com/docs/drivers/java/sync/current/quick-start/).

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>quickstart</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>18</maven.compiler.source>
        <maven.compiler.target>18</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.mongodb</groupId>
            <artifactId>mongodb-driver-sync</artifactId>
            <version>5.1.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>18</source>
                    <target>18</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Connecting to MongoDB Atlas Cluster

After adding the MongoDB driver dependency, the next step is to instruct the application to connect to the Atlas 
cluster using the Java Synchronous Driver from the Maven repository. It is necessary to have a valid connection 
string or URI to connect to the Atlas cluster. This can be obtained from the Atlas interface by navigating to
`"Databases" -> "Connect" -> "Drivers"` and selecting `"Java 4.3 or Later"` as the version.

The connection string provided by Atlas should be used in the Java code. The following example demonstrates how 
to create a new file named `Connection.java` in the `src` folder of the project and utilize the connection string 
to establish a connection with the Atlas cluster. Create a new file named `Connection.java` in the `src` folder 
and insert the following code:

```java
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
```

### Compiling and Running the Project

To compile the project, execute the following Maven command from the terminal in the project's root directory:

```bash
mvn --quiet compile
```

To run the application and connect to the Atlas cluster, use the following Maven command, making sure to replace
`<username>` with the username, and `<password>` with the password. For demo purposes during the project, both
username and password will be set to `admin`. This is just a toy example, and for security reasons the actual
database password should be more complex to ensure safety

```bash
mvn compile exec:java -Dexec.mainClass="com.mongodb.quickstart.Connection" -Dmongodb.uri="mongodb+srv://admin:admin@learningmongodb.hikoksa.mongodb.net/?retryWrites=true&w=majority&appName=LearningMongoDB"
```

If the command executes successfully, it will return a list of databases contained within the Atlas cluster.

### Best Practices for MongoClient Instances

For optimal performance and cost efficiency, it is recommended to have only one `MongoClient` instance per Atlas 
cluster in the application. Creating multiple `MongoClient` instances can lead to higher-than-normal database costs.

The following example demonstrates the use of a Singleton pattern to ensure a single `MongoClient` 
instance is used throughout the application, thereby preventing the creation of multiple instance:

```java
package com.mongodb.quickstart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Connection {
    private static Connection instance;
    private final MongoClient mongoClient;

    private Connection(String connectionString) {
        this.mongoClient = MongoClients.create(connectionString);
    }

    public static synchronized Connection getInstance(String connectionString) {
        if (instance == null) {
            instance = new Connection(connectionString);
        }
        return instance;
    }

    public void listDatabases() {
        // Retrieve the list of databases
        List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());

        // Print information about each database
        databases.forEach(db -> System.out.println(db.toJson()));
    }

    public static void main(String[] args) {
        // Retrieve the MongoDB URI from the system properties
        String connectionString = System.getProperty("mongodb.uri");

        // Establish a connection to the MongoDB instance
        Connection connection = Connection.getInstance(connectionString);
        connection.listDatabases();
    }
}
```

### Troubleshooting Connection Issues

Common connection issues include Atlas IP access restrictions, invalid connection string format, 
incorrect authentication, firewall misconfiguration, and flawed connection code. 
Ensure these aspects are correctly configured to avoid connection problems.

## Inserting Documents in a MongoDB Collection

### Methods for Inserting Documents

There are two methods available for inserting documents into a collection in MongoDB: `insertOne()` and `insertMany()`. 
To use the `insertOne()` method, it is appended to the database as follows: `db.<collection>.insertOne()`. 
For instance, when connected to a sample database, one can use the command `db.grades.insertOne()`.

If the `grades` collection does not yet exist in the database, MongoDB will automatically create the collection.
This is an important point to remember to avoid inadvertently creating new collections within the database. 
Once the command is set, the document intended for insertion is passed as a parameter within the `insertOne` method. 
The following example code can be executed in a bash terminal window connected to an Atlas cluster:

```java
db.grades.insertOne({
  student_id: 654321,
  products: [
    {
      type: "exam",
      score: 90,
    },
    {
      type: "homework",
      score: 59,
    },
    {
      type: "quiz",
      score: 75,
    },
    {
      type: "homework",
      score: 88,
    },
  ],
  class_id: 550,
})
```

If the operation is successful, it will return `acknowledged: true` along with the `ObjectID`
of the newly created document, which is generated automatically.

### Inserting Multiple Documents

To insert multiple documents at once, the `insertMany()` method is used with the following syntax:

```java
db.<collection>.insertMany([
	<document 1>,
	<document 2>,
	<document 3>
])
```

As the name suggests, this code inserts multiple documents. An array of documents intended for insertion is passed, 
separated by commas. This method can be executed in the shell similarly to `insertOne()`. 
Again, an acknowledgment confirming the insertion of multiple documents into the database, 
along with their respective `ObjectID` values, will be returned. Below is an example of how to use `insertMany()`
in a bash terminal window connected to an Atlas cluster:

```java
db.grades.insertMany([
  {
    student_id: 546789,
    products: [
      {
        type: "quiz",
        score: 50,
      },
      {
        type: "homework",
        score: 70,
      },
      {
        type: "quiz",
        score: 66,
      },
      {
        type: "exam",
        score: 70,
      },
    ],
    class_id: 551,
  },
  {
    student_id: 777777,
    products: [
      {
        type: "exam",
        score: 83,
      },
      {
        type: "quiz",
        score: 59,
      },
      {
        type: "quiz",
        score: 72,
      },
      {
        type: "quiz",
        score: 67,
      },
    ],
    class_id: 550,
  },
  {
    student_id: 223344,
    products: [
      {
        type: "exam",
        score: 45,
      },
      {
        type: "homework",
        score: 39,
      },
      {
        type: "quiz",
        score: 40,
      },
      {
        type: "homework",
        score: 88,
      },
    ],
    class_id: 551,
  },
])
```

## Finding Documents in a MongoDB Collection

### Using the `find()` Method

The `find()` method can be used to locate objects within a collection. Additionally, the `$in` operator can be 
utilized alongside this method. To use the `find()` method, one can simply execute `db.<collection>.find()`. 
For instance, assuming there is a collection named `zips`, the following command can be executed from a terminal 
connected to an Atlas cluster: `db.zips.find()`. This command will return some of the documents contained within 
the collection.

To view more results, the `it` shell directive can be employed. This directive will (it)erate over the extensive 
list of results. Therefore, by entering `it` and pressing enter, more results from the collection can be viewed.
To retrieve a specific document from the collection, the syntax `{ field: <value> }` can be used. 
For example, `db.zips.find({ state: "AZ" })` will return all documents with `state: AZ`. Another example command might be:

```bash
db.zips.find({ _id: ObjectId("5c8eccc1caa187d17ca6ed16") })
```

### Using the `$in` Operator

The `$in` operator allows for the selection of all documents that have a field value matching one of the values 
specified in an array. A query in the terminal might follow the syntax:

```bash
db.<collection>.find({
    <field>: {$in:
        [<value>, <value>, ...]
    }
})
```

Here, the keyword `in` is followed by an array of values to be matched. For example, in the following code, 
the goal is to find every document containing a city value that is either PHOENIX or CHICAGO. 
By executing this command, the database will respond with a list of documents that meet the query's criteria:

```bash
db.zips.find({ city: { $in: ["PHOENIX", "CHICAGO"] } })
```

## Finding Documents by Using Comparison Operators

Comparison operators can be utilized to find documents. These include greater than or `$gt`, less than or `$lt`, 
less than or equal to or `$lte`, and greater than or equal to or `$gte`. To use a comparison operator, 
the syntax is `<field>: { <operator> : <value> }`.

Consider the following examples, starting with `$gt`, which returns documents where the field contains a value 
greater than the specified value. For instance, one might search for prices greater than 50 dollars. 
In the following code, we specify the document field name, followed by the sub-document field name in quotes. 
In this case, it is the field `items.price`. When this command is executed, all sub-documents with a price 
greater than $50 are returned.

The same logic applies for elements that are less than, greater than or equal to, or less than or equal to a 
specified value. In the code provided below, `sales` is the collection, 
while the subdocument fields are `items.price` and `customer.age`.

```bash
> db.sales.find({ "items.price": { $gt: 50 } })

> db.sales.find({ "items.price": { $lt: 50 } })

> db.sales.find({ "customer.age": { $gte: 50 } })

> db.sales.find({ "customer.age": { $lte: 50 } })
```

## Querying on Array Elements in MongoDB

### Querying Specific Values in an Array

To understand how to query specific values, also known as elements, within an array in a MongoDB database, 
consider a common scenario. This involves searching for all documents that have a field containing the specified value. 
For example, consider a collection named `Accounts` defined as follows:

```json
{
	"account_id": 470650,
	"limit": 10000,
	"products": [
		"CurrencyService",
		"Commodity",
		"InvestmentStock"
	]
}
```

Each document in this collection has a field called `products`. A query can be defined to find all documents 
containing the value `InvestmentStock`. The syntax for this query is as follows:

```bash
db.accounts.find({ products: "InvestmentStock" })
```

This syntax is familiar to those used for equality matches. Upon executing the query, all documents will be returned 
that have a `products` field containing either an array or a scalar value that includes `InvestmentStock`. 
Documents not containing this value will not be returned.

### Using $elemMatch for Array Elements

To perform a query for one or more values but only return a match if they are elements of an array, 
the `$elemMatch` operator can be used. The syntax for this is shown below:

```bash
db.accounts.find({
	products: {
		$elemMatch: { $eq: "InvestmentStock" }
	}
})
```

This query ensures that the `products` field is an array containing `InvestmentStock`. Therefore, the returned 
documents will have a `products` field that is an array containing an element equal to `InvestmentStock`.

The `$elemMatch` operator can also be used to find documents where a single array element matches multiple query 
criteria. Each query criterion is placed in `$elemMatch`, separated by a comma, as shown in the following syntax:

```bash
{ <field>: { $elemMatch: 
	{
		<query1>,
		<query2>,
		...
	}
}}
```

### Example with Multiple Criteria

Consider a collection named `sales`, focusing on the `items` field. This field contains an array of sub-documents 
with information about the items. The following query, executed in the terminal, will find all documents with at 
least one element in the `sales` collection that is a laptop priced over $800 and with a quantity of at least 1.

```bash
db.sales.find({
	items: {
		$elemMatch: { name: "laptop", price: { $gt: 800 }, quantity: { $gte: 1 } }
	}
})
```

After executing this query, the returned documents will contain laptops with quantities greater than or equal to 1
and prices greater than $800. In other words, the `$elemMatch` operator can be used to find all documents that 
contain the specified sub-document.

## Finding Documents by Using Logical Operators

### Logical Operators in MongoDB

In MongoDB, the logical operators `$and` and `$or` can be used to perform queries. The `$and` operator executes a 
logical AND on an array of one or more expressions, returning all documents that meet all the criteria specified in 
the array. The syntax for this operator is as follows:

```bash
db.<collection>.find({
  $and: [
    {<expression>},
    {<expression>},
    ...
  ]
})
```

This `$and` operator has an implicit syntax often used to simplify a query expression. It is sufficient to add a comma 
between expressions to specify an implicit AND, for example:

```bash
db.collection.find({ <expression>, <expression> })
```

### Understanding the `$and` Operator

It is important to remember that this comma acts just like the AND operator. When used, if even one of the specified 
criteria does not pass, the document will not be included in the results. Before proceeding to an example, consider 
a sample document from a collection called `Routes`. Each element in the collection contains information about a 
particular flight route:

```json
{
  "_id": ObjectId ("56e9b39b732b6122f877fa80"),
  "airline": {
    "id": 8359,
    "name": "Star Peru (2I)",
    "alias": "21",
    "iata": "\|N"
  },
  "src_airport": "ТРP",
  "dst_airport": "LIM",
  "codeshare": "",
  "stops": 0,
  "airplane": "142 146"
}
```

Consider the following query, where all documents are being searched for those whose airline is "Southwest Airlines" 
and whose number of stops is greater than or equal to 1. This query will return the relevant documents:

```bash
db.routes.find({
  $and: [{ "airline": "Southwest Airlines" }, { "stops": { $gte: 1 } }],
})
```

However, the implicit syntax can simplify the query:

```bash
db.routes.find({ "airline.name": "Southwest Airlines", stops: { $gte: 1 } })
```

This will return the same documents as before.

### The `$or` Operator

Next, consider the `$or` operator, which performs a logical OR on an array of two or more expressions, returning 
documents that match at least one of the provided expressions.

```bash
db.<collection>.find({
  $or: [
    {<expression>},
    {<expression>},
    ...
  ]
})
```

An example query using this operator will return all flights either departing from or arriving at the SEA airport:

```bash
db.routes.find({
  $or: [{ dst_airport: "SEA" }, { src_airport: "SEA" }],
})
```

### Combining Logical Operators

Logical operators can also be combined. Consider the following example, where an `$and` operator contains two `$or` 
operators. This query searches for every flight that has SEA as either the departure or arrival airport, and also 
all flights operated by American Airlines or using an Airbus 320 airplane:

```bash
db.routes.find({
  $and: [
    { $or: [{ dst_airport: "SEA" }, { src_airport: "SEA" }] },
    { $or: [{ "airline.name": "American Airlines" }, { airplane: 320 }] },
  ]
})
```

In such cases, the implicit syntax of `$and` is not used. This is because the first OR expression would be overwritten
by the following OR expression. This occurs because two fields with the same name cannot be stored in the same JSON object. 
Thus, as a general rule, when including the same operator more than once in a query, the explicit `$and` operator must be used.

## Replacing a Document in MongoDB

Occasionally, documents are erroneously inserted into a collection. Fortunately, replacing them is straightforward. 
To replace a single document, the `replaceOne()` method is used, for example: `db.collection.replaceOne(filter, replacement, options)`. 
This method accepts three arguments: filter, replacement, and options. The latter is optional. Here is an example: 
incomplete or temporary documents can be replaced with complete ones while retaining the same `_id`. 
Below is an example of a document created before the book was ready for publication, with both `ISBN` and `thumbnailUrl` set to default values.

```json
{
	_id: "62c5671541e2c6bcb528308",
	title: "Deep Dive into React Hooks",
	ISBN: "000000000",
	thumbnailUrl: "",
	publicationDate: ISODate ("2019-01-01T00:00:00.000z"),
	authors: ["Ada Lovelace"]
}
```

To replace this document with an updated version, the `replaceOne` method is used on the Book Collection as follows. 
The `_id` is provided as the filter criteria because it is guaranteed to be unique. The entire document is replaced by 
passing the replacement document as the second parameter. The program output will return a `matchedCount` 
(how many documents matched the filter) and a `modifiedCount` (how many of these documents were modified) to indicate 
the number of updated documents. In this case, both values will be 1.

```bash
db.books.replaceOne(
  {
    _id: ObjectId("6282afeb441a74a98dbbec4e")
  },
  {
    title: "Data Science Fundamentals for Python and MongoDB",
    isbn: "1484235967",
    publishedDate: new Date("2018-05-10"),
    thumbnailUrl: "https://m.media-amazon.com/images/I/71opmUBc2wL._AC_UY218_.jpg",
    authors: ["David Paper"],
    categories: ["Data Science"]
  }
)
```

To confirm the modification, the `db.books.findOne({_id: ObjectId("6282afeb441a74a98dbbec4e")})` method can be invoked. 
Running this command will allow confirmation that the document has been updated, as it will display the updated document.

## Updating MongoDB Documents by Using `updateOne()`

Next, update operators in the MongoDB Shell are discussed. The `updateOne()` method, used with the update operators 
`$set` and `$push`, is introduced. This method updates a single document and accepts three arguments: filter, update, 
and options. When updating documents, the `$set` operator can be used to add new fields and values to a document or 
replace the value of a field with a specified value, while the `$push` operator appends a value to an array. 
If the array field is absent, `$push` adds it with the value as its element. Consider managing a database called 
`audio` that contains a collection named `Podcasts`. Below is an example using the `$set` operator to replace the 
value of a field with a specified value. After running this, `matchedCount` and `modifiedCount` will again be returned.

```bash
db.podcasts.updateOne(
  {
    _id: ObjectId("5e8f8f8f8f8f8f8f8f8f8f8")
  },
  {
    $set: {
      subscribers: 98562
    }
  }
)
```

An example of using the `upsert` option, which allows creating a new document if no documents match the filter criteria, 
is provided below. Upsert stands for Update or Insert. In the following example, an attempt is made to update a 
non-existent document, but since it does not exist and `upsert` is set to true, it will be created.

```bash
db.podcasts.updateOne(
  { title: "The Developer Hub" },
  { $set: { topics: ["databases", "MongoDB"] } },
  { upsert: true }
)
```

The final example demonstrates the `$push` operator, which in the following case adds a new value to the `hosts` array field.

```bash
db.podcasts.updateOne(
  { _id: ObjectId("5e8f8f8f8f8f8f8f8f8f8f8") },
  { $push: { hosts: "Nic Raboy" } }
)
```

## Updating MongoDB Documents by Using `findAndModify()`

The `findAndModify()` method is used to return the document that has just been updated. In other words, it performs in 
a single operation what would otherwise require two operations with `updateOne()` and `findOne()`. This avoids two 
roundtrips to the server and ensures that another user does not modify the document before it is viewed, thus 
returning the correct version of the document. This powerful method ensures the correct version of the document 
is returned before another thread can modify it. Below is an example that, in addition to modifying the document, 
also returns the modified document. The `new: true` option is specified to return the modified document instead of the original.

```bash
db.podcasts.findAndModify({
  query: { _id: ObjectId("6261a92dfee1ff300dc80bf1") },
  update: { $inc: { subscribers: 1 } },
  new: true
})
```

## Updating MongoDB Documents by Using `updateMany()`

To update multiple documents, the `updateMany()` method can be used, which also accepts a filter, an update document, 
and an optional options object. The example below updates all books published before 2019 to the status `LEGACY`. 
If `matchedCount` and `modifiedCount` are the same, the update was successful. This method is not an all-or-nothing 
operation and will not roll back updates. If this occurs, `updateMany()` must be run again to update the remaining 
documents. Additionally, `updateMany()` lacks isolation: updates will be visible as soon as they are performed, 
which may not be appropriate for some business requirements.

```bash
db.books.updateMany(
  { publishedDate: { $lt: new Date("2019-01-01") } },
  { $set: { status: "LEGACY" } }
)
```

## Deleting Documents in MongoDB

To delete documents in MongoDB, the `deleteOne()` and `deleteMany()` methods can be used. Both methods accept a filter 
document and an options object, as seen previously. Below are examples showing how to delete a single document and 
multiple documents. Once executed, these methods return an `acknowledged` boolean value and an integer `deletedCount` 
value to confirm the process was successful.

```bash
# Delete a Single Document
db.podcasts.deleteOne({ _id: ObjectId("6282c9862acb966e76bbf20a") })

# Delete Multiple Documents
db.podcasts.deleteMany({ category: "crime" })
```

## Sorting and Limiting Query Results in MongoDB

### Using Cursors in MongoDB

In MongoDB, a Cursor is a pointer to the result set of a query. For instance, the `find()` method returns a cursor that 
points to the documents matching the query. There are also Cursor Methods that can be chained to queries and used to 
perform actions on the resulting set, such as sorting or limiting the search results, before returning the data to the client.

To begin with, results can be returned in a specified order using the `cursor.sort()` method, which has the following syntax:

```plaintext
db.collection.find(<query>).sort(<sort>)
```

Within the parentheses of `sort()`, an object specifying the field(s) to sort by and the order of the sort must be 
included. Use `1` for ascending order and `-1` for descending order. The following code example illustrates this by 
returning companies with a `category_code` of "music" in alphabetical order. A projection is also shown to return only the names:

```bash
# Return data on all music companies, sorted alphabetically from A to Z.
db.companies.find({ category_code: "music" }).sort({ name: 1 });

# Projection to return only names
db.companies.find({ category_code: "music" }, { name: 1 }).sort({ name: 1 });

# Return data on all music companies, sorted alphabetically from A to Z. Ensure consistent sort order.
db.companies.find({ category_code: "music" }).sort({ name: 1, _id: 1 });
```

To ensure that documents are returned in a consistent order, a field containing unique values can be included in the 
sort. A simple way to achieve this is by including the `_id` field in the sort as demonstrated above. The `sort` 
method can be applied to virtually any type of field.

### Limiting the Number of Results

Limiting the number of returned results can improve application performance by avoiding unnecessary data processing. 
The `Limit Cursor Method` achieves this by using `cursor.limit()` to specify the maximum number of documents that 
the cursor will return. The syntax is as follows:

```plaintext
db.collection.find(<query>).limit(<number>)
```

Here is an example where the three music companies with the highest number of employees are returned. A projection 
can also be added to simplify the returned document:

```bash
# Return the three music companies with the highest number of employees. Ensure consistent sort order.
db.companies.find({ category_code: "music" })
  .sort({ number_of_employees: -1, _id: 1 })
  .limit(3);

# Projection on two fields
db.companies.find({ category_code: "music" }, { name: 1, number_of_employees: 1 })
  .sort({ number_of_employees: -1, _id: 1 })
  .limit(3);
```

## Returning Specific Data From a Query in MongoDB

By default, queries in MongoDB return all fields in the matching document. However, sometimes an application may need 
to use data only from a subset of these fields. In this case, the amount of data returned by MongoDB can be limited 
by selecting specific fields to return. This process, known as projection, can be used in most find queries. The syntax is:

```plaintext
db.collection.find(<query>, <projection>)
```

To include a field, set its value to `1` in the projection document, as shown in the example below. To exclude a field, 
set its value to `0`. While the `_id` field is included by default, it can be suppressed by setting its value to `0` 
in any projection, as illustrated in the third example. Note that inclusion and exclusion cannot be combined in most 
projections, except for the `_id` field, which can be both included and excluded. Accessing a subdocument is also shown, 
ensuring that the zip code is excluded:

```bash
# Return all restaurant inspections - business name, result, and _id fields only
db.inspections.find(
  { sector: "Restaurant - 818" },
  { business_name: 1, result: 1 }  # This is the projection document
)

# Return all inspections with result of "Pass" or "Warning" - exclude date and zip code
db.inspections.find(
  { result: { $in: ["Pass", "Warning"] } },
  { date: 0, "address.zip": 0 }  # This is the projection document
)

# Return all restaurant inspections - business name and result fields only
db.inspections.find(
  { sector: "Restaurant - 818" },
  { business_name: 1, result: 1, _id: 0 }  # This is the projection document
)
```

## Counting Documents in a MongoDB Collection

The `db.collection.countDocuments()` method can be used to count the number of documents matching a query. This method
takes two parameters: a query document and an options document. The syntax is:

```plaintext
db.collection.countDocuments(<query>, <options>)
```

Here are some code examples:

```bash
# Count number of documents in trip collection
db.trips.countDocuments({})

# Count number of trips over 120 minutes by subscribers
db.trips.countDocuments({ tripduration: { $gt: 120 }, usertype: "Subscriber" })
```

# Working with MongoDB Documents in Java

## BSON Format in MongoDB

Binary JSON, or BSON, is the data format that MongoDB uses to organize and store data. BSON is optimized for storage, 
retrieval, and transmission across the wire. Additionally, it is more secure than plain text JSON and supports more 
data types. The MongoDB Java Driver provides several classes for representing BSON documents, with the `Document` 
class being recommended due to its flexible and concise data representation. MongoDB provides a BSON interface for 
types that can render themselves into a BSON Document, and the `Document` class implements this interface.

Here is an example of a BSON document, which includes the usual `_id` field serving as the Primary Key and a subdocument 
represented by the `address` field. The `date` field is represented as a String, though it is advisable to use a 
specific BSON type for dates. Summarizing the aforementioned points, one way to represent BSON documents is by using 
the `Document` class. The `Document` class offers a flexible representation of a BSON document.

```json
{
	"_id": { "$oid": "56d61033a378eccde8a8354f" },
	"business_id": "10021-2015-ENFO",
	"certificate_number": 9278806,
	"business_name": "ATLIXCO DELI GROCERY INC.",
	"date": "Feb 20 2015",
	"result": "No Violation Issued",
	"sector": "Cigarette Retail Dealer - 127",
	"address": {
		"city": "RIDGEWOOD",
		"zip": 11385,
		"street": "MENAHAN ST",
		"number": 1712
	}
}
```

To instantiate this document in Java, use the following syntax. This example demonstrates instantiating a new document 
and setting its Primary Key, which in this case is `_id`. Subsequently, the corresponding fields and values are appended,
such as `Date` for the date. The document is then ready to be sent to the MongoDB Server.

```java
Document inspection = new Document("_id", new ObjectId())
	.append("business_id", "10021-2015-ENFO")
	.append("certificate_number", 9278886)
	.append("business_name", "ATLIXCQ DELI GROCERY INC.")
	.append("date", Date.from(LocalDate.of(2015, 2, 20).atStartOfDay(ZoneId.systemDefault()).toInstant()))
	.append("result", "No Violation Issued")
	.append("sector", "Cigarette Retail Dealer - 127")
	.append("address", new Document().append("city", "RIDGEWOOD").append("zip", 11385).append("street", "MENAHAN ST").append("number", 1712));
```

## Inserting a Document in Java Applications

To insert a single document into a collection, use the `getCollection()` method to access the `MongoCollection` object, 
which represents the specified collection. Then, append the `insertOne()` method to the collection object. Within the
parentheses of `insertOne()`, include an object that contains the document data and print the inserted document’s ID,
as shown in the following example, which also contains a subdocument in the `address` field.

```java
MongoDatabase database = mongoClient.getDatabase("sample_training");
MongoCollection<Document> collection = database.getCollection("inspections");

Document inspection = new Document("_id", new ObjectId())
        .append("id", "10021-2015-ENFO")
        .append("certificate_number", 9278806)
        .append("business_name", "ATLIXCO DELI GROCERY INC.")
        .append("date", Date.from(LocalDate.of(2015, 2, 20).atStartOfDay(ZoneId.systemDefault()).toInstant()))
        .append("result", "No Violation Issued")
        .append("sector", "Cigarette Retail Dealer - 127")
        .append("address", new Document().append("city", "RIDGEWOOD").append("zip", 11385).append("street", "MENAHAN ST").append("number", 1712));

InsertOneResult result = collection.insertOne(inspection);
BsonValue id = result.getInsertedId();
System.out.println(id);
```

Similarly, to insert multiple documents into a collection, append the `insertMany()` method to the collection object. 
Within the parentheses of `insertMany()`, include an object that contains the document data and print out the IDs of
the inserted documents. The following example clarifies this process.

```java
MongoDatabase database = mongoClient.getDatabase("bank");
MongoCollection<Document> collection = database.getCollection("accounts");

Document doc1 = new Document().append("account_holder", "john doe").append("account_id", "MDB99115881").append("balance", 1785).append("account_type", "checking");
Document doc2 = new Document().append("account_holder", "jane doe").append("account_id", "MDB79101843").append("balance", 1468).append("account_type", "checking");

List<Document> accounts = Arrays.asList(doc1, doc2);
InsertManyResult result = collection.insertMany(accounts);
result.getInsertedIds().forEach((x, y) -> System.out.println(y.asObjectId()));
```

Custom methods can be created to simplify these functions, as demonstrated in the following examples:

```java
// Example Methods
public void insertOneDocument(Document doc) {
  System.out.println("Inserting one account document");
  InsertOneResult result = collection.insertOne(doc);
  BsonValue id = result.getInsertedId();
  System.out.println("Inserted document Id: " + id);
}

public void insertManyDocuments(List<Document> documents) {
  InsertManyResult result = collection.insertMany(documents);
  System.out.println("\tTotal # of documents: " + result.getInsertedIds().size());
}
```

## Querying a MongoDB Collection in Java Applications

The `find()` method can be used to search for specific conditions. For example, in the following code, `find()` is used
to locate all checking accounts with a balance of at least 1000. Each document returned by the `find()` method is 
processed by iterating the `MongoCursor` using a try block and a while loop. The `find()` method accepts a query 
filter and returns documents that match the filters in the collection.

```java
MongoDatabase database = mongoClient.getDatabase("bank");
MongoCollection<Document> collection = database.getCollection("accounts");
try(MongoCursor<Document> cursor = collection.find(and(gte("balance", 1000), eq("account_type", "checking"))).iterator()) {
    while(cursor.hasNext()) {
        System.out.println(cursor.next().toJson());
    }
}
```

The `find()` and `first()` methods can be concatenated to find and return only the first document that matches the 
query filter given to the `find()` method. For example, the following code returns a single document from the same 
query. It is important to remember that all queries on MongoDB should use a Query Filter to optimize the use of database 
resources. The Java `Filters` builder class helps define more efficient queries by using query predicates.

```java
MongoDatabase database = mongoClient.getDatabase("bank");
MongoCollection<Document> collection = database.getCollection("accounts");
Document doc = collection.find(Filters.and(gte("balance", 1000), Filters.eq("account_type", "checking"))).first();
System.out.println(doc.toJson());
```

Again, useful custom methods can be built to perform the same functions but be invoked more easily:

```java
// Example Methods
public void findOneDocument(Bson query) {
  Document doc = collection.find(query).first();
  System.out.println(doc != null ? doc.toJson() : null);
}

public void findDocuments(Bson query) {
  try (MongoCursor<Document> cursor = collection.find(query).iterator()) {
    while (cursor.hasNext()) {
      System.out.println(cursor.next().toJson());
    }
  }
}
```

## Updating Documents in Java Applications

### Updating a Single Document

To update a single document, use the `updateOne()` method on a `MongoCollection` object. This method accepts a filter 
that matches the document to be updated and an update statement that instructs the driver on how to modify the matching 
document. The `updateOne()` method updates only the first document that matches the filter.

In the following example, one document is updated by increasing the balance of a specific account by 100 and setting 
the account status to active:

```java
MongoDatabase database = mongoClient.getDatabase("bank");
MongoCollection<Document> collection = database.getCollection("accounts");
Bson query  = Filters.eq("account_id", "MDB12234728");
Bson updates  = Updates.combine(Updates.set("account_status", "active"), Updates.inc("balance", 100));
UpdateResult upResult = collection.updateOne(query, updates);
```

### Updating Multiple Documents

To update multiple documents, use the `updateMany()` method on a `MongoCollection` object. This method also accepts a 
filter to match the documents that need to be updated, along with an update statement. The `updateMany()` method 
updates all documents that match the filter.

In the following example, the minimum balance of all savings accounts is increased to 100:

```java
MongoDatabase database = mongoClient.getDatabase("bank");
MongoCollection<Document> collection = database.getCollection("accounts");
Bson query  = Filters.eq("account_type", "savings");
Bson updates  = Updates.combine(Updates.set("minimum_balance", 100));
UpdateResult upResult = collection.updateMany(query, updates);
```

### Creating Utility Methods

Utility methods can be created and called as shown below:

```java
// Example of Methods and Usage #1
public class Crud {
    private final MongoCollection<Document> collection;

    public Crud(MongoClient client) {
        this.collection = client.getDatabase("bank").getCollection("accounts");
    }

    public void updateOneDocument(Bson query, Bson update) {
        UpdateResult updateResult = collection.updateOne(query, update);
        System.out.println("Updated a document:");
        System.out.println("\t" + updateResult.getModifiedCount());
    }
}

Bson query = Filters.eq("account_id", "MDB333829449");
Bson update = Updates.combine(Updates.set("account_status", "active"), Updates.inc("balance", 100));
crud.updateOneDocument(query, update);

// Example of Methods and Usage #2
public class Crud {
    private final MongoCollection<Document> collection;

    public Crud(MongoClient client) {
        this.collection = client.getDatabase("bank").getCollection("accounts");
    }

    public void updateManyDocuments(Document query, Bson update) {
        UpdateResult updateResult = collection.updateMany(query, update);
        System.out.println("Updated this many documents:");
        System.out.println("\t" + updateResult.getModifiedCount());
    }
}
```

## Deleting Documents in Java Applications

### Deleting a Single Document

To delete a single document from a collection, use the `deleteOne()` method on a `MongoCollection` object. This method 
accepts a query filter that matches the document to be deleted. If no filter is specified, MongoDB matches the first 
document in the collection. The `deleteOne()` method deletes only the first document that matches.

In the following example, a single document related to John Doe's account is deleted. Assume that instances of 
`MongoClient` and `MongoCollection` have already been instantiated:

```java
MongoDatabase database = mongoClient.getDatabase("bank");
MongoCollection<Document> collection = database.getCollection("accounts");
Bson query = Filters.eq("account_holder", "john doe");
DeleteResult delResult = collection.deleteOne(query);
System.out.println("Deleted a document:");
System.out.println("\t" + delResult.getDeletedCount());
```

### Deleting Multiple Documents

To delete multiple documents in a single operation, use the `deleteMany()` method on a `MongoCollection` object. 
Specify the documents to be deleted with a query filter. If an empty document is provided, MongoDB matches all 
documents in the collection and deletes them.

In the following example, all dormant accounts are deleted using a query object, and the total number of deleted 
documents is printed:

```java
MongoDatabase database = mongoClient.getDatabase("bank");
MongoCollection<Document> collection = database.getCollection("accounts");
Bson query = Filters.eq("account_status", "dormant");
DeleteResult delResult = collection.deleteMany(query);
System.out.println(delResult.getDeletedCount());
```

### Creating Utility Methods

Utility methods for deletion can also be created and called as shown below:

```java
// Example of Methods and Usage #1
public class Crud {
    private final MongoCollection<Document> collection;

    public Crud(MongoClient client) {
        this.collection = client.getDatabase("bank").getCollection("accounts");
    }

    public void deleteOneDocument(Bson query) {
        DeleteResult delResult = collection.deleteOne(query);
        System.out.println("Deleted a document:");
        System.out.println("\t" + delResult.getDeletedCount());
    }    
}

// Example of Methods and Usage #2
public class Crud {
    private final MongoCollection<Document> collection;

    public Crud(MongoClient client) {
        this.collection = client.getDatabase("bank").getCollection("accounts");
    }

    public void deleteManyDocuments(Bson query) {
        DeleteResult delResult = collection.deleteMany(query);
        System.out.println("Deleted this many documents:");
        System.out.println("\t" + delResult.getDeletedCount());
    }
}
```

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