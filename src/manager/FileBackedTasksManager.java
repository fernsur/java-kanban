package manager;

import exceptions.ManagerOpenException;
import exceptions.ManagerSaveException;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.Status;
import tasks.TaskType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static manager.CSVTaskFormat.historyToString;
import static manager.CSVTaskFormat.historyFromString;
import static manager.CSVTaskFormat.fromString;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

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
        for (int i = 1; i < lines.length; i++) {
            int id = -1;
            if (lines[i].isBlank()) {
                break;
            }
            String[] parts = lines[i].split(",");
            TaskType taskType = TaskType.valueOf(parts[1]);
            Task task = fromString(parts);

            switch (taskType) {
                case TASK:
                    id = task.getId();
                    fileBackedTasksManager.checkId(id);
                    fileBackedTasksManager.tasks.put(id,task);
                    break;
                case EPIC:
                    id = task.getId();
                    fileBackedTasksManager.checkId(id);
                    fileBackedTasksManager.epics.put(id,(Epic) task);
                    break;
                case SUBTASK:
                    id = task.getId();
                    fileBackedTasksManager.checkId(id);
                    fileBackedTasksManager.subtasks.put(id,(Subtask) task);
                    fileBackedTasksManager.updateEpicField((Subtask) task);
                    break;
            }
        }
        int readLimit = 3;
        if (lines.length > readLimit) {
            if (!(lines[lines.length - 1].isBlank())) {
                String history = lines[lines.length - 1];
                List<Integer> idHistory = historyFromString(history);
                for (Integer id : idHistory) {
                    Task task = null;
                    if (fileBackedTasksManager.tasks.containsKey(id)) {
                        task = fileBackedTasksManager.tasks.get(id);
                    } else if (fileBackedTasksManager.epics.containsKey(id)) {
                        task = fileBackedTasksManager.epics.get(id);
                    } else if (fileBackedTasksManager.subtasks.containsKey(id)) {
                        task = fileBackedTasksManager.subtasks.get(id);
                    }
                    fileBackedTasksManager.historyManager.add(task);
                }
            }
        }
        return fileBackedTasksManager;
    }

    private void checkId(int id) {
        if (id > identifier) {
            identifier = id + 1;
        }
    }
    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id, type, title, status, description, epic, duration, startTime\n");
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

            writer.write(" \n");
            writer.write(historyToString(this.historyManager));
        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось сохранить файл.");
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
}
