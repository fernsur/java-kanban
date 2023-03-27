package tasks;

import java.util.Arrays;

public class Epic extends Task {
    protected int[] epicSubtasks;

    public Epic(int id, String title, String description, Status status, int[] epicSubtasks) {
        super(id, title, description, status);
        this.epicSubtasks = epicSubtasks;
    }

    public int[] getEpicSubtasks() {
        return epicSubtasks;
    }

    @Override
    public String toString() {
        return "ЭПИК {" +
                "№=" + id +
                ", Название ='" + title + '\'' +
                ", Описание ='" + description + '\'' +
                ", Статус ='" + status + '\'' +
                ", Подзадачи ='" + Arrays.toString(epicSubtasks) + '\'' +
                '}' +
                "\n";
    }
}
