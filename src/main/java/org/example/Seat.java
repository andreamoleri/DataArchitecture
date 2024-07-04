package org.example;

import org.bson.Document;

import javax.print.Doc;

public class Seat {
    private String status;
    private String id;
    private String name;
    private String surname;
    private String documentInfo;
    private String dateOfBirth;
    private int balance;

    public Seat(String status, String id, String name, String surname, String documentInfo, String dateOfBirth, int balance) {
        this.status = status;
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.documentInfo = documentInfo;
        this.dateOfBirth = dateOfBirth;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "status='" + status + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", documentInfo='" + documentInfo + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", balance=" + balance +
                '}';
    }

    public Document getDocument() {
        return new Document("status", this.status)
                .append("id", this.id)
                .append("name", this.name)
                .append("surname", this.surname)
                .append("documentInfo", this.documentInfo)
                .append("dateOfBirth", this.dateOfBirth)
                .append("balance", this.balance);
    }
}