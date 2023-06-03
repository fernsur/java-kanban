package manager;

import java.util.List;
import java.util.Set;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.Status;

public interface TaskManager {

    void createTask(Task task);

    void updateTask(Task task);

    Task getTask(int id);

    List<Task> getAllTasks();

    void deleteTask(int id);

    void deleteAllTasks();

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    Epic getEpic(int id);

    List<Epic> getAllEpics();

    List<Subtask> getEpicSubtask(int id);

    void deleteEpic(int id);

    void deleteAllEpics();

    void createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    List<Subtask> getAllSubtasks();

    void deleteSubtask(int id);

    void deleteAllSubtasks();

    void statusChangeTask(int identifier, Status status);

    void statusChangeSubtask(int identifier, Status status);

    List<Task> history();

    Set<Task> getPrioritizedTasks();

}
