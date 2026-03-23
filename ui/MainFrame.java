package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

import model.*;
import service.Scheduler;

public class MainFrame extends JFrame {

    JTextField subjectField, facultyField, sectionField, hoursField, daysField, slotsField;
    JComboBox<String> typeBox;
    JTable table;
    DefaultTableModel tableModel;

    java.util.List<Subject> subjects = new ArrayList<>();

    public MainFrame() {

        setTitle("TimeTable Maker");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔹 FORM PANEL
        JPanel form = new JPanel(new GridLayout(8, 2));

        subjectField = new JTextField();
        facultyField = new JTextField();
        sectionField = new JTextField();
        hoursField = new JTextField();

        typeBox = new JComboBox<>(new String[]{"Theory", "Lab"});

        daysField = new JTextField("Mon,Tue,Wed");
        slotsField = new JTextField("9-10,10-11,11-12");

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

        form.add(new JLabel("Days (comma separated)"));
        form.add(daysField);

        form.add(new JLabel("Time Slots (comma separated)"));
        form.add(slotsField);

        JButton addBtn = new JButton("Add Subject");
        JButton generateBtn = new JButton("Generate Timetable");

        form.add(addBtn);
        form.add(generateBtn);

        add(form, BorderLayout.NORTH);

        // 🔹 TABLE
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 🔹 ACTIONS
        addBtn.addActionListener(e -> addSubject());
        generateBtn.addActionListener(e -> generateTT());
    }

    // 🔹 ADD SUBJECT
    private void addSubject() {

        if (subjectField.getText().isEmpty() ||
            facultyField.getText().isEmpty() ||
            sectionField.getText().isEmpty() ||
            hoursField.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill all subject fields");
            return;
        }

        int hours;
        try {
            hours = Integer.parseInt(hoursField.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hours must be a number");
            return;
        }

        subjects.add(new Subject(
                subjectField.getText().trim(),
                (String) typeBox.getSelectedItem(),
                facultyField.getText().trim(),
                sectionField.getText().trim(),
                hours
        ));

        JOptionPane.showMessageDialog(this, "Subject Added");
    }

    // 🔹 GENERATE TIMETABLE
    private void generateTT() {

        if (daysField.getText().trim().isEmpty() ||
            slotsField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Enter days and time slots (comma separated)");
            return;
        }

        // 🔹 Rooms (static for now)
        java.util.List<Room> rooms = Arrays.asList(
                new Room("101", "Classroom"),
                new Room("Lab1", "Lab")
        );

        // 🔹 Parse input
        String[] days = daysField.getText().split(",");
        String[] times = slotsField.getText().split(",");

        // 🔹 Create slots dynamically
        java.util.List<TimeSlot> slots = new ArrayList<>();

        for (String day : days) {
            for (String time : times) {
                slots.add(new TimeSlot(day.trim(), time.trim()));
            }
        }

        // 🔹 Generate timetable
        Map<TimeSlot, String> timetable =
                Scheduler.generate(subjects, rooms, slots);

        // 🔹 Dynamic Columns
        String[] columns = new String[times.length + 1];
        columns[0] = "Day/Time";

        for (int i = 0; i < times.length; i++) {
            columns[i + 1] = times[i].trim();
        }

        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        // 🔹 Fill Table
        for (String day : days) {

            String[] row = new String[times.length + 1];
            row[0] = day.trim();

            // default empty
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
}