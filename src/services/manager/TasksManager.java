package services.manager;
import java.util.Set;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;

import java.util.HashMap;
import java.util.List;

public interface TasksManager {
    Set<Task> getPrioritizedTasks();
    int createTask(Task task);
    int createEpic(Epic epic);
    int createSubTask(Subtask subTask);
    Task getTaskById(int taskId);
    HashMap<Integer, Task> getTasks();
    HashMap<Integer, Epic> getEpics();
    HashMap<Integer, Subtask> getSubTasks();
    List<Subtask> getSubtasksInEpic(Epic epic);
    void deleteTaskById(int taskId);
    void updateTask(Task task);
    void deleteAllTask();
    void deleteAllEpic();
    void deleteAllSubTasksInEpic(Epic epic);
    Epic getEpicById(int epicId);
    Subtask getSubTaskById(int subTaskId);
    List<Task> getHistory();
    void updateSubTask(Subtask subTask);
    void updateEpic(Epic updateEpic);
}
