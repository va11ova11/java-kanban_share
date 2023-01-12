package httpServerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import funcTests.TaskManagerTest;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;
import kvServer.KVServer;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.manager.HttpTaskManager;
import services.manager.TasksManager;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

  private static KVServer kvServer;

  @BeforeAll
  public static void beforeAll() throws IOException {
    kvServer = Managers.getDefaultKVServer();
    kvServer.start();
  }

  @AfterAll
  public static void afterEach() {
    kvServer.stop();
  }

  public HttpTaskManagerTest() throws IOException, InterruptedException {
    super(new HttpTaskManager(new URL("http://localhost:8078"), false));
  }

  private final HttpTaskManager httpTaskManager = super.getTasksManager();

  private void createEpics() {
    Epic epic1 = new Epic("Epic1", "Epic1_desc");
    httpTaskManager.createEpic(epic1);
    Epic epic2 = new Epic("Epic1", "Epic1_desc");
    httpTaskManager.createEpic(epic2);
  }

  private void createSubtasks() {
    Epic epic = new Epic("Epic2", "Epic1_desc");
    int epicId = httpTaskManager.createEpic(epic);
    Subtask subtask1 = new Subtask("Subtask", "Subtask_description",
        TaskStatus.IN_PROGRESS,
        LocalDateTime.of(2023, 3, 14, 6, 0), 60, epicId);
    Subtask subtask2 = new Subtask("Subtask", "Subtask_description",
        TaskStatus.IN_PROGRESS,
        LocalDateTime.of(2022, 12, 5, 6, 0), 60, epicId);
    httpTaskManager.createSubTask(subtask1);
    httpTaskManager.createSubTask(subtask2);
  }

  private void addTaskInHistory() {
    Task task1 = new Task("First_Task", "FirstTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 10, 14, 10, 0), 60);
    Task task2 = new Task("SameTimeTask", "FirstTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 9, 2, 10, 0), 60);
    int taskId1 = httpTaskManager.createTask(task1);
    int taskId2 = httpTaskManager.createTask(task2);

    httpTaskManager.getTaskById(taskId1);
    httpTaskManager.getTaskById(taskId2);
  }


  private void createTasks() {
    Task task1 = new Task("First_Task", "FirstTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 10, 0), 60);
    Task task2 = new Task("SameTimeTask", "FirstTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 15, 8, 0), 60);
    httpTaskManager.createTask(task1);
    httpTaskManager.createTask(task2);
  }


  @Test
  public void shouldLoadTasksFromKVServer() throws IOException, InterruptedException {
    createTasks();
    TasksManager loadManager = Managers.getDefault(new URL("http://localhost:8078"), true);
    Map<Integer, Task> tasks = loadManager.getTasks();
    assertEquals(2, tasks.size(), "Task-и не восстанавливаются");
  }

  @Test
  public void shouldLoadEpicsFromKVServer() throws IOException, InterruptedException {
    createEpics();
    TasksManager loadManager = Managers.getDefault(new URL("http://localhost:8078"), true);
    Map<Integer, Epic> epics = loadManager.getEpics();
    assertEquals(2, epics.size(), "Epic-и не восстанавливаются");
  }

  @Test
  public void shouldLoadSubtasksFromKVServer() throws IOException, InterruptedException {
    createSubtasks();
    TasksManager loadManager = Managers.getDefault(new URL("http://localhost:8078"), true);
    Map<Integer, Subtask> subtasks = loadManager.getSubTasks();
    assertEquals(2, subtasks.size(), "Subtask-и не восстанавливаются");
  }

  @Test
  public void shouldLoadHistoryFromKVServer() throws IOException, InterruptedException {
    addTaskInHistory();
    TasksManager loadManager = Managers.getDefault(new URL("http://localhost:8078"), true);
    Map<Integer, Subtask> subtasks = loadManager.getSubTasks();
    assertEquals(2, loadManager.getHistory().size(), "History не восстанавливается");
  }
}
