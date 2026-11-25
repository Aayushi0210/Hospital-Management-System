import java.util.Arrays;

/**
 * Custom Sorting Engine built from scratch (Merge Sort & Quick Sort) for Patient records.
 *
 * Supports sorting by Severity, Name, Age, or Bill Amount, and records comparison
 * counts and execution time for DSA analysis.
 */
public class PatientSorter {

    public enum SortBy {
        SEVERITY,
        NAME,
        AGE,
        BILL_AMOUNT
    }

    public enum Algorithm {
        MERGE_SORT,
        QUICK_SORT
    }

    public static class SortResult {
        private final Patient[] sorted;
        private final Algorithm algorithm;
        private final SortBy sortBy;
        private final long comparisons;
        private final long elapsedNanos;

        public SortResult(Patient[] sorted, Algorithm algorithm, SortBy sortBy, long comparisons, long elapsedNanos) {
            this.sorted = sorted;
            this.algorithm = algorithm;
            this.sortBy = sortBy;
            this.comparisons = comparisons;
            this.elapsedNanos = elapsedNanos;
        }

        public Patient[] getSorted() {
            return sorted;
        }

        public Algorithm getAlgorithm() {
            return algorithm;
        }

        public SortBy getSortBy() {
            return sortBy;
        }

        public long getComparisons() {
            return comparisons;
        }

        public double getElapsedMillis() {
            return elapsedNanos / 1_000_000.0;
        }
    }

    private long comparisons;

    public SortResult sort(Patient[] input, SortBy sortBy, Algorithm algorithm) {
        Patient[] copy = Arrays.copyOf(input, input.length);
        comparisons = 0;

        long start = System.nanoTime();
        if (copy.length > 1) {
            if (algorithm == Algorithm.MERGE_SORT) {
                Patient[] aux = new Patient[copy.length];
                mergeSort(copy, aux, 0, copy.length - 1, sortBy);
            } else {
                quickSort(copy, 0, copy.length - 1, sortBy);
            }
        }
        long elapsed = System.nanoTime() - start;

        return new SortResult(copy, algorithm, sortBy, comparisons, elapsed);
    }

    private int compare(Patient a, Patient b, SortBy sortBy) {
        comparisons++;
        switch (sortBy) {
            case SEVERITY:
                int sevCmp = Integer.compare(a.getSeverity(), b.getSeverity());
                return (sevCmp != 0) ? sevCmp : Integer.compare(a.getArrivalOrder(), b.getArrivalOrder());
            case NAME:
                return a.getName().compareToIgnoreCase(b.getName());
            case AGE:
                return Integer.compare(a.getAge(), b.getAge());
            case BILL_AMOUNT:
                return Double.compare(b.getBillAmount(), a.getBillAmount()); // Highest bill first
            default:
                return a.getId().compareTo(b.getId());
        }
    }

    // --- Merge Sort: O(N log N) worst-case, stable ---

    private void mergeSort(Patient[] arr, Patient[] aux, int left, int right, SortBy sortBy) {
        if (left >= right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(arr, aux, left, mid, sortBy);
        mergeSort(arr, aux, mid + 1, right, sortBy);
        merge(arr, aux, left, mid, right, sortBy);
    }

    private void merge(Patient[] arr, Patient[] aux, int left, int mid, int right, SortBy sortBy) {
        for (int k = left; k <= right; k++) {
            aux[k] = arr[k];
        }
        int i = left;
        int j = mid + 1;
        for (int k = left; k <= right; k++) {
            if (i > mid) {
                arr[k] = aux[j++];
            } else if (j > right) {
                arr[k] = aux[i++];
            } else if (compare(aux[i], aux[j], sortBy) <= 0) {
                arr[k] = aux[i++];
            } else {
                arr[k] = aux[j++];
            }
        }
    }

    // --- Quick Sort: O(N log N) average, in-place ---

    private void quickSort(Patient[] arr, int low, int high, SortBy sortBy) {
        if (low < high) {
            int p = partition(arr, low, high, sortBy);
            quickSort(arr, low, p - 1, sortBy);
            quickSort(arr, p + 1, high, sortBy);
        }
    }

    private int partition(Patient[] arr, int low, int high, SortBy sortBy) {
        Patient pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (compare(arr[j], pivot, sortBy) <= 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private void swap(Patient[] arr, int i, int j) {
        Patient tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
