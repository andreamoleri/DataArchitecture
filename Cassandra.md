# Cassandra

Apache Cassandra is a distributed and open-source NoSQL database designed to handle large volumes of data distributed across many servers. Originally created by Facebook to improve their ability to manage massive amounts of data, Cassandra has become one of the most widely used databases for applications that require high availability, scalability, and performance.

## Architecture

The Key Components in Cassandra are:
- node: any system running Cassandra, such as a physical host, cloud instance, or Docker container.
- rack: a group of nearby nodes, either on the same network switch or in the same cloud availability zone.
- data center: a collection of racks connected by a reliable network, corresponding to a physical building or a cloud region (e.g., AWS's us-west-1).

To organize components at a logical level, the concept of a ring is often used. The ring consists of nodes belonging to the same data center, and each node is assigned a token value range. Typically, two adjacent nodes along the ring do not belong to the same rack. The token value range assigned to each node is designed so that the right/left endpoint of a range is adjacent to the endpoint of the range of the adjacent node to the right/left.

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

