package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int identifier = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void createTask(Task task) {
        task.setId(++identifier);
        tasks.put(identifier, task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id: tasks.keySet()){
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(++identifier);
        epics.put(identifier, epic);
    }
    
    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        if (epics.containsKey(epicId)) {
            Epic oldEpic = epics.get(epicId);
            epic.setEpicSubtasks(oldEpic.getEpicSubtasks());
            epics.put(epicId, epic);
            statusChangeEpic(epicId);
        }
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
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

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            for (int j : epics.get(id).getEpicSubtasks()) {
                subtasks.remove(j);
                historyManager.remove(j);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllEpics() {
        for (Integer id: epics.keySet()){
            historyManager.remove(id);
        }
        epics.clear();
        for (Integer id: subtasks.keySet()){
            historyManager.remove(id);
        }
        subtasks.clear();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(++identifier);
        subtasks.put(identifier, subtask);
        updateSubtaskOfEpic(subtask);
        statusChangeEpic(subtask.getEpicNumber());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateSubtaskOfEpic(subtask);
            statusChangeEpic(subtask.getEpicNumber());
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            for (Epic epic: epics.values()) {
                epic.getEpicSubtasks().remove(Integer.valueOf(id));
            }
            int epicID = subtasks.get(id).getEpicNumber();
            subtasks.remove(id);
            statusChangeEpic(epicID);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic: epics.values()) {
            epic.getEpicSubtasks().clear();
        }
        for (Integer id: subtasks.keySet()){
            historyManager.remove(id);
        }
        subtasks.clear();
    }

    @Override
    public void statusChangeTask(int identifier, Status status) {
        tasks.get(identifier).setStatus(status);
    }

    @Override
    public void statusChangeSubtask(int identifier, Status status) {
        subtasks.get(identifier).setStatus(status);
        statusChangeEpic(subtasks.get(identifier).getEpicNumber());
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
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

    private void updateSubtaskOfEpic(Subtask subtask) {
        int epicID = subtask.getEpicNumber();
        int subtaskID = subtask.getId();
        ArrayList<Integer> epicSubtasks = epics.get(epicID).getEpicSubtasks();
        if (!epicSubtasks.contains(subtaskID)){
            epicSubtasks.add(subtaskID);
        }
        epics.get(epicID).setEpicSubtasks(epicSubtasks);
    }
}
