package tasks;

public class Subtask extends Task {
    protected String epicNumber;
    public Subtask(int id, String title, String description, Status status, String epicNumber) {
        super(id, title, description, status);
        this.epicNumber = epicNumber;
    }

    public String getEpicNumber() {
        return epicNumber;
    }

    @Override
    public String toString() {
        return "ПОДЗАДАЧА {" +
                "№=" + id +
                ", Название ='" + title + '\'' +
                ", Описание ='" + description + '\'' +
                ", Статус ='" + status + '\'' +
                ", Примечание ='" + epicNumber + '\'' +
                '}' +
                "\n";
    }
}
