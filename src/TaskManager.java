import java.util.HashMap;
import java.util.Objects;

import tasks.*;
public class TaskManager {
    protected int identifier = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void addTask(Task task){
        task.setId(++identifier);
        tasks.put(identifier, task);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public HashMap<Integer, Task> getAllTask() {
        return tasks;
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public void addEpic(Epic epic){
        epic.setId(++identifier);
        epics.put(identifier, epic);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public HashMap<Integer, Epic> getAllEpic() {
        return epics;
    }

    public void getEpicSubtask(int identifier) {
        System.out.println(epics.get(identifier));
        String markEpic = null;
            if (epics.containsKey(identifier)) {
                Epic foundEpic = epics.get(identifier);
                markEpic = foundEpic.getEpicNumber();
            }
            for (Subtask subtask : subtasks.values()) {
                if (Objects.equals(subtask.getEpicNumber(), markEpic)) {
                    System.out.println(subtask);
                }
            }
    }

    public void deleteEpic(int id) {
        int[] idDelete = new int[10];
        int i = 0;
        for (Subtask sTask : subtasks.values()) {
            if (Objects.equals(getEpic(id).getEpicNumber(), sTask.getEpicNumber())) {
                idDelete[i++] = sTask.getId();
            }
        }
        for (int j : idDelete) {
            deleteSubtasks(j);
        }
        epics.remove(id);
    }

    public void deleteAllEpic() {
        epics.clear();
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(++identifier);
        subtasks.put(identifier, subtask);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public HashMap<Integer, Subtask> getAllSubtask() {
        return subtasks;
    }

    public void deleteSubtasks(int id) {
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
        statusChangeEpic();
    }

    public void statusChangeEpic() {
        String markEpic;
        String markSubtask;
        Status statusSubtask;
        int count1;
        int count2;
        int countAll;

        for (Epic epic : epics.values()) {
            markEpic = epic.getEpicNumber();
            count1 = 0;
            count2 = 0;
            countAll = 0;
            for (Subtask subtask : subtasks.values()) {
                markSubtask = subtask.getEpicNumber();
                statusSubtask = subtask.getStatus();
                if (Objects.equals(markEpic, markSubtask)) {
                    countAll++;
                    if (statusSubtask == Status.DONE){
                        count1++;
                    } else if (statusSubtask == Status.NEW){
                        count2++;
                    }
                }
                if (count1 == countAll) {
                    epic.setStatus(Status.DONE);
                }
                if (count2 == countAll) {
                    epic.setStatus(Status.NEW);
                }
                if (count1 != countAll && count2 != countAll){
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        }
    }
}
