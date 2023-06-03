package manager.test;

import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void startManager() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
    }
}