import exception.ManagerSaveException;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.Util.Managers;
import models.business.Util.Printer;
import models.business.enums.TaskStatus;
import services.manager.TasksManager;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;

public class Main {
    public static void main(String[] args) throws ManagerSaveException {
        TasksManager tasksManager = Managers.getDefault();

        Task taskHasTime = new Task("TaskTime", "Task has time", TaskStatus.NEW, "10.01.2022;09:00", 60);
        int task1 = tasksManager.createTask(taskHasTime);

        Task taskHasTime2 = new Task("TaskTime", "Task has time", TaskStatus.NEW, "10.01.2022;10:00", 60);
        int task2 = tasksManager.createTask(taskHasTime2);
        taskHasTime2.setStartTime("10.01.2022;06:00");
        tasksManager.updateTask(taskHasTime2);

        Epic epic = new Epic("Epic1", "Epic1_desc");
        int epicId = tasksManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Subtask_desc1", TaskStatus.DONE, "10.01.2022;08:00", 30, epicId);
        tasksManager.createSubTask(subtask);

        Task taskHasTime3 = new Task("TaskTime", "Task has time", TaskStatus.NEW, "10.01.2022;07:00", 30);
        tasksManager.createTask(taskHasTime3);
        Printer.printAllTask(tasksManager);


        System.out.println("------------");
        System.out.println("Задачи отсортированные по времени выполнения");
        System.out.println(tasksManager.getPrioritizedTasks());


    }
}
