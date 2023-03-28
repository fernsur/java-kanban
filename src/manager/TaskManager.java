package manager;

import java.util.ArrayList;
import java.util.Collection;
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

    public Collection<Task> getAllTasks() {
        return tasks.values();
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
            int epicID = epic.getId();
            epics.put(epicID, epic);
            for (Subtask j : subtasks.values()) {
                if (j.getEpicNumber() == epicID){
                    updateSubtaskOfEpic(j);
                }
            }
            statusChangeEpic(epicID);
        }
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    public ArrayList<Subtask> getEpicSubtask(int id) {
        if (!epics.containsKey(id)) {
            return null;
        }

        ArrayList<Subtask> foundEpicSubtasks = new ArrayList<>();
            if (epics.get(id).getEpicSubtasks() != null) {
                for (int j : epics.get(id).getEpicSubtasks()) {
                     foundEpicSubtasks.add(subtasks.get(j));
            }
        }
        return foundEpicSubtasks;
    }

    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            for (int j : epics.get(id).getEpicSubtasks()) {
                subtasks.remove(j);
            }
            epics.remove(id);
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(++identifier);
        subtasks.put(identifier, subtask);
        updateSubtaskOfEpic(subtask);
        statusChangeEpic(subtask.getEpicNumber());
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateSubtaskOfEpic(subtask);
        statusChangeEpic(subtask.getEpicNumber());
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    public void deleteSubtask(int id) {
        int epicID = subtasks.get(id).getEpicNumber();
        deleteSubtaskOfEpic(subtasks.get(id));
        subtasks.remove(id);
        statusChangeEpic(epicID);
    }

    public void deleteAllSubtasks() {
        for (Subtask subtask: subtasks.values()) {
            deleteSubtaskOfEpic(subtask);
        }
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
        int countAll = epics.get(identifier).getEpicSubtasks().size();
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

    public void updateSubtaskOfEpic(Subtask subtask) {
        int epicID = subtask.getEpicNumber();
        int subtaskID = subtask.getId();
        ArrayList<Integer> epicSubtasks = epics.get(epicID).getEpicSubtasks();
        if (!epicSubtasks.contains(subtaskID)){
            epicSubtasks.add(subtaskID);
        }
        epics.get(epicID).setEpicSubtasks(epicSubtasks);
    }

    public void deleteSubtaskOfEpic(Subtask subtask) {
        int epicID = subtask.getEpicNumber();
        Integer subtaskID = subtask.getId();
        ArrayList<Integer> epicSubtasks = epics.get(epicID).getEpicSubtasks();
        epicSubtasks.remove(subtaskID);
        epics.get(epicID).setEpicSubtasks(epicSubtasks);
    }
}
