package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.KVServer;
import server.KVTaskClient;
import server.LocalDateTimeAdapter;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskManager(String url) {
        kvTaskClient = new KVTaskClient(url);
        load();
    }

    @Override
    public void save() {
        String tasks = gson.toJson(super.getAllTasks());
        kvTaskClient.put("tasks",tasks);
        String epics = gson.toJson(super.getAllEpics());
        kvTaskClient.put("epics",epics);
        String subtasks = gson.toJson(super.getAllSubtasks());
        kvTaskClient.put("subtasks",subtasks);
        String history = gson.toJson(super.history());
        kvTaskClient.put("history", history);
    }

    public void load() {
        if (tasks.size() != 0 && epics.size() != 0 && subtasks.size() != 0) {
            int id = 0;
            List<Task> tasksLoad = new ArrayList<>
                    (List.of(gson.fromJson(kvTaskClient.load("tasks"), Task[].class)));
            for (Task task : tasksLoad) {
                int idCount = task.getId();
                if (idCount > id) {
                    id = idCount;
                }
                tasks.put(idCount, task);
                prioritizedTasks.put(task.getStartTime(), task);
            }

            List<Epic> epicsLoad = new ArrayList<>
                    (List.of(gson.fromJson(kvTaskClient.load("epics"), Epic[].class)));
            for (Epic epic : epicsLoad) {
                int idCount = epic.getId();
                if (idCount > id) {
                    id = idCount;
                }
                epics.put(idCount, epic);
            }

            List<Subtask> subtasksLoad = new ArrayList<>
                    (List.of(gson.fromJson(kvTaskClient.load("subtasks"), Subtask[].class)));
            for (Subtask subtask : subtasksLoad) {
                int idCount = subtask.getId();
                if (idCount > id) {
                    id = idCount;
                }
                subtasks.put(idCount, subtask);
                prioritizedTasks.put(subtask.getStartTime(), subtask);
                updateEpicField(subtask);
            }

            List<Task> historyLoad = new ArrayList<>
                    (List.of(gson.fromJson(kvTaskClient.load("history"), Task[].class)));
            for (Task task : historyLoad) {
                historyManager.add(task);
            }

            identifier = id + 1;
        }
    }
}
