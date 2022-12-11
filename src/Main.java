import exception.ManagerSaveException;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;
import services.manager.TasksManager;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;

public class Main {
    public static void main(String[] args) throws ManagerSaveException {
        TasksManager tasksManager = Managers.getDefault();

        Task task1 = new Task("Task1", "Task1_desc", TaskStatus.NEW,
                LocalDateTime.of(2022, FEBRUARY, 10, 20, 0), 100);

        Task task2 = new Task("Task1", "Task1_desc", TaskStatus.NEW);

//        System.out.println(task1.getEndTime());
//        System.out.println(task2.getEndTime());


        Epic epic1 = new Epic("Epic1", "Epic1_desc");
        int epicId = tasksManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Sub1", "Sub1_desc", TaskStatus.NEW,
            LocalDateTime.of(2022, FEBRUARY, 15, 10, 0), 30, epicId);
        tasksManager.createSubTask(subtask1);

        Subtask subtask2 = new Subtask("Sub1", "Sub1_desc", TaskStatus.NEW,
            LocalDateTime.of(2022, FEBRUARY, 14, 10, 0), 30, epicId);
        tasksManager.createSubTask(subtask2);

        Subtask subtask4 = new Subtask("Sub1", "Sub1_desc", TaskStatus.NEW,epicId);
        tasksManager.createSubTask(subtask4);

        Subtask subtask3 = new Subtask("Sub1", "Sub1_desc", TaskStatus.NEW,
            LocalDateTime.of(2022, FEBRUARY, 17, 10, 0), 30, epicId);
        tasksManager.createSubTask(subtask3);

        Subtask subtask5 = new Subtask("Sub1", "Sub1_desc", TaskStatus.NEW,epicId);
        tasksManager.createSubTask(subtask5);

        tasksManager.deleteAllSubTasksInEpic(epic1);


        System.out.println(tasksManager.getPrioritizedTasks());

        System.out.println(epic1.getStartTime());

    }
}
