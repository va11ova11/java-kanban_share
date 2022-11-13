import exception.ManagerSaveException;
import models.business.Epic;
import models.business.SubTask;
import models.business.Util.Managers;
import models.business.enums.Status;
import services.manager.TasksManager;

public class Main {
    public static void main(String[] args) throws ManagerSaveException {
        TasksManager manager = Managers.getDefault();

        Epic epic1 = new Epic("Epic#1", "Epic1 description");
        Epic epic2 = new Epic("Epic#2", "Epic2 description");
        int epicId1 = manager.createEpic(epic1);
        int epicId2 = manager.createEpic(epic2);

        SubTask subTask1 = new SubTask("Subtask in Epic 1", "Subtask1 description", Status.NEW, epicId1);
        SubTask subtask2 = new SubTask("Subtask in Epic 1", "Subtask2 description", Status.IN_PROGRESS, epicId1);
        SubTask subtask3 = new SubTask("Subtask in Epic 2", "Subtask3 description", Status.DONE, epicId2);
        int subtaskId1 = manager.createSubTask(subTask1);
        int subtaskId2 = manager.createSubTask(subtask2);
        int subtaskId3 = manager.createSubTask(subtask3);

        System.out.println("Добавить первую задачу (Эпик) в список истории");
        manager.getEpicById(epicId1);
        manager.printHistory();
        System.out.println("------------------------------------");
        System.out.println();

        System.out.println("Добавить вторую задачу (Сабтаск) в список");
        manager.getSubTaskById(subtaskId1);
        manager.printHistory();
        System.out.println("------------------------------------");
        System.out.println();

        System.out.println("Добавить третью задачу (Сабтаск) в список");
        manager.getSubTaskById(subtaskId2);
        manager.printHistory();
        System.out.println("------------------------------------");
        System.out.println();

        System.out.println("Добавить четвёртую задачу (Сабтаск) в список");
        manager.getSubTaskById(subtaskId3);
        manager.printHistory();
        System.out.println("------------------------------------");
        System.out.println();

        System.out.println("Добавить пятую задачу (Сабтаск) в список");
        manager.getEpicById(epicId2);
        manager.printHistory();
        System.out.println("------------------------------------");
        System.out.println();


        System.out.println("Удалить 1 задачу (Эпик1)");
        manager.deleteTaskById(epicId1);
        manager.printHistory();
        System.out.println("------------------------------------");
        System.out.println();

        System.out.println("Удалить 2 задачу (Сабтаск3)");
        manager.deleteTaskById(subtaskId3);
        manager.printHistory();
        System.out.println("------------------------------------");
        System.out.println();

        System.out.println("Удалить последнюю задачу (Епик2)");
        manager.deleteTaskById(epicId2);
        manager.printHistory();
        System.out.println("------------------------------------");
        System.out.println();
    }
}
