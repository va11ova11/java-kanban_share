package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import models.business.Epic;
import models.business.SubTask;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.manager.TasksManager;

public class EpicTest {

  private static TasksManager manager;
  private static Epic epic;
  private static int epicId;
  private static SubTask subTask1;
  private static SubTask subTask2;

  @BeforeAll
  public static void beforeAll() {
    manager = Managers.getDefault();
    epic = new Epic("Epic1", "Epic1_description");
    epicId = manager.createEpic(epic);
    subTask1 = new SubTask("Subtask1", "Subtask1_description", epicId);
    subTask2 = new SubTask("Subtask2", "Subtask2_description", epicId);
  }

  @BeforeEach
  public void beforeEach() {
    manager.createSubTask(subTask1);
    manager.createSubTask(subTask2);
  }


  @Test
  public void shouldEpicStatusIsNewWhenSubtaskListIsEmpty() {
    manager.deleteAllSubTasksInEpic(epic);
    TaskStatus actual = manager.getEpicById(epicId).getTaskStatus();
    assertEquals(TaskStatus.NEW, actual, "Неправильный статус Эпика");
  }

  @Test
  public void shouldEpicStatusIsNewWhenAllSubtasksStatusIsNew() {
    manager.setSubtaskStatus(subTask1, TaskStatus.NEW);
    manager.setSubtaskStatus(subTask2, TaskStatus.NEW);
    TaskStatus actual = manager.getEpicById(epicId).getTaskStatus();
    assertEquals(TaskStatus.NEW, actual, "Неправильный статус Эпика");
  }

  @Test
  public void shouldEpicStatusIsDoneWhenAllSubtasksStatusIsDone() {
    manager.setSubtaskStatus(subTask1, TaskStatus.DONE);
    manager.setSubtaskStatus(subTask2, TaskStatus.DONE);
    TaskStatus actual = manager.getEpicById(epicId).getTaskStatus();
    assertEquals(TaskStatus.DONE, actual, "Неправильный статус Эпика");
  }

  @Test
  public void shouldEpicStatusIsInProgressWhenSubtaskStatusNewAndDone() {
    manager.setSubtaskStatus(subTask1, TaskStatus.NEW);
    manager.setSubtaskStatus(subTask2, TaskStatus.DONE);
    TaskStatus actual = manager.getEpicById(epicId).getTaskStatus();
    assertEquals(TaskStatus.IN_PROGRESS, actual, "Неправильный статус Эпика");
  }

  @Test
  public void shouldEpicStatusIsInProgressWhenAllSubtasksStatusIsInProgress() {
    manager.setSubtaskStatus(subTask1, TaskStatus.IN_PROGRESS);
    manager.setSubtaskStatus(subTask2, TaskStatus.IN_PROGRESS);
    TaskStatus actual = manager.getEpicById(epicId).getTaskStatus();
    assertEquals(TaskStatus.IN_PROGRESS, actual, "Неправильный статус Эпика");
  }
}
