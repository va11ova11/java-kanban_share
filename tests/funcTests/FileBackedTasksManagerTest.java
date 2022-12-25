package funcTests;

import exception.ManagerSaveException;
import java.io.File;
import java.util.List;
import models.business.Task;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import services.manager.FileBackedTasksManager;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

  private final FileBackedTasksManager fileBackedTasksManager = super.getTasksManager();

  public FileBackedTasksManagerTest() {
    super(new FileBackedTasksManager());
  }

  @Test
  public void shouldWhenDeleteTaskHistoryIsEmpty() {
    Task task = new Task("Task1", "Task1_desc", TaskStatus.NEW);
    int taskId = fileBackedTasksManager.createTask(task);
    fileBackedTasksManager.getTaskById(taskId);
    fileBackedTasksManager.deleteTaskById(taskId);

    List<Task> history = fileBackedTasksManager.getHistory();
    assertNull(history, "История задач не пуста");
  }

  @Test
  public void shouldHistoryNotEmpty() {
    Task task = new Task("Task1", "Task1_desc", TaskStatus.NEW);
    int taskId = fileBackedTasksManager.createTask(task);
    fileBackedTasksManager.getTaskById(taskId);

    List<Task> history = fileBackedTasksManager.getHistory();
    assertNotNull(history, "Задачи не добааляются в историю");
  }

  @Test
  public void shouldThrowExceptionWhileLoadFromFileAndFileIsEmpty() {

    ManagerSaveException ex = assertThrows(
            ManagerSaveException.class,
            () -> Managers.loadFromFile(new File("Empty.csv"))
    );

    assertEquals("Загружаемый файл пуст", ex.getMessage());
  }

  @Test
  public void shouldLoadFromFile() {
    FileBackedTasksManager f = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
    assertNotNull(f, "FileBackedTasksManager не загружается с файла");
  }
}
