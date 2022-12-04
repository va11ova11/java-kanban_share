package tests;

import org.junit.jupiter.api.Test;
import services.manager.InMemoryTasksManager;


public class InMemoryTasksManagerTests extends TaskManagerTest<InMemoryTasksManager> {

  public InMemoryTasksManagerTests() {
    super(new InMemoryTasksManager());
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
