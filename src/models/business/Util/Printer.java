package models.business.Util;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import services.manager.TasksManager;

public class Printer {

  public static void printPrioritizedTask(TasksManager tasksManager) {
    System.out.println("Задачи отсортированные по времени");
    int counter = 0;
    for(Task task :tasksManager.getPrioritizedTasks()) {
      System.out.println(++counter + ". - " + task.toString());
    }
  }

  public static void printAllTaskAndHistory(TasksManager tasksManager) {
    printAllTask(tasksManager);
    printHistory(tasksManager);
  }

  public static void printAllTask(TasksManager tasksManager) {
    System.out.println("Задачи:");
    int j = 1;
    for (Task task : tasksManager.getTasks().values()) {
      System.out.println(j++ + ". " + task);
    }
    System.out.println();
    if(tasksManager.getEpics().size() > 0) {
      System.out.println("Эпики:");
      for (Epic epic : tasksManager.getEpics().values()) {
        int i = 0;
        System.out.println(epic);
        System.out.println("---> Подзадачи эпика");
        for (Subtask subTask : tasksManager.getSubTasks().values()) {
          if (subTask.getEpicId() == epic.getId()) {
            System.out.println(++i + ". " + subTask);
          }
        }
        System.out.println();
      }
    }
  }

  public static void printHistory(TasksManager tasksManager) {
    if (tasksManager.getHistory() != null) {
      int taskNumber = 1;
      System.out.println("История просмотренных задач: ");
      for (Task task : tasksManager.getHistory()) {
        System.out.println(taskNumber++ + ". " + task);
      }
    } else {
      System.out.println("История задач пуста.");
    }
  }

}
