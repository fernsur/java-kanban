package manager;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.Status;
import tasks.TaskType;

import java.time.LocalDateTime;
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
        long duration = Long.parseLong(parts[6]);;
        LocalDateTime startTime = LocalDateTime.parse(parts[7]);;

        switch (type) {
            case TASK:
                return new Task(id, title, description, status, duration, startTime);
            case EPIC:
                return new Epic(id, title, description, status, duration, startTime);
            case SUBTASK:
                int epicNumber = Integer.parseInt(parts[5]);
                return new Subtask(id, title, description, status, epicNumber, duration, startTime);
            default:
                return null;
        }
    }
}
