import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The PeopleGenerator class is responsible for generating a list of Person objects with
 * random attributes, including name, surname, document information, date of birth, and balance.
 * It can also generate a "poor" person with a limited balance.
 *
 * @version 1.0
 * @since 2024-07-02
 * @author Andrea Moleri
 */
public class PeopleGenerator {

    private static final Random RANDOM = new Random();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    /**
     * Generates a list of Person objects with specified count.
     *
     * @param count The number of Person objects to generate.
     * @return A list of generated Person objects.
     */
    public List<Person> generatePeople(int count) {
        List<Person> people = new ArrayList<>(count);
        Set<String> documentNumbers = new HashSet<>(count);

        for (int i = 0; i < count; i++) {
            String name = NameSurnamePool.NAMES.get(RANDOM.nextInt(NameSurnamePool.NAMES.size()));
            String surname = NameSurnamePool.SURNAMES.get(RANDOM.nextInt(NameSurnamePool.SURNAMES.size()));
            String documentInfo;

            // Generate a unique document number
            do {
                documentInfo = generateDocumentInfo();
            } while (documentNumbers.contains(documentInfo));

            documentNumbers.add(documentInfo);
            String dateOfBirth = generateDateOfBirth();
            double balance = 100 + (100000 - 100) * RANDOM.nextDouble();

            // Round the balance to two decimal places
            balance = Double.parseDouble(DECIMAL_FORMAT.format(balance));

            people.add(new Person(name, surname, documentInfo, dateOfBirth, balance));
        }

        return people;
    }

    /**
     * Generates a 7-character alphanumeric document information string.
     *
     * @return A randomly generated document information string.
     */
    private String generateDocumentInfo() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(7);
        for (int i = 0; i < 7; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Generates a random date of birth between 18 and 100 years ago.
     *
     * @return A randomly generated date of birth in "yyyy-MM-dd" format.
     */
    private String generateDateOfBirth() {
        long now = System.currentTimeMillis();
        long eighteenYearsAgo = now - (18L * 365 * 24 * 60 * 60 * 1000);
        long hundredYearsAgo = now - (100L * 365 * 24 * 60 * 60 * 1000);
        long randomMillis = ThreadLocalRandom.current().nextLong(hundredYearsAgo, eighteenYearsAgo);
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(randomMillis));
    }

    /**
     * Generates a Person object with limited balance, representing a "poor" person.
     *
     * @return A generated "poor" Person object.
     */
    public Person generatePoorPerson() {
        String name = NameSurnamePool.NAMES.get(RANDOM.nextInt(NameSurnamePool.NAMES.size()));
        String surname = NameSurnamePool.SURNAMES.get(RANDOM.nextInt(NameSurnamePool.SURNAMES.size()));
        String documentInfo = generateDocumentInfo();
        String dateOfBirth = generateDateOfBirth();
        double balance = 10.0; // Balance limited to 10$

        return new Person(name, surname, documentInfo, dateOfBirth, balance);
    }

    /**
     * The Person class represents an individual with attributes such as name, surname, document information,
     * date of birth, and balance. It also tracks the previous balance and balance difference for transactions.
     */
    public static class Person {
        private String name;
        private String surname;
        private String documentInfo;
        private String dateOfBirth;
        private double balance;
        private double oldBalance; // Added to manage the previous balance
        private double difference; // Added to manage balance difference

        /**
         * Constructs a new Person instance.
         *
         * @param name The name of the person.
         * @param surname The surname of the person.
         * @param documentInfo The document information of the person.
         * @param dateOfBirth The date of birth of the person.
         * @param balance The balance of the person.
         */
        public Person(String name, String surname, String documentInfo, String dateOfBirth, double balance) {
            this.name = name;
            this.surname = surname;
            this.documentInfo = documentInfo;
            this.dateOfBirth = dateOfBirth;
            this.balance = balance;
            this.oldBalance = balance; // Initially, the previous balance is equal to the current balance
            this.difference = 0.0; // Initially, there is no balance difference
        }

        // Getters and setters...

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

    /**
     * The main method to test the generation of Person objects.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        PeopleGenerator generator = new PeopleGenerator();
        List<Person> people = generator.generatePeople(10000);

        // Print the first 10 people to verify the output
        for (int i = 0; i < 10; i++) {
            System.out.println(people.get(i));
        }
    }
}
