/**
 * Central Hospital Engine coordinating all four custom DSA structures:
 * 1. EmergencyPriorityQueue (Binary Min-Heap -> O(log N) emergency triage)
 * 2. PatientHashTable       (Separate Chaining -> O(1) avg Patient ID lookup)
 * 3. PatientRecordList      (Doubly Linked List -> chronological patient log)
 * 4. PatientSorter          (Merge Sort & Quick Sort -> O(N log N) sorting)
 */
public class HospitalEngine {
    private final EmergencyPriorityQueue triageHeap;
    private final PatientHashTable patientMap;
    private final PatientRecordList recordList;
    private final PatientSorter sorter;
    private int nextIdNum;
    private int arrivalCounter;

    public HospitalEngine() {
        this.triageHeap = new EmergencyPriorityQueue(16);
        this.patientMap = new PatientHashTable(8);
        this.recordList = new PatientRecordList();
        this.sorter = new PatientSorter();
        this.nextIdNum = 101;
        this.arrivalCounter = 1;
    }

    public void loadDemoData() {
        admitPatient("Aarav Sharma", 34, "Cardiology", 1, 14500);
        admitPatient("Priya Verma", 27, "General Medicine", 3, 2200);
        admitPatient("Rohan Patel", 52, "Neurology", 1, 18900);
        admitPatient("Sneha Gupta", 19, "Orthopedics", 2, 6800);
        admitPatient("Vikram Singh", 64, "Cardiology", 2, 11200);
        admitPatient("Ananya Das", 41, "General Medicine", 4, 1500);
    }

    public Patient admitPatient(String name, int age, String department, int severity, double billAmount) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String id = "P" + (nextIdNum++);
        Patient p = new Patient(id, name, age, department, severity, arrivalCounter++, billAmount);
        patientMap.put(p);
        recordList.addLast(p);
        triageHeap.insert(p);
        return p;
    }

    public Patient treatNextEmergency() {
        if (triageHeap.isEmpty()) {
            return null;
        }
        Patient next = triageHeap.extractMin();
        next.setStatus(Patient.Status.TREATED);
        return next;
    }

    public Patient dischargePatient(String patientId) {
        Patient p = patientMap.get(patientId);
        if (p == null) {
            return null;
        }
        if (p.getStatus() == Patient.Status.WAITING_TRIAGE) {
            triageHeap.removeById(p.getId());
        }
        p.setStatus(Patient.Status.DISCHARGED);
        return p;
    }

    public boolean removePatientRecord(String patientId) {
        Patient p = patientMap.get(patientId);
        if (p == null) {
            return false;
        }
        triageHeap.removeById(p.getId());
        recordList.removeById(p.getId());
        patientMap.remove(p.getId());
        return true;
    }

    public Patient findById(String patientId) {
        return patientMap.get(patientId);
    }

    public int getBucketForId(String patientId) {
        return patientMap.hashIndex(patientId);
    }

    public PatientSorter.SortResult getSortedPatients(PatientSorter.SortBy sortBy, PatientSorter.Algorithm algo) {
        return sorter.sort(recordList.toArray(), sortBy, algo);
    }

    public int countByStatus(Patient.Status status) {
        int count = 0;
        for (Patient p : recordList.toArray()) {
            if (p.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    public EmergencyPriorityQueue getTriageHeap() {
        return triageHeap;
    }

    public PatientHashTable getPatientMap() {
        return patientMap;
    }

    public PatientRecordList getRecordList() {
        return recordList;
    }
}
