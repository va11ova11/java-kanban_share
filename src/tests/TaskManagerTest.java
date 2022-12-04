package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.business.enums.TaskStatus;
import services.manager.TasksManager;


public abstract class TaskManagerTest<T extends TasksManager> {

  private final T tasksManager;

  public TaskManagerTest(T tasksManager) {
    this.tasksManager = tasksManager;
  }

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


  public void createEpic() {
    Epic epic = new Epic("NewEpic", "EpicNew_desc");
    final int epicId = tasksManager.createEpic(epic);
    final Epic savedEpic = tasksManager.getEpicById(epicId);

    assertEquals(epic.getTaskStatus(), TaskStatus.NEW,
        "Неправильно рассчитывается статус Эпика");
    assertEquals(epic, savedEpic, "Эпики не равны");
    assertNotNull(savedEpic, "Эпик не найден");

    final Map<Integer, Epic> epics = tasksManager.getEpics();

    assertEquals(1, epics.size(), "Неверное количество Эпиков");
    assertNotNull(epics, "Эпики не возвращаются");
    assertEquals(epic, epics.get(epic.getId()), "Эпики не совпадают");
  }

  public void createSubTask() {
    Epic epic = new Epic("NewEpic", "EpicNew_desc");
    final int epicId = tasksManager.createEpic(epic);
    SubTask subTask = new SubTask("NewSubTask", "NewSub_desc",
        TaskStatus.NEW, epicId);
    final int subTaskId = tasksManager.createSubTask(subTask);
    SubTask savedSubTask = tasksManager.getSubTaskById(subTaskId);

    assertEquals(subTask.getEpicId(), epicId, "У Сабтаски нет эпика");
    assertEquals(subTask, savedSubTask, "Сабтаски не равны");
    assertNotNull(subTask, "Сабтаск не найден");

    final Map<Integer, SubTask> subTasks = tasksManager.getSubTasks();

    assertEquals(1, subTasks.size(), "Неверное количество Сабтасков");
    assertNotNull(subTasks, "Сабтаски не возвращаются");
    assertEquals(subTask, subTasks.get(subTask.getId()), "Сабтаски не совпадают");
  }
}
