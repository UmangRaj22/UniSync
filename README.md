# UniSync – Timetable Management System (Java Swing)

UniSync is a desktop-based timetable generation system built using Java Swing. It allows users to manage subjects, sections, rooms, and automatically generate optimized timetables using a constraint-based scheduling approach.

---

## Features

### Subject Management

* Add subjects with:

  * Subject Name
  * Faculty
  * Section
  * Type (Theory / Lab)
  * Hours per week
* Visual subject table for tracking added entries

---

### Section-Based Scheduling

* Automatic section detection from subjects
* Dropdown to generate timetable per section
* Supports multi-section environments (e.g., CSE-AB, CSE-CD)

---

### Dynamic Room Management

* Input rooms manually (comma-separated)

  * Example: `101,102,Lab1,Lab2`
* Automatic classification:

  * Rooms containing "lab" → Lab rooms
  * Others → Classrooms

---

### Smart Timetable Generation

* Constraint-based scheduling using backtracking
* Balanced and randomized subject allocation
* Avoids repetitive fixed time slots

---

### Scheduling Constraints

* Faculty clash prevention
* Room clash prevention
* Lab subjects:

  * Assigned only to lab rooms
  * Allocated in continuous slots (2 slots minimum)
* Theory subjects assigned only to classrooms
* Unassigned slots are marked as `---`

---

### UI Features

* Clean table-based timetable display
* Center-aligned multi-line cells
* Manual cell editing support
* Clear individual slots

---

### File Support

* Import data via CSV
* Export generated timetable to PDF

---

## Tech Stack

* Java
* Java Swing (JFrame, JTable, JOptionPane)
* iText (for PDF export)

---

## Project Structure

```
UniSync/
├── Main.java
├── model/
│   ├── Subject.java
│   ├── Room.java
│   ├── TimeSlot.java
├── service/
│   └── Scheduler.java
├── ui/
│   └── MainFrame.java
```

---

## How to Run

1. Open terminal in project root:

   ```
   G:\Unisync
   ```

2. Compile:

   ```
   javac Main.java
   ```

3. Run:

   ```
   java Main
   ```

---

## Usage

1. Enter subject details
2. Click **Add Subject** (repeat for all subjects)
3. Enter:

   * Days → `Mon,Tue,Wed,Thu,Fri`
   * Time slots →
     `9:30-10:20,10:20-11:10,11:10-12:00,12:00-12:50,12:50-1:35,1:35-2:20,2:20-3:05,3:05-3:50,3:50-4:35`
4. Enter rooms:

   ```
   101,102,Lab1
   ```
5. Select section from dropdown
6. Click **Generate**
7. View timetable in table

---

## CSV Format

```
Subject,Type,Faculty,Section,Hours
```

### Example:

```
DBMS,Theory,Faculty Name,CSE-AB,4
JAVA Lab,Lab,Faculty Name,CSE-AB,2
```

---

## Future Enhancements

* Faculty timetable view
* Color-coded timetable (Lab / Theory / Free)
* Database integration (SQLite)
* JavaFX migration (modern UI)
* AI/ML-based scheduling optimization
* Conflict visualization dashboard

---

## License

This project is for educational and learning purposes.
