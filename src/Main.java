import HttpServer.HttpTaskServer;
import com.google.gson.Gson;
import java.io.IOException;
import java.time.LocalDateTime;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.Util.Managers;
import models.business.Util.Printer;
import models.business.enums.TaskStatus;
import services.manager.TasksManager;

public class Main {
    public static void main(String[] args) throws IOException {


            HttpTaskServer httpTaskServer = new HttpTaskServer();

//        TasksManager tasksManager = Managers.getDefault();
//
//        Task task1 = new Task("First_Task", "FirstTask_description", TaskStatus.NEW,
//            LocalDateTime.of(2022, 12, 14, 10, 0), 60);
//        tasksManager.createTask(task1);
//
//        Task task2 = new Task("Second_Task", "SecondTask_description", TaskStatus.DONE,
//            LocalDateTime.of(2022, 12, 14, 8, 0), 30);
//        tasksManager.createTask(task2);
//
//        Epic epic = new Epic("Epic1", "Epic1_desc");
//        int epicId1 = tasksManager.createEpic(epic);
//
//        Subtask subtask1 = new Subtask("Subtask", "Subtask_description", TaskStatus.IN_PROGRESS,
//            LocalDateTime.of(2022, 12, 14, 6,0), 60, epicId1);
//        tasksManager.createSubTask(subtask1);
//
//        Task task4 = new Task("Forth_Task", "ForthTask_description", TaskStatus.NEW,
//            LocalDateTime.of(2022, 12, 14, 20, 0), 120);
//        tasksManager.createTask(task4);
//        Printer.printAllTask(tasksManager);
//
//
//        System.out.println("------------");
//        Printer.printPrioritizedTask(tasksManager);
    }
}
