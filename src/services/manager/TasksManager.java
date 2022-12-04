package services.manager;
import exception.ManagerSaveException;
import models.business.Epic;
import models.business.SubTask;
import models.business.Task;

import java.util.HashMap;
import java.util.List;
import models.business.enums.TaskStatus;

public interface TasksManager {
    int createTask(Task task);
    int createEpic(Epic epic);
    int createSubTask(SubTask subTask);
    Task getTaskById(int taskId);
    HashMap<Integer, Task> getTasks();
    HashMap<Integer, Epic> getEpics();
    HashMap<Integer, SubTask> getSubTasks();
    void deleteTaskById(int taskId) throws ManagerSaveException;
    void updateTask(Task task);
    void deleteAllTask();
    void deleteAllEpic();
    void deleteAllSubTasksInEpic(Epic epic);
    void printAllTask();
    Epic getEpicById(int epicId) throws ManagerSaveException;
    SubTask getSubTaskById(int subTaskId) throws ManagerSaveException;
    List<Task> getHistory();
    void printHistory();
    void updateSubTask(SubTask subTask);
    void checkEpicStatus(SubTask subTask);
    void updateEpicStatus(int newCount, int doneCount, Epic epic);
    void setSubtaskStatus(SubTask subTask, TaskStatus taskStatus);
}
