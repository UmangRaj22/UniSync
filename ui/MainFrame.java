package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Color;

import java.util.*;
import java.io.*;

import model.*;
import service.Scheduler;

public class MainFrame extends JFrame {

    JTextField subjectField, facultyField, sectionField, hoursField, daysField, slotsField, roomField;
    JComboBox<String> typeBox, sectionDropdown;

    JTable table, subjectTable;
    DefaultTableModel tableModel, subjectTableModel;

    List<Subject> subjects = new ArrayList<>();
    boolean isManualEdited = false;

    public MainFrame() {

        setTitle("Timetable Management System");
        setSize(1100, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));

        subjectField = new JTextField();
        facultyField = new JTextField();
        sectionField = new JTextField();
        hoursField = new JTextField();
        roomField = new JTextField();

        typeBox = new JComboBox<>(new String[]{"Theory", "Lab"});
        sectionDropdown = new JComboBox<>();

        daysField = new JTextField("Mon,Tue,Wed,Thu,Fri");
        slotsField = new JTextField("9:30-10:20,10:20-11:10,11:10-12:00,12:00-12:50,12:50-1:35,1:35-2:20,2:20-3:05,3:05-3:50,3:50-4:35");

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
        form.add(new JLabel("Rooms (comma separated)"));
        form.add(roomField);
        form.add(new JLabel("Days"));
        form.add(daysField);
        form.add(new JLabel("Time Slots"));
        form.add(slotsField);

        subjectTableModel = new DefaultTableModel(
                new String[]{"Subject", "Faculty", "Section", "Type", "Hours"}, 0);

        subjectTable = new JTable(subjectTableModel);
        JScrollPane subjectScroll = new JScrollPane(subjectTable);
        subjectScroll.setPreferredSize(new Dimension(1000, 120));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(subjectScroll, BorderLayout.CENTER);
        topPanel.add(sectionDropdown, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        table.setRowHeight(60);
        table.setDefaultRenderer(Object.class, new CenterRenderer());

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(form),
                new JScrollPane(table)
        );

        splitPane.setDividerLocation(260);
        add(splitPane, BorderLayout.CENTER);

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

        addBtn.addActionListener(e -> addSubject());
        generateBtn.addActionListener(e -> generateTT());
        importBtn.addActionListener(e -> importCSV());
        exportBtn.addActionListener(e -> exportPDF());
        editBtn.addActionListener(e -> editCell());
        clearBtn.addActionListener(e -> clearSlot());
    }

    private void addSubject() {

        if (subjectField.getText().isEmpty() ||
                facultyField.getText().isEmpty() ||
                sectionField.getText().isEmpty() ||
                hoursField.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "All fields are required");
            return;
        }

        int hours;

        try {
            hours = Integer.parseInt(hoursField.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hours must be numeric");
            return;
        }

        if (typeBox.getSelectedItem().equals("Lab") && hours % 2 != 0) {
            JOptionPane.showMessageDialog(this, "Lab hours must be multiple of 2");
            return;
        }

        Subject s = new Subject(
                subjectField.getText(),
                (String) typeBox.getSelectedItem(),
                facultyField.getText(),
                sectionField.getText(),
                hours
        );

        subjects.add(s);

        subjectTableModel.addRow(new Object[]{
                s.getName(),
                s.getFaculty(),
                s.getSection(),
                s.getType(),
                s.getHoursPerWeek()
        });

        if (((DefaultComboBoxModel<String>) sectionDropdown.getModel())
                .getIndexOf(s.getSection()) == -1) {
            sectionDropdown.addItem(s.getSection());
        }

        JOptionPane.showMessageDialog(this, "Subject added");
    }

    private void generateTT() {

        if (subjects.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No subjects available");
            return;
        }

        String selectedSection = (String) sectionDropdown.getSelectedItem();

        if (selectedSection == null) {
            JOptionPane.showMessageDialog(this, "Select a section");
            return;
        }

        List<Room> rooms = new ArrayList<>();

        String[] roomInputs = roomField.getText().split(",");

        for (String r : roomInputs) {
            r = r.trim();
            if (r.isEmpty()) continue;

            if (r.toLowerCase().contains("lab")) {
                rooms.add(new Room(r, "Lab"));
            } else {
                rooms.add(new Room(r, "Classroom"));
            }
        }

        if (rooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter at least one room");
            return;
        }

        String[] days = daysField.getText().split(",");
        String[] times = slotsField.getText().split(",");

        List<TimeSlot> slots = new ArrayList<>();

        for (String day : days) {
            for (String time : times) {
                slots.add(new TimeSlot(day.trim(), time.trim()));
            }
        }

        List<Subject> filtered = new ArrayList<>();

        for (Subject s : subjects) {
            if (s.getSection().equals(selectedSection)) {
                filtered.add(s);
            }
        }

        Map<TimeSlot, String> timetable =
                Scheduler.generate(filtered, rooms, slots);

        String[] columns = new String[times.length + 1];
        columns[0] = "Day/Time";

        for (int i = 0; i < times.length; i++) {
            columns[i + 1] = times[i].trim();
        }

        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        for (String day : days) {

            String[] row = new String[times.length + 1];
            row[0] = day.trim();

            Arrays.fill(row, "-");
            row[0] = day.trim();

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

    private void importCSV() {

        JFileChooser fileChooser = new JFileChooser();

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            File file = fileChooser.getSelectedFile();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line;

                while ((line = br.readLine()) != null) {

                    String[] data = line.split(",");

                    if (data.length < 5) continue;

                    Subject s = new Subject(
                            data[0].trim(),
                            data[1].trim(),
                            data[2].trim(),
                            data[3].trim(),
                            Integer.parseInt(data[4].trim())
                    );

                    subjects.add(s);

                    subjectTableModel.addRow(new Object[]{
                            s.getName(),
                            s.getFaculty(),
                            s.getSection(),
                            s.getType(),
                            s.getHoursPerWeek()
                    });

                    if (((DefaultComboBoxModel<String>) sectionDropdown.getModel())
                            .getIndexOf(s.getSection()) == -1) {
                        sectionDropdown.addItem(s.getSection());
                    }
                }

                JOptionPane.showMessageDialog(this, "CSV imported");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error importing CSV");
            }
        }
    }

    private void exportPDF() {
        JOptionPane.showMessageDialog(this, "PDF export unchanged");
    }

    private void editCell() {

        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();

        if (row == -1 || col <= 0) {
            JOptionPane.showMessageDialog(this, "Select a valid cell");
            return;
        }

        String current = (String) tableModel.getValueAt(row, col);

        String updated = JOptionPane.showInputDialog(this, "Edit", current);

        if (updated != null && !updated.isEmpty()) {
            tableModel.setValueAt(updated, row, col);
            isManualEdited = true;
        }
    }

    private void clearSlot() {

        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();

        if (row == -1 || col <= 0) {
            JOptionPane.showMessageDialog(this, "Select a valid cell");
            return;
        }

        tableModel.setValueAt("-", row, col);
        isManualEdited = true;
    }

    static class CenterRenderer extends JTextArea implements TableCellRenderer {

        public CenterRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setFont(new Font("Arial", Font.BOLD, 12));
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            String text = value == null ? "" : value.toString();
            setText(text);

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                return this;
            }

            if (text.toLowerCase().contains("lab")) {
                setBackground(new Color(200, 255, 200));
            } else {
                setBackground(Color.WHITE);
            }

            return this;
        }
    }
} 