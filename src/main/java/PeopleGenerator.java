import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PeopleGenerator {

    private static final Random RANDOM = new Random();

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

    // Classe interna per rappresentare una persona
    public class Person {
        private String name;
        private String surname;
        private String documentInfo;
        private String dateOfBirth;
        private double balance;

        public Person(String name, String surname, String documentInfo, String dateOfBirth, double balance) {
            this.name = name;
            this.surname = surname;
            this.documentInfo = documentInfo;
            this.dateOfBirth = dateOfBirth;
            this.balance = balance;
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
        for (int i = 0; i < 100; i++) {
            System.out.println(people.get(i));
        }
    }
}
