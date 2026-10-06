import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Node {
    String key;
    int frequency;
    Node next;

    Node(String key) {
        this.key = key;
        this.frequency = 1;
        this.next = null;
    }
}

class HashTable {

    private Node[] table;
    private int tableSize;
    private int totalElements;

    // Constructor
    HashTable(int size) {
        tableSize = size;
        totalElements = 0;
        table = new Node[tableSize];
    }

    private int hashFunction(String key) {

        long hash = 0;
        int base = 31;

        for (int i = 0; i < key.length(); i++) {
            hash = hash * base + key.charAt(i);
        }

        hash = Math.abs(hash);

        return (int)(hash % tableSize);
    }

    public void insert(String key) {

        int index = hashFunction(key);

        Node current = table[index];

        // Check whether key already exists
        while (current != null) {

            if (current.key.equals(key)) {
                current.frequency++;
                return;
            }

            current = current.next;
        }

        // Create new node
        Node newNode = new Node(key);

        // Insert at beginning of linked list
        newNode.next = table[index];
        table[index] = newNode;

        totalElements++;
    }

    public int search(String key) {

        int index = hashFunction(key);

        Node current = table[index];

        while (current != null) {

            if (current.key.equals(key)) {
                return current.frequency;
            }

            current = current.next;
        }

        return 0;
    }
    public void displayStatistics() {

        int occupiedBuckets = 0;
        int collisions = 0;
        int longestChain = 0;

        for (int i = 0; i < tableSize; i++) {

            if (table[i] != null) {

                occupiedBuckets++;

                int chainLength = 0;

                Node current = table[i];

                while (current != null) {
                    chainLength++;
                    current = current.next;
                }

                if (chainLength > 1) {
                    collisions += chainLength - 1;
                }

                if (chainLength > longestChain) {
                    longestChain = chainLength;
                }
            }
        }

        System.out.println("\n HASH TABLE STATISTICS ");
        System.out.println("Table Size       : " + tableSize);
        System.out.println("Unique Trigrams  : " + totalElements);
        System.out.println("Occupied Buckets : " + occupiedBuckets);
        System.out.println("Collisions       : " + collisions);
        System.out.println("Longest Chain    : " + longestChain);
        System.out.println("    ");
    }
}

public class PlagiarismDetection {

    public static String cleanWord(String word) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < word.length(); i++) {

            char ch = word.charAt(i);

            if (Character.isLetterOrDigit(ch)) {
                result.append(Character.toLowerCase(ch));
            }
        }

        return result.toString();
    }

    public static String[] readFile(String filename)
            throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(filename));

        StringBuilder text = new StringBuilder();

        while (scanner.hasNext()) {

            String word = scanner.next();

            String cleaned = cleanWord(word);

            if (!cleaned.isEmpty()) {
                text.append(cleaned).append(" ");
            }
        }

        scanner.close();

        return text.toString().trim().split("\\s+");
    }
    public static String createTrigram(
            String[] words,
            int start) {

        return words[start] + " "
                + words[start + 1] + " "
                + words[start + 2];
    }
    public static int buildHashTable(
            String[] words,
            HashTable hashTable) {

        if (words.length < 3) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i <= words.length - 3; i++) {

            String trigram = createTrigram(words, i);

            hashTable.insert(trigram);

            count++;
        }

        return count;
    }

    public static void detectPlagiarism(
            String[] document2,
            HashTable hashTable) {

        if (document2.length < 3) {

            System.out.println(
                    "\nDocument 2 is too short for comparison.");

            return;
        }

        int totalTrigrams = document2.length - 2;

        int matchedTrigrams = 0;

        System.out.println("\n MATCHING PHRASES ");

        for (int i = 0; i <= document2.length - 3; i++) {

            String trigram =
                    createTrigram(document2, i);

            if (hashTable.search(trigram) > 0) {

                matchedTrigrams++;

                System.out.println("MATCH: " + trigram);
            }
        }

        double similarity =
                ((double) matchedTrigrams
                        / totalTrigrams) * 100.0;


        System.out.println("\n RESULT ");

        System.out.printf(
                "Total Trigrams       : %d%n",
                totalTrigrams);

        System.out.printf(
                "Matching Trigrams    : %d%n",
                matchedTrigrams);

        System.out.printf(
                "Similarity Percentage: %.2f%%%n",
                similarity);


        if (similarity >= 70) {

            System.out.println(
                    "Status               : HIGH PLAGIARISM");

        } else if (similarity >= 40) {

            System.out.println(
                    "Status               : MODERATE PLAGIARISM");

        } else if (similarity >= 20) {

            System.out.println(
                    "Status               : LOW SIMILARITY");

        } else {

            System.out.println(
                    "Status               : VERY LOW SIMILARITY");
        }

        System.out.println("   ");
    }
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.println("    ");
        System.out.println("       PLAGIARISM DETECTION SYSTEM");
        System.out.println("       USING HASHING AND STRING PROCESSING");
        System.out.println("      ");

        System.out.print(
                "\nEnter first document filename: ");

        String file1 = input.nextLine();


        System.out.print(
                "Enter second document filename: ");

        String file2 = input.nextLine();


        try {
            String[] document1 = readFile(file1);

            String[] document2 = readFile(file2);


            System.out.println(
                    "\n DOCUMENT INFORMATION ");

            System.out.println(
                    "Document 1 Words: "
                    + document1.length);

            System.out.println(
                    "Document 2 Words: "
                    + document2.length);
            HashTable hashTable =
                    new HashTable(1009);
            int totalTrigrams =
                    buildHashTable(
                            document1,
                            hashTable);


            System.out.println(
                    "Document 1 Trigrams: "
                    + totalTrigrams);
            hashTable.displayStatistics();

            detectPlagiarism(
                    document2,
                    hashTable);


        } catch (FileNotFoundException e) {

            System.out.println(
                    "\nERROR: File not found.");

            System.out.println(
                    "Please check the filename and try again.");
        }


        input.close();

        System.out.println(
                "\nProgram completed successfully.");
    }
}