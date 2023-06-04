package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected int id;
    protected String title;
    protected String description;
    protected Status status;
    protected long duration;
    protected LocalDateTime startTime;

    public Task(int id, String title, String description, Status status, long duration, LocalDateTime startTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String title, String description, Status status, long duration, LocalDateTime startTime) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    @Override
    public String toString() {
        return "ЗАДАЧА {" +
                "№=" + id +
                ", Название ='" + title + '\'' +
                ", Описание ='" + description + '\'' +
                ", Статус ='" + status + '\'' +
                ", Длительность ='" + duration + '\'' +
                ", Дата начала ='" + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + '\'' +
                ", Дата конца ='" + startTime
                   .plusMinutes(duration).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + '\'' +
                '}' +
                "\n";
    }

    public String toStringFromFile() {
        return String.format("%s,TASK,%s,%s,%s,-,%s,%s", id, title, status, description, duration, startTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return task.title.equals(title) && task.description.equals(description)
                && task.status.equals(status) && task.startTime.isEqual(startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title,description,status,startTime);
    }
}
