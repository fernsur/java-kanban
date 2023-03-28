import manager.TaskManager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.Status;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.addTask(new Task(0,"Выключить посудомойку","В 12:30", Status.NEW));
        taskManager.addTask(new Task(0,"Забрать квитанции","Утром из почтового ящика", Status.DONE));
        taskManager.addTask(new Task(0,"Заказать воду","До 14:00", Status.NEW));

        taskManager.addEpic(new Epic(0,"Отправить посылку","До пятницы", Status.IN_PROGRESS));

        taskManager.addSubtask(new Subtask(0,"Упаковать посылку","Перемотать скотчем коробку",
                Status.DONE, 4));
        taskManager.addSubtask(new Subtask(0,"Дойти до почты","На п. Карла Маркса",
                Status.IN_PROGRESS, 4));
        taskManager.addSubtask(new Subtask(0,"Оформить отправку","Не забыть наличные",
                Status.NEW, 4));

        taskManager.addEpic(new Epic(0,"Заполнить холодильник","Не забыть курицу",
                Status.IN_PROGRESS));

        taskManager.addSubtask(new Subtask(0,"Оформить доставку продуктов","Не забыть промокод",
                Status.DONE,8));
        taskManager.addSubtask(new Subtask(0,"Разобрать пакеты"," ", Status.IN_PROGRESS,
                8));

        // Получение конкретной задачи
        System.out.println(taskManager.getTask(2));
        System.out.println(taskManager.getEpic(8));
        System.out.println(taskManager.getSubtask(10));

        // Получение подзадач конкретного эпика
        System.out.println(taskManager.getEpicSubtask(4));

        // Обновление всех видов задач
        taskManager.updateTask(new Task(2,"Оплатить квитанции","Не забыть счетчики", Status.DONE));
        taskManager.updateEpic(new Epic(8,"Приготовить ужин","Не хватает курицы",
                Status.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask(10,"Сварить суп"," ", Status.IN_PROGRESS,
                8));

        // Изменение статуса конкретной задачи
        taskManager.statusChangeTask(1,Status.DONE);
        taskManager.statusChangeSubtask(10,Status.DONE);

        // Получение задач каждого из списков
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        // Удаление конкретной задачи
        taskManager.deleteTask(2);
        taskManager.deleteEpic(4);
        taskManager.deleteSubtask(10);

        // Очистка списков задач
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }
}
