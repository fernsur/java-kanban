package tasks;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private List<Integer> epicSubtasks;

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.epicSubtasks = new ArrayList<>();
    }

    public List<Integer> getEpicSubtasks() {
        return epicSubtasks;
    }

    public void setEpicSubtasks(List<Integer> epicSubtasks) {
        this.epicSubtasks = epicSubtasks;
    }

    @Override
    public String toString() {
        return "ЭПИК {" +
                "№=" + id +
                ", Название ='" + title + '\'' +
                ", Описание ='" + description + '\'' +
                ", Статус ='" + status + '\'' +
                ", Подзадачи ='" + epicSubtasks + '\'' +
                '}' +
                "\n";
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,EPIC,%s,%s,%s", id, title, status, description);
    }
}
