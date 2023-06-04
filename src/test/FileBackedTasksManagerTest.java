package test;

import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void startManager() {
        taskManager = (FileBackedTasksManager) Managers
                .getFileBackedTasksManager(new File("src/test/resources/dataTest.csv"));
    }

    @Test
    public void shouldLoadFromFile() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        final int epicId = epic1.getId();
        final int taskId = task2.getId();
        final int subtaskId = subtask2.getId();
        final Epic savedEpic = taskManager.getEpic(epicId);
        final Task savedTask = taskManager.getTask(taskId);
        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        TaskManager fileBackedTasksManager = FileBackedTasksManager
                .loadFromFile(new File("src/test/resources/dataTest.csv"));

        assertEquals(3, fileBackedTasksManager.getAllTasks().size(),
                "Количество задач после загрузки из файла не совпадает.");
        assertEquals(1, fileBackedTasksManager.getAllEpics().size(),
                "Количество эпиков после загрузки из файла не совпадает.");
        assertEquals(3, fileBackedTasksManager.getAllSubtasks().size(),
                "Количество подзадач после загрузки из файла не совпадает.");

        List<Task> history = taskManager.history();
        assertEquals(List.of(savedEpic,savedTask,savedSubtask), history, "История пуста.");
    }

    @Test
    public void shouldLoadFromFileIfFileIsEmpty() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();

        TaskManager fileBackedTasksManager = FileBackedTasksManager
                .loadFromFile(new File("src/test/resources/dataTest.csv"));

        assertEquals(0, fileBackedTasksManager.getAllTasks().size(),
                "Количество задач после загрузки из файла не совпадает.");
        assertEquals(0, fileBackedTasksManager.getAllEpics().size(),
                "Количество эпиков после загрузки из файла не совпадает.");
        assertEquals(0, fileBackedTasksManager.getAllSubtasks().size(),
                "Количество подзадач после загрузки из файла не совпадает.");

        List<Task> history = taskManager.history();
        assertEquals(Collections.EMPTY_LIST, history, "История не пуста.");
    }

    @Test
    public void shouldLoadFromFileIfHistoryIsEmpty() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        TaskManager fileBackedTasksManager = FileBackedTasksManager
                .loadFromFile(new File("src/test/resources/dataTest.csv"));

        assertEquals(3, fileBackedTasksManager.getAllTasks().size(),
                "Количество задач после загрузки из файла не совпадает.");
        assertEquals(1, fileBackedTasksManager.getAllEpics().size(),
                "Количество эпиков после загрузки из файла не совпадает.");
        assertEquals(3, fileBackedTasksManager.getAllSubtasks().size(),
                "Количество подзадач после загрузки из файла не совпадает.");

        List<Task> history = taskManager.history();
        assertEquals(Collections.EMPTY_LIST, history, "История не пуста.");
    }
}