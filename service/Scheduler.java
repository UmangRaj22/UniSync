package service;

import model.*;
import java.util.*;

public class Scheduler {

    public static Map<TimeSlot, String> generate(
            List<Subject> subjects,
            List<Room> rooms,
            List<TimeSlot> slots) {

        Map<TimeSlot, String> timetable = new LinkedHashMap<>();
        Map<Subject, Integer> remaining = new HashMap<>();
        Map<Subject, Integer> subjectCount = new HashMap<>();

        for (Subject s : subjects) {
            remaining.put(s, s.getHoursPerWeek());
        }

        Collections.shuffle(subjects);

        backtrack(0, slots, subjects, rooms, timetable, remaining, subjectCount);

        return timetable;
    }

    private static boolean backtrack(
            int index,
            List<TimeSlot> slots,
            List<Subject> subjects,
            List<Room> rooms,
            Map<TimeSlot, String> timetable,
            Map<Subject, Integer> remaining,
            Map<Subject, Integer> subjectCount) {

        if (index >= slots.size()) return true;

        TimeSlot slot = slots.get(index);

        // Lunch slot fixed
        if (isLunchSlot(slot)) {
            timetable.put(slot, "LUNCH");
            return backtrack(index + 1, slots, subjects, rooms, timetable, remaining, subjectCount);
        }

        // Sort for fairness
        subjects.sort(Comparator.comparingInt(s -> subjectCount.getOrDefault(s, 0)));

        for (Subject subject : subjects) {

            if (remaining.get(subject) <= 0) continue;

            for (Room room : rooms) {

                if (!isValid(subject, room, slot, timetable)) continue;

                // LAB handling
                if (subject.getType().equals("Lab")) {

                    TimeSlot next = getNextSlot(slots, slot);

                    if (next == null || isLunchSlot(next) || !slot.getDay().equals(next.getDay())) continue;

                    if (timetable.containsKey(next)) continue;

                    String value = format(subject, room) + " (LAB)";

                    timetable.put(slot, value);
                    timetable.put(next, value);

                    remaining.put(subject, remaining.get(subject) - 2);
                    subjectCount.put(subject, subjectCount.getOrDefault(subject, 0) + 2);

                    if (backtrack(index + 2, slots, subjects, rooms, timetable, remaining, subjectCount))
                        return true;

                    timetable.remove(slot);
                    timetable.remove(next);
                    remaining.put(subject, remaining.get(subject) + 2);
                    subjectCount.put(subject, subjectCount.get(subject) - 2);
                }

                else {
                    timetable.put(slot, format(subject, room));

                    remaining.put(subject, remaining.get(subject) - 1);
                    subjectCount.put(subject, subjectCount.getOrDefault(subject, 0) + 1);

                    if (backtrack(index + 1, slots, subjects, rooms, timetable, remaining, subjectCount))
                        return true;

                    timetable.remove(slot);
                    remaining.put(subject, remaining.get(subject) + 1);
                    subjectCount.put(subject, subjectCount.get(subject) - 1);
                }
            }
        }

        timetable.put(slot, "---");

        if (backtrack(index + 1, slots, subjects, rooms, timetable, remaining, subjectCount))
            return true;

        timetable.remove(slot);
        return false;
    }

    private static boolean isLunchSlot(TimeSlot slot) {
        return slot.getTime().equals("12:50-1:35");
    }

    private static TimeSlot getNextSlot(List<TimeSlot> slots, TimeSlot current) {

        for (int i = 0; i < slots.size() - 1; i++) {

            TimeSlot curr = slots.get(i);
            TimeSlot next = slots.get(i + 1);

        
            if (curr.equals(current) && curr.getDay().equals(next.getDay())) {
                return next;
            }
        }
        return null;
    }

    private static boolean isValid(
            Subject subject,
            Room room,
            TimeSlot slot,
            Map<TimeSlot, String> timetable) {

        if (subject.getType().equals("Lab") && !room.getType().equals("Lab"))
            return false;

        if (subject.getType().equals("Theory") && !room.getType().equals("Classroom"))
            return false;

        if (isLunchSlot(slot)) return false;

        for (TimeSlot ts : timetable.keySet()) {
            if (ts.getDay().equals(slot.getDay())) {
                if (timetable.get(ts).contains(subject.getName()))
                    return false;
            }
        }

        return true;
    }

    private static String format(Subject s, Room r) {
        return s.getName() + "\n(" + s.getFaculty() + ")\nRoom: " + r.getRoomNumber();
    }
} 