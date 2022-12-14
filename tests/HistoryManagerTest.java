

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import models.business.Task;
import models.business.Util.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.manager.TasksManager;
import services.manager.history.HistoryManager;

public class HistoryManagerTest {
  private HistoryManager historyManager;
  private int taskId1;
  private int taskId2;
  private int taskId3;
  private Task task1;
  private Task task2;
  private Task task3;

  @BeforeEach
  public void beforeEach() {
    historyManager = Managers.getDefaultHistory();
    TasksManager taskManager = Managers.getDefault();
    task1 = new Task("Task1", "Task1_desc");
    task2 = new Task("Task2", "Task2_desc");
    task3 = new Task("Task3", "Task3_desc");
    taskId1 = taskManager.createTask(task1);
    taskId2 = taskManager.createTask(task2);
    taskId3 = taskManager.createTask(task3);
  }

  @Test
  public void shouldAddTaskInHistory() {
    historyManager.add(task1);
    List<Task> history = historyManager.getHistory();
    assertEquals(1, history.size(), "Не добавляются задачи в историю");
  }

  @Test
  public void shouldDeleteFirstTaskInHistory() {
    historyManager.add(task1);
    historyManager.add(task2);
    historyManager.add(task3);

    historyManager.remove(taskId3);

    List<Task> history = historyManager.getHistory();
    boolean firstElementIsRemoved = history.contains(taskId2);
    assertFalse(firstElementIsRemoved, "Не удаляется задача из начала истории");
  }


  @Test
  public void shouldDeleteMiddleElementInHistory() {
    historyManager.add(task1);
    historyManager.add(task2);
    historyManager.add(task3);

    historyManager.remove(taskId2);
    List<Task> history = historyManager.getHistory();
    boolean middleElementIsRemoved = history.contains(taskId2);
    assertFalse(middleElementIsRemoved, "Не удаляется элемент с середины истории");
  }

  @Test
  public void shouldDeleteLastElementInHistory() {
    historyManager.add(task1);
    historyManager.add(task2);
    historyManager.remove(taskId1);
    List<Task> history = historyManager.getHistory();

    boolean isRemoveInHistory = history.contains(taskId1);
    assertFalse(isRemoveInHistory, "Не удаляется задача из конца истории");
  }

  @Test
  public void shouldNotDuplicateInHistory() {
    historyManager.add(task1);
    historyManager.add(task1);

    List<Task> history = historyManager.getHistory();
    assertEquals(1, history.size(), "В истории есть дубликаты");
  }

  @Test
  public void shouldHistoryIdFromString() {
    String historyString = "1,2,3";

  }
}
