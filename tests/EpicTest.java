import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import models.business.Epic;
import models.business.Subtask;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.manager.TasksManager;

public class EpicTest {

  private static TasksManager tasksManager;

  @BeforeAll
  public static void beforeAll() {
    tasksManager = Managers.getDefault();
  }
  @BeforeEach
  public void deleteEpic(){
    tasksManager.deleteAllEpic();
  }

  @Test
  public void shouldEpicStatusIsNewWhenSubtaskListIsEmpty() {
    Epic epic = new Epic("Epic1", "Epic1_description");
    final int epicId = tasksManager.createEpic(epic);

    TaskStatus actual = tasksManager.getEpicById(epicId).getStatus();

    assertEquals(TaskStatus.NEW, actual, "Неправильно задаётся статус эпика при создании");
  }

  @Test
  public void shouldEpicStatusIsNewWhenDeleteAllSubtask() {
    Epic epic = new Epic("Epic1", "Epic1_description");
    final int epicId = tasksManager.createEpic(epic);
    Subtask subtask1 = new Subtask("Subtask1", "Subtask1_description", TaskStatus.NEW, epicId);
    Subtask subtask2 = new Subtask("Subtask2", "Subtask2_description", TaskStatus.NEW, epicId);
    tasksManager.createSubTask(subtask1);
    tasksManager.createSubTask(subtask2);

    tasksManager.deleteAllSubTasksInEpic(epic);
    TaskStatus actual = tasksManager.getEpicById(epicId).getStatus();

    assertEquals(TaskStatus.NEW, actual, "При удалении всех сабтасок статус эпика не меняться на New");
  }

  @Test
  public void shouldEpicStatusIsNewWhenAllSubtasksStatusIsNew() {
    Epic epic = new Epic("Epic1", "Epic1_description");
    final int epicId = tasksManager.createEpic(epic);
    Subtask subtask1 = new Subtask("Subtask1", "Subtask1_description", TaskStatus.DONE, epicId);
    Subtask subtask2 = new Subtask("Subtask2", "Subtask2_description", TaskStatus.DONE, epicId);
    tasksManager.createSubTask(subtask1);
    tasksManager.createSubTask(subtask2);

    subtask1.setStatus(TaskStatus.NEW);
    subtask2.setStatus(TaskStatus.NEW);
    tasksManager.updateSubTask(subtask1);
    tasksManager.updateSubTask(subtask2);
    TaskStatus actual = tasksManager.getEpicById(epicId).getStatus();

    assertEquals(TaskStatus.NEW, actual, "Статус эпика не меняется на NEW");
  }

  @Test
  public void shouldEpicStatusIsDoneWhenAllSubtasksStatusIsDone() {
    Epic epic = new Epic("Epic1", "Epic1_description");
    final int epicId = tasksManager.createEpic(epic);
    Subtask subtask1 = new Subtask("Subtask1", "Subtask1_description", TaskStatus.NEW, epicId);
    Subtask subtask2 = new Subtask("Subtask2", "Subtask2_description", TaskStatus.NEW, epicId);
    tasksManager.createSubTask(subtask1);
    tasksManager.createSubTask(subtask2);

    subtask1.setStatus(TaskStatus.DONE);
    subtask2.setStatus(TaskStatus.DONE);
    tasksManager.updateSubTask(subtask1);
    tasksManager.updateSubTask(subtask2);
    TaskStatus actual = tasksManager.getEpicById(epicId).getStatus();

    assertEquals(TaskStatus.DONE, actual,
            "При изменение статуса всех сабтасок на DONE статус Эпика не меняется на DONE");
  }

  @Test
  public void shouldEpicStatusIsInProgressWhenSubtaskStatusNewAndDone() {
    Epic epic = new Epic("Epic1", "Epic1_description");
    final int epicId = tasksManager.createEpic(epic);
    Subtask subtask1 = new Subtask("Subtask1", "Subtask1_description",  TaskStatus.DONE, epicId);
    Subtask subtask2 = new Subtask("Subtask2", "Subtask2_description", TaskStatus.NEW, epicId);
    tasksManager.createSubTask(subtask1);
    tasksManager.createSubTask(subtask2);

    subtask1.setStatus(TaskStatus.NEW);
    subtask2.setStatus(TaskStatus.DONE);
    TaskStatus actual = tasksManager.getEpicById(epicId).getStatus();

    assertEquals(TaskStatus.IN_PROGRESS, actual,
            "Не меняется статус эпика на IN_PROGRESS при подзадачах со статусом NEW и DONE");
  }

  @Test
  public void shouldEpicStatusIsInProgressWhenAllSubtasksStatusIsInProgress() {
    Epic epic = new Epic("Epic1", "Epic1_description");
    final int epicId = tasksManager.createEpic(epic);
    Subtask subtask1 = new Subtask("Subtask1", "Subtask1_description", TaskStatus.NEW, epicId);
    Subtask subtask2 = new Subtask("Subtask2", "Subtask2_description", TaskStatus.DONE, epicId);
    tasksManager.createSubTask(subtask1);
    tasksManager.createSubTask(subtask2);

    subtask1.setStatus(TaskStatus.IN_PROGRESS);
    subtask2.setStatus(TaskStatus.IN_PROGRESS);
    tasksManager.updateSubTask(subtask1);
    tasksManager.updateSubTask(subtask2);
    TaskStatus actual = tasksManager.getEpicById(epicId).getStatus();

    assertEquals(TaskStatus.IN_PROGRESS, actual,
            "Не меняется статус эпика на IN_PROGRESS при подзадачах со статусом IN_PROGRESS");
  }

  @Test
  public void shouldEndTimeAndStartTimeIsTrue() {
    Epic epic = new Epic ("Epic1", "Epic1_desc");
    int epicId1 = tasksManager.createEpic(epic);

    Subtask subtask1 = new Subtask("Sub1", "Sub1_desc", TaskStatus.NEW,
       LocalDateTime.of(2022, 1, 1, 10, 0) , 30, epicId1);
    tasksManager.createSubTask(subtask1);

    Subtask subtask2 = new Subtask("Sub1", "Sub1_desc", TaskStatus.NEW,
        LocalDateTime.of(2022, 1, 1, 12, 0), 30, epicId1);
    tasksManager.createSubTask(subtask2);


    LocalDateTime expectedStartTime = LocalDateTime.of(2022, 1, 1, 10, 0);
    LocalDateTime expectedEndTime = LocalDateTime.of(2022, 1, 1, 12, 30);

    assertEquals(expectedStartTime, epic.getStartTime(), "Не правильно рассчитывается время начала эпика");
    assertEquals(expectedEndTime, epic.getEndTime(), "Не правильно рассчитывается время очкончания эпика");
  }
}
