package tests;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import services.manager.InMemoryTasksManager;
import services.manager.TasksManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;


public class InMemoryTasksManagerTests extends TaskManagerTest<InMemoryTasksManager> {

  public InMemoryTasksManagerTests() {
    super(new InMemoryTasksManager());
  }

  private final TasksManager tasksManager = super.getTasksManager();

  @Test
  public void shouldUpdateTask() {
    Task task = new Task("Task1", "Task1_desc");
    final int taskId = tasksManager.createTask(task);
    task.setName("Task1_update");
    tasksManager.updateTask(task);
    Task updateTask = tasksManager.getTaskById(taskId);

    assertEquals(updateTask.getName(), "Task1_update", "Таска не обновляется");
  }


  @Test
  public void updateSubTask() {
    Epic epic = new Epic("Epic1", "Epic1_desc");
    final int epicId1 = tasksManager.createEpic(epic);
    Subtask subTask = new Subtask("SubTask1", "SubTask1_desc", TaskStatus.NEW, epicId1);
    final int subTaskId = tasksManager.createSubTask(subTask);

    subTask.setStatus(TaskStatus.DONE);
    tasksManager.updateSubTask(subTask);
    Subtask updateSubtask = tasksManager.getSubTaskById(subTaskId);

    assertEquals(epic.getStatus(), TaskStatus.DONE, "Не изменяется статус эпика приудалении Сабтаски");
    assertEquals(updateSubtask.getStatus(), TaskStatus.DONE, "Не обновляется Сабтаска");
  }


  @Test
  public void deleteAllTask() {
    Task task1 = new Task("Task1", "Task1_desc1");
    Task task2 = new Task("Task2", "Task1_des2");
    tasksManager.createTask(task1);
    tasksManager.createTask(task2);

    tasksManager.deleteAllTask();
    Map<Integer, Task> tasks = tasksManager.getTasks();

    assertEquals(0, tasks.size(), "Задачи не удаляются");
  }


  @Test
  public void deleteALlEpic() {
    Epic epic1 = new Epic("Epic1", "Epic1_desc");
    Epic epic2 = new Epic("Epic2", "Epic2_desc");
    final int epicId1 = tasksManager.createEpic(epic1);
    final int epicId2 = tasksManager.createEpic(epic2);

    Subtask subtask1 = new Subtask("Subtask1", "Sub1_desc", TaskStatus.NEW, epicId1);
    Subtask subtask2 = new Subtask("Subtask2", "Sub2_desc", TaskStatus.NEW, epicId2);
    tasksManager.createSubTask(subtask1);
    tasksManager.createSubTask(subtask2);

    tasksManager.deleteAllEpic();
    Map<Integer, Epic> epics = tasksManager.getEpics();
    Map<Integer, Subtask> subTasks = tasksManager.getSubTasks();

    assertEquals(0, epics.size(), "Не удаляются все эпики");
    assertEquals(0, subTasks.size(), "Не удаляются Сабтаски при удалении всех Эпиков");
  }


  @Test
  public void deleteAllSubTasksInEpic() {
    Epic epic = new Epic("Epic1", "Epic1_desc");
    final int epicId1 = tasksManager.createEpic(epic);
    Subtask subtask1 = new Subtask("Sub1", "Sub1_desc", TaskStatus.NEW, epicId1);
    Subtask subtask2 = new Subtask("Sub2", "Sub2_desc", TaskStatus.IN_PROGRESS, epicId1);
    tasksManager.createSubTask(subtask1);
    tasksManager.createSubTask(subtask2);

    tasksManager.deleteAllSubTasksInEpic(epic);
    List<Subtask> subTasksInEpic = tasksManager.getSubTasksInEpic(epic);

    assertEquals(0, subTasksInEpic.size(), "Не удаляются Сабтаски");
    assertEquals(TaskStatus.NEW, epic.getStatus(), "Не меняется статус Эпика после удаления Сабтасок");
  }

  @Test
  public void getHistory() {
    Task task1 = new Task("Task1", "Task1_desc1");
    Task task2 = new Task("Task2", "Task1_des2");
    final int taskId1 = tasksManager.createTask(task1);
    final int taskId2 = tasksManager.createTask(task2);
    List<Task> expectedHistory = new ArrayList<>(List.of(task1, task2));


    tasksManager.getTaskById(taskId1);
    tasksManager.getTaskById(taskId2);
    List<Task> actualHistory = new ArrayList<>(tasksManager.getHistory());

    assertNotNull(actualHistory);
    assertArrayEquals(new List[]{expectedHistory}, new List[]{actualHistory}, "Не правильная история");
  }

  @Test
  public void shouldCheckAndUpdateEpicStatusWhenAddNewSubTask() {
    Epic epic = new Epic("Epic1", "Epic1_desc");
    final int epicId1 = tasksManager.createEpic(epic);
    Subtask subTask = new Subtask("SubTask1", "Sub1_desc", TaskStatus.DONE, epicId1);
    tasksManager.createSubTask(subTask);

    assertEquals(epic.getStatus(), TaskStatus.DONE);
  }
}
