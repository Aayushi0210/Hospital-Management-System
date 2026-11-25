import java.util.ArrayList;
import java.util.List;

/**
 * Custom Hash Table (Hashing with Separate Chaining) built from scratch for O(1) Patient ID lookup.
 *
 * Time Complexity:
 * - put / get / remove: O(1) average, O(N) worst case
 */
public class PatientHashTable {
    private static final int DEFAULT_CAPACITY = 8;
    private static final double MAX_LOAD_FACTOR = 0.75;

    private static class Entry {
        final String key;
        Patient value;
        Entry next;

        Entry(String key, Patient value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry[] buckets;
    private int size;
    private int collisionCount;

    public PatientHashTable() {
        this(DEFAULT_CAPACITY);
    }

    public PatientHashTable(int initialCapacity) {
        this.buckets = new Entry[Math.max(4, initialCapacity)];
        this.size = 0;
        this.collisionCount = 0;
    }

    public int hashIndex(String patientId) {
        return hashForCapacity(normalize(patientId), buckets.length);
    }

    private int hashForCapacity(String key, int capacity) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * 31 + key.charAt(i)) & 0x7fffffff;
        }
        return hash % capacity;
    }

    private String normalize(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Patient ID cannot be null.");
        }
        return key.trim().toUpperCase();
    }

    public boolean put(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null.");
        }
        String key = normalize(patient.getId());
        int idx = hashForCapacity(key, buckets.length);

        Entry curr = buckets[idx];
        while (curr != null) {
            if (curr.key.equals(key)) {
                curr.value = patient;
                return false;
            }
            curr = curr.next;
        }

        if (buckets[idx] != null) {
            collisionCount++;
        }
        buckets[idx] = new Entry(key, patient, buckets[idx]);
        size++;

        if (getLoadFactor() > MAX_LOAD_FACTOR) {
            rehash(buckets.length * 2);
        }
        return true;
    }

    public Patient get(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return null;
        }
        String key = normalize(patientId);
        int idx = hashForCapacity(key, buckets.length);
        Entry curr = buckets[idx];
        while (curr != null) {
            if (curr.key.equals(key)) {
                return curr.value;
            }
            curr = curr.next;
        }
        return null;
    }

    public boolean remove(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return false;
        }
        String key = normalize(patientId);
        int idx = hashForCapacity(key, buckets.length);
        Entry prev = null;
        Entry curr = buckets[idx];
        while (curr != null) {
            if (curr.key.equals(key)) {
                if (prev == null) {
                    buckets[idx] = curr.next;
                } else {
                    prev.next = curr.next;
                }
                size--;
                return true;
            }
            prev = curr;
            curr = curr.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public int getCapacity() {
        return buckets.length;
    }

    public int getCollisionCount() {
        return collisionCount;
    }

    public double getLoadFactor() {
        return (double) size / buckets.length;
    }

    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        for (Entry head : buckets) {
            Entry curr = head;
            while (curr != null) {
                list.add(curr.value);
                curr = curr.next;
            }
        }
        return list;
    }

    private void rehash(int newCapacity) {
        Entry[] old = buckets;
        buckets = new Entry[newCapacity];
        collisionCount = 0;

        for (Entry head : old) {
            Entry curr = head;
            while (curr != null) {
                Entry next = curr.next;
                int newIdx = hashForCapacity(curr.key, newCapacity);
                if (buckets[newIdx] != null) {
                    collisionCount++;
                }
                curr.next = buckets[newIdx];
                buckets[newIdx] = curr;
                curr = next;
            }
        }
    }

    public String toBucketMapString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Patients: %d | Buckets: %d | Load: %.2f | Collisions: %d%n",
                size, buckets.length, getLoadFactor(), collisionCount));
        sb.append("----------------------------------------\n");
        for (int i = 0; i < buckets.length; i++) {
            sb.append(String.format("[%2d]: ", i));
            Entry curr = buckets[i];
            if (curr == null) {
                sb.append("(empty)");
            } else {
                while (curr != null) {
                    sb.append(curr.key);
                    if (curr.next != null) {
                        sb.append(" -> ");
                    }
                    curr = curr.next;
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
