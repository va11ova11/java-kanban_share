package tests;


import org.junit.jupiter.api.Test;
import services.manager.FileBackedTasksManager;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{

  public FileBackedTasksManagerTest() {
    super(new FileBackedTasksManager());
  }

  @Test
  public void shouldCreateEpic() {
    super.createEpic();
  }

  @Test
  public void shouldCreateTask() {
    super.createTask();
  }

  @Test
  public void shouldCreateSubTask() {
    super.createSubTask();
  }
}
