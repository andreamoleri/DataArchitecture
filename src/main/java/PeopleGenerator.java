import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PeopleGenerator {

    private static final Random RANDOM = new Random();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    // Metodo per generare una lista di persone
    public List<Person> generatePeople(int count) {
        List<Person> people = new ArrayList<>(count);
        Set<String> documentNumbers = new HashSet<>(count);

        for (int i = 0; i < count; i++) {
            String name = NameSurnamePool.NAMES.get(RANDOM.nextInt(NameSurnamePool.NAMES.size()));
            String surname = NameSurnamePool.SURNAMES.get(RANDOM.nextInt(NameSurnamePool.SURNAMES.size()));
            String documentInfo;

            // Genera un numero di documento univoco
            do {
                documentInfo = generateDocumentInfo();
            } while (documentNumbers.contains(documentInfo));

            documentNumbers.add(documentInfo);
            String dateOfBirth = generateDateOfBirth();
            double balance = 100 + (100000 - 100) * RANDOM.nextDouble();

            // Arrotonda il saldo a due decimali
            balance = Double.parseDouble(DECIMAL_FORMAT.format(balance));

            people.add(new Person(name, surname, documentInfo, dateOfBirth, balance));
        }

        return people;
    }

    // Metodo per generare una sequenza alfanumerica di 7 cifre
    private String generateDocumentInfo() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(7);
        for (int i = 0; i < 7; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Metodo per generare una data di nascita casuale tra 18 e 100 anni fa
    private String generateDateOfBirth() {
        long now = System.currentTimeMillis();
        long eighteenYearsAgo = now - (18L * 365 * 24 * 60 * 60 * 1000);
        long hundredYearsAgo = now - (100L * 365 * 24 * 60 * 60 * 1000);
        long randomMillis = ThreadLocalRandom.current().nextLong(hundredYearsAgo, eighteenYearsAgo);
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(randomMillis));
    }

    // Metodo per generare una persona "povera" con saldo limitato
    public Person generatePoorPerson() {
        String name = NameSurnamePool.NAMES.get(RANDOM.nextInt(NameSurnamePool.NAMES.size()));
        String surname = NameSurnamePool.SURNAMES.get(RANDOM.nextInt(NameSurnamePool.SURNAMES.size()));
        String documentInfo = generateDocumentInfo();
        String dateOfBirth = generateDateOfBirth();
        double balance = 10.0; // Saldo limitato a 10$

        return new Person(name, surname, documentInfo, dateOfBirth, balance);
    }

    // Classe interna per rappresentare una persona
    public static class Person {
        private String name;
        private String surname;
        private String documentInfo;
        private String dateOfBirth;
        private double balance;
        private double oldBalance; // Aggiunto per gestire il bilancio precedente
        private double difference; // Aggiunto per gestire la differenza di saldo

        public Person(String name, String surname, String documentInfo, String dateOfBirth, double balance) {
            this.name = name;
            this.surname = surname;
            this.documentInfo = documentInfo;
            this.dateOfBirth = dateOfBirth;
            this.balance = balance;
            this.oldBalance = balance; // Inizialmente il bilancio precedente è uguale al bilancio attuale
            this.difference = 0.0; // Inizialmente non c'è differenza di saldo
        }

        // Getters e setters...
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getDocumentInfo() {
            return documentInfo;
        }

        public void setDocumentInfo(String documentInfo) {
            this.documentInfo = documentInfo;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public double getOldBalance() {
            return oldBalance;
        }

        public void setOldBalance(double oldBalance) {
            this.oldBalance = oldBalance;
        }

        public double getDifference() {
            return difference;
        }

        public void setDifference(double difference) {
            this.difference = difference;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", surname='" + surname + '\'' +
                    ", documentInfo='" + documentInfo + '\'' +
                    ", dateOfBirth='" + dateOfBirth + '\'' +
                    ", balance=" + balance +
                    '}';
        }
    }

    // Metodo principale per testare la generazione delle persone
    public static void main(String[] args) {
        PeopleGenerator generator = new PeopleGenerator();
        List<Person> people = generator.generatePeople(10000);

        // Stampa le prime 10 persone per verificare l'output
        for (int i = 0; i < 10; i++) {
            System.out.println(people.get(i));
        }
    }
}