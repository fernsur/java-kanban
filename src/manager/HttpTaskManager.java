package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.KVTaskClient;
import server.LocalDateTimeAdapter;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends InMemoryTaskManager {

    private final String url;
    private final KVTaskClient kvTaskClient;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskManager(String url) {
        this.url = url;
        kvTaskClient = new KVTaskClient(url);
    }

    public void save() {
        String tasks = gson.toJson(super.getAllTasks());
        kvTaskClient.put("tasks",tasks);
        String epics = gson.toJson(super.getAllEpics());
        kvTaskClient.put("epics",epics);
        String subtasks = gson.toJson(super.getAllSubtasks());
        kvTaskClient.put("subtasks",subtasks);
        String history = gson.toJson(super.history());
        kvTaskClient.put("history",history);
        String prioritized = gson.toJson(super.getPrioritizedTasks());
        kvTaskClient.put("prioritized",prioritized);
    }

    public static HttpTaskManager load(String url) {
        HttpTaskManager manager = new HttpTaskManager(url);
        List<Task> tasksLoad = new ArrayList<>
                (List.of(HttpTaskManager.gson.fromJson(manager.kvTaskClient.load("tasks"), Task[].class)));
        for (Task task: tasksLoad) {
            manager.tasks.put(task.getId(), task);
            manager.prioritizedTasks.put(task.getStartTime(),task);
        }

        List<Epic> epicsLoad = new ArrayList<>
                (List.of(HttpTaskManager.gson.fromJson(manager.kvTaskClient.load("epics"), Epic[].class)));
        for (Epic epic: epicsLoad) {
            manager.epics.put(epic.getId(), epic);
        }

        List<Subtask> subtasksLoad = new ArrayList<>
                (List.of(HttpTaskManager.gson.fromJson(manager.kvTaskClient.load("subtasks"), Subtask[].class)));
        for (Subtask subtask: subtasksLoad) {
            manager.subtasks.put(subtask.getId(), subtask);
            manager.prioritizedTasks.put(subtask.getStartTime(),subtask);
        }

        List<Task> historyLoad = new ArrayList<>
                (List.of(gson.fromJson(manager.kvTaskClient.load("history"), Task[].class)));
        for (Task task: historyLoad) {
            manager.historyManager.add(task);
        }

        manager.checkId();

        return manager;
    }

    private void checkId() {
        int id = 0;
        for (Integer idCount: tasks.keySet()) {
            if (idCount > id) {
                id = idCount;
                identifier = id + 1;
            }
        }
        for (Integer idCount: epics.keySet()) {
            if (idCount > id) {
                id = idCount;
                identifier = id + 1;
            }
        }
        for (Integer idCount: subtasks.keySet()) {
            if (idCount > id) {
                id = idCount;
                identifier = id + 1;
            }
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}
