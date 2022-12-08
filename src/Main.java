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

        System.out.println(task1.getEndTime());
        System.out.println(task2.getEndTime());

    }
}
