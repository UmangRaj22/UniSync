package model;

public class Subject {

    private String name;
    private String type;        // Theory / Lab
    private String faculty;
    private String section;     // e.g., CSE-A, CSE-B
    private int hoursPerWeek;   // number of lectures per week

    // ✅ Constructor
    public Subject(String name, String type, String faculty, String section, int hoursPerWeek) {
        this.name = name;
        this.type = type;
        this.faculty = faculty;
        this.section = section;
        this.hoursPerWeek = hoursPerWeek;
    }

    // ✅ Getters
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getSection() {
        return section;
    }

    public int getHoursPerWeek() {
        return hoursPerWeek;
    }

    // ✅ Setters (optional but useful for UI editing)
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setHoursPerWeek(int hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
    }

    // ✅ toString (useful for debugging / logs)
    @Override
    public String toString() {
        return name + " [" + section + "] (" + type + ") - " + faculty +
               " | Hours/Week: " + hoursPerWeek;
    }
}