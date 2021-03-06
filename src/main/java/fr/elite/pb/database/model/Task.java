package fr.elite.pb.database.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Task {

    private String title;
    private String mainMemberId;
    private String mainMemberName;
    private List<String> otherMembers;
    private String description;
    private Date creationDate;
    private Date deadline;
    private double timeToComplete;
    private String state;
    private List<Date> alarms;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainMemberId() {
        return mainMemberId;
    }

    public void setMainMemberId(String mainMemberId) {
        this.mainMemberId = mainMemberId;
    }

    public String getMainMemberName() {
        return mainMemberName;
    }

    public void setMainMemberName(String mainMemberName) {
        this.mainMemberName = mainMemberName;
    }

    public List<String> getOtherMembers() {
        return otherMembers;
    }

    public void setOtherMembers(List<String> otherMembers) {
        this.otherMembers = otherMembers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public double getTimeToComplete() {
        return timeToComplete;
    }

    public void setTimeToComplete(double timeToComplete) {
        this.timeToComplete = timeToComplete;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Date> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<Date> alarms) {
        this.alarms = alarms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Double.compare(task.timeToComplete, timeToComplete) == 0 &&
                Objects.equals(title, task.title) &&
                Objects.equals(mainMemberId, task.mainMemberId) &&
                Objects.equals(mainMemberName, task.mainMemberName) &&
                Objects.equals(otherMembers, task.otherMembers) &&
                Objects.equals(description, task.description) &&
                Objects.equals(creationDate, task.creationDate) &&
                Objects.equals(deadline, task.deadline) &&
                Objects.equals(state, task.state) &&
                Objects.equals(alarms, task.alarms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, mainMemberId, mainMemberName, otherMembers, description, creationDate, deadline, timeToComplete, state, alarms);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", mainMemberId='" + mainMemberId + '\'' +
                ", mainMemberName='" + mainMemberName + '\'' +
                ", otherMembers=" + otherMembers +
                ", description='" + description + '\'' +
                ", creationDate=" + creationDate +
                ", deadline=" + deadline +
                ", timeToComplete=" + timeToComplete +
                ", state='" + state + '\'' +
                ", alarms=" + alarms +
                '}';
    }
}
