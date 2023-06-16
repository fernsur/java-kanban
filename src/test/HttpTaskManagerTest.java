package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.HttpTaskManager;
import manager.TaskManager;
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

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {

    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private Task task1;
    private Task task2;
    private Task task3;
    private Epic epic1;
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
    public void shouldLoadFromServer() throws IOException, InterruptedException {
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addEpic(epic1);
        addSubtask(subtask1);
        addSubtask(subtask2);
        addSubtask(subtask3);
        getTask(1);

        TaskManager manager = HttpTaskManager.load("http://localhost:8078");

        assertEquals(3,manager.getAllTasks().size());
        assertEquals(1,manager.getAllEpics().size());
        assertEquals(3,manager.getAllSubtasks().size());
        assertEquals(1,manager.history().size());
        assertEquals(6,manager.getPrioritizedTasks().size());
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