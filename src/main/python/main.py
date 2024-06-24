from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
from cassandra import ConsistencyLevel
import ssl
import time

from cassandra.query import SimpleStatement

import elaboraredati

# Leggi le credenziali AWS dalle variabili d'ambiente
aws_access_key_id = "architetture-dati-at-058264487248"
aws_secret_access_key = "nB3V9FiumaBMMiDlw1kuyvPSc05cQiDWvMJB3Nwi/aL/AeUtFtmnEREWCjE="

# Configura le credenziali di autenticazione
auth_provider = PlainTextAuthProvider(username=aws_access_key_id, password=aws_secret_access_key)

# Configura SSL
ssl_context = ssl.create_default_context()
ssl_context.check_hostname = False
ssl_context.verify_mode = ssl.CERT_NONE  # Disabilita la verifica del certificato per semplicità (non consigliato in produzione)

# Imposta l'endpoint di Amazon Keyspaces
keyspaces_endpoint = 'cassandra.eu-north-1.amazonaws.com'

# Configura il cluster con SSL e autenticazione
cluster = Cluster([keyspaces_endpoint], port=9142, auth_provider=auth_provider, ssl_context=ssl_context)

# Crea la sessione
session = cluster.connect()

# Esegui una query di prova
result = session.execute("SELECT release_version FROM system.local")
for row in result:
    print("Cassandra version:", row.release_version)

session.execute("USE my_airport;")

query = SimpleStatement("CREATE TABLE IF NOT EXISTS airport (ID text, Geo_Point text, Name text, Name_en text, Name_fr text, IATA_code text, ICAO_code text, Operator text, Country text, Country_code text, Size int, PRIMARY KEY (ID));")
session.execute(query)

query = SimpleStatement("CREATE TABLE IF NOT EXISTS flight (ID text, Number_of_Seats int, Day date, Hour time, Operator text, Duration text, Price_per_Person int, Destination text, Departure text, PRIMARY KEY ((Destination, Departure, Day), Hour));")
session.execute(query)

query = SimpleStatement("CREATE TABLE IF NOT EXISTS seat (Flight text, Status text, ID text, Name text, Surname text, Document_Info text, Date_of_Birth date, Balance int, PRIMARY KEY ((ID, Flight)));")
session.execute(query)

time.sleep(60)

lista1 = elaboraredati.get_airport()

print("1")
for l1 in lista1:
    query = SimpleStatement("INSERT INTO airport (ID, Geo_Point, Name, Name_en, Name_fr, IATA_code, ICAO_code, Operator, Country, Country_code, Size) VALUES " + str(l1) +";", consistency_level=ConsistencyLevel.LOCAL_QUORUM)
    session.execute(query)

lista2 = elaboraredati.get_lista_voli()

print("2")
for l2 in lista2:
    query = SimpleStatement("INSERT INTO flight (ID, Departure, Destination, Number_of_Seats, Day, Hour, Operator, Duration, Price_per_Person) VALUES " + str(l2) + ";", consistency_level=ConsistencyLevel.LOCAL_QUORUM)
    session.execute(query)

lista3 = elaboraredati.get_lista_posti()

print("3")
for l3 in lista3:
    query = SimpleStatement("INSERT INTO seat (Flight, ID, Status, Name, Surname, Document_Info, Date_of_Birth, Balance) VALUES " + str(l3) + ";", consistency_level=ConsistencyLevel.LOCAL_QUORUM)
    session.execute(query)

cluster.shutdown()
