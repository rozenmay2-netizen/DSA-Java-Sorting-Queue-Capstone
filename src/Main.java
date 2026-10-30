import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Event Registration and Check-In System
 *
 * Console-based, procedural Java program (no custom classes, no OOP).
 * - Records are stored in five parallel ArrayLists (same index = same participant).
 * - The check-in line is a FIFO Queue (LinkedList) of registration numbers.
 * - Insertion Sort (by name) and Selection Sort (by registration number)
 *   are written manually; no built-in sorting methods are used.
 */
public class Main {

    // ===================== DATA (parallel ArrayLists) =====================
    static ArrayList<String> names = new ArrayList<>();
    static ArrayList<Integer> regNumbers = new ArrayList<>();
    static ArrayList<String> regDates = new ArrayList<>();
    static ArrayList<String> ticketTypes = new ArrayList<>();
    static ArrayList<Boolean> checkedIn = new ArrayList<>();

    // ===================== CHECK-IN QUEUE (FIFO) =====================
    // Stores registration numbers (not list positions) so sorting the
    // records never changes the order of the line.
    static Queue<Integer> checkInQueue = new LinkedList<>();

    static int nextRegNumber = 1001;
    static Scanner input = new Scanner(System.in);

    // ===================== MAIN =====================
    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            showMenu();
            int choice = readInt("Enter your choice (1-10): ", 1, 10);

            switch (choice) {
                case 1:
                    registerParticipant();
                    break;
                case 2:
                    displayAllParticipants();
                    break;
                case 3:
                    searchParticipant();
                    break;
                case 4:
                    addToQueue();
                    break;
                case 5:
                    checkInNext();
                    break;
                case 6:
                    peekNext();
                    break;
                case 7:
                    displayQueue();
                    break;
                case 8:
                    sortByName();
                    break;
                case 9:
                    sortByRegNumber();
                    break;
                case 10:
                    System.out.println("\nThank you for using the Event Registration and Check-In System. Goodbye!");
                    running = false;
                    break;
            }
        }
        input.close();
    }

    // ===================== MENU =====================
    static void showMenu() {
        System.out.println("\n==============================================");
        System.out.println("   EVENT REGISTRATION AND CHECK-IN SYSTEM");
        System.out.println("==============================================");
        System.out.println(" 1. Register participant");
        System.out.println(" 2. Display all participants");
        System.out.println(" 3. Search participant");
        System.out.println(" 4. Add to check-in queue");
        System.out.println(" 5. Check in next participant (dequeue)");
        System.out.println(" 6. Peek next participant");
        System.out.println(" 7. Display check-in queue");
        System.out.println(" 8. Sort by name (Insertion Sort)");
        System.out.println(" 9. Sort by registration number (Selection Sort)");
        System.out.println("10. Exit");
        System.out.println("----------------------------------------------");
    }

    // ===================== INPUT HELPERS =====================

    // Reads an integer within [min, max]; keeps asking until valid.
    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Error: Please enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Error: \"" + line + "\" is not a valid number. Please try again.");
            }
        }
    }

    // Reads a non-empty name made of letters, spaces, dots, apostrophes, hyphens.
    static String readName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("Error: Name cannot be empty.");
            } else if (!line.matches("[A-Za-z][A-Za-z .'-]*")) {
                System.out.println("Error: Name may only contain letters, spaces, . ' and -.");
            } else {
                return line;
            }
        }
    }

    // Reads a date in YYYY-MM-DD format with basic range checking.
    static String readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            if (isValidDate(line)) {
                return line;
            }
            System.out.println("Error: Invalid date. Use the format YYYY-MM-DD (e.g., 2026-10-30).");
        }
    }

    static boolean isValidDate(String s) {
        if (!s.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        int year = Integer.parseInt(s.substring(0, 4));
        int month = Integer.parseInt(s.substring(5, 7));
        int day = Integer.parseInt(s.substring(8, 10));
        if (year < 2000 || year > 2100 || month < 1 || month > 12 || day < 1) {
            return false;
        }
        int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        boolean leap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
        int maxDay = daysInMonth[month - 1];
        if (month == 2 && leap) {
            maxDay = 29;
        }
        return day <= maxDay;
    }

    // ===================== 1. REGISTER =====================
    static void registerParticipant() {
        System.out.println("\n--- Register Participant ---");
        String name = readName("Enter participant name: ");
        String date = readDate("Enter registration date (YYYY-MM-DD): ");

        System.out.println("Ticket type:");
        System.out.println("  1. Regular");
        System.out.println("  2. Special Guest");
        int type = readInt("Choose ticket type (1-2): ", 1, 2);
        String ticket = (type == 1) ? "Regular" : "Special Guest";

        // Add to ALL five lists together to keep indexes synchronized
        names.add(name);
        regNumbers.add(nextRegNumber);
        regDates.add(date);
        ticketTypes.add(ticket);
        checkedIn.add(false);

        System.out.println("Success: " + name + " registered with registration number " + nextRegNumber + ".");
        nextRegNumber++;
    }

    // ===================== 2. DISPLAY ALL =====================
    static void displayAllParticipants() {
        System.out.println("\n--- Participant Records ---");
        if (names.isEmpty()) {
            System.out.println("No participants registered yet.");
            return;
        }
        printTable();
        System.out.println("Total participants: " + names.size());
    }

    static void printTable() {
        System.out.printf("%-8s %-22s %-12s %-14s %-10s%n",
                "Reg No.", "Name", "Date", "Ticket", "Checked In");
        System.out.println("---------------------------------------------------------------------");
        for (int i = 0; i < names.size(); i++) {
            System.out.printf("%-8d %-22s %-12s %-14s %-10s%n",
                    regNumbers.get(i), names.get(i), regDates.get(i),
                    ticketTypes.get(i), checkedIn.get(i) ? "Yes" : "No");
        }
    }

    // ===================== 3. SEARCH =====================
    static void searchParticipant() {
        System.out.println("\n--- Search Participant ---");
        if (names.isEmpty()) {
            System.out.println("No participants registered yet. Nothing to search.");
            return;
        }
        System.out.println("  1. Search by registration number");
        System.out.println("  2. Search by name (full or partial)");
        int option = readInt("Choose search type (1-2): ", 1, 2);

        if (option == 1) {
            int reg = readInt("Enter registration number: ", 1, Integer.MAX_VALUE);
            int index = findIndexByRegNumber(reg);
            if (index == -1) {
                System.out.println("No participant found with registration number " + reg + ".");
            } else {
                System.out.println("Participant found:");
                printRecord(index);
            }
        } else {
            System.out.print("Enter name to search: ");
            String keyword = input.nextLine().trim().toLowerCase();
            if (keyword.isEmpty()) {
                System.out.println("Error: Search text cannot be empty.");
                return;
            }
            int matches = 0;
            for (int i = 0; i < names.size(); i++) {
                if (names.get(i).toLowerCase().contains(keyword)) {
                    if (matches == 0) {
                        System.out.println("Matching participants:");
                    }
                    printRecord(i);
                    matches++;
                }
            }
            if (matches == 0) {
                System.out.println("No participant found matching \"" + keyword + "\".");
            } else {
                System.out.println(matches + " match(es) found.");
            }
        }
    }

    static void printRecord(int i) {
        System.out.println("  Reg No.    : " + regNumbers.get(i));
        System.out.println("  Name       : " + names.get(i));
        System.out.println("  Date       : " + regDates.get(i));
        System.out.println("  Ticket     : " + ticketTypes.get(i));
        System.out.println("  Checked In : " + (checkedIn.get(i) ? "Yes" : "No"));
        System.out.println();
    }

    // Linear search: returns index in the ArrayLists, or -1 if not found.
    static int findIndexByRegNumber(int reg) {
        for (int i = 0; i < regNumbers.size(); i++) {
            if (regNumbers.get(i) == reg) {
                return i;
            }
        }
        return -1;
    }

    // ===================== 4. ENQUEUE =====================
    static void addToQueue() {
        System.out.println("\n--- Add to Check-In Queue ---");
        if (names.isEmpty()) {
            System.out.println("No participants registered yet. Register someone first.");
            return;
        }
        int reg = readInt("Enter registration number: ", 1, Integer.MAX_VALUE);
        int index = findIndexByRegNumber(reg);

        if (index == -1) {
            System.out.println("Error: No participant found with registration number " + reg + ".");
        } else if (checkedIn.get(index)) {
            System.out.println("Error: " + names.get(index) + " has already checked in.");
        } else if (checkInQueue.contains(reg)) {
            System.out.println("Error: " + names.get(index) + " is already in the queue.");
        } else {
            checkInQueue.add(reg); // enqueue at the back
            System.out.println("Success: " + names.get(index) + " added to the queue at position "
                    + checkInQueue.size() + ".");
        }
    }

    // ===================== 5. DEQUEUE =====================
    static void checkInNext() {
        System.out.println("\n--- Check In Next Participant ---");
        if (checkInQueue.isEmpty()) {
            System.out.println("The check-in queue is empty. No one to check in.");
            return;
        }
        int reg = checkInQueue.poll(); // dequeue from the front
        int index = findIndexByRegNumber(reg);
        checkedIn.set(index, true);
        System.out.println("Checked in: " + names.get(index) + " (Reg No. " + reg + ", "
                + ticketTypes.get(index) + ")");
        System.out.println("People still waiting: " + checkInQueue.size());
    }

    // ===================== 6. PEEK =====================
    static void peekNext() {
        System.out.println("\n--- Next in Line ---");
        if (checkInQueue.isEmpty()) {
            System.out.println("The check-in queue is empty.");
            return;
        }
        int reg = checkInQueue.peek(); // view front without removing
        int index = findIndexByRegNumber(reg);
        System.out.println("Next participant: " + names.get(index) + " (Reg No. " + reg + ", "
                + ticketTypes.get(index) + ")");
    }

    // ===================== 7. DISPLAY QUEUE =====================
    static void displayQueue() {
        System.out.println("\n--- Check-In Queue (front to back) ---");
        if (checkInQueue.isEmpty()) {
            System.out.println("The check-in queue is empty.");
            return;
        }
        int position = 1;
        for (int reg : checkInQueue) {
            int index = findIndexByRegNumber(reg);
            String marker = (position == 1) ? "  <-- next" : "";
            System.out.println(position + ". " + names.get(index) + " (Reg No. " + reg + ", "
                    + ticketTypes.get(index) + ")" + marker);
            position++;
        }
        System.out.println("Total waiting: " + checkInQueue.size());
    }

    // ===================== SWAP (keeps all lists in sync) =====================
    static void swapAll(int a, int b) {
        String tName = names.get(a);
        names.set(a, names.get(b));
        names.set(b, tName);

        int tReg = regNumbers.get(a);
        regNumbers.set(a, regNumbers.get(b));
        regNumbers.set(b, tReg);

        String tDate = regDates.get(a);
        regDates.set(a, regDates.get(b));
        regDates.set(b, tDate);

        String tTicket = ticketTypes.get(a);
        ticketTypes.set(a, ticketTypes.get(b));
        ticketTypes.set(b, tTicket);

        boolean tChecked = checkedIn.get(a);
        checkedIn.set(a, checkedIn.get(b));
        checkedIn.set(b, tChecked);
    }

    // ===================== 8. INSERTION SORT (by name, A-Z) =====================
    static void sortByName() {
        System.out.println("\n--- Sort by Name (Insertion Sort, A to Z) ---");
        if (names.isEmpty()) {
            System.out.println("No participants to sort.");
            return;
        }
        System.out.println("BEFORE sorting:");
        printTable();

        insertionSortByName();

        System.out.println("\nAFTER sorting:");
        printTable();
        System.out.println("Note: The check-in queue order was not changed.");
    }

    static void insertionSortByName() {
        for (int i = 1; i < names.size(); i++) {
            int j = i;
            // Slide record i to the left until it is in its alphabetical place
            while (j > 0 && names.get(j - 1).compareToIgnoreCase(names.get(j)) > 0) {
                swapAll(j - 1, j);
                j--;
            }
        }
    }

    // ===================== 9. SELECTION SORT (by reg number, low-high) =====================
    static void sortByRegNumber() {
        System.out.println("\n--- Sort by Registration Number (Selection Sort, lowest to highest) ---");
        if (names.isEmpty()) {
            System.out.println("No participants to sort.");
            return;
        }
        System.out.println("BEFORE sorting:");
        printTable();

        selectionSortByRegNumber();

        System.out.println("\nAFTER sorting:");
        printTable();
        System.out.println("Note: The check-in queue order was not changed.");
    }

    static void selectionSortByRegNumber() {
        for (int i = 0; i < regNumbers.size() - 1; i++) {
            int minIndex = i;
            // Find the smallest registration number in the unsorted part
            for (int j = i + 1; j < regNumbers.size(); j++) {
                if (regNumbers.get(j) < regNumbers.get(minIndex)) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                swapAll(i, minIndex);
            }
        }
    }
}