/**
 * Represents a patient record in the Hospital Management System.
 */
public class Patient {
    public enum Status {
        WAITING_TRIAGE,
        TREATED,
        DISCHARGED
    }

    private final String id;
    private final String name;
    private final int age;
    private final String department;
    private final int severity; // 1 = CRITICAL, 2 = HIGH, 3 = MEDIUM, 4 = LOW
    private final int arrivalOrder;
    private Status status;
    private double billAmount;

    public Patient(
            String id,
            String name,
            int age,
            String department,
            int severity,
            int arrivalOrder,
            double billAmount) {
        this.id = id.trim().toUpperCase();
        this.name = name.trim();
        this.age = Math.max(0, age);
        this.department = department.trim();
        this.severity = Math.max(1, Math.min(4, severity));
        this.arrivalOrder = arrivalOrder;
        this.status = Status.WAITING_TRIAGE;
        this.billAmount = Math.max(0.0, billAmount);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDepartment() {
        return department;
    }

    public int getSeverity() {
        return severity;
    }

    public String getSeverityLabel() {
        switch (severity) {
            case 1:
                return "1 - CRITICAL";
            case 2:
                return "2 - HIGH";
            case 3:
                return "3 - MEDIUM";
            default:
                return "4 - LOW";
        }
    }

    public int getArrivalOrder() {
        return arrivalOrder;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status != null) {
            this.status = status;
        }
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        if (billAmount >= 0) {
            this.billAmount = billAmount;
        }
    }

    @Override
    public String toString() {
        return String.format("%s | %s (Age %d) | %s | Sev %s | %s | Rs.%.0f",
                id, name, age, department, getSeverityLabel(), status, billAmount);
    }
}
