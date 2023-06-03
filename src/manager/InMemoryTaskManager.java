package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {
    protected int identifier = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    protected final Set<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if ((task1.getStartTime() != null) && (task2.getStartTime() != null)) {
            return task1.getStartTime().compareTo(task2.getStartTime());
        } else if (task1.getStartTime() == null) {
            return 1;
        } else if (task2.getStartTime() == null) {
            return -1;
        } else {
            return 0;
        }
    });

    @Override
    public void createTask(Task task) {
        tasksWithoutIntersections(task);
        task.setId(++identifier);
        tasks.put(identifier, task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateTask(Task task) {
        tasksWithoutIntersections(task);
        int id = task.getId();
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(tasks.get(id));
            tasks.put(task.getId(), task);
            prioritizedTasks.add(tasks.get(id));
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteTask(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id: tasks.keySet()){
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
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
            changeTimeEpic(epicId);
        }
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getEpicSubtask(int id) {
        if (!epics.containsKey(id)) {
            return null;
        }

        List<Subtask> foundEpicSubtasks = new ArrayList<>();
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
        tasksWithoutIntersections(subtask);
        subtask.setId(++identifier);
        subtasks.put(identifier, subtask);
        prioritizedTasks.add(subtask);
        updateSubtaskOfEpic(subtask);
        statusChangeEpic(subtask.getEpicNumber());
        changeTimeEpic(subtask.getEpicNumber());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        tasksWithoutIntersections(subtask);
        int id = subtask.getId();
        if (subtasks.containsKey(subtask.getId())) {
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.put(id, subtask);
            prioritizedTasks.add(subtasks.get(id));
            updateSubtaskOfEpic(subtask);
            statusChangeEpic(subtask.getEpicNumber());
            changeTimeEpic(subtask.getEpicNumber());
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            for (Epic epic: epics.values()) {
                epic.getEpicSubtasks().remove(Integer.valueOf(id));
            }
            int epicID = subtasks.get(id).getEpicNumber();
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            statusChangeEpic(epicID);
            changeTimeEpic(epicID);
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
            prioritizedTasks.remove(subtasks.get(id));
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void tasksWithoutIntersections(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();

        for (Task curTask : prioritizedTasks) {
            LocalDateTime start = curTask.getStartTime();
            LocalDateTime end = curTask.getEndTime();
            if ((startTime.isAfter(start) && startTime.isBefore(end))
                    || (endTime.isAfter(start) && endTime.isBefore(end))) {
                throw new RuntimeException("Задачи пересекаются по времени!");
            }
        }
    }

    private void changeTimeEpic(int id) {
        Epic epic = epics.get(id);
        List<Integer> subId = epic.getEpicSubtasks();
        LocalDateTime startCur = LocalDateTime.of(2024,1,1,1,1);
        LocalDateTime endCur = LocalDateTime.of(2023,1,1,1,1);
        long duration = 0;
        for (Integer idSub : subId) {
            Subtask sub = subtasks.get(idSub);
            duration+=sub.getDuration();
            LocalDateTime start = sub.getStartTime();
            LocalDateTime end = sub.getEndTime();
            if (start.isBefore(startCur)){
                startCur = start;
                epic.setStartTime(start);
            }
            if (end.isAfter(endCur)){
                endCur = end;
                epic.setEndTime(end);
            }
        }
        epic.setDuration(duration);
        epics.put(id,epic);
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
        List<Integer> epicSubtasks = epics.get(epicID).getEpicSubtasks();
        if (!epicSubtasks.contains(subtaskID)){
            epicSubtasks.add(subtaskID);
        }
        epics.get(epicID).setEpicSubtasks(epicSubtasks);
    }
}
