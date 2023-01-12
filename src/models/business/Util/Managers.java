package models.business.Util;
import java.io.IOException;
import java.net.URI;
import kvServer.KVServer;
import services.manager.FileBackedTasksManager;
import services.manager.HttpTaskManager;
import services.manager.history.HistoryManager;
import services.manager.history.InMemoryHistoryManager;
import services.manager.InMemoryTasksManager;
import services.manager.TasksManager;
import java.net.URL;

import java.io.File;

public class Managers {
    public static TasksManager getDefault(URL url, boolean isLoad) throws IOException, InterruptedException {
        return new HttpTaskManager(url, isLoad);
    }

    public static KVServer getDefaultKVServer() throws IOException {
        return new KVServer();
    }

    public static TasksManager getTaskManager() {
        return new InMemoryTasksManager();
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
    public static FileBackedTasksManager loadFromFile(File file) {
        return FileBackedTasksManager.load(file);
    }

    public static FileBackedTasksManager getFailBackedTaskManager() {
        return new FileBackedTasksManager();
    }
}
