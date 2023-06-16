package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import server.LocalDateTimeAdapter;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {

    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private Task task1;
    private Task task2;
    private Task task3;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;
    private Subtask subtask3;
    private HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

    @BeforeEach
    public void beforeEach() {
        task1 = new Task("Выключить посудомойку", "В 12:30", Status.NEW, 5,
                LocalDateTime.of(2023, 6, 1, 12, 30));
        task2 = new Task("Забрать квитанции", "Из почтового ящика", Status.DONE, 5,
                LocalDateTime.of(2023, 5, 29, 8, 15));
        task3 = new Task("Заказать воду", "До 14:00", Status.NEW, 15,
                LocalDateTime.of(2023, 5, 30, 9, 5));

        epic1 = new Epic("Отправить посылку", "До 3.06");

        subtask1 = new Subtask("Упаковать посылку", "Перемотать скотчем коробку",
                Status.DONE, 4, 60, LocalDateTime.of(2023, 5,
                31, 19, 20));
        subtask2 = new Subtask("Дойти до почты", "На п. Карла Маркса",
                Status.IN_PROGRESS, 4, 20, LocalDateTime.of(2023, 6,
                1, 11, 40));
        subtask3 = new Subtask("Оформить отправку", "Не забыть наличные",
                Status.NEW, 4, 60, LocalDateTime.of(2023, 6,
                1, 12, 0));

        epic2 = new Epic("Заполнить холодильник", "Не забыть курицу");
    }

    @BeforeEach
    public void start() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
    }

    @AfterEach
    public void stop() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void shouldCreateTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201,response.statusCode(), "Статус код при создания задачи не совпадает");
        assertEquals("Задача создана",response.body(), "Задача не создается");
    }

    @Test
    public void shouldUpdateTask() throws IOException, InterruptedException {
        addTask(task1);

        String json = gson.toJson(new Task(1,"Тест","Тест", Status.DONE,10,
                LocalDateTime.of(2023, 6, 1, 19, 30)));

        URI url = URI.create("http://localhost:8080/tasks/task/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201,response.statusCode(),"Статус код при обновлении задачи не совпадает");
        assertEquals("Задача обновлена",response.body(), "Задача не обновляется");
    }

    @Test
    public void shouldGetTask() throws IOException, InterruptedException {
        addTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        String json = gson.toJson(new Task(1,"Выключить посудомойку","В 12:30", Status.NEW,5,
                LocalDateTime.of(2023, 6, 1, 12, 30)));

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200,response.statusCode(),"Статус код при получении задачи не совпадает");
        assertEquals(json,response.body(),"Ожидаемая задача не совпадает с полученной");
    }

    @Test
    public void shouldGetAllTasks() throws IOException, InterruptedException {
        addTask(task1);
        addTask(task2);

        Task taskTest1 = new Task(1,"Выключить посудомойку","В 12:30", Status.NEW,5,
                LocalDateTime.of(2023, 6, 1, 12, 30));
        Task taskTest2 = new Task(2,"Забрать квитанции","Из почтового ящика", Status.DONE,5,
                LocalDateTime.of(2023, 5, 29, 8, 15));
        String json = gson.toJson(List.of(taskTest1,taskTest2));

        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200,response.statusCode(),"Статус код при получении всех задач не совпадает");
        assertEquals(json,response.body(),"Ожидаемые задачи не совпают с полученными");
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        addTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201,response.statusCode(),"Статус код при удалении задачи не совпадает");
        assertEquals("Задача удалена",response.body(),"Задача не удаляется");
    }

    @Test
    public void shouldDeleteAllTasks() throws IOException, InterruptedException {
        addTask(task1);
        addTask(task2);

        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201,response.statusCode(),"Статус код при удалении всех задач не совпадает");
        assertEquals("Все задачи удалены",response.body(),"Задачи не удаляются");
    }

    @Test
    public void shouldCreateEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201,response.statusCode(),"Статус код при создании эпика не совпадает");
        assertEquals("Эпик создан",response.body(),"Эпик не создается");
    }

    @Test
    public void shouldUpdateEpic() throws IOException, InterruptedException {
        addEpic(epic1);

        String json = gson.toJson(new Epic(1,"Отправить посылку","До 3.06"));

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201,response.statusCode(),"Статус код при обновлении эпика не совпадает");
        assertEquals("Эпик обновлен",response.body(),"Эпик не обновляется");
    }

    @Test
    public void shouldGetEpic() throws IOException, InterruptedException {
        addEpic(epic1);

        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        String json = gson.toJson(new Epic(1,"Отправить посылку","До 3.06"));

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200,response.statusCode(),"Статус код при получении эпика не совпадает");
        assertEquals(json,response.body(),"Ожидаемый эпик не совпадает с полученным");
    }

    @Test
    public void shouldGetAllEpics() throws IOException, InterruptedException {
        addEpic(epic1);
        addEpic(epic2);

        Epic epicTest1 = new Epic(1,"Отправить посылку","До 3.06");
        Epic epicTest2 = new Epic(2,"Заполнить холодильник","Не забыть курицу");
        String json = gson.toJson(List.of(epicTest1,epicTest2));

        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200,response.statusCode(),"Статус код при получении всех эпиков не совпадает");
        assertEquals(json,response.body(),"Ожидаемые эпики не совпадают с полученными");
    }

    @Test
    public void shouldDeleteEpic() throws IOException, InterruptedException {
        addEpic(epic1);

        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201,response.statusCode(),"Статус код при удалении эпика не совпадает");
        assertEquals("Эпик удален",response.body(),"Эпик не удаляется");
    }

    @Test
    public void shouldDeleteAllEpics() throws IOException, InterruptedException {
        addEpic(epic1);
        addEpic(epic2);

        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201,response.statusCode(),"Статус код при удалении эпиков не совпадает");
        assertEquals("Все эпики удалены",response.body(),"Эпики не удаляются");
    }

    @Test
    public void shouldCreateSubtask() throws IOException, InterruptedException {
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addEpic(epic1);

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        String json = gson.toJson(subtask1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201,response.statusCode(),"Статус код при создании подзадачи не совпадает");
        assertEquals("Подзадача создана",response.body(),"Подзадача не создается");
    }

    @Test
    public void shouldUpdateSubtask() throws IOException, InterruptedException {
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addEpic(epic1);
        addSubtask(subtask1);
        addSubtask(subtask2);
        addSubtask(subtask3);

        String json = gson.toJson(new Subtask(7,"Оформить отправку","Не забыть наличные",
                Status.NEW, 4,60, LocalDateTime.of(2023, 6,
                1, 12, 0)));

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201,response.statusCode(),"Статус код при обновлении подзадачи не совпадает");
        assertEquals("Подзадача обновлена",response.body(),"Подзадача не обновляется");
    }

    @Test
    public void shouldGetSubtask() throws IOException, InterruptedException {
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addEpic(epic1);
        addSubtask(subtask1);
        addSubtask(subtask2);
        addSubtask(subtask3);

        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=7");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        String json = gson.toJson(new Subtask(7,"Оформить отправку","Не забыть наличные",
                Status.NEW, 4,60, LocalDateTime.of(2023, 6,
                1, 12, 0)));

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200,response.statusCode(),"Статус код при получении подзадачи не совпадает");
        assertEquals(json,response.body(),"Ожидаемая подзадача не совпадает с полученной");
    }

    @Test
    public void shouldGetAllSubtasks() throws IOException, InterruptedException {
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addEpic(epic1);
        addSubtask(subtask1);
        addSubtask(subtask2);
        addSubtask(subtask3);

        Subtask subtaskTest1 = new Subtask(5,"Упаковать посылку","Перемотать скотчем коробку",
                Status.DONE, 4,60, LocalDateTime.of(2023, 5,
                31, 19, 20));
        Subtask subtaskTest2 = new Subtask(6,"Дойти до почты","На п. Карла Маркса",
                Status.IN_PROGRESS, 4,20, LocalDateTime.of(2023, 6,
                1, 11, 40));
        Subtask subtaskTest3 = new Subtask(7,"Оформить отправку","Не забыть наличные",
                Status.NEW, 4,60, LocalDateTime.of(2023, 6,
                1, 12, 0));
        String json = gson.toJson(List.of(subtaskTest1,subtaskTest2,subtaskTest3));

        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200,response.statusCode(),"Статус код при получении всех подзадач не совпадает");
        assertEquals(json,response.body(),"Ожидаемые подзадачи не совпадают с полученными");
    }

    @Test
    public void shouldDeleteSubtask() throws IOException, InterruptedException {
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addEpic(epic1);
        addSubtask(subtask1);
        addSubtask(subtask2);
        addSubtask(subtask3);

        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=6");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201,response.statusCode(),"Статус код при удалении подзадачи не совпадает");
        assertEquals("Подзадача удалена",response.body(),"Подзадача не удаляется");
    }

    @Test
    public void shouldDeleteAllSubtasks() throws IOException, InterruptedException {
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addEpic(epic1);
        addSubtask(subtask1);
        addSubtask(subtask2);
        addSubtask(subtask3);

        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201,response.statusCode(),"Статус код при удалении всех подзадач не совпадает");
        assertEquals("Все подзадачи удалены",response.body(),"Подзадачи не удаляются");
    }

    @Test
    public void shouldGetSubtaskOfEpic() throws IOException, InterruptedException {
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addEpic(epic1);
        addSubtask(subtask1);
        addSubtask(subtask2);
        addSubtask(subtask3);

        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=4");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200,response.statusCode(),
                "Статус код при получении подзадач эпика не совпадает");
    }

    @Test
    public void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addEpic(epic1);
        addSubtask(subtask1);
        addSubtask(subtask2);
        addSubtask(subtask3);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200,response.statusCode(),
                "Статус код при получении преоритизированного списка задач не совпадает");
    }

    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        addTask(task1);
        addTask(task2);
        addTask(task3);

        getTask(2);
        getTask(1);
        getTask(3);

        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        String json = "[" + gson.toJson(new Task(2,"Забрать квитанции","Из почтового ящика",
                Status.DONE,5, LocalDateTime.of(2023, 5, 29, 8, 15)))
                + "," + gson.toJson(new Task(1,"Выключить посудомойку","В 12:30", Status.NEW,
                5, LocalDateTime.of(2023, 6, 1, 12, 30)))
                + "," + gson.toJson(new Task(3,"Заказать воду","До 14:00", Status.NEW,15,
                LocalDateTime.of(2023, 5, 30, 9, 5))) +"]";
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200,response.statusCode(), "Статус код при получении истории не совпадает");
        assertEquals(json,response.body(),"Ожидаемая история не совпадает с полученной");
    }

    private void addTask(Task task) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        add(task,url);
    }

    private void addEpic(Epic epic) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        add(epic,url);
    }

    private void addSubtask(Subtask subtask) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        add(subtask,url);
    }

    private void add(Task task, URI url) throws IOException, InterruptedException {
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        client.send(request, handler);
    }

    private void getTask(int id) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        client.send(request, handler);
    }
}