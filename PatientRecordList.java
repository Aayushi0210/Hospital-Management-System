import java.util.ArrayList;
import java.util.List;

/**
 * Custom Doubly Linked List built from scratch for chronological Patient Records.
 *
 * Time Complexity:
 * - addLast: O(1)
 * - removeById: O(N) search, O(1) pointer unlink
 */
public class PatientRecordList {

    private static class Node {
        final Patient data;
        Node prev;
        Node next;

        Node(Patient data) {
            this.data = data;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public PatientRecordList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void addLast(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Cannot add null patient.");
        }
        Node node = new Node(patient);
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        size++;
    }

    public Patient removeById(String patientId) {
        if (patientId == null) {
            return null;
        }
        String target = patientId.trim().toUpperCase();
        Node curr = head;
        while (curr != null) {
            if (curr.data.getId().equals(target)) {
                unlink(curr);
                return curr.data;
            }
            curr = curr.next;
        }
        return null;
    }

    private void unlink(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        node.prev = null;
        node.next = null;
        size--;
    }

    public List<Patient> toList() {
        List<Patient> list = new ArrayList<>();
        Node curr = head;
        while (curr != null) {
            list.add(curr.data);
            curr = curr.next;
        }
        return list;
    }

    public Patient[] toArray() {
        Patient[] arr = new Patient[size];
        Node curr = head;
        int i = 0;
        while (curr != null) {
            arr[i++] = curr.data;
            curr = curr.next;
        }
        return arr;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public String toInspectorString() {
        if (isEmpty()) {
            return "HEAD -> (null) <- TAIL\nTotal Records: 0";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Doubly Linked List Records: ").append(size).append("\n");
        sb.append("----------------------------------------\n");
        Node curr = head;
        int shown = 0;
        while (curr != null && shown < 10) {
            String pre = (curr == head) ? "HEAD -> " : "        ";
            String suf = (curr == tail) ? " <- TAIL" : " <->";
            sb.append(pre).append("[").append(curr.data.getId()).append(" ")
                    .append(curr.data.getName()).append("]").append(suf).append("\n");
            curr = curr.next;
            shown++;
        }
        if (size > shown) {
            sb.append("        ... (+").append(size - shown).append(" more nodes)\n");
        }
        return sb.toString();
    }
}
