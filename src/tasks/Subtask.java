package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Subtask extends Task {
    private int epicNumber;

    public Subtask(int id, String title, String description, Status status, int epicNumber, long duration,
                   LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
        this.epicNumber = epicNumber;
    }

    public Subtask(String title, String description, Status status, int epicNumber, long duration,
                   LocalDateTime startTime) {
        super(title, description, status, duration, startTime);
        this.id = 0;
        this.epicNumber = epicNumber;
    }

    public int getEpicNumber() {
        return epicNumber;
    }

    @Override
    public String toString() {
        return "ПОДЗАДАЧА {" +
                "№=" + id +
                ", Название ='" + title + '\'' +
                ", Описание ='" + description + '\'' +
                ", Статус ='" + status + '\'' +
                ", Относится к эпику ='" + epicNumber + '\'' +
                ", Длительность ='" + duration + '\'' +
                ", Дата начала ='" + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + '\'' +
                ", Дата конца ='" + startTime
                   .plusMinutes(duration).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + '\'' +
                '}' +
                "\n";
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,SUBTASK,%s,%s,%s,%s,%s,%s", id, title, status, description, epicNumber,
                duration, startTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Subtask subTask = (Subtask) obj;
        return subTask.title.equals(title) && subTask.description.equals(description)
                && subTask.status.equals(status) && subTask.startTime.isEqual(startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title,description,status,startTime);
    }
}
