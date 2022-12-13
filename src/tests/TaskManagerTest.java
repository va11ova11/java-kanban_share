package tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Map;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import services.manager.TasksManager;


public abstract class TaskManagerTest<T extends TasksManager> {

  private final T tasksManager;

  public TaskManagerTest(T tasksManager) {
    this.tasksManager = tasksManager;
  }
  public T getTasksManager() {
    return tasksManager;
  }

  @Test
  public void createTask() {
    Task task = new Task("TaskNew", "TaskNew_desc", TaskStatus.NEW);
    final int taskId = tasksManager.createTask(task);
    final Task savedTask = tasksManager.getTaskById(taskId);

    assertEquals(task, savedTask, "Задачи не совпадают");
    assertNotNull(savedTask, "Задача не найдена");

    final Map<Integer, Task> tasks = tasksManager.getTasks();

    assertEquals(1, tasks.size(), "Неверное количество задач");
    assertNotNull(tasks, "Задачи не возвращаются");
    assertEquals(task, tasks.get(task.getId()), "Задачи не совпадают");
  }


  @Test
  public void createEpic() {
    Epic epic = new Epic("NewEpic", "EpicNew_desc");
    final int epicId = tasksManager.createEpic(epic);
    final Epic savedEpic = tasksManager.getEpicById(epicId);

    assertEquals(epic.getStatus(), TaskStatus.NEW,
        "Неправильно рассчитывается статус Эпика");
    assertEquals(epic, savedEpic, "Эпики не равны");
    assertNotNull(savedEpic, "Эпик не найден");

    final Map<Integer, Epic> epics = tasksManager.getEpics();

    assertEquals(1, epics.size(), "Неверное количество Эпиков");
    assertNotNull(epics, "Эпики не возвращаются");
    assertEquals(epic, epics.get(epic.getId()), "Эпики не совпадают");
  }

  @Test
  public void createSubTask() {
    Epic epic = new Epic("NewEpic", "EpicNew_desc");
    final int epicId = tasksManager.createEpic(epic);
    Subtask subTask = new Subtask("NewSubTask", "NewSub_desc",
        TaskStatus.NEW, epicId);
    final int subTaskId = tasksManager.createSubTask(subTask);
    Subtask savedSubtask = tasksManager.getSubTaskById(subTaskId);

    assertEquals(subTask.getEpicId(), epicId, "У Сабтаски нет эпика");
    assertEquals(subTask, savedSubtask, "Сабтаски не равны");
    assertNotNull(subTask, "Сабтаск не найден");

    Subtask notCorrectEpicIdSubTask = new Subtask("NewSubTask", "NewSub_desc",
        TaskStatus.NEW, 10);
    RuntimeException ex = assertThrows(
        RuntimeException.class,
        () -> tasksManager.createSubTask(notCorrectEpicIdSubTask)
    );
    assertEquals("Не существует эпика для подзадачи", ex.getMessage());


    final Map<Integer, Subtask> subTasks = tasksManager.getSubTasks();

    assertEquals(1, subTasks.size(), "Неверное количество Сабтасков");
    assertNotNull(subTasks, "Сабтаски не возвращаются");
    assertEquals(subTask, subTasks.get(subTask.getId()), "Сабтаски не совпадают");
  }

  @Test
  public void deleteTaskById() {
    Task task1 = new Task("Task1", "Task1_disc", TaskStatus.NEW);
    final int taskId1 = tasksManager.createTask(task1);
    Epic epic = new Epic("Task1", "Task1_desc");
    tasksManager.createEpic(epic);

    tasksManager.deleteTaskById(taskId1);
    assertEquals(0, tasksManager.getTasks().size());

    NullPointerException ex = assertThrows(
        NullPointerException.class,
        () -> tasksManager.deleteTaskById(5));
    assertEquals("Передан не верный идентификатор", ex.getMessage());
  }

  @Test
  public void shouldExceptionWhenCreatingTaskEqualStartTime() {
    Task taskHasTime = new Task("TaskTime", "Task has time", TaskStatus.NEW, "10.01.2022;09:00", 60);
    tasksManager.createTask(taskHasTime);

    Task taskHasTime2 = new Task("TaskTime", "Task has time", TaskStatus.NEW, "10.01.2022;09:00", 60);

    RuntimeException ex = assertThrows(
        RuntimeException.class,
        () -> tasksManager.createTask(taskHasTime2)
    );
    assertEquals("Задача на это время уже существует", ex.getMessage());
  }

  @Test
  public void shouldExceptionWhenCreatingTaskNotCorrectStartTime() {
    Task taskHasTime = new Task("TaskTime", "Task has time", TaskStatus.NEW, "10.01.2022;09:00", 60);
    tasksManager.createTask(taskHasTime);

    Task taskHasTime2 = new Task("TaskTime", "Task has time", TaskStatus.NEW, "10.01.2022;09:30", 60);

    RuntimeException ex = assertThrows(
        RuntimeException.class,
        () -> tasksManager.createTask(taskHasTime2)
    );
    assertEquals("Задача на это время уже существует", ex.getMessage());
  }

  @Test
  public void shouldExceptionWhenCreatingTaskNotCorrectEndTime() {
    Task taskHasTime = new Task("TaskTime", "Task has time", TaskStatus.NEW, "10.01.2022;09:00", 60);
    tasksManager.createTask(taskHasTime);

    Task taskHasTime2 = new Task("TaskTime", "Task has time", TaskStatus.NEW, "10.01.2022;08:00", 90);

    RuntimeException ex = assertThrows(
        RuntimeException.class,
        () -> tasksManager.createTask(taskHasTime2)
    );
    assertEquals("Задача на это время уже существует", ex.getMessage());
  }
}
