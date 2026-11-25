import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

/**
 * Interactive Java Swing Desktop GUI for the Hospital Management System.
 *
 * Features:
 * - Left Panel: Admit Patient form + Treat Next Emergency (Min-Heap Extract-Min)
 * - Center Panel: Sortable Patient Directory Table (Merge Sort / Quick Sort) + Discharge/Remove
 * - Right Panel: Live DSA Inspector tabs (Min-Heap Priority Queue, Hash Table, Doubly Linked List)
 * - Top Bar: KPI Counters + O(1) Hash Lookup by Patient ID
 */
public class HospitalGUI extends JFrame {

    private static final Color BG_DARK = new Color(16, 22, 32);
    private static final Color PANEL_DARK = new Color(25, 34, 48);
    private static final Color CARD_DARK = new Color(36, 48, 66);
    private static final Color TEXT_LIGHT = new Color(240, 244, 250);
    private static final Color TEXT_MUTED = new Color(156, 170, 190);
    private static final Color ACCENT_TEAL = new Color(13, 148, 136);
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);
    private static final Color ACCENT_RED = new Color(220, 38, 38);
    private static final Color ACCENT_AMBER = new Color(217, 119, 6);
    private static final Color BTN_NEUTRAL = new Color(56, 68, 88);

    private final HospitalEngine engine;

    // Header & Lookup
    private JLabel kpiLabel;
    private JTextField lookupField;

    // Left Admit Form
    private JTextField nameField;
    private JTextField ageField;
    private JComboBox<String> deptBox;
    private JComboBox<String> severityBox;
    private JTextField billField;
    private JButton treatNextBtn;

    // Center Table & Sort Controls
    private DefaultTableModel tableModel;
    private JTable patientTable;
    private JComboBox<PatientSorter.SortBy> sortByBox;
    private JComboBox<PatientSorter.Algorithm> sortAlgoBox;
    private JLabel sortStatsLabel;

    // Right DSA Inspector
    private JTextArea heapArea;
    private JTextArea hashArea;
    private JTextArea listArea;
    private JLabel statusLabel;

    public HospitalGUI() {
        super("Hospital Management System \u2014 Java DSA Project");
        this.engine = new HospitalEngine();
        this.engine.loadDemoData();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 700));
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BG_DARK);

        add(createTopBar(), BorderLayout.NORTH);
        add(createLeftPanel(), BorderLayout.WEST);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createRightInspector(), BorderLayout.EAST);
        add(createStatusBar(), BorderLayout.SOUTH);

        refreshAllViews();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout(16, 0));
        top.setBackground(PANEL_DARK);
        top.setBorder(new EmptyBorder(12, 18, 12, 18));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel title = new JLabel("Hospital Management & Emergency Triage System");
        title.setForeground(TEXT_LIGHT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        kpiLabel = new JLabel("Triage Waiting: 0  |  Treated: 0  |  Total Records: 0");
        kpiLabel.setForeground(new Color(94, 234, 212));
        kpiLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(kpiLabel);

        JPanel lookupPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        lookupPanel.setOpaque(false);

        JLabel lbl = new JLabel("O(1) Hash Lookup ID:");
        lbl.setForeground(TEXT_LIGHT);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));

        lookupField = new JTextField(8);
        lookupField.setBackground(BG_DARK);
        lookupField.setForeground(TEXT_LIGHT);
        lookupField.setCaretColor(TEXT_LIGHT);
        lookupField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_DARK),
                new EmptyBorder(5, 8, 5, 8)));
        lookupField.addActionListener(e -> handleHashLookup());

        JButton findBtn = createStyledButton("Find Patient", ACCENT_BLUE);
        findBtn.addActionListener(e -> handleHashLookup());

        lookupPanel.add(lbl);
        lookupPanel.add(lookupField);
        lookupPanel.add(findBtn);

        top.add(titleBox, BorderLayout.WEST);
        top.add(lookupPanel, BorderLayout.EAST);
        return top;
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(PANEL_DARK);
        left.setPreferredSize(new Dimension(260, 580));
        left.setBorder(new EmptyBorder(14, 14, 14, 14));

        left.add(sectionLabel("ADMIT NEW PATIENT"));
        left.add(Box.createVerticalStrut(8));

        nameField = addFormField(left, "Patient Name:", "");
        ageField = addFormField(left, "Age:", "30");

        left.add(fieldLabel("Department:"));
        deptBox = new JComboBox<>(new String[]{
                "Cardiology", "Neurology", "Orthopedics", "General Medicine", "Pediatrics", "Emergency Trauma"
        });
        deptBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        deptBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        left.add(deptBox);
        left.add(Box.createVerticalStrut(8));

        left.add(fieldLabel("Emergency Severity (Priority):"));
        severityBox = new JComboBox<>(new String[]{
                "1 - CRITICAL (Immediate)",
                "2 - HIGH (Urgent)",
                "3 - MEDIUM (Standard)",
                "4 - LOW (Non-Urgent)"
        });
        severityBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        severityBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        left.add(severityBox);
        left.add(Box.createVerticalStrut(8));

        billField = addFormField(left, "Estimated Bill (Rs.):", "3500");

        JButton admitBtn = createStyledButton("+ Admit Patient (Insert All)", ACCENT_TEAL);
        admitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        admitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        admitBtn.addActionListener(e -> handleAdmitPatient());
        left.add(Box.createVerticalStrut(4));
        left.add(admitBtn);
        left.add(Box.createVerticalStrut(18));

        left.add(sectionLabel("EMERGENCY TRIAGE (MIN-HEAP)"));
        left.add(Box.createVerticalStrut(8));

        treatNextBtn = createStyledButton("Treat Next Emergency (Extract-Min)", ACCENT_RED);
        treatNextBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        treatNextBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        treatNextBtn.addActionListener(e -> handleTreatNext());
        left.add(treatNextBtn);
        left.add(Box.createVerticalStrut(8));

        JLabel hint = new JLabel("<html>Extracts the patient with the lowest severity number (1 = Critical) in O(log N) from the Min-Heap.</html>");
        hint.setForeground(TEXT_MUTED);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(hint);

        return left;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(BG_DARK);
        center.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Sort Controls Bar
        JPanel sortBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        sortBar.setBackground(CARD_DARK);

        JLabel sLbl = new JLabel("Sort By:");
        sLbl.setForeground(TEXT_LIGHT);
        sLbl.setFont(new Font("SansSerif", Font.BOLD, 12));

        sortByBox = new JComboBox<>(PatientSorter.SortBy.values());
        sortByBox.addActionListener(e -> refreshAllViews());

        JLabel aLbl = new JLabel("Algorithm:");
        aLbl.setForeground(TEXT_LIGHT);
        aLbl.setFont(new Font("SansSerif", Font.BOLD, 12));

        sortAlgoBox = new JComboBox<>(PatientSorter.Algorithm.values());
        sortAlgoBox.addActionListener(e -> refreshAllViews());

        sortStatsLabel = new JLabel("Comparisons: 0");
        sortStatsLabel.setForeground(new Color(253, 224, 71));
        sortStatsLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));

        sortBar.add(sLbl);
        sortBar.add(sortByBox);
        sortBar.add(aLbl);
        sortBar.add(sortAlgoBox);
        sortBar.add(sortStatsLabel);
        center.add(sortBar, BorderLayout.NORTH);

        // Patient Table
        String[] cols = {"ID", "Name", "Age", "Department", "Severity", "Status", "Bill (Rs.)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setRowHeight(28);
        patientTable.setBackground(PANEL_DARK);
        patientTable.setForeground(TEXT_LIGHT);
        patientTable.setSelectionBackground(ACCENT_BLUE);
        patientTable.setSelectionForeground(Color.WHITE);
        patientTable.setGridColor(CARD_DARK);
        patientTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        patientTable.getTableHeader().setBackground(CARD_DARK);
        patientTable.getTableHeader().setForeground(TEXT_LIGHT);
        patientTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane tableScroll = new JScrollPane(patientTable);
        tableScroll.getViewport().setBackground(PANEL_DARK);
        tableScroll.setBorder(BorderFactory.createLineBorder(CARD_DARK));
        center.add(tableScroll, BorderLayout.CENTER);

        // Bottom Row Actions
        JPanel rowActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rowActions.setOpaque(false);

        JButton dischargeBtn = createStyledButton("Discharge Selected Patient", ACCENT_AMBER);
        dischargeBtn.addActionListener(e -> handleDischargeSelected());

        JButton removeBtn = createStyledButton("Remove Record (Unlink)", BTN_NEUTRAL);
        removeBtn.addActionListener(e -> handleRemoveSelected());

        rowActions.add(dischargeBtn);
        rowActions.add(removeBtn);
        center.add(rowActions, BorderLayout.SOUTH);

        return center;
    }

    private JPanel createRightInspector() {
        JPanel right = new JPanel(new BorderLayout(0, 8));
        right.setBackground(PANEL_DARK);
        right.setPreferredSize(new Dimension(310, 580));
        right.setBorder(new EmptyBorder(14, 14, 14, 14));

        right.add(sectionLabel("LIVE DSA STRUCTURE INSPECTOR"), BorderLayout.NORTH);

        heapArea = createInspectorArea(new Color(252, 165, 165));
        hashArea = createInspectorArea(new Color(110, 231, 183));
        listArea = createInspectorArea(new Color(196, 181, 253));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 11));
        tabs.addTab("Priority Queue", new JScrollPane(heapArea));
        tabs.addTab("Hash Table", new JScrollPane(hashArea));
        tabs.addTab("Linked List", new JScrollPane(listArea));

        right.add(tabs, BorderLayout.CENTER);
        return right;
    }

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(11, 15, 22));
        bar.setBorder(new EmptyBorder(8, 18, 8, 18));

        statusLabel = new JLabel("Ready! Admit a patient, extract highest priority emergency, or sort records.");
        statusLabel.setForeground(TEXT_LIGHT);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private JTextField addFormField(JPanel parent, String label, String initial) {
        parent.add(fieldLabel(label));
        JTextField tf = new JTextField(initial);
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        tf.setBackground(BG_DARK);
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(TEXT_LIGHT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_DARK),
                new EmptyBorder(4, 8, 4, 8)));
        parent.add(tf);
        parent.add(Box.createVerticalStrut(8));
        return tf;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_LIGHT);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_MUTED);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextArea createInspectorArea(Color fg) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(BG_DARK);
        area.setForeground(fg);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
        return area;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // -------------------------------------------------------------------------
    // ACTIONS
    // -------------------------------------------------------------------------

    private void handleAdmitPatient() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a patient name.");
            return;
        }
        int age;
        double bill;
        try {
            age = Integer.parseInt(ageField.getText().trim());
            bill = Double.parseDouble(billField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age and Bill must be valid numbers.");
            return;
        }
        String dept = (String) deptBox.getSelectedItem();
        int severity = severityBox.getSelectedIndex() + 1;

        Patient p = engine.admitPatient(name, age, dept, severity, bill);
        if (p != null) {
            int bucket = engine.getBucketForId(p.getId());
            nameField.setText("");
            setStatus("Admitted " + p.getId() + " (" + p.getName() + ") -> Min-Heap + Linked List + Hash Bucket [" + bucket + "].");
            refreshAllViews();
        }
    }

    private void handleTreatNext() {
        Patient p = engine.treatNextEmergency();
        if (p == null) {
            setStatus("Emergency Priority Queue is empty.");
            return;
        }
        setStatus("Extracted highest priority patient: " + p.getId() + " " + p.getName()
                + " (Severity " + p.getSeverityLabel() + ") in O(log N)!");
        refreshAllViews();
        JOptionPane.showMessageDialog(
                this,
                "Now Treating Next Emergency Patient:\n\n" + p,
                "Min-Heap Extract-Min",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleHashLookup() {
        String id = lookupField.getText().trim().toUpperCase();
        if (id.isEmpty()) {
            return;
        }
        Patient found = engine.findById(id);
        int bucket = engine.getBucketForId(id);
        if (found != null) {
            setStatus("O(1) Hash lookup FOUND " + found.getId() + " in Bucket [" + bucket + "].");
            JOptionPane.showMessageDialog(
                    this,
                    "Found in Hash Bucket [" + bucket + "] (O(1) avg):\n\n" + found,
                    "Hash Lookup Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            setStatus("Patient ID " + id + " not found in Hash Bucket [" + bucket + "].");
            JOptionPane.showMessageDialog(
                    this,
                    "Patient ID " + id + " not found (checked Bucket [" + bucket + "]).",
                    "Not Found",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleDischargeSelected() {
        int row = patientTable.getSelectedRow();
        if (row < 0) {
            setStatus("Select a patient row in the table first.");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        Patient p = engine.dischargePatient(id);
        if (p != null) {
            setStatus("Discharged patient " + p.getId() + " (" + p.getName() + ").");
            refreshAllViews();
        }
    }

    private void handleRemoveSelected() {
        int row = patientTable.getSelectedRow();
        if (row < 0) {
            setStatus("Select a patient row in the table to remove.");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        if (engine.removePatientRecord(id)) {
            setStatus("Removed " + id + " from Hash Table, Linked List, and Triage Heap.");
            refreshAllViews();
        }
    }

    private void refreshAllViews() {
        PatientSorter.SortBy sortBy = (PatientSorter.SortBy) sortByBox.getSelectedItem();
        PatientSorter.Algorithm algo = (PatientSorter.Algorithm) sortAlgoBox.getSelectedItem();
        PatientSorter.SortResult res = engine.getSortedPatients(sortBy, algo);

        tableModel.setRowCount(0);
        for (Patient p : res.getSorted()) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getAge(),
                    p.getDepartment(),
                    p.getSeverityLabel(),
                    p.getStatus(),
                    String.format("%.0f", p.getBillAmount())
            });
        }

        sortStatsLabel.setText(String.format("Comparisons: %d | %.3f ms",
                res.getComparisons(), res.getElapsedMillis()));

        kpiLabel.setText(String.format("Triage Waiting: %d  |  Treated: %d  |  Discharged: %d  |  Total: %d",
                engine.getTriageHeap().size(),
                engine.countByStatus(Patient.Status.TREATED),
                engine.countByStatus(Patient.Status.DISCHARGED),
                engine.getRecordList().size()));

        treatNextBtn.setEnabled(!engine.getTriageHeap().isEmpty());

        heapArea.setText(engine.getTriageHeap().toInspectorString());
        hashArea.setText(engine.getPatientMap().toBucketMapString());
        listArea.setText(engine.getRecordList().toInspectorString());
    }

    private void setStatus(String msg) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
        }
    }
}
