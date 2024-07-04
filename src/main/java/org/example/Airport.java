package org.example;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Airport {
    private String geoPoint;
    private String name;
    private String nameEn;
    private String nameFr;
    private String iataCode;
    private String icaoCode;
    private String operator;
    private String country;
    private String countryCode;
    private int size;
    private List<Flight> flights;

    public Airport(String geoPoint, String name, String nameEn, String nameFr, String iataCode, String icaoCode,
                   String operator, String country, String countryCode, int size, List<Flight> flights) {
        this.geoPoint = geoPoint;
        this.name = name;
        this.nameEn = nameEn;
        this.nameFr = nameFr;
        this.iataCode = iataCode;
        this.icaoCode = icaoCode;
        this.operator = operator;
        this.country = country;
        this.countryCode = countryCode;
        this.size = size;
        this.flights = flights;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "geoPoint='" + geoPoint + '\'' +
                ", name='" + name + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", nameFr='" + nameFr + '\'' +
                ", iataCode='" + iataCode + '\'' +
                ", icaoCode='" + icaoCode + '\'' +
                ", operator='" + operator + '\'' +
                ", country='" + country + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", size=" + size +
                ", flights=" + flights +
                '}';
    }

    public Document toDocument() {
        Document document = new Document("geoPoint", this.geoPoint)
                .append("name", this.name)
                .append("nameEn", this.nameEn)
                .append("nameFr", this.nameFr)
                .append("iataCode", this.iataCode)
                .append("icaoCode", this.icaoCode)
                .append("operator", this.operator)
                .append("country", this.country)
                .append("countryCode", this.countryCode)
                .append("size", this.size);

        List<Document> flightDocuments = new ArrayList<>();
        for (Flight flight : this.flights) {
            flightDocuments.add(flight.getDocument());
        }

        document.append("flights", flightDocuments);

        return document;
    }
}







