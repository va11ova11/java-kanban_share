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
    int createSubTask(Subtask subTask) throws RuntimeException;
    Task getTaskById(int taskId) throws NullPointerException;
    HashMap<Integer, Task> getTasks();
    HashMap<Integer, Epic> getEpics();
    HashMap<Integer, Subtask> getSubTasks();
    List<Subtask> getSubtasksInEpic(Epic epic) throws NullPointerException;
    void deleteTaskById(int taskId) throws NullPointerException;
    void updateTask(Task task) throws NullPointerException;
    void deleteAllTask();
    void deleteAllEpic();
    void deleteAllSubTasksInEpic(Epic epic) throws NullPointerException;
    Epic getEpicById(int epicId) throws NullPointerException;
    Subtask getSubTaskById(int subTaskId) throws NullPointerException;
    List<Task> getHistory();
    void updateSubTask(Subtask subTask) throws NullPointerException;
    void updateEpic(Epic updateEpic) throws NullPointerException;
}
