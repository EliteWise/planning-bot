package fr.elite.pb.database.model;

import java.util.Objects;

public class Statistic {

    private String pseudo;
    private int taskInProgress;
    private int taskCompleted;

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getTaskInProgress() {
        return taskInProgress;
    }

    public void setTaskInProgress(int taskInProgress) {
        this.taskInProgress = taskInProgress;
    }

    public int getTaskCompleted() {
        return taskCompleted;
    }

    public void setTaskCompleted(int taskCompleted) {
        this.taskCompleted = taskCompleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistic statistic = (Statistic) o;
        return taskInProgress == statistic.taskInProgress &&
                taskCompleted == statistic.taskCompleted &&
                Objects.equals(pseudo, statistic.pseudo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pseudo, taskInProgress, taskCompleted);
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "pseudo='" + pseudo + '\'' +
                ", taskInProgress=" + taskInProgress +
                ", taskCompleted=" + taskCompleted +
                '}';
    }
}
