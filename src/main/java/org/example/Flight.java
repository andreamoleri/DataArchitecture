package org.example;

import org.bson.Document;

import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.List;

public class Flight {
    private String id;
    private int numberOfSeats;
    private String day;
    private String hour;
    private String operator;
    private String duration;
    private int pricePerPerson;
    private List<Seat> seats;

    public Flight(String id, int numberOfSeats, String day, String hour, String operator, String duration,
                  int pricePerPerson, List<Seat> seats) {
        this.id = id;
        this.numberOfSeats = numberOfSeats;
        this.day = day;
        this.hour = hour;
        this.operator = operator;
        this.duration = duration;
        this.pricePerPerson = pricePerPerson;
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id='" + id + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                ", day='" + day + '\'' +
                ", hour='" + hour + '\'' +
                ", operator='" + operator + '\'' +
                ", duration='" + duration + '\'' +
                ", pricePerPerson=" + pricePerPerson +
                ", seats=" + seats +
                '}';
    }

    public Document getDocument() {
        Document document = new Document("id", this.id)
                .append("numberOfSeats", this.numberOfSeats)
                .append("day", this.day)
                .append("hour", this.hour)
                .append("operator", this.operator)
                .append("duration", this.duration)
                .append("pricePerPerson", this.pricePerPerson);

        List<Document> listSeats = new ArrayList<>();

        for(Seat seat: seats) {
            listSeats.add(seat.getDocument());
        }

        document.append("seats", listSeats);

        return document;
    }
}