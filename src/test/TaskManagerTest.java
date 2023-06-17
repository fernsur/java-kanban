package test;

import exceptions.TaskValidationException;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static tasks.Status.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    protected Task task1;
    protected Task task2;
    protected Task task3;
    protected Epic epic1;
    protected Epic epic2;
    protected Subtask subtask1;
    protected Subtask subtask2;
    protected Subtask subtask3;
    protected Subtask subtask4;
    protected Subtask subtask5;

    @BeforeEach
    public void beforeEach() {
        task1 = new Task("Выключить посудомойку","В 12:30", Status.NEW,5,
                LocalDateTime.of(2023, 6, 1, 12, 30));
        task2 = new Task("Забрать квитанции","Из почтового ящика", Status.DONE,5,
                LocalDateTime.of(2023, 5, 29, 8, 15));
        task3 = new Task("Заказать воду","До 14:00", Status.NEW,15,
                LocalDateTime.of(2023, 5, 30, 9, 5));

        epic1 = new Epic("Отправить посылку","До 3.06");

        subtask1 = new Subtask("Упаковать посылку","Перемотать скотчем коробку",
                Status.DONE, 4,60, LocalDateTime.of(2023, 5,
                31, 19, 20));
        subtask2 = new Subtask("Дойти до почты","На п. Карла Маркса",
                Status.IN_PROGRESS, 4,20, LocalDateTime.of(2023, 6,
                1, 11, 40));
        subtask3 = new Subtask("Оформить отправку","Не забыть наличные",
                Status.NEW, 4,60, LocalDateTime.of(2023, 6,
                1, 12, 0));

        epic2 = new Epic("Заполнить холодильник","Не забыть курицу");

        subtask4 = new Subtask("Оформить доставку продуктов","Не забыть промокод",
                Status.DONE,8,15, LocalDateTime.of(2023, 6,
                1, 14, 30));
        subtask5 = new Subtask("Разобрать пакеты"," ", Status.IN_PROGRESS,
                8, 10, LocalDateTime.of(2023, 6, 1, 15, 10));
    }

    @Test
    public void shouldReturnTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        final int taskId = task1.getId();
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void shouldUpdateTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        Task task = new Task(2, "Оплатить квитанции","Не забыть счетчики", Status.DONE,
                10, LocalDateTime.of(2023, 5, 29, 8, 15));

        taskManager.updateTask(task);

        final int taskId = task.getId();
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задача обновляется некорректно.");
    }

    @Test
    public void shouldReturnNullIfIncorrectIdTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        final Task savedTask = taskManager.getTask(9);
        assertNull(savedTask, "Найдена задача с некорректным идентификатором.");
    }

    @Test
    public void shouldReturnAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
        assertEquals(task2, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    public void shouldDeletedTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        final int taskId = task3.getId();

        taskManager.deleteTask(taskId);

        final Task savedTask = taskManager.getTask(taskId);

        assertNull(savedTask, "Задача не удалена.");
    }

    @Test
    public void shouldDeleteAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.deleteAllTasks();
        final List<Task> tasks = taskManager.getAllTasks();

        assertEquals(Collections.EMPTY_LIST, tasks, "Список всех задач не очищается.");
    }

    @Test
    public void shouldChangeStatusTaskToNew() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        final int taskId = task2.getId();

        taskManager.statusChangeTask(taskId,NEW);

        final Task savedTask = taskManager.getTask(taskId);
        final Status savedTaskStatus = savedTask.getStatus();

        assertNotNull(savedTaskStatus, "Статус задачи пуст.");
        assertEquals(NEW, savedTaskStatus, "Статус подзадачи не сохраняется в состоянии New.");
    }

    @Test
    public void shouldChangeStatusTaskToInProgress() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        final int taskId = task2.getId();

        taskManager.statusChangeTask(taskId,IN_PROGRESS);

        final Task savedTask = taskManager.getTask(taskId);
        final Status savedTaskStatus = savedTask.getStatus();

        assertNotNull(savedTaskStatus, "Статус задачи пуст.");
        assertEquals(IN_PROGRESS, savedTaskStatus, "Статус подзадачи не сохраняется в состоянии In Progress.");
    }

    @Test
    public void shouldChangeStatusTaskToDone() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        final int taskId = task1.getId();

        taskManager.statusChangeTask(taskId,DONE);

        final Task savedTask = taskManager.getTask(taskId);
        final Status savedTaskStatus = savedTask.getStatus();

        assertNotNull(savedTaskStatus, "Статус задачи пуст.");
        assertEquals(DONE, savedTaskStatus, "Статус задачи не сохраняется в состоянии Done.");
    }

    @Test
    public void shouldReturnEpic() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        final int epicId = epic2.getId();
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic2, savedEpic , "Эпики не совпадают.");
    }

    @Test
    public void shouldUpdateEpic() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Epic epic = new Epic(2,"Приготовить ужин","Не хватает курицы");

        taskManager.updateEpic(epic);

        final int epicId = epic.getId();
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпик обновлен некорректно.");
    }

    @Test
    public void shouldReturnNullIfIncorrectIdEpic() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        final Epic savedEpic = taskManager.getEpic(9);
        assertNull(savedEpic, "Найден эпик с некорректным идентификатором.");
    }

    @Test
    public void shouldReturnAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic2, epics.get(1), "Эпики не совпадают.");
    }

    @Test
    public void shouldDeletedEpic() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        final int epicId = epic1.getId();

        taskManager.deleteEpic(epicId);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNull(savedEpic, "Эпик не удален.");
    }

    @Test
    public void shouldDeleteAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.deleteAllEpics();
        final List<Epic> epics = taskManager.getAllEpics();

        assertEquals(Collections.EMPTY_LIST, epics, "Список всех эпиков не очищается.");
    }

    @Test
    public void shouldReturnSubtask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.createEpic(epic2);

        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);

        final int subtaskId = subtask3.getId();
        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask3, savedSubtask, "Подзадачи не совпадают.");
    }

    @Test
    public void shouldUpdateSubtask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.createEpic(epic2);

        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);

        Subtask subtask = new Subtask(7,"Сварить суп"," ", Status.IN_PROGRESS,
                8,125, LocalDateTime.of(2023, 6, 1, 15, 10));

        taskManager.updateSubtask(subtask);

        final int subtaskId = subtask.getId();
        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадача обновлена некорректно.");
    }

    @Test
    public void shouldReturnNullIfIncorrectIdSubtask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        final Subtask savedSubtask = taskManager.getSubtask(20);
        assertNull(savedSubtask, "Найдена подзадача с некорректным идентификатором.");
    }

    @Test
    public void shouldReturnAllSubtask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.createEpic(epic2);

        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(5, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask3, subtasks.get(2), "Подзадачи не совпадают.");
    }

    @Test
    public void shouldDeletedSubtask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.createEpic(epic2);

        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);

        final int subtaskId = subtask4.getId();

        taskManager.deleteSubtask(subtaskId);

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNull(savedSubtask, "Подзадача не удалена.");
    }

    @Test
    public void shouldDeleteAllSubtask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.createEpic(epic2);

        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);

        taskManager.deleteAllSubtasks();
        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(Collections.EMPTY_LIST, subtasks, "Список всех подзадач не очищается.");
    }

    @Test
    public void shouldChangeStatusSubtaskToNew() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        final int subtaskId = subtask2.getId();

        taskManager.statusChangeSubtask(subtaskId,NEW);

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);
        final Status savedSubtaskStatus = savedSubtask.getStatus();

        assertNotNull(savedSubtaskStatus, "Статус подзадачи пуст.");
        assertEquals(NEW, savedSubtaskStatus, "Статус подзадачи не сохраняется в состоянии New.");
    }

    @Test
    public void shouldChangeStatusSubtaskToInProgress() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        final int subtaskId = subtask1.getId();

        taskManager.statusChangeSubtask(subtaskId,IN_PROGRESS);

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);
        final Status savedSubtaskStatus = savedSubtask.getStatus();

        assertNotNull(savedSubtaskStatus, "Статус подзадачи пуст.");
        assertEquals(IN_PROGRESS, savedSubtaskStatus, "Статус подзадачи не сохраняется в состоянии In Progress.");
    }

    @Test
    public void shouldChangeStatusSubtaskToDone() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        final int subtaskId = subtask2.getId();

        taskManager.statusChangeSubtask(subtaskId,DONE);

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);
        final Status savedSubtaskStatus = savedSubtask.getStatus();

        assertNotNull(savedSubtaskStatus, "Статус подзадачи пуст.");
        assertEquals(DONE, savedSubtaskStatus, "Статус подзадачи не сохраняется в состоянии Done.");
    }

    @Test
    public void shouldReturnSubtaskListForEpic() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        final int epicId = epic1.getId();
        final List<Subtask> subtaskOfEpic = taskManager.getEpicSubtask(epicId);

        assertNotNull(subtaskOfEpic, "Подзадачи для эпика не найдены.");
        assertEquals(List.of(subtask1,subtask2,subtask3), subtaskOfEpic, "Списки подзадач не совпадают.");
    }

    @Test
    public void shouldReturnEmptyListSubtaskForEpic() {
        taskManager.createEpic(epic1);

        final int epicId = epic1.getId();
        final List<Subtask> subtaskOfEpic = taskManager.getEpicSubtask(epicId);

        assertEquals(Collections.EMPTY_LIST, subtaskOfEpic, "Списки подзадач не пусты.");
    }

    @Test
    public void shouldReturnNullForIncorrectEpic() {
        final List<Subtask> subtaskOfEpic = taskManager.getEpicSubtask(10);
        assertNull(subtaskOfEpic, "Найден список подзадач для эпика с некорректным идентификатором.");
    }

    @Test
    public void shouldChangeStatusEpicToNewIfAllSubtaskNew() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        final int epicId = epic1.getId();
        final int subtaskId1 = subtask1.getId();
        final int subtaskId2 = subtask2.getId();
        taskManager.statusChangeSubtask(subtaskId1,NEW);
        taskManager.statusChangeSubtask(subtaskId2,NEW);

        final Epic savedEpic = taskManager.getEpic(epicId);
        final Status savedEpicStatus = savedEpic.getStatus();

        assertNotNull(savedEpicStatus, "Статус эпика пуст.");
        assertEquals(NEW, savedEpicStatus,
                "Статусы сохраняются некорректно при наличии подзадач в состоянии New.");
    }

    @Test
    public void shouldChangeStatusEpicToDoneIfAllSubtaskDone() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        final int epicId = epic1.getId();
        final int subtaskId2 = subtask2.getId();
        final int subtaskId3 = subtask3.getId();
        taskManager.statusChangeSubtask(subtaskId2,DONE);
        taskManager.statusChangeSubtask(subtaskId3,DONE);

        final Epic savedEpic = taskManager.getEpic(epicId);
        final Status savedEpicStatus = savedEpic.getStatus();

        assertNotNull(savedEpicStatus, "Статус эпика пуст.");
        assertEquals(DONE, savedEpicStatus,
                "Статусы сохраняются некорректно при наличии подзадач в состоянии Done.");
    }

    @Test
    public void shouldChangeStatusEpicToInProgressIfAllSubtaskDoneOrNew() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        final int epicId = epic1.getId();
        final int subtaskId2 = subtask2.getId();
        taskManager.statusChangeSubtask(subtaskId2,DONE);

        final Epic savedEpic = taskManager.getEpic(epicId);
        final Status savedEpicStatus = savedEpic.getStatus();

        assertNotNull(savedEpicStatus, "Статус эпика пуст.");
        assertEquals(IN_PROGRESS, savedEpicStatus,
                "Статусы сохраняются некорректно при наличии подзадач в состоянии Done или New.");
    }

    @Test
    public void shouldChangeStatusEpicToInProgressIfAllSubtaskInProgress() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        final int epicId = epic1.getId();
        final int subtaskId1 = subtask1.getId();
        final int subtaskId3 = subtask3.getId();
        taskManager.statusChangeSubtask(subtaskId1,IN_PROGRESS);
        taskManager.statusChangeSubtask(subtaskId3,IN_PROGRESS);

        final Epic savedEpic = taskManager.getEpic(epicId);
        final Status savedEpicStatus = savedEpic.getStatus();

        assertNotNull(savedEpicStatus, "Статус эпика пуст.");
        assertEquals(IN_PROGRESS, savedEpicStatus,
                "Статусы сохраняются некорректно при наличии подзадач в состоянии In Progress.");
    }

    @Test
    public void shouldReturnIdEpicOfSubtask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);

        final int epicId = epic1.getId();
        final int subtaskEpicId = subtask1.getEpicNumber();

        assertNotNull(subtaskEpicId, "Подзадача не имеет эпика.");
        assertEquals(epicId, subtaskEpicId, "В подзадаче содержится неверный id эпика");
    }

    @Test
    void shouldReturnEmptyHistory() {
        List<Task> history = taskManager.history();
        assertEquals(Collections.EMPTY_LIST, history, "История не пуста.");
    }

    @Test
    void shouldReturnHistory() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);

        final int epicId = epic1.getId();
        final int taskId = task2.getId();
        final int subtaskId = subtask1.getId();
        final Epic savedEpic = taskManager.getEpic(epicId);
        final Task savedTask = taskManager.getTask(taskId);
        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        List<Task> history = taskManager.history();

        assertNotNull(history, "История пуста.");
        assertEquals(List.of(savedEpic,savedTask,savedSubtask), history, "История сохраняется некорректно.");
    }

    @Test
    public void shouldReturnHistoryWithoutDouble() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);

        final int epicId = epic1.getId();
        final int taskId = task2.getId();
        final int subtaskId = subtask1.getId();
        final Epic savedEpic = taskManager.getEpic(epicId);
        final Task savedTask = taskManager.getTask(taskId);
        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);
        final Subtask savedSubtask1 = taskManager.getSubtask(subtaskId);
        final Task savedTask1 = taskManager.getTask(taskId);

        List<Task> history = taskManager.history();

        assertNotNull(history, "История пуста.");
        assertEquals(List.of(savedEpic,savedSubtask1,savedTask1), history,
                "История сохраняется некорректно при дублировании.");
    }

    @Test
    public void shouldReturnHistoryWhenDeleteSomething() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        final int epicId = epic1.getId();
        final int taskId = task2.getId();
        final int taskId1 = task1.getId();
        final int subtaskId = subtask1.getId();
        final int subtaskId1 = subtask2.getId();

        final Epic savedEpic = taskManager.getEpic(epicId);
        final Task savedTask = taskManager.getTask(taskId);
        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);
        final Subtask savedSubtask1 = taskManager.getSubtask(subtaskId1);
        final Task savedTask1 = taskManager.getTask(taskId1);

        taskManager.deleteTask(taskId1);
        List<Task> history = taskManager.history();
        assertNotNull(history, "История пуста.");
        assertEquals(List.of(savedEpic,savedTask,savedSubtask,savedSubtask1), history,
                "История сохраняется некорректно при удалении с конца.");

        taskManager.deleteSubtask(subtaskId);
        history = taskManager.history();
        assertNotNull(history, "История пуста.");
        assertEquals(List.of(savedEpic,savedTask,savedSubtask1), history,
                "История сохраняется некорректно при удалении из середины.");

        taskManager.deleteEpic(epicId);
        history = taskManager.history();
        assertNotNull(history, "История пуста.");
        assertEquals(List.of(savedTask), history,
                "История сохраняется некорректно при удалении из начала.");
    }

    @Test
    public void shouldListPrioritizedTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertNotNull(prioritized, "Список задач по приоритету не выводится.");
        assertEquals(List.of(task2,task3,task1), prioritized,
                "Задачи не сортируются по приоритету.");
    }

    @Test
    public void shouldThrowExceptionWhenTaskIntersectInTime() {
        taskManager.createTask(task1);
        Task task = new Task("Title","Description", Status.NEW,3,
                LocalDateTime.of(2023, 6, 1, 12, 31));
        TaskValidationException ex = assertThrows(
                TaskValidationException.class, () -> taskManager.createTask(task));
        assertEquals("Задачи пересекаются по времени!", ex.getMessage());
    }
}