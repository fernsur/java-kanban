package manager;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.Status;
import tasks.TaskType;

import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {
    public static String historyToString(HistoryManager manager) {
        List<String> history = new ArrayList<>();
        for (Task task: manager.getHistory()) {
            history.add(String.valueOf(task.getId()));
        }
        return String.join(",", history);
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> idHistory = new ArrayList<>();
        String[] parts = value.split(",");
        for (String part: parts) {
            Integer id = Integer.parseInt(part);
            idHistory.add(id);
        }
        return idHistory;
    }

    public static Task fromString(String[] parts) {
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case TASK:
                return new Task(id, title, description, status);
            case EPIC:
                return new Epic(id,title,description,status);
            case SUBTASK:
                int epicNumber = Integer.parseInt(parts[5]);
                return new Subtask(id,title,description,status,epicNumber);
            default:
                return null;
        }
    }
}
