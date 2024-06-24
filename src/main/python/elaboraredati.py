import json

file_path = "Data/Data Modeling Exports/Airports.airportCollection.json"



def get_airport():
    lista_aeroporti = []

    with open(file_path, 'r', encoding='utf-8') as file:
        data = json.load(file)

        for i in range(len(data)):
            try:
                ide = str(data[i]["_id"]["$oid"]).replace("\'", "")
            except KeyError:
                ide = ""

            try:
                geo = str(data[i]["Geo_Point"]).replace("\'", "")
            except KeyError:
                geo = ""

            try:
                name = str(data[i]["Name"]).replace("\'", "")
            except KeyError:
                name = ""

            try:
                nameen = str(data[i]["Name_(en)"]).replace("\'", "")
            except KeyError:
                nameen = ""

            try:
                namefr = str(data[i]["Name_(fr)"]).replace("\'", "")
            except KeyError:
                namefr = ""

            try:
                iata = str(data[i]["IATA_code"]).replace("\'", "")
            except KeyError:
                iata = ""

            try:
                icao = str(data[i]["ICAO_code"]).replace("\'", "")
            except KeyError:
                icao = ""

            try:
                operator = str(data[i]["Operator"]).replace("\'", "")
            except KeyError:
                operator = ""

            try:
                country = str(data[i]["Country"]).replace("\'", "")
            except KeyError:
                country = ""

            try:
                countryc = str(data[i]["Country_code"]).replace("\'", "")
            except KeyError:
                countryc = ""

            try:
                size = data[i]["Size"]
            except KeyError:
                size = "0"

            lista_aeroporti.append((ide, geo, name, nameen, namefr, iata, icao, operator, country, countryc, size))

    return lista_aeroporti


def get_lista_voli():
    lista_voli = []

    with open(file_path, 'r', encoding='utf-8') as file:
        data = json.load(file)

        for i in range(len(data)):
            for j in range(len(data[i]["Flights"])):
                code_flight = data[i]['Flights'][j]["ID"]
                destination = data[i]['Flights'][j]["Destination"]['$oid']
                departure = data[i]['_id']['$oid']

                lista_voli.append((code_flight, departure, destination,
                                   data[i]['Flights'][j]["Number_of_Seats"],
                                   data[i]['Flights'][j]["Day"],
                                   str(str(data[i]['Flights'][j]["Hour"])+":00"),
                                   data[i]['Flights'][j]["Operator"],
                                   data[i]['Flights'][j]["Duration"],
                                   data[i]['Flights'][j]["Price_per_Person"]))

    return lista_voli


def get_lista_posti():
    lista_posti = []

    with open(file_path, 'r', encoding='utf-8') as file:
        data = json.load(file)

        for i in range(len(data)):
            for j in range(len(data[i]["Flights"])):
                code_flight = data[i]['Flights'][j]["ID"]

                for k in range(len(data[i]["Flights"][j]["Seats"])):
                    if not str(data[i]["Flights"][j]["Seats"][k]["Date_of_Birth"]):
                        dataB = "2000-01-01"

                    row = (code_flight, data[i]["Flights"][j]["Seats"][k]["ID"],
                           data[i]["Flights"][j]["Seats"][k]["Status"],
                           data[i]["Flights"][j]["Seats"][k]["Name"],
                           data[i]["Flights"][j]["Seats"][k]["Surname"],
                           data[i]["Flights"][j]["Seats"][k]["Document_Info"],
                           dataB,
                           data[i]["Flights"][j]["Seats"][k]["Balance"])

                    lista_posti.append(row)

    return lista_posti
