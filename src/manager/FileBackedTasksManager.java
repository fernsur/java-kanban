package manager;

import exceptions.*;
import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTasksManager() {
        this.file = new File("resources/data.csv");
    }

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        String data;
        String path = file.getAbsolutePath();
        try {
            data = Files.readString(Path.of(path));
        } catch (IOException exception) {
            throw new ManagerOpenException("Не удалось считать файл.");
        }
        String[] lines = data.split("\n");
        for (int i = 1; i < lines.length-2; i++) {
            int id = -1;
            String[] parts = lines[i].split(",");
            TaskType taskType = TaskType.valueOf(parts[1]);
            Task task = fromString(parts);

            switch (taskType) {
                case TASK:
                    id = task.getId();
                    fileBackedTasksManager.tasks.put(id,task);
                    break;
                case EPIC:
                    id = task.getId();
                    fileBackedTasksManager.epics.put(id,(Epic) task);
                    break;
                case SUBTASK:
                    id = task.getId();
                    fileBackedTasksManager.subtasks.put(id,(Subtask) task);
                    break;
            }
        }

        String history = lines[lines.length-1];
        List<Integer> idHistory = historyFromString(history);
        for (Integer id: idHistory) {
            Task task = null;
            if (fileBackedTasksManager.tasks.containsKey(id)){
                task = fileBackedTasksManager.tasks.get(id);
            }
            if (fileBackedTasksManager.epics.containsKey(id)){
                task = fileBackedTasksManager.epics.get(id);
            }
            if (fileBackedTasksManager.tasks.containsKey(id)){
                task = fileBackedTasksManager.subtasks.get(id);
            }
            fileBackedTasksManager.historyManager.add(task);
        }
        return fileBackedTasksManager;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id, taskType, title, description, status, epicSubtasks\n");
            List<String> allTasks = new ArrayList<>();

            List<Task> tasks = super.getAllTasks();
            for (Task task: tasks) {
                allTasks.add(task.toStringFromFile());
            }

            List<Epic> epics = super.getAllEpics();
            for (Task epic: epics) {
                allTasks.add(epic.toStringFromFile());
            }

            List<Subtask> subtasks = super.getAllSubtasks();
            for (Task subtask: subtasks) {
                allTasks.add(subtask.toStringFromFile());
            }

            for(String line: allTasks) {
                writer.write(String.format("%s\n", line));
            }

            writer.write("\n");
            writer.write(historyToString(this.historyManager));
        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось сохранить файл.");
        }
    }

    static String historyToString(HistoryManager manager) {
        List<String> history = new ArrayList<>();
        for (Task task: manager.getHistory()) {
            history.add(String.valueOf(task.getId()));
        }
        return String.join(",", history);
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> idHistory = new ArrayList<>();
        String[] parts = value.split(",");
        for (String part: parts) {
            Integer id = Integer.parseInt(part);
            idHistory.add(id);
        }
        return idHistory;
    }

    static Task fromString(String[] parts) {
        int id = Integer.parseInt(parts[0]);
        TaskType taskType = TaskType.valueOf(parts[1]);
        String title = parts[2];
        String description = parts[3];
        Status status = Status.valueOf(parts[4]);

        switch (taskType) {
            case TASK:
                return new Task(id, title, description, status);
            case EPIC:
                ArrayList<Integer> epicSubtasks = new ArrayList<>();
                for (int i = 5; i < parts.length; i++) {
                    String list = parts[i].replace("[","")
                            .replace("]","").replace(" ","");
                    epicSubtasks.add(Integer.valueOf(list));
                }
                return new Epic(id,title,description,status,epicSubtasks);
            case SUBTASK:
                int epicNumber = Integer.parseInt(parts[5]);
                return new Subtask(id,title,description,status,epicNumber);
            default:
                return null;
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

    @Override
    public void statusChangeTask(int identifier, Status status) {
        super.statusChangeTask(identifier,status);
        save();
    }

    @Override
    public void statusChangeSubtask(int identifier, Status status) {
        super.statusChangeSubtask(identifier,status);
        save();
    }

    @Override
    public List<Task> history() {
        return super.history();
    }
}