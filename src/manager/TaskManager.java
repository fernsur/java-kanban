package manager;

import java.util.ArrayList;
import java.util.HashMap;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.Status;

public class TaskManager {
    private int identifier = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void addTask(Task task) {
        task.setId(++identifier);
        tasks.put(identifier, task);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void addEpic(Epic epic) {
        epic.setId(++identifier);
        epics.put(identifier, epic);
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public ArrayList<Subtask> getEpicSubtask(int id) {
        ArrayList<Subtask> foundEpicSubtasks = new ArrayList<>();
        for (int j : epics.get(id).getEpicSubtasks()) {
            foundEpicSubtasks.add(subtasks.get(j));
        }
        return foundEpicSubtasks;
    }

    public void deleteEpic(int id) {
        for (int j : epics.get(id).getEpicSubtasks()) {
            subtasks.remove(j);
        }
        epics.remove(id);
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(++identifier);
        subtasks.put(identifier, subtask);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        statusChangeEpic(subtask.getEpicNumber());
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    public void deleteSubtask(int id) {
        subtasks.remove(id);
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    public void statusChangeTask(int identifier, Status status) {
        getTask(identifier).setStatus(status);
    }

    public void statusChangeSubtask(int identifier, Status status) {
        getSubtask(identifier).setStatus(status);
        statusChangeEpic(getSubtask(identifier).getEpicNumber());
    }

    private void statusChangeEpic(int identifier) {
        int count1 = 0;
        int count2 = 0;
        int countAll = epics.get(identifier).getEpicSubtasks().length;
        for (int j : epics.get(identifier).getEpicSubtasks()) {
            if (subtasks.get(j).getStatus() == Status.DONE) {
                count1++;
            } else if (subtasks.get(j).getStatus() == Status.NEW) {
                count2++;
            }
        }
        if (count1 == countAll) {
            epics.get(identifier).setStatus(Status.DONE);
        } else if (count2 == countAll) {
            epics.get(identifier).setStatus(Status.NEW);
        } else {
            epics.get(identifier).setStatus(Status.IN_PROGRESS);
        }
    }
}
