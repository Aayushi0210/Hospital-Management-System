import javax.swing.SwingUtilities;
import java.util.Scanner;

/**
 * Entry point for the Hospital Management System (Java DSA Project).
 *
 * Demonstrates:
 * - Custom Binary Min-Heap Priority Queue (EmergencyPriorityQueue.java)
 * - Custom Hash Table with Separate Chaining (PatientHashTable.java)
 * - Custom Doubly Linked List (PatientRecordList.java)
 * - Custom Merge Sort & Quick Sort (PatientSorter.java)
 * - Desktop Swing GUI (HospitalGUI.java)
 *
 * Modes:
 * - java Main           -> Launches the Desktop Swing GUI
 * - java Main --console -> Launches the interactive Terminal Menu
 * - java Main --test    -> Runs the automated DSA verification suite
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && "--test".equalsIgnoreCase(args[0])) {
            runSelfTests();
            return;
        }
        if (args.length > 0 && "--console".equalsIgnoreCase(args[0])) {
            runConsoleMode();
            return;
        }
        SwingUtilities.invokeLater(HospitalGUI::new);
    }

    private static void runConsoleMode() {
        HospitalEngine engine = new HospitalEngine();
        engine.loadDemoData();
        Scanner sc = new Scanner(System.in);

        System.out.println("====================================================");
        System.out.println("     HOSPITAL MANAGEMENT SYSTEM (CONSOLE MODE)      ");
        System.out.println("   Priority Queue | Linked List | Sorting | Hashing ");
        System.out.println("====================================================");

        while (true) {
            System.out.println("\n1. Admit New Patient");
            System.out.println("2. Treat Next Emergency (Min-Heap Extract-Min)");
            System.out.println("3. Lookup Patient by ID (O(1) Hashing)");
            System.out.println("4. View Patients Sorted (Merge Sort / Quick Sort)");
            System.out.println("5. Discharge Patient");
            System.out.println("6. Inspect Min-Heap, Hash Buckets & Linked List");
            System.out.println("7. Run Built-in Verification Tests");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            String choice = sc.nextLine().trim();
            if ("0".equals(choice)) {
                System.out.println("Goodbye!");
                break;
            } else if ("1".equals(choice)) {
                System.out.print("Name: ");
                String name = sc.nextLine().trim();
                System.out.print("Age: ");
                int age = parseIntOrDefault(sc.nextLine(), 30);
                System.out.print("Department: ");
                String dept = sc.nextLine().trim();
                System.out.print("Severity (1=Critical to 4=Low): ");
                int sev = parseIntOrDefault(sc.nextLine(), 3);
                System.out.print("Bill Amount (Rs.): ");
                double bill = parseDoubleOrDefault(sc.nextLine(), 2500);
                Patient p = engine.admitPatient(name, age, dept.isEmpty() ? "General" : dept, sev, bill);
                System.out.println(p != null ? "Admitted: " + p : "Admission failed.");
            } else if ("2".equals(choice)) {
                Patient p = engine.treatNextEmergency();
                System.out.println(p != null ? "Treating Emergency: " + p : "Triage heap is empty.");
            } else if ("3".equals(choice)) {
                System.out.print("Patient ID (e.g. P101): ");
                String id = sc.nextLine().trim();
                Patient p = engine.findById(id);
                System.out.println(p != null
                        ? "Found in Bucket [" + engine.getBucketForId(id) + "]: " + p
                        : "Not found in Bucket [" + engine.getBucketForId(id) + "].");
            } else if ("4".equals(choice)) {
                PatientSorter.SortResult res = engine.getSortedPatients(
                        PatientSorter.SortBy.SEVERITY, PatientSorter.Algorithm.MERGE_SORT);
                System.out.println("--- Patients Sorted by Severity (Merge Sort, "
                        + res.getComparisons() + " comparisons) ---");
                for (Patient p : res.getSorted()) {
                    System.out.println("  " + p);
                }
            } else if ("5".equals(choice)) {
                System.out.print("Patient ID to discharge: ");
                Patient p = engine.dischargePatient(sc.nextLine().trim());
                System.out.println(p != null ? "Discharged: " + p : "Patient ID not found.");
            } else if ("6".equals(choice)) {
                System.out.println("\n" + engine.getTriageHeap().toInspectorString());
                System.out.println(engine.getPatientMap().toBucketMapString());
                System.out.println(engine.getRecordList().toInspectorString());
            } else if ("7".equals(choice)) {
                runSelfTests();
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static int parseIntOrDefault(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static double parseDoubleOrDefault(String s, double def) {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static void runSelfTests() {
        System.out.println("Running Hospital Management System DSA Verification Suite...");
        int passed = 0;

        // Test 1: Min-Heap Priority Queue ordering & tie-breaking
        EmergencyPriorityQueue pq = new EmergencyPriorityQueue(4);
        pq.insert(new Patient("P1", "Low", 20, "Gen", 4, 1, 500));
        pq.insert(new Patient("P2", "CritFirst", 50, "Cardio", 1, 2, 10000));
        pq.insert(new Patient("P3", "High", 40, "Neuro", 2, 3, 7000));
        pq.insert(new Patient("P4", "CritSecond", 35, "Cardio", 1, 4, 12000));
        pq.insert(new Patient("P5", "Med", 28, "Ortho", 3, 5, 3000));
        assertTrue(pq.extractMin().getId().equals("P2"), "Expected P2 (CritFirst) first");
        assertTrue(pq.extractMin().getId().equals("P4"), "Expected P4 (CritSecond) second");
        assertTrue(pq.extractMin().getId().equals("P3"), "Expected P3 (High) third");
        passed++;

        // Test 2: Custom Hash Table insert, lookup, rehash, remove
        PatientHashTable ht = new PatientHashTable(4);
        for (int i = 1; i <= 12; i++) {
            ht.put(new Patient("P" + i, "Patient " + i, 20 + i, "Gen", 3, i, 1000));
        }
        assertTrue(ht.size() == 12 && ht.getCapacity() >= 16, "HashTable rehash failed");
        assertTrue(ht.get("P9") != null && ht.get("P9").getName().equals("Patient 9"), "HashTable get failed");
        assertTrue(ht.remove("P9") && ht.get("P9") == null, "HashTable remove failed");
        passed++;

        // Test 3: Custom Doubly Linked List
        PatientRecordList list = new PatientRecordList();
        list.addLast(new Patient("P1", "A", 30, "Gen", 2, 1, 1000));
        list.addLast(new Patient("P2", "B", 25, "Gen", 1, 2, 2000));
        assertTrue(list.size() == 2, "LinkedList size mismatch");
        assertTrue(list.removeById("P1") != null && list.size() == 1, "LinkedList unlink failed");
        passed++;

        // Test 4: Custom Merge Sort & Quick Sort
        HospitalEngine engine = new HospitalEngine();
        engine.loadDemoData();
        PatientSorter.SortResult mergeRes = engine.getSortedPatients(
                PatientSorter.SortBy.AGE, PatientSorter.Algorithm.MERGE_SORT);
        PatientSorter.SortResult quickRes = engine.getSortedPatients(
                PatientSorter.SortBy.AGE, PatientSorter.Algorithm.QUICK_SORT);
        for (int i = 1; i < mergeRes.getSorted().length; i++) {
            assertTrue(mergeRes.getSorted()[i - 1].getAge() <= mergeRes.getSorted()[i].getAge(),
                    "MergeSort age ordering failed");
            assertTrue(quickRes.getSorted()[i - 1].getAge() <= quickRes.getSorted()[i].getAge(),
                    "QuickSort age ordering failed");
        }
        passed++;

        System.out.println("ALL " + passed + " TEST GROUPS PASSED!");
    }

    private static void assertTrue(boolean cond, String msg) {
        if (!cond) {
            throw new AssertionError("TEST FAILED: " + msg);
        }
    }
}
