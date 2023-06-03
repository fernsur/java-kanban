import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.Status;

import java.io.File;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        /* TaskManager manager = Managers.getFileBackedTasksManager();

        manager.createTask(new Task("Выключить посудомойку","В 12:30", Status.NEW,5,
                LocalDateTime.of(2023, 6, 1, 12, 30)));
        manager.createTask(new Task("Забрать квитанции","Из почтового ящика", Status.DONE,5,
                LocalDateTime.of(2023, 5, 29, 8, 15)));
        manager.createTask(new Task("Заказать воду","До 14:00", Status.NEW,15,
                LocalDateTime.of(2023, 5, 30, 9, 5)));

        manager.createEpic(new Epic("Отправить посылку","До 3.06", Status.IN_PROGRESS,
                LocalDateTime.of(2023, 5, 31, 19, 20)));

        manager.createSubtask(new Subtask("Упаковать посылку","Перемотать скотчем коробку",
                Status.DONE, 4,60, LocalDateTime.of(2023, 5,
                31, 19, 20)));
        manager.createSubtask(new Subtask("Дойти до почты","На п. Карла Маркса",
                Status.IN_PROGRESS, 4,20, LocalDateTime.of(2023, 6,
                1, 11, 40)));
        manager.createSubtask(new Subtask("Оформить отправку","Не забыть наличные",
                Status.NEW, 4,60, LocalDateTime.of(2023, 6,
                1, 12, 0)));

        manager.createEpic(new Epic("Заполнить холодильник","Не забыть курицу",
                Status.IN_PROGRESS, LocalDateTime.of(2023, 6, 1, 14, 30)));

        manager.createSubtask(new Subtask("Оформить доставку продуктов","Не забыть промокод",
                Status.DONE,8,15, LocalDateTime.of(2023, 6,
                1, 14, 30)));
        manager.createSubtask(new Subtask("Разобрать пакеты"," ", Status.IN_PROGRESS,
                8, 10, LocalDateTime.of(2023, 6, 1, 15, 10)));

        // Получение конкретной задачи
        System.out.println(manager.getTask(2));
        System.out.println(manager.getEpic(8));
        System.out.println(manager.getSubtask(10));

        // Получение списка просмотренных задач
        System.out.println(manager.history());

        // Получение подзадач конкретного эпика
        System.out.println(manager.getEpicSubtask(4));

        // Обновление всех видов задач
        manager.updateTask(new Task(2, "Оплатить квитанции","Не забыть счетчики", Status.DONE,
                10, LocalDateTime.of(2023, 5, 29, 8, 15)));
        manager.updateEpic(new Epic(8,"Приготовить ужин","Не хватает курицы",
                Status.IN_PROGRESS, LocalDateTime.of(2023, 6, 1, 14, 30)));
        manager.updateSubtask(new Subtask(10,"Сварить суп"," ", Status.IN_PROGRESS,
                8,125, LocalDateTime.of(2023, 6, 1, 15, 10)));

        // Изменение статуса конкретной задачи
        manager.statusChangeTask(1,Status.DONE);
        manager.statusChangeSubtask(10,Status.DONE);

        // Получение задач каждого из списков
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        // Удаление конкретной задачи
        manager.deleteTask(2);
        manager.deleteEpic(4);
        manager.deleteSubtask(10);

        // Файл
         TaskManager fileBackedTasksManager = FileBackedTasksManager
                .loadFromFile(new File("resources/data.csv"));

        // Получение списка просмотренных задач (учитывая удаления)
        System.out.println(manager.history());

        // Получение задач с приоретизацией
        System.out.println(manager.getPrioritizedTasks());

        // Проверка, что файл считался
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        // Очистка списков задач
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks(); */
    }
}
