# Hospital Management System (Java DSA Project)

An interactive **Java Swing Desktop Hospital Management & Emergency Triage System** (zero external dependencies) implementing four core Data Structures & Algorithms concepts from scratch: **Binary Min-Heap Priority Queue**, **Doubly Linked List**, **Merge Sort & Quick Sort**, and **Hash Table (Separate Chaining)**.

## DSA Concepts Implemented

| Concept | File | How It Is Used |
| :--- | :--- | :--- |
| **Priority Queue (Min-Heap)** | `src/EmergencyPriorityQueue.java` | Binary min-heap (`insert`, `extractMin`, `siftUp`, `siftDown`) prioritizing Emergency Triage patients by severity (`1 = CRITICAL` to `4 = LOW`) with FIFO tie-breaking in $O(\log N)$. |
| **Hashing (Separate Chaining)** | `src/PatientHashTable.java` | Polynomial rolling hash with collision chains and dynamic rehashing at 0.75 load factor for $O(1)$ average Patient ID lookup. |
| **Doubly Linked List** | `src/PatientRecordList.java` | Custom `head`/`tail` doubly linked list maintaining chronological patient records with $O(1)$ append and $O(1)$ pointer unlinking. |
| **Sorting (Merge & Quick Sort)** | `src/PatientSorter.java` | Custom Merge Sort ($O(N \log N)$ stable) and Quick Sort ($O(N \log N)$ avg) supporting sorting by Severity, Name, Age, or Bill Amount with live comparison counts. |
| **Swing GUI + Live Inspector** | `src/HospitalGUI.java` | Desktop interface with Admit Patient form, Extract-Min Emergency Triage button, sortable table, $O(1)$ ID lookup, and live tabbed DSA Inspector. |

## How to Run

Requires **JDK 11 or higher**.

- **One-click on Windows**: Double-click `run.bat` inside `hospital-management-system/`.
- **Or in VS Code PowerShell** (inside `hospital-management-system/`):
  ```powershell
  javac -d bin src\*.java
  java -cp bin Main
  ```
- **Optional modes**: `java -cp bin Main --console` (terminal menu) or `java -cp bin Main --test` (automated DSA verification suite).
