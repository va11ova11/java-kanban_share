package services.manager;

import java.util.ArrayList;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;
import services.manager.history.HistoryManager;
import java.util.HashMap;
import java.util.List;


public class InMemoryTasksManager implements TasksManager {

  private int id;
  private final HashMap<Integer, Task> tasks;
  private final HashMap<Integer, Subtask> subtasks;
  private final HashMap<Integer, Epic> epics;
  private final HistoryManager historyManager;

  public InMemoryTasksManager() {
    tasks = new HashMap<>();
    subtasks = new HashMap<>();
    epics = new HashMap<>();
    historyManager = Managers.getDefaultHistory();
    id = 0;
  }

  @Override
  public HistoryManager getHistoryManager() {
    return historyManager;
  }

  @Override
  public int createTask(Task task) {
    final int taskId = ++id;
    task.setId(taskId);
    tasks.put(taskId, task);
    return taskId;
  }

  @Override
  public int createEpic(Epic epic) {
    final int epicId = ++id;
    epic.setId(epicId);
    epic.setTaskStatus(TaskStatus.NEW);
    epics.put(epicId, epic);
    return epicId;
  }

  @Override
  public int createSubTask(Subtask subTask) {
    if (epics.containsKey(subTask.getEpicId())) {
      final int subTaskId = ++id;
      subTask.setId(subTaskId);
      subtasks.put(subTaskId, subTask);

      final Epic epic = epics.get(subTask.getEpicId());
      epic.addSubTaskInEpicList(subTaskId);
      checkEpicStatus(subTask);
      return subTaskId;
    }
    return 0;
  }

  @Override
  public Task getTaskById(int id) {
    if (tasks.containsKey(id)) {
      historyManager.addTaskInHistory(tasks.get(id));
      return tasks.get(id);
    }
    return null;
  }

  @Override
  public Epic getEpicById(int epicId) {
    if (epics.containsKey(epicId)) {
      historyManager.addTaskInHistory(epics.get(epicId));
      return epics.get(epicId);
    }
    return null;
  }

  @Override
  public Subtask getSubTaskById(int subTaskId) {
    if (subtasks.containsKey(subTaskId)) {
      historyManager.addTaskInHistory(subtasks.get(subTaskId));
      return subtasks.get(subTaskId);
    }
    return null;
  }

  @Override
  public List<Task> getHistory() {
    return historyManager.getHistory();
  }

  @Override
  public void printHistory() {
    if (getHistory() != null) {
      int taskNumber = 1;
      System.out.println("История просмотренных задач: ");
      for (Task task : getHistory()) {
        System.out.println(taskNumber++ + ". " + task);
      }
      //System.out.println();
    } else {
      System.out.println("История задач пуста.");
      //System.out.println();
    }
  }

  @Override
  public void deleteTaskById(int id) {
    //Удалить задачу из истории если она там есть
    if (historyManager.getHistoryMap().containsKey(id)) {
      historyManager.removeInHistory(id);
    }
    if (tasks.containsKey(id)) {
      tasks.remove(id);
    } else if (subtasks.containsKey(id)) {
      final Subtask subTask = subtasks.get(id);
      subtasks.remove(id);
      epics.get(subTask.getEpicId()).deleteSubTaskInEpic(id);
      checkEpicStatus(subTask);
    } else if (epics.containsKey(id)) {
      for (int subTasksId : epics.get(id).getSubTasksId()) {
        subtasks.remove(subTasksId);
      }
      epics.remove(id);
    }
  }

  @Override
  public void updateTask(Task task) {
    if (tasks.containsKey(task.getId())) {
      tasks.put(task.getId(), task);
    }
  }

  @Override
  public void updateSubTask(Subtask subTask) {
    if (subtasks.containsKey(subTask.getId())) {
      subtasks.put(subTask.getId(), subTask);
      checkEpicStatus(subTask);
    }
  }

  private void checkEpicStatus(Subtask subTask) {
    int newCount = 0;
    int doneCount = 0;
    final Epic epic = epics.get(subTask.getEpicId());
    for (int subTaskIdInEpic : epic.getSubTasksId()) {
      if (subtasks.get(subTaskIdInEpic).getTaskStatus() == TaskStatus.NEW) {
        newCount++;
      } else if (subtasks.get(subTaskIdInEpic).getTaskStatus() == TaskStatus.DONE) {
        doneCount++;
      }
    }
    updateEpicStatus(newCount, doneCount, epic);
  }

  private void updateEpicStatus(int newCount, int doneCount, Epic epic) {
    if (epic.getSubTasksId().isEmpty() || epic.getSubTasksSize() == newCount) {
      epic.setTaskStatus(TaskStatus.NEW);
      epics.put(epic.getId(), epic);
    } else if (epic.getSubTasksSize() == doneCount) {
      epic.setTaskStatus(TaskStatus.DONE);
      epics.put(epic.getId(), epic);
    } else {
      epic.setTaskStatus(TaskStatus.IN_PROGRESS);
      epics.put(epic.getId(), epic);
    }
  }


  @Override
  public void printAllTask() {
    System.out.println("Задачи:");
    int j = 1;
    for (Task task : tasks.values()) {
      System.out.println(j++ + ". " + task);
    }
    System.out.println();
    System.out.println("Эпики:");
    for (Epic epic : epics.values()) {
      int i = 0;
      System.out.println(epic);
      System.out.println("---> Подзадачи эпика");
      for (Subtask subTask : subtasks.values()) {
        if (subTask.getEpicId() == epic.getId()) {
          System.out.println(++i + ". " + subTask);
        }
      }
      System.out.println();
    }
  }

  @Override
  public HashMap<Integer, Task> getTasks() {
    return new HashMap<>(tasks);
  }

  @Override
  public HashMap<Integer, Epic> getEpics() {
    return new HashMap<>(epics);
  }

  @Override
  public HashMap<Integer, Subtask> getSubTasks() {
    return new HashMap<>(subtasks);
  }

  public void addTaskInMap(Task task) {
    tasks.put(task.getId(), task);
  }

  public void addEpicInMap(Epic epic) {
    epics.put(epic.getId(), epic);
  }

  public void addSubTaskInMap(Subtask subTask) {
    subtasks.put(subTask.getId(), subTask);
  }

  @Override
  public void deleteAllTask() {
    tasks.clear();
  }

  @Override
  public void deleteAllEpic() {
    epics.clear();
    subtasks.clear();
  }

  @Override
  public void deleteAllSubTasksInEpic(Epic epic) {
    epic.removeSubTasksIdInEpic();
    epic.setTaskStatus(TaskStatus.NEW);
    subtasks.clear();
  }

  @Override
  public List<Subtask> getSubTasksInEpic(Epic epic) {
    final List<Integer> subTasksId = epic.getSubTasksId();
    final List<Subtask> subtasksInEpic = new ArrayList<>();
    for (int subTaskId : subTasksId) {
      subtasksInEpic.add(subtasks.get(subTaskId));
    }
    return subtasksInEpic;
  }

//  @Override
//  public void setSubtaskStatus(Subtask subTask, TaskStatus taskStatus) {
//    subTask.setTaskStatus(taskStatus);
//    subtasks.put(subTask.getId(), subTask);
//    checkEpicStatus(subTask);
//  }
}