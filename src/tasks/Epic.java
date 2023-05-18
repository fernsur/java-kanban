package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> epicSubtasks;

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.epicSubtasks = new ArrayList<>();
    }

    public Epic(int id, String title, String description, Status status, ArrayList<Integer> epicSubtasks) {
        super(id, title, description, status);
        this.epicSubtasks = epicSubtasks;
    }

    public ArrayList<Integer> getEpicSubtasks() {
        return epicSubtasks;
    }

    public void setEpicSubtasks(ArrayList<Integer> epicSubtasks) {
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
        return String.format("%s,EPIC,%s,%s,%s,%s", id, title, description, status, epicSubtasks);
    }
}
