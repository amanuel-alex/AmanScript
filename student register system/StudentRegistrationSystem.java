// package for array lst
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
// package for user input
import java.util.Scanner;

public class StudentRegistrationSystem {

    public static ArrayList<Student> students = new ArrayList<>();
    private static final String DATA_FILE = "students_data.txt";

    public static void main(String[] args) {

        // creating object

        Scanner scanner = new Scanner(System.in);
        loadStudentsFromFile();

        boolean isRunning = true;
        while (isRunning) {
            System.out.println("Welcome to the HARAMAYA UNIVERSITY Student Registration System!");
            System.out.println("Please select an option:");
            System.out.println("1. Register a new student or senior student");
            System.out.println("2. View all registered students");
            System.out.println("3. Search student by ID");
            System.out.println("4. Update student by ID");
            System.out.println("5. Remove student by ID");
            System.out.println("6. Show registration summary");
            System.out.println("7. Exit");

            int choice = readInt(scanner, "Enter menu choice (1-7): ");

            switch (choice) {
                case 1:
                    registerStudent(scanner);
                    break;
                case 2:
                    viewRegisteredStudents();
                    break;
                case 3:
                    searchStudentById(scanner);
                    break;
                case 4:
                    updateStudentById(scanner);
                    break;
                case 5:
                    removeStudentById(scanner);
                    break;
                case 6:
                    showRegistrationSummary();
                    break;
                case 7:
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        }

        saveStudentsToFile();
        scanner.close();
    }

    private static void registerStudent(Scanner scanner) {
        try {
            String firstName = readValidatedName(scanner, "Enter the student's first name: ");
            String lastName = readValidatedName(scanner, "Enter the student's last name: ");
            String motherName = readValidatedName(scanner, "Enter the student's mother name: ");
            String gender = readGender(scanner);
            String nationality = readRequiredText(scanner, "Enter your nationality: ");
            String continent = "";
            String country = "";

// to check you are either foreigner or not


            if (nationality.equalsIgnoreCase("ethiopia")) {
                // Handling for students from Ethiopia

            } else {
                // Handling for students from other countries
                continent = readRequiredText(scanner, "Enter continent: ");
                country = readRequiredText(scanner, "Enter country: ");


            }

            String region = readRequiredText(scanner, "Enter region: ");

            String phoneNumber = readPhoneNumber(scanner);
            if (studentPhoneExists(phoneNumber)) {
                System.out.println("This phone number is already registered. Registration canceled.");
                return;
            }

// when we insert email it must  contain both  @ and .com other ways return invalid email format
            String email = readEmail(scanner);
            if (studentEmailExists(email)) {
                System.out.println("This email is already registered. Registration canceled.");
                return;
            }

            String department = readRequiredText(scanner, "Enter the student's department or stream: ");

            String userInput = readYesNo(scanner, "Do you have a student ID? (yes/no): ");
//new student may not have student id but for registering senior student may be need
            int studentId = 0;
            if (userInput.equalsIgnoreCase("yes")) {
                studentId = readPositiveInt(scanner, "Please enter your ID: ");
                if (studentIdExists(studentId)) {
                    System.out.println("This student ID already exists. Registration canceled.");
                    return;
                }
                System.out.println("ID entered: " + studentId);
                // Further processing with the ID can be done here
            } else {
                System.out.println("You have no ID.");
            }

            Student student = new Student(
                    firstName,
                    lastName,
                    motherName,
                    gender,
                    nationality,
                    continent,
                    country,
                    region,
                    phoneNumber,
                    email,
                    studentId,
                    department
            );
            students.add(student);
            saveStudentsToFile();

            System.out.println("Student registered successfully!");
            System.out.println();
        } catch (Exception e) {
            System.out.println("An error occurred during student registration: " + e.getMessage());
        }

    }

    private static void viewRegisteredStudents() {
        if (students.isEmpty()) {
            System.out.println("No students are registered yet.");
        } else {
            System.out.println("Registered students:");
            int count = 1;
            for (Student student : students) {
                System.out.println("Student #" + count++);
                System.out.println(student);

            }
        }
        System.out.println();
        System.out.println("Thank you for using this system!" +
                " I appreciate your participation .");
    }

    private static void searchStudentById(Scanner scanner) {
        if (students.isEmpty()) {
            System.out.println("No students are registered yet.");
            return;
        }

        int targetId = readPositiveInt(scanner, "Enter student ID to search: ");
        for (Student student : students) {
            if (student.getStudentId() == targetId) {
                System.out.println("Student found:");
                System.out.println(student);
                return;
            }
        }

        System.out.println("No student found with ID: " + targetId);
    }

    private static void updateStudentById(Scanner scanner) {
        if (students.isEmpty()) {
            System.out.println("No students are registered yet.");
            return;
        }

        int targetId = readPositiveInt(scanner, "Enter student ID to update: ");
        Student student = findStudentById(targetId);
        if (student == null) {
            System.out.println("No student found with ID: " + targetId);
            return;
        }

        System.out.println("Leave a field blank to keep current value.");

        String newRegion = readOptionalText(scanner, "Region (current: " + student.getRegion() + "): ");
        if (!newRegion.isEmpty()) {
            student.setRegion(newRegion);
        }

        String newDepartment = readOptionalText(scanner, "Department/Stream (current: " + student.getDepartment() + "): ");
        if (!newDepartment.isEmpty()) {
            student.setDepartment(newDepartment);
        }

        String newPhone = readOptionalPhone(scanner, "Phone (current: " + student.getPhoneNumber() + "): ");
        if (!newPhone.isEmpty()) {
            if (studentPhoneExistsExcept(newPhone, student.getStudentId())) {
                System.out.println("Another student already uses that phone number. Phone not changed.");
            } else {
                student.setPhoneNumber(newPhone);
            }
        }

        String newEmail = readOptionalEmail(scanner, "Email (current: " + student.getEmail() + "): ");
        if (!newEmail.isEmpty()) {
            if (studentEmailExistsExcept(newEmail, student.getStudentId())) {
                System.out.println("Another student already uses that email. Email not changed.");
            } else {
                student.setEmail(newEmail);
            }
        }

        System.out.println("Student updated successfully.");
        saveStudentsToFile();
    }

    private static void removeStudentById(Scanner scanner) {
        if (students.isEmpty()) {
            System.out.println("No students are registered yet.");
            return;
        }

        int targetId = readPositiveInt(scanner, "Enter student ID to remove: ");
        int index = findStudentIndexById(targetId);
        if (index < 0) {
            System.out.println("No student found with ID: " + targetId);
            return;
        }

        String confirm = readYesNo(scanner, "Are you sure you want to remove this student? (yes/no): ");
        if (confirm.equalsIgnoreCase("yes")) {
            students.remove(index);
            saveStudentsToFile();
            System.out.println("Student removed successfully.");
        } else {
            System.out.println("Removal canceled.");
        }
    }

    private static void showRegistrationSummary() {
        int total = students.size();
        int withId = 0;
        int male = 0;
        int female = 0;

        for (Student student : students) {
            if (student.getStudentId() > 0) {
                withId++;
            }
            if (student.getGender().equalsIgnoreCase("male")) {
                male++;
            } else if (student.getGender().equalsIgnoreCase("female")) {
                female++;
            }
        }

        System.out.println("Registration Summary:");
        System.out.println("Total students: " + total);
        System.out.println("Students with ID: " + withId);
        System.out.println("Students without ID: " + (total - withId));
        System.out.println("Male students: " + male);
        System.out.println("Female students: " + female);
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter digits only.");
            }
        }
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Number must be greater than 0.");
        }
    }

    private static String readRequiredText(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    private static String readValidatedName(Scanner scanner, String prompt) {
        while (true) {
            String value = readRequiredText(scanner, prompt);
            if (value.matches("[A-Za-z ]+")) {
                return value;
            }
            System.out.println("Name must contain letters and spaces only.");
        }
    }

    private static String readGender(Scanner scanner) {
        while (true) {
            String value = readRequiredText(scanner, "Please enter your gender (Male/Female)||(M/F): ");
            if (value.equalsIgnoreCase("male") || value.equalsIgnoreCase("m")) {
                return "Male";
            }
            if (value.equalsIgnoreCase("female") || value.equalsIgnoreCase("f")) {
                return "Female";
            }
            System.out.println("Invalid gender. Enter Male, Female, M, or F.");
        }
    }

    private static String readPhoneNumber(Scanner scanner) {
        while (true) {
            String value = readRequiredText(scanner, "Enter the student's phone number: ");
            if (value.matches("^\\+?[0-9]{10,15}$")) {
                return value;
            }
            System.out.println("Invalid phone number. Use 10-15 digits, optional leading +.");
        }
    }

    private static String readEmail(Scanner scanner) {
        while (true) {
            String value = readRequiredText(scanner, "Enter your email: ");
            if (value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return value.toLowerCase();
            }
            System.out.println("Invalid email format. Please enter a valid email address.");
        }
    }

    private static String readOptionalText(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static String readOptionalPhone(Scanner scanner, String prompt) {
        while (true) {
            String value = readOptionalText(scanner, prompt);
            if (value.isEmpty() || value.matches("^\\+?[0-9]{10,15}$")) {
                return value;
            }
            System.out.println("Invalid phone number. Use 10-15 digits, optional leading +.");
        }
    }

    private static String readOptionalEmail(Scanner scanner, String prompt) {
        while (true) {
            String value = readOptionalText(scanner, prompt);
            if (value.isEmpty()) {
                return value;
            }
            if (value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return value.toLowerCase();
            }
            System.out.println("Invalid email format. Please enter a valid email address.");
        }
    }

    private static String readYesNo(Scanner scanner, String prompt) {
        while (true) {
            String value = readRequiredText(scanner, prompt);
            if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("no")) {
                return value;
            }
            System.out.println("Invalid input. Please enter either 'yes' or 'no'.");
        }
    }

    private static boolean studentIdExists(int studentId) {
        for (Student student : students) {
            if (student.getStudentId() == studentId) {
                return true;
            }
        }
        return false;
    }

    private static boolean studentEmailExists(String email) {
        for (Student student : students) {
            if (student.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    private static boolean studentEmailExistsExcept(String email, int exemptId) {
        for (Student student : students) {
            if (student.getStudentId() != exemptId && student.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    private static boolean studentPhoneExists(String phoneNumber) {
        for (Student student : students) {
            if (student.getPhoneNumber().equals(phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    private static boolean studentPhoneExistsExcept(String phoneNumber, int exemptId) {
        for (Student student : students) {
            if (student.getStudentId() != exemptId && student.getPhoneNumber().equals(phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    private static Student findStudentById(int studentId) {
        for (Student student : students) {
            if (student.getStudentId() == studentId) {
                return student;
            }
        }
        return null;
    }

    private static int findStudentIndexById(int studentId) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId() == studentId) {
                return i;
            }
        }
        return -1;
    }

    private static void loadStudentsFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }

        int loadedCount = 0;
        int skippedCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                Student student = deserializeStudent(line);
                if (student == null) {
                    skippedCount++;
                    continue;
                }

                if (student.getStudentId() > 0 && studentIdExists(student.getStudentId())) {
                    skippedCount++;
                    continue;
                }

                if (studentEmailExists(student.getEmail()) || studentPhoneExists(student.getPhoneNumber())) {
                    skippedCount++;
                    continue;
                }

                students.add(student);
                loadedCount++;
            }

            if (loadedCount > 0 || skippedCount > 0) {
                System.out.println("Loaded " + loadedCount + " student(s) from file.");
                if (skippedCount > 0) {
                    System.out.println("Skipped " + skippedCount + " invalid/duplicate record(s).");
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load data file: " + e.getMessage());
        }
    }

    private static void saveStudentsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Student student : students) {
                writer.write(serializeStudent(student));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Could not save data file: " + e.getMessage());
        }
    }

    private static String serializeStudent(Student student) {
        return escape(student.getFirstName()) + "|"
                + escape(student.getLastName()) + "|"
                + escape(student.getMotherName()) + "|"
                + escape(student.getGender()) + "|"
                + escape(student.getNationality()) + "|"
                + escape(student.getContinent()) + "|"
                + escape(student.getCountry()) + "|"
                + escape(student.getRegion()) + "|"
                + escape(student.getPhoneNumber()) + "|"
                + escape(student.getEmail()) + "|"
                + student.getStudentId() + "|"
                + escape(student.getDepartment());
    }

    private static Student deserializeStudent(String line) {
        String[] parts = splitEscaped(line, '|');
        if (parts.length != 12) {
            return null;
        }

        try {
            int id = Integer.parseInt(parts[10]);
            if (id < 0) {
                return null;
            }

            String email = unescape(parts[9]).toLowerCase();
            String phone = unescape(parts[8]);

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return null;
            }
            if (!phone.matches("^\\+?[0-9]{10,15}$")) {
                return null;
            }

            return new Student(
                    unescape(parts[0]),
                    unescape(parts[1]),
                    unescape(parts[2]),
                    unescape(parts[3]),
                    unescape(parts[4]),
                    unescape(parts[5]),
                    unescape(parts[6]),
                    unescape(parts[7]),
                    phone,
                    email,
                    id,
                    unescape(parts[11])
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    private static String unescape(String value) {
        StringBuilder sb = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (escaping) {
                sb.append(ch);
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else {
                sb.append(ch);
            }
        }
        if (escaping) {
            sb.append('\\');
        }
        return sb.toString();
    }

    private static String[] splitEscaped(String text, char separator) {
        ArrayList<String> items = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (escaping) {
                current.append(ch);
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else if (ch == separator) {
                items.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        if (escaping) {
            current.append('\\');
        }
        items.add(current.toString());

        return items.toArray(new String[0]);
    }

    private static class Student {
        private String firstName;
        private String lastName;
        private String motherName;
        private String gender;
        private String nationality;
        private String continent;
        private String country;
        private String region;
        private String phoneNumber;
        private String email;
        private int studentId;
        private String department;

        public Student(String firstName, String lastName, String motherName, String gender, String nationality, String continent, String country, String region, String phoneNumber, String email, int studentId, String department) {
            this.firstName = firstName;

            this.lastName = lastName;

            this.motherName = motherName;

            this.gender = gender;

            this.nationality = nationality;

            this.continent = continent;

            this.country = country;

            this.region = region;

            this.phoneNumber = phoneNumber;

            this.email = email;

            this.studentId = studentId;

            this.department = department;
        }

        public int getStudentId() {
            return studentId;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getMotherName() {
            return motherName;
        }

        public String getGender() {
            return gender;
        }

        public String getNationality() {
            return nationality;
        }

        public String getContinent() {
            return continent;
        }

        public String getCountry() {
            return country;
        }

        public String getRegion() {
            return region;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public String getDepartment() {
            return department;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        @Override
        public String toString() {
            StringBuilder details = new StringBuilder();
            details.append("Name: ").append(firstName).append(" ").append(lastName).append("\n")
                    .append("Mother Name: ").append(motherName).append("\n")
                    .append("Gender: ").append(gender).append("\n")
                    .append("Nationality: ").append(nationality).append("\n");

            if (!continent.isEmpty()) {
                details.append("Continent: ").append(continent).append("\n");
            }
            if (!country.isEmpty()) {
                details.append("Country: ").append(country).append("\n");
            }

            details.append("Region: ").append(region).append("\n")
                    .append("Phone Number: ").append(phoneNumber).append("\n")
                    .append("Email: ").append(email).append("\n")
                    .append("Student ID: ").append(studentId == 0 ? "N/A" : studentId).append("\n")
                    .append("Department/Stream: ").append(department).append("\n");

            return details.toString();
        }
    }
}