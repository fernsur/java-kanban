package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final TaskManager manager = Managers.getHttpTaskManager("http://localhost:8078");
    private final HttpServer httpServer;


    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT),0);
        httpServer.createContext("/tasks", this::handle);
        httpServer.start();
    }

    private void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String method = exchange.getRequestMethod();

        Endpoint endpoint = getEndpoint(uri,path,method);

        switch (endpoint) {
            case GET_TASKS: {
                getPrioritizedTasksHandle(exchange);
                break;
            }
            case GET_HISTORY: {
                getHistoryHandle(exchange);
                break;
            }
            case GET_SUBTASK_EPIC: {
                getSubtaskOfEpicHandle(exchange);
                break;
            }
            case GET_ALL_TASKS: {
                getAllTasksHandle(exchange);
                break;
            }
            case GET_TASK: {
                getTaskHandle(exchange);
                break;
            }
            case GET_ALL_EPICS: {
                getAllEpicsHandle(exchange);
                break;
            }
            case GET_EPIC: {
                getEpicHandle(exchange);
                break;
            }
            case GET_ALL_SUBTASKS: {
                getAllSubtasksHandle(exchange);
                break;
            }
            case GET_SUBTASK: {
                getSubtaskHandle(exchange);
                break;
            }
            case DELETE_ALL_TASKS: {
                deleteAllTasksHandle(exchange);
                break;
            }
            case DELETE_TASK: {
                deleteTaskHandle(exchange);
                break;
            }
            case DELETE_ALL_EPICS: {
                deleteAllEpicsHandle(exchange);
                break;
            }
            case DELETE_EPIC: {
                deleteEpicHandle(exchange);
                break;
            }
            case DELETE_ALL_SUBTASKS: {
                deleteAllSubtaskHandle(exchange);
                break;
            }
            case DELETE_SUBTASK: {
                deleteSubtaskHandle(exchange);
                break;
            }
            case POST_TASK: {
                createTaskHandle(exchange);
                break;
            }
            case POST_EPIC: {
                createEpicHandle(exchange);
                break;
            }
            case POST_SUBTASK: {
                createSubtaskHandle(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private Endpoint getEndpoint(URI uri, String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        String query = uri.getQuery();

        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            return Endpoint.GET_TASKS;
        }

        if (pathParts.length == 3 && pathParts[1].equals("tasks") && query == null) {
            if (pathParts[2].equals("history")) {
                return Endpoint.GET_HISTORY;
            } else if (requestMethod.equals("GET")) {
                switch (pathParts[2]) {
                    case "task":
                        return Endpoint.GET_ALL_TASKS;
                    case "epic":
                        return Endpoint.GET_ALL_EPICS;
                    case "subtask":
                        return Endpoint.GET_ALL_SUBTASKS;
                }
            } else if (requestMethod.equals("DELETE")) {
                switch (pathParts[2]) {
                    case "task":
                        return Endpoint.DELETE_ALL_TASKS;
                    case "epic":
                        return Endpoint.DELETE_ALL_EPICS;
                    case "subtask":
                        return Endpoint.DELETE_ALL_SUBTASKS;
                }
            } else if (requestMethod.equals("POST")) {
                switch (pathParts[2]) {
                    case "task":
                        return Endpoint.POST_TASK;
                    case "epic":
                        return Endpoint.POST_EPIC;
                    case "subtask":
                        return Endpoint.POST_SUBTASK;
                }
            }
        }

        if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                switch (pathParts[2]) {
                    case "task":
                        return Endpoint.GET_TASK;
                    case "epic":
                        return Endpoint.GET_EPIC;
                    case "subtask":
                        return Endpoint.GET_SUBTASK;
                }
            } else if (requestMethod.equals("DELETE")) {
                switch (pathParts[2]) {
                    case "task":
                        return Endpoint.DELETE_TASK;
                    case "epic":
                        return Endpoint.DELETE_EPIC;
                    case "subtask":
                        return Endpoint.DELETE_SUBTASK;
                }
            }
        }

        if (pathParts.length == 4 && pathParts[1].equals("tasks")
                && pathParts[2].equals("subtask") && pathParts[3].equals("epic")) {
            return Endpoint.GET_SUBTASK_EPIC;
        }

        return Endpoint.UNKNOWN;
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private void getHistoryHandle(HttpExchange exchange) throws IOException {
        String response = gson.toJson(manager.history());
        writeResponse(exchange, response, 200);
    }

    private void getPrioritizedTasksHandle(HttpExchange exchange) throws IOException {
        String response = gson.toJson(manager.getPrioritizedTasks());
        writeResponse(exchange, response, 200);
    }

    private void getSubtaskOfEpicHandle(HttpExchange exchange) throws IOException {
        int id = parseId(exchange);
        if (id == -1) {
            writeResponse(exchange, "Передан некорректный идентификатор", 400);
            return;
        }
        String response = gson.toJson(manager.getEpicSubtask(id));
        writeResponse(exchange, response, 200);
    }

    private void getAllTasksHandle(HttpExchange exchange) throws IOException {
        String response = gson.toJson(manager.getAllTasks());
        if (response.isEmpty()) {
            writeResponse(exchange, "Ошибка при получении задач", 400);
            return;
        }
        writeResponse(exchange, response, 200);
    }

    private void getTaskHandle(HttpExchange exchange) throws IOException {
        int id = parseId(exchange);
        if (id == -1) {
            writeResponse(exchange, "Передан некорректный идентификатор", 400);
            return;
        }
        String response = gson.toJson(manager.getTask(id));
        if (response.isEmpty() || response.isBlank()) {
            writeResponse(exchange, "Ошибка при получении задачи", 400);
            return;
        }
        writeResponse(exchange, response, 200);
    }

    private void deleteAllTasksHandle(HttpExchange exchange) throws IOException {
        manager.deleteAllTasks();
        writeResponse(exchange, "Все задачи удалены", 201);
    }

    private void deleteTaskHandle(HttpExchange exchange) throws IOException {
        int id = parseId(exchange);
        if (id == -1) {
            writeResponse(exchange, "Передан некорректный идентификатор", 400);
            return;
        }
        manager.deleteTask(id);
        writeResponse(exchange, "Задача удалена", 201);
    }

    private void createTaskHandle(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        if (body.isEmpty()) {
            writeResponse(exchange, "Получен некорректный JSON задачи", 400);
            return;
        }
            Task taskFromJson = gson.fromJson(body, Task.class);

        if (taskFromJson.getTitle() == null || taskFromJson.getStartTime() == null) {
                writeResponse(exchange,
                        "Поля заголовка или времени начала не могут быть пустыми", 400);
                return;
        }
        if (taskFromJson.getId() != 0) {
            manager.updateTask(taskFromJson);
            writeResponse(exchange, "Задача обновлена", 201);
        } else {
            manager.createTask(taskFromJson);
            writeResponse(exchange, "Задача создана", 201);
        }
    }

    private void getAllEpicsHandle(HttpExchange exchange) throws IOException {
        String response = gson.toJson(manager.getAllEpics());
        if (response.isEmpty()) {
            writeResponse(exchange, "Ошибка при получении эпиков", 400);
            return;
        }
        writeResponse(exchange, response, 200);
    }

    private void getEpicHandle(HttpExchange exchange) throws IOException {
        int id = parseId(exchange);
        if (id == -1) {
            writeResponse(exchange, "Передан некорректный идентификатор", 400);
            return;
        }
        String response = gson.toJson(manager.getEpic(id));
        if (response.isEmpty()) {
            writeResponse(exchange, "Ошибка при получении эпика", 400);
            return;
        }
        writeResponse(exchange, response, 200);
    }

    private void deleteAllEpicsHandle(HttpExchange exchange) throws IOException {
        manager.deleteAllEpics();
        writeResponse(exchange, "Все эпики удалены", 201);
    }

    private void deleteEpicHandle(HttpExchange exchange) throws IOException {
        int id = parseId(exchange);
        if (id == -1) {
            writeResponse(exchange, "Передан некорректный идентификатор", 400);
            return;
        }
        manager.deleteEpic(id);
        writeResponse(exchange, "Эпик удален", 201);
    }

    private void createEpicHandle(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        if (body.isEmpty()) {
            writeResponse(exchange, "Получен некорректный JSON задачи", 400);
            return;
        }
        Epic epicFromJson = gson.fromJson(body, Epic.class);
        if (epicFromJson.getTitle() == null) {
            writeResponse(exchange,
                    "Поле заголовка не может быть пустым", 400);
            return;
        }
        if (epicFromJson.getId() != 0) {
            manager.updateEpic(epicFromJson);
            writeResponse(exchange, "Эпик обновлен", 201);
        } else {
            manager.createEpic(epicFromJson);
            writeResponse(exchange, "Эпик создан", 201);
        }
    }

    private void getAllSubtasksHandle(HttpExchange exchange) throws IOException {
        String response = gson.toJson(manager.getAllSubtasks());
        if (response.isEmpty()) {
            writeResponse(exchange, "Ошибка при получении подзадач", 400);
            return;
        }
        writeResponse(exchange, response, 200);
    }

    private void getSubtaskHandle(HttpExchange exchange) throws IOException {
        int id = parseId(exchange);
        if (id == -1) {
            writeResponse(exchange, "Передан некорректный идентификатор", 400);
            return;
        }
        String response = gson.toJson(manager.getSubtask(id));
        if (response.isEmpty()) {
            writeResponse(exchange, "Ошибка при получении подзадачи", 400);
            return;
        }
        writeResponse(exchange, response, 200);
    }

    private void deleteAllSubtaskHandle(HttpExchange exchange) throws IOException {
        manager.deleteAllSubtasks();
        writeResponse(exchange, "Все подзадачи удалены", 201);
    }

    private void deleteSubtaskHandle(HttpExchange exchange) throws IOException {
        int id = parseId(exchange);
        if (id == -1) {
            writeResponse(exchange, "Передан некорректный идентификатор", 400);
            return;
        }
        manager.deleteSubtask(id);
        writeResponse(exchange, "Подзадача удалена", 201);
    }

    private void createSubtaskHandle(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        if (body.isEmpty()) {
            writeResponse(exchange, "Получен некорректный JSON задачи", 400);
            return;
        }
        Subtask subtaskFromJson = gson.fromJson(body, Subtask.class);
        if (subtaskFromJson.getTitle() == null || subtaskFromJson.getStartTime() == null
                || subtaskFromJson.getEpicNumber() == 0) {
            writeResponse(exchange,
                    "Поля заголовка/номера эпика/времени начала не могут быть пустыми", 400);
            return;
        }
        if (subtaskFromJson.getId() != 0) {
            manager.updateSubtask(subtaskFromJson);
            writeResponse(exchange, "Подзадача обновлена", 201);
        } else {
            manager.createSubtask(subtaskFromJson);
            writeResponse(exchange, "Подзадача создана", 201);
        }
    }

    private int parseId(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        try {
            return Integer.parseInt(query.split("=")[1]);
        } catch (NumberFormatException exp) {
            return -1;
        }
    }

    public void stop() {
        httpServer.stop(1);
    }
}