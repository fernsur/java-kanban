import manager.Managers;
import manager.TaskManager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.Status;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        manager.createTask(new Task(0,"Выключить посудомойку","В 12:30", Status.NEW));
        manager.createTask(new Task(0,"Забрать квитанции","Утром из почтового ящика", Status.DONE));
        manager.createTask(new Task(0,"Заказать воду","До 14:00", Status.NEW));

        manager.createEpic(new Epic(0,"Отправить посылку","До пятницы", Status.IN_PROGRESS));

        manager.createSubtask(new Subtask(0,"Упаковать посылку","Перемотать скотчем коробку",
                Status.DONE, 4));
        manager.createSubtask(new Subtask(0,"Дойти до почты","На п. Карла Маркса",
                Status.IN_PROGRESS, 4));
        manager.createSubtask(new Subtask(0,"Оформить отправку","Не забыть наличные",
                Status.NEW, 4));

        manager.createEpic(new Epic(0,"Заполнить холодильник","Не забыть курицу",
                Status.IN_PROGRESS));

        manager.createSubtask(new Subtask(0,"Оформить доставку продуктов","Не забыть промокод",
                Status.DONE,8));
        manager.createSubtask(new Subtask(0,"Разобрать пакеты"," ", Status.IN_PROGRESS,
                8));

        // Получение конкретной задачи
        System.out.println(manager.getTask(2));
        System.out.println(manager.getEpic(8));
        System.out.println(manager.getSubtask(10));

        // Получение списка просмотренных задач
        System.out.println(manager.history());

        // Получение подзадач конкретного эпика
        System.out.println(manager.getEpicSubtask(4));

        // Обновление всех видов задач
        manager.updateTask(new Task(2,"Оплатить квитанции","Не забыть счетчики", Status.DONE));
        manager.updateEpic(new Epic(8,"Приготовить ужин","Не хватает курицы",
                Status.IN_PROGRESS));
        manager.updateSubtask(new Subtask(10,"Сварить суп"," ", Status.IN_PROGRESS,
                8));

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

        // Очистка списков задач
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();

    }
}
