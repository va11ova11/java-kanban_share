package services.manager;

import comparators.TaskStartTimeComparator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;
import services.manager.history.HistoryManager;
import services.manager.time.EpicUpdater;


public class InMemoryTasksManager implements TasksManager {

  private int id;
  private final HashMap<Integer, Task> tasks;
  private final HashMap<Integer, Subtask> subtasks;
  private final HashMap<Integer, Epic> epics;
  private final HistoryManager historyManager;
  private final Set<Task> prioritizedTask;
  private final EpicUpdater epicUpdater;

  public InMemoryTasksManager() {
    tasks = new HashMap<>();
    subtasks = new HashMap<>();
    epics = new HashMap<>();
    historyManager = Managers.getDefaultHistory();
    TaskStartTimeComparator comparator = new TaskStartTimeComparator();
    prioritizedTask = new TreeSet<>(comparator);
    epicUpdater = new EpicUpdater();
    id = 0;
  }

  @Override
  public Set<Task> getPrioritizedTasks() {
    return prioritizedTask;
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
    if (task.getStartTime() == null) {
      prioritizedTask.add(task);
      return taskId;
    } else {
      checkTheTaskCompletionTime(task);
      prioritizedTask.add(task);
      return taskId;
    }
  }

  private void checkTheTaskCompletionTime(Task task) {
    if(prioritizedTask.size() == 0) {
      return;
    }
    LocalDateTime startTime = task.getStartTime();
    for (Task t : prioritizedTask) {
      if(t.getStartTime() == null) continue;
      if (t.getStartTime().equals(startTime)) {
        throw new RuntimeException("Задача на это время уже существует");
      }
    }
  }

  @Override
  public int createEpic(Epic epic) {
    final int epicId = ++id;
    epic.setId(epicId);
    epic.setStatus(TaskStatus.NEW);
    epics.put(epicId, epic);
    return epicId;
  }

  @Override
  public int createSubTask(Subtask subtask) {
    if (epics.containsKey(subtask.getEpicId())) {
      final int subtaskId = ++id;
      subtask.setId(subtaskId);
      subtasks.put(subtaskId, subtask);

      final Epic updateEpic = epics.get(subtask.getEpicId());
      updateEpic.addSubTaskInEpicList(subtaskId);
      epicUpdater.checkEpicStatus(updateEpic, subtasks);
      //Если время не указано
      if (subtask.getStartTime() == null) {
        prioritizedTask.add(subtask);
        return subtaskId;
      }
      Epic newEpic = epicUpdater.updateEpicOnSubtaskOperation(subtask, updateEpic, subtasks);
      updateEpic(newEpic);
      checkTheTaskCompletionTime(subtask);
      prioritizedTask.add(subtask);
      return subtaskId;
    }
    return 0;
  }

  @Override
  public Task getTaskById(int id) {
    if (tasks.containsKey(id)) {
      historyManager.addTaskInHistory(tasks.get(id));
      return tasks.get(id);
    }
    //to do
    return null;
  }

  @Override
  public Epic getEpicById(int epicId) {
    if (epics.containsKey(epicId)) {
      historyManager.addTaskInHistory(epics.get(epicId));
      return epics.get(epicId);
    }
    //to do
    return null;
  }

  @Override
  public Subtask getSubTaskById(int subTaskId) {
    if (subtasks.containsKey(subTaskId)) {
      historyManager.addTaskInHistory(subtasks.get(subTaskId));
      return subtasks.get(subTaskId);
    }
    //to do
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
      Epic updateEpic = epics.get(subtasks.get(id).getEpicId());
      final Subtask subTask = subtasks.get(id);
      subtasks.remove(id);
      epics.get(subTask.getEpicId()).deleteSubTaskInEpic(id);
      Epic newEpic = epicUpdater.updateEpicOnSubtaskOperation(subTask, updateEpic, subtasks);
      updateEpic(newEpic);
    } else if (epics.containsKey(id)) {
      for (int subTasksId : epics.get(id).getSubTasksId()) {
        subtasks.remove(subTasksId);
      }
      epics.remove(id);
    } else {
      throw new NullPointerException("Передан не верный идентификатор");
    }
  }

  @Override
  public void updateTask(Task task) {
    if (tasks.containsKey(task.getId())) {
      tasks.put(task.getId(), task);
    } else {
      throw new NullPointerException("Обновляемая таска ещё не создана");
    }
  }

  @Override
  public void updateSubTask(Subtask subTask) {
    if (subtasks.containsKey(subTask.getId())) {
      Epic updateEpic = epics.get(subTask.getEpicId());
      subtasks.put(subTask.getId(), subTask);
      Epic newEpic = epicUpdater.updateEpicOnSubtaskOperation(subTask, updateEpic, subtasks);
      updateEpic(newEpic);
    } else {
      throw new NullPointerException("Обновляемая сабтаска ещё не создана");
    }
  }

  @Override
  public void updateEpic(Epic epic) {
    if (epics.containsKey(epic.getId())) {
      epics.put(epic.getId(), epic);
    } else {
      throw new NullPointerException("Обновляемый эпик ещё не создан");
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

  public void addSubtaskInMap(Subtask subTask) {
    Epic updateEpic = epics.get(subTask.getEpicId());
    updateEpic.addSubTaskInEpicList(subTask.getId());
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
    if (epics.containsKey(epic.getId())) {
      epicUpdater.resetEpic(epic);
      List<Integer> subtasksInEpic = epic.getSubTasksId();
      if (subtasksInEpic != null) {
        for (int id : subtasksInEpic) {
          subtasks.remove(id);
        }
      }
    } else {
      throw new NullPointerException("Такой сабтаски не существует");
    }
  }

  @Override
  public List<Subtask> getSubTasksInEpic(Epic epic) {
    if (epics.containsKey(epic.getId())) {
      final List<Integer> subtasksId = epic.getSubTasksId();
      final List<Subtask> subtasksInEpic = new ArrayList<>();
      if (subtasksId != null) {
        for (int subTaskId : subtasksId) {
          subtasksInEpic.add(subtasks.get(subTaskId));
        }
      }
      return subtasksInEpic;
    } else {
      throw new NullPointerException("Такого эпика не существует");
    }
  }
}