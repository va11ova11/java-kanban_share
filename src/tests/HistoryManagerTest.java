package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
  private TasksManager taskManager;
  private int taskId1;
  private int taskId2;
  private int taskId3;

  @BeforeEach
  public void beforeEach() {
    taskManager = Managers.getDefault();
    historyManager = taskManager.getHistoryManager();
    Task task1 = new Task("Task1", "Task1_desc");
    Task task2 = new Task("Task2", "Task2_desc");
    Task task3 = new Task("Task3", "Task3_desc");
    taskId1 = taskManager.createTask(task1);
    taskId2 = taskManager.createTask(task2);
    taskId3 = taskManager.createTask(task3);
  }

  @Test
  public void shouldAddTaskInHistory() {
    taskManager.getTaskById(taskId1);
    List<Task> history = historyManager.getHistory();
    assertEquals(1, history.size(), "Не добавляются задачи в историю");
  }

  @Test
  public void shouldDeleteFirstTaskInHistory() {
    taskManager.getTaskById(taskId1);
    taskManager.getTaskById(taskId2);
    historyManager.removeInHistory(taskId2);

    List<Task> history = historyManager.getHistory();
    boolean isRemoveInHistory = !(history.contains(taskManager.getTaskById(taskId2)));
    assertTrue(isRemoveInHistory, "Не удаляется задача из начала истории");
  }


  @Test
  public void shouldDeleteMiddleElementInHistory() {
    taskManager.getTaskById(taskId1);
    taskManager.getTaskById(taskId2);
    taskManager.getTaskById(taskId3);

    historyManager.removeInHistory(taskId2);
    List<Task> history = historyManager.getHistory();
    assertEquals(2, history.size(), "Не удаляется элемент с середины Истории");
  }

  @Test
  public void shouldDeleteLastElementInHistory() {
    taskManager.getTaskById(taskId1);
    taskManager.getTaskById(taskId2);
    historyManager.removeInHistory(taskId1);
    List<Task> history = historyManager.getHistory();

    boolean isRemoveInHistory = !(history.contains(taskManager.getTaskById(taskId1)));
    assertTrue(isRemoveInHistory, "Не удаляется задача из конца истории");
  }

  @Test
  public void shouldNotDuplicateInHistory() {
    taskManager.getTaskById(taskId1);
    taskManager.getTaskById(taskId1);

    List<Task> history = historyManager.getHistory();
    assertEquals(1, history.size(), "В истории есть дубликаты");
  }
}
