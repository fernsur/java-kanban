package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> epicSubtasks;
    private LocalDateTime endTime;

    public Epic(int id, String title, String description, Status status, LocalDateTime startTime) {
        super(id, title, description, status);
        this.epicSubtasks = new ArrayList<>();
        this.startTime = startTime;
        this.duration = 0;
        this.endTime = getEndTime();
    }

    public Epic(int id, String title, String description, Status status, long duration, LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
        this.epicSubtasks = new ArrayList<>();
        this.endTime = getEndTime();
    }

    public Epic(String title, String description, Status status, LocalDateTime startTime) {
        super(title, description, status);
        this.epicSubtasks = new ArrayList<>();
        this.startTime = startTime;
        this.duration = 0;
        this.endTime = getEndTime();
    }

    public List<Integer> getEpicSubtasks() {
        return epicSubtasks;
    }

    public void setEpicSubtasks(List<Integer> epicSubtasks) {
        this.epicSubtasks = epicSubtasks;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "ЭПИК {" +
                "№=" + id +
                ", Название ='" + title + '\'' +
                ", Описание ='" + description + '\'' +
                ", Статус ='" + status + '\'' +
                ", Подзадачи ='" + epicSubtasks + '\'' +
                ", Длительность ='" + duration + '\'' +
                ", Дата начала ='" + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + '\'' +
                ", Дата конца ='" + endTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + '\'' +
                '}' +
                "\n";
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,EPIC,%s,%s,%s,-,%s,%s", id, title, status, description, duration, startTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return epic.title.equals(title) && epic.description.equals(description)
                && epic.status.equals(status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title,description,status);
    }
}
