package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import java.io.*;

import model.*;
import service.Scheduler;

public class MainFrame extends JFrame {

    JTextField subjectField, facultyField, sectionField, hoursField, daysField, slotsField;
    JComboBox<String> typeBox;
    JTable table;
    DefaultTableModel tableModel;

    List<Subject> subjects = new ArrayList<>();
    boolean isManualEdited = false;

    public MainFrame() {

        setTitle("TimeTable Maker");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔹 FORM PANEL
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));

        subjectField = new JTextField();
        facultyField = new JTextField();
        sectionField = new JTextField();
        hoursField = new JTextField();

        typeBox = new JComboBox<>(new String[]{"Theory", "Lab"});

        daysField = new JTextField("Mon,Tue,Wed");
        slotsField = new JTextField("9:30-10:20,10:20-11:10,11:10-12:00");

        form.add(new JLabel("Subject"));
        form.add(subjectField);

        form.add(new JLabel("Faculty"));
        form.add(facultyField);

        form.add(new JLabel("Section"));
        form.add(sectionField);

        form.add(new JLabel("Type"));
        form.add(typeBox);

        form.add(new JLabel("Hours/Week"));
        form.add(hoursField);

        form.add(new JLabel("Days"));
        form.add(daysField);

        form.add(new JLabel("Time Slots"));
        form.add(slotsField);

        // 🔹 TABLE
        tableModel = new DefaultTableModel();

        table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        // 🔹 SPLIT PANE
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(form),
                new JScrollPane(table)
        );

        splitPane.setDividerLocation(220);
        add(splitPane, BorderLayout.CENTER);

        // 🔹 BUTTON PANEL
        JPanel buttonPanel = new JPanel();

        JButton addBtn = new JButton("Add Subject");
        JButton generateBtn = new JButton("Generate");
        JButton importBtn = new JButton("Import CSV");
        JButton exportBtn = new JButton("Export PDF");
        JButton editBtn = new JButton("Edit Cell");
        JButton clearBtn = new JButton("Clear Slot");

        buttonPanel.add(addBtn);
        buttonPanel.add(generateBtn);
        buttonPanel.add(importBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(clearBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // 🔹 ACTIONS
        addBtn.addActionListener(e -> addSubject());
        generateBtn.addActionListener(e -> generateTT());
        importBtn.addActionListener(e -> importCSV());
        exportBtn.addActionListener(e -> exportPDF());
        editBtn.addActionListener(e -> editCell());
        clearBtn.addActionListener(e -> clearSlot());
    }

    // 🔹 ADD SUBJECT
    private void addSubject() {

        if (subjectField.getText().isEmpty() ||
                facultyField.getText().isEmpty() ||
                sectionField.getText().isEmpty() ||
                hoursField.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Fill all fields");
            return;
        }

        int hours;

        try {
            hours = Integer.parseInt(hoursField.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hours must be number");
            return;
        }

        subjects.add(new Subject(
                subjectField.getText(),
                (String) typeBox.getSelectedItem(),
                facultyField.getText(),
                sectionField.getText(),
                hours
        ));

        JOptionPane.showMessageDialog(this, "Subject Added");
    }

    // 🔹 GENERATE TIMETABLE
    private void generateTT() {

        // Manual edit check
        if (isManualEdited) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Manual changes will be lost. Continue?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            isManualEdited = false;
        }

        if (daysField.getText().isEmpty() || slotsField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter days & slots");
            return;
        }

        List<Room> rooms = Arrays.asList(
                new Room("101", "Classroom"),
                new Room("Lab1", "Lab")
        );

        String[] days = daysField.getText().split(",");
        String[] times = slotsField.getText().split(",");

        List<TimeSlot> slots = new ArrayList<>();

        for (String day : days) {
            for (String time : times) {
                slots.add(new TimeSlot(day.trim(), time.trim()));
            }
        }

        Map<TimeSlot, String> timetable =
                Scheduler.generate(subjects, rooms, slots);

        // Columns
        String[] columns = new String[times.length + 1];
        columns[0] = "Day/Time";

        for (int i = 0; i < times.length; i++) {
            columns[i + 1] = times[i].trim();
        }

        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        // Fill table
        for (String day : days) {

            String[] row = new String[times.length + 1];
            row[0] = day.trim();

            for (int i = 1; i < row.length; i++) {
                row[i] = "-";
            }

            for (TimeSlot slot : timetable.keySet()) {

                if (slot.getDay().equals(day.trim())) {

                    for (int i = 0; i < times.length; i++) {

                        if (slot.getTime().equals(times[i].trim())) {
                            row[i + 1] = timetable.get(slot);
                        }
                    }
                }
            }

            tableModel.addRow(row);
        }
    }

    // 🔹 IMPORT CSV
    private void importCSV() {

        JFileChooser fileChooser = new JFileChooser();

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            File file = fileChooser.getSelectedFile();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line = br.readLine();
                tableModel.setColumnIdentifiers(line.split(","));
                tableModel.setRowCount(0);

                while ((line = br.readLine()) != null) {
                    tableModel.addRow(line.split(","));
                }

                JOptionPane.showMessageDialog(this, "Imported");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error");
            }
        }
    }

    // 🔹 EXPORT PDF
    private void exportPDF() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("timetable.pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

            try {
                File file = fileChooser.getSelectedFile();

                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(file));

                document.open();

                document.add(new com.itextpdf.text.Paragraph("TimeTable\n\n"));

                int cols = tableModel.getColumnCount();

                com.itextpdf.text.pdf.PdfPTable pdfTable =
                        new com.itextpdf.text.pdf.PdfPTable(cols);

                for (int i = 0; i < cols; i++) {
                    pdfTable.addCell(tableModel.getColumnName(i));
                }

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < cols; j++) {
                        Object val = tableModel.getValueAt(i, j);
                        pdfTable.addCell(val == null ? "-" : val.toString());
                    }
                }

                document.add(pdfTable);
                document.close();

                JOptionPane.showMessageDialog(this, "PDF Exported");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting PDF");
            }
        }
    }

    // 🔹 EDIT CELL
    private void editCell() {

        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();

        if (row == -1 || col <= 0) {
            JOptionPane.showMessageDialog(this, "Select valid cell");
            return;
        }

        String current = (String) tableModel.getValueAt(row, col);

        String updated = JOptionPane.showInputDialog(this, "Edit", current);

        if (updated != null && !updated.isEmpty()) {
            tableModel.setValueAt(updated, row, col);
            isManualEdited = true;
        }
    }

    // 🔹 CLEAR SLOT
    private void clearSlot() {

        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();

        if (row == -1 || col <= 0) {
            JOptionPane.showMessageDialog(this, "Select valid cell");
            return;
        }

        tableModel.setValueAt("-", row, col);
        isManualEdited = true;
    }
}