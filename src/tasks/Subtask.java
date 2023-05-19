package tasks;

public class Subtask extends Task {
    private int epicNumber;
    public Subtask(int id, String title, String description, Status status, int epicNumber) {
        super(id, title, description, status);
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
                '}' +
                "\n";
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,SUBTASK,%s,%s,%s,%s", id, title, status, description, epicNumber);
    }
}
