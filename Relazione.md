# Titolo Bello
## Introduzione
Spieghiamo di cosa tratteremo durante la relazione e principali differenze tra db relazionale e non relazionale.
"in questa relazione analizzeremo MongoDB e Cassandra, due db non relazionali, evidenziandone caratteristiche e differenze aiutandoci con un esempio da noi ideato"

## Architettura
### Architettura di MongoDB
Non mi ricordo se nel file README c'è già qualcosa

Questo è quello che è uscito chiedendo a ChatGPT dell'architettura di Mongo, ovviamente da espandere e dettagliare:

```
MongoDB è un database NoSQL che utilizza un'architettura distribuita e scalabile, progettata per gestire grandi volumi di dati. Ecco i principali componenti dell'architettura di un database MongoDB:

1. **Database**: MongoDB organizza i dati in database, che possono contenere più collezioni di documenti. Un database è un'unità di archiviazione logica con i suoi set di dati.

2. **Collection**: Una collezione in MongoDB è un gruppo di documenti. È equivalente concettualmente a una tabella in un database relazionale, ma non richiede uno schema rigido. Le collezioni possono contenere documenti con strutture diverse.

3. **Documento**: Un documento è l'unità di base dei dati in MongoDB, simile a un record o una riga in un database relazionale. I documenti sono rappresentati in BSON (Binary JSON), che è un formato binario esteso del JSON.

4. **Cluster**: MongoDB può essere configurato come un cluster, che è un insieme di nodi che lavorano insieme per gestire i dati e garantire la disponibilità e l'affidabilità del sistema. I cluster MongoDB possono includere diversi tipi di nodi:

    - **Nodi di dati (Data Nodes)**: Nodi che archiviano i dati effettivi.
    - **Nodi di query (Query Nodes)**: Nodi che accettano le query dagli utenti e le inoltrano ai nodi di dati.
    - **Nodi di configurazione (Config Nodes)**: Nodi che mantengono la configurazione e il metadata del cluster.
    - **Nodi di routing (Router Nodes)**: Nodi che dirigono le richieste dei client agli altri nodi del cluster.

5. **Sharding**: MongoDB supporta lo sharding, una tecnica per distribuire i dati su più macchine. Consiste nel partizionare i dati e distribuirli tra più nodi di un cluster (chiamati shard). Lo sharding consente di scalare orizzontalmente il database MongoDB per gestire volumi di dati molto grandi e carichi di lavoro intensi.

6. **Replica set**: Un replica set è un gruppo di nodi MongoDB che contengono lo stesso set di dati. Fornisce ridondanza e alta disponibilità, consentendo al database di sopravvivere a guasti di nodi o interruzioni del servizio.

In sintesi, l'architettura di MongoDB è progettata per essere flessibile, scalabile e affidabile, permettendo di gestire una vasta gamma di applicazioni e carichi di lavoro dai più semplici ai più complessi.
```

### Architettura Cassandra
Me ne occupo io

## Sintassi Linguaggi
### Sintassi di MongoDB

Principali comandi tipo Inserimento documenti, query, delete (solo linguaggio di Mongo, niente parti di Java)

### Sintassi di Cassandra

Me ne occupo io

## Modello dati

Presentazione del nostro caso di studio (aereoporto), citiamo la fonte dei dati, come li abbiamo strutturati e il processo di generazione di quelli mancanti
Anche le assunzioni varie (tipo no login)

### Modello dati MongoDB

Come a partire dal caso di studio e dei dati a tua disposizione hai creato il modello dati

### Modello dati Cassandra

Me ne occupo io

## Transazioni
Spiegazione su cosa è una transazione e perchè è importante per il nostro caso di studio (semplicemnte non posso avere due persone sullo stesso posto)

### Transazioni in Mongo
spieghi come hai modellato le transazioni e come lato backend vengono gestite (quindi come le gestisce il db)

### Transazioni in Cassandra

Me ne occupo ancora io

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
