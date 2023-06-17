package test;

import manager.HttpTaskManager;
import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.HttpTaskServer;
import server.KVServer;

import java.io.IOException;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;

    @BeforeEach
    public void startManager() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();

        taskManager = (HttpTaskManager) Managers.getHttpTaskManager("http://localhost:8078");
    }

    @AfterEach
    public void stop() {
        kvServer.stop();
        httpTaskServer.stop();
    }
}