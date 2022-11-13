package services.manager;
import exception.ManagerSaveException;
import models.business.Epic;
import models.business.SubTask;
import models.business.Task;

import java.util.HashMap;
import java.util.List;

public interface TasksManager {
    int createTask(Task task) throws ManagerSaveException;
    int createEpic(Epic epic) throws ManagerSaveException;
    int createSubTask(SubTask subTask) throws ManagerSaveException;
    Task getTaskById(int taskId) throws ManagerSaveException;
    HashMap<Integer, Task> getTasks();
    HashMap<Integer, Epic> getEpics();
    HashMap<Integer, SubTask> getSubTasks();
    void deleteTaskById(int taskId) throws ManagerSaveException;
    void updateTask(Task task);
    void deleteAllTask();
    void deleteAllEpic();
    void deleteAllSubTask();
    void printAllTask();
    Epic getEpicById(int epicId) throws ManagerSaveException;
    SubTask getSubTaskById(int subTaskId) throws ManagerSaveException;
    List<Task> getHistory();
    void printHistory();
    void updateSubTask(SubTask subTask);
    void checkEpicStatus(SubTask subTask);
    void updateEpicStatus(int newCount, int doneCount, Epic epic);
}
