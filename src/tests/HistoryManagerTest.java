package tests;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import models.business.Task;
import models.business.Util.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.manager.TasksManager;
import services.manager.history.HistoryManager;

public class HistoryManagerTest {

  private HistoryManager historyManager;
  private TasksManager taskManager;

  @BeforeEach
  public void beforeEach() {
     historyManager = Managers.getDefaultHistory();
     taskManager = Managers.getDefault();
  }

  @Test
  public void shouldHistoryIsEmpty() {
    Task task = new Task("Task1", "Task1_desc");
    int taskId = taskManager.createTask(task);

    historyManager.addTaskInHistory(task);
    historyManager.removeInHistory(taskId);

    List<Task> history = historyManager.getHistory();

    assertNull(history, "История задач не отчистилась");
  }

  @Test
  public void shouldBeNoDuplicatesInTheHistory () {
    Task task1 = new Task("Task1", "Task1_desc");
    Task task2 = new Task("Task2", "Task2_desc");

    historyManager.addTaskInHistory(task1);
    historyManager.addTaskInHistory(task2);
    historyManager.addTaskInHistory(task1);

    List<Task> history = historyManager.getHistory();
    Assertions.assertEquals(2, history.size());

  }

}
