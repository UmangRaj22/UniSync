package service;

import model.*;
import java.util.*;

public class Scheduler {

    public static Map<TimeSlot, String> generate(
            List<Subject> subjects,
            List<Room> rooms,
            List<TimeSlot> slots) {

        Map<TimeSlot, String> timetable = new LinkedHashMap<>();

        // Track usage
        Map<String, Set<String>> facultySchedule = new HashMap<>();
        Map<String, Set<String>> roomSchedule = new HashMap<>();

        // Shuffle for better distribution
        Collections.shuffle(subjects);

        for (TimeSlot slot : slots) {

            boolean assigned = false;

            for (Subject subject : subjects) {

                for (Room room : rooms) {

                    if (!isValid(subject, room, slot,
                            facultySchedule, roomSchedule)) {
                        continue;
                    }

                    // Assign
                    timetable.put(slot,
                            subject.getName() + " [" + subject.getSection() + "] - "
                                    + subject.getFaculty() + " @ " + room.getRoomNumber());

                    markBusy(subject, room, slot,
                            facultySchedule, roomSchedule);

                    assigned = true;
                    break;
                }

                if (assigned) break;
            }

            // If no subject fits → mark FREE
            if (!assigned) {
                timetable.put(slot, "FREE");
            }
        }

        return timetable;
    }

    // ✅ Check constraints
    private static boolean isValid(
            Subject subject,
            Room room,
            TimeSlot slot,
            Map<String, Set<String>> facultySchedule,
            Map<String, Set<String>> roomSchedule) {

        // Room type constraint
        if (subject.getType().equals("Lab") && !room.getType().equals("Lab"))
            return false;

        if (subject.getType().equals("Theory") && !room.getType().equals("Classroom"))
            return false;

        String slotKey = slot.getDay() + "_" + slot.getTime();

        // Faculty clash
        if (facultySchedule.containsKey(subject.getFaculty()) &&
                facultySchedule.get(subject.getFaculty()).contains(slotKey))
            return false;

        // Room clash
        if (roomSchedule.containsKey(room.getRoomNumber()) &&
                roomSchedule.get(room.getRoomNumber()).contains(slotKey))
            return false;

        return true;
    }

    // ✅ Mark resources as busy
    private static void markBusy(
            Subject subject,
            Room room,
            TimeSlot slot,
            Map<String, Set<String>> facultySchedule,
            Map<String, Set<String>> roomSchedule) {

        String slotKey = slot.getDay() + "_" + slot.getTime();

        facultySchedule
                .computeIfAbsent(subject.getFaculty(), k -> new HashSet<>())
                .add(slotKey);

        roomSchedule
                .computeIfAbsent(room.getRoomNumber(), k -> new HashSet<>())
                .add(slotKey);
    }
}