# UniSync - Timetable Maker (Java Swing)

UniSync is a simple desktop timetable generator built with Java Swing.
It lets you add subjects, define working days/time slots, and generate a timetable with basic clash checks.

## Features

- Add subjects with:
  - Subject name
  - Faculty
  - Section
  - Type (`Theory` or `Lab`)
  - Hours per week
- Define custom days and time slots (comma-separated).
- Auto-generate timetable entries in a table view.
- Basic scheduling constraints:
  - Lab subjects are assigned only to lab rooms.
  - Theory subjects are assigned only to classroom rooms.
  - Faculty clashes are prevented for the same time slot.
  - Room clashes are prevented for the same time slot.
- Unassigned slots are marked as `FREE`.

## Tech Stack

- Java
- Java Swing (`JFrame`, `JTable`, `JOptionPane`)

## Project Structure

```text
Unisync/
|- Main.java
|- model/
|  |- Subject.java
|  |- Room.java
|  `- TimeSlot.java
|- service/
|  `- Scheduler.java
`- ui/
   `- MainFrame.java
```

## How to Run

1. Open terminal in the project root (`g:\Unisync`).
2. Compile:

```bash
javac Main.java
```

3. Run:

```bash
java Main
```

## Usage

1. Enter subject details.
2. Click **Add Subject** (repeat for all subjects).
3. Enter:
   - Days like: `Mon,Tue,Wed`
   - Time slots like: `9-10,10-11,11-12`
4. Click **Generate Timetable**.
5. View the generated timetable in the table.

## Notes / Current Limitations

- Rooms are currently hardcoded in `ui/MainFrame.java`:
  - `101` (`Classroom`)
  - `Lab1` (`Lab`)
- `hoursPerWeek` is captured but not yet strictly enforced by the scheduler.
- Subject load balancing and advanced optimization are not implemented yet.

## Future Improvements

- Enforce `hoursPerWeek` constraints during scheduling.
- Allow room management from UI.
- Export timetable to CSV/PDF.
- Add conflict warnings and validation summaries.
- Add unit tests for scheduler rules.
=======
# UniSync
>>>>>>> 0e2ed57ebf76a7b5ce3ee176d90754e829f40e87
