package models.business.Util;
import services.manager.FileBackedTasksManager;
import services.manager.history.HistoryManager;
import services.manager.history.InMemoryHistoryManager;
import services.manager.InMemoryTasksManager;
import services.manager.TasksManager;

import java.io.File;

public class Managers {
    public static TasksManager getDefault() {
        return new InMemoryTasksManager();
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
    public static FileBackedTasksManager loadFromFile(File file){
        return FileBackedTasksManager.loadFromFile(file);
    }

    public static FileBackedTasksManager getFailBackedTaskManager() {
        return new FileBackedTasksManager();
    }
}
