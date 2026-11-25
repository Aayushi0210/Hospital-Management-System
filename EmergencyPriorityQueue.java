import java.util.Arrays;

/**
 * Custom Binary Min-Heap Priority Queue built from scratch for Emergency Triage.
 *
 * Orders patients by lowest severity number first (1 = CRITICAL, 4 = LOW),
 * breaking ties by earliest arrivalOrder (FIFO within the same severity).
 *
 * Time Complexity:
 * - insert: O(log N)
 * - extractMin: O(log N)
 * - peek / isEmpty / size: O(1)
 */
public class EmergencyPriorityQueue {
    private static final int DEFAULT_CAPACITY = 16;

    private Patient[] heap;
    private int size;

    public EmergencyPriorityQueue() {
        this(DEFAULT_CAPACITY);
    }

    public EmergencyPriorityQueue(int initialCapacity) {
        this.heap = new Patient[Math.max(4, initialCapacity)];
        this.size = 0;
    }

    public void insert(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Cannot insert null patient.");
        }
        if (size == heap.length) {
            heap = Arrays.copyOf(heap, heap.length * 2);
        }
        heap[size] = patient;
        siftUp(size);
        size++;
    }

    public Patient extractMin() {
        if (isEmpty()) {
            throw new IllegalStateException("Priority queue is empty.");
        }
        Patient top = heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        if (size > 0) {
            siftDown(0);
        }
        return top;
    }

    public Patient peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Priority queue is empty.");
        }
        return heap[0];
    }

    public boolean removeById(String patientId) {
        if (patientId == null) {
            return false;
        }
        String target = patientId.trim().toUpperCase();
        for (int i = 0; i < size; i++) {
            if (heap[i].getId().equals(target)) {
                size--;
                heap[i] = heap[size];
                heap[size] = null;
                if (i < size) {
                    siftDown(i);
                    siftUp(i);
                }
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public Patient[] toHeapArray() {
        return Arrays.copyOf(heap, size);
    }

    private boolean hasHigherPriority(Patient a, Patient b) {
        if (a.getSeverity() != b.getSeverity()) {
            return a.getSeverity() < b.getSeverity();
        }
        return a.getArrivalOrder() < b.getArrivalOrder();
    }

    private void siftUp(int idx) {
        while (idx > 0) {
            int parent = (idx - 1) / 2;
            if (hasHigherPriority(heap[idx], heap[parent])) {
                swap(idx, parent);
                idx = parent;
            } else {
                break;
            }
        }
    }

    private void siftDown(int idx) {
        while (true) {
            int left = 2 * idx + 1;
            int right = 2 * idx + 2;
            int best = idx;

            if (left < size && hasHigherPriority(heap[left], heap[best])) {
                best = left;
            }
            if (right < size && hasHigherPriority(heap[right], heap[best])) {
                best = right;
            }
            if (best != idx) {
                swap(idx, best);
                idx = best;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        Patient tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    public String toInspectorString() {
        if (isEmpty()) {
            return "(empty Min-Heap)\n\nNo patients waiting in\nemergency triage.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Min-Heap Triage Queue (size=").append(size).append(")\n");
        sb.append("Root [0] = Next Emergency:\n");
        sb.append("----------------------------------------\n");
        for (int i = 0; i < size; i++) {
            Patient p = heap[i];
            String tag = (i == 0) ? "ROOT -> " : "        ";
            sb.append(String.format("%s[%d] %s %s (Sev %d, #%d)%n",
                    tag, i, p.getId(), p.getName(), p.getSeverity(), p.getArrivalOrder()));
        }
        return sb.toString();
    }
}
