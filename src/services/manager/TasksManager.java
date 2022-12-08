package services.manager;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;

import java.util.HashMap;
import java.util.List;
import models.business.enums.TaskStatus;
import services.manager.history.HistoryManager;

public interface TasksManager {
    HistoryManager getHistoryManager();
    int createTask(Task task);
    int createEpic(Epic epic);
    int createSubTask(Subtask subTask);
    Task getTaskById(int taskId);
    HashMap<Integer, Task> getTasks();
    HashMap<Integer, Epic> getEpics();
    HashMap<Integer, Subtask> getSubTasks();
    List<Subtask> getSubTasksInEpic(Epic epic);
    void deleteTaskById(int taskId);
    void updateTask(Task task);
    void deleteAllTask();
    void deleteAllEpic();
    void deleteAllSubTasksInEpic(Epic epic);
    void printAllTask();
    Epic getEpicById(int epicId);
    Subtask getSubTaskById(int subTaskId);
    List<Task> getHistory();
    void printHistory();
    void updateSubTask(Subtask subTask);
   // void setSubtaskStatus(Subtask subTask, TaskStatus taskStatus);
}
