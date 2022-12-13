package services.manager;

import comparators.TaskStartTimeComparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.Util.EpicUpdater;
import models.business.Util.Managers;
import models.business.Util.Printer;
import models.business.enums.TaskStatus;
import services.manager.history.HistoryManager;


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
    if (task.getStartTime() != null) {
      checkTheTaskCompletionTime(task);
    }
    return taskId;
  }

  private void checkTheTaskCompletionTime(Task task) {
    if (prioritizedTask.size() == 0) {
      prioritizedTask.add(task);
    } else {
      for (Task t : prioritizedTask) {
        if (t.getStartTime().equals(task.getStartTime()) ||
            (task.getStartTime().isAfter(t.getStartTime()) && task.getEndTime().isBefore(t.getEndTime()))) {
          throw new RuntimeException("Задача на это время уже существует");
        }
      }
      prioritizedTask.add(task);
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
      //Если время указано
      if (subtask.getStartTime() != null) {
        checkTheTaskCompletionTime(subtask);
      }
      Epic newEpic = epicUpdater.updateEpicOnSubtaskOperation(subtask, updateEpic, subtasks);
      updateEpic(newEpic);
      return subtaskId;
    }
    throw new RuntimeException("Не существует эпика для подзадачи");
  }

  @Override
  public Task getTaskById(int id) {
    if (tasks.containsKey(id)) {
      historyManager.addTaskInHistory(tasks.get(id));
      return tasks.get(id);
    }
    throw new NullPointerException("Не существует таска по данному идентификатору");
  }

  @Override
  public Epic getEpicById(int epicId) {
    if (epics.containsKey(epicId)) {
      historyManager.addTaskInHistory(epics.get(epicId));
      return epics.get(epicId);
    }
    throw new NullPointerException("Не существует эпика по данному идентификатору");
  }

  @Override
  public Subtask getSubTaskById(int subTaskId) {
    if (subtasks.containsKey(subTaskId)) {
      historyManager.addTaskInHistory(subtasks.get(subTaskId));
      return subtasks.get(subTaskId);
    }
    throw new NullPointerException("Не существует сабтаски по данному идентификатору");
  }

  @Override
  public List<Task> getHistory() {
    return historyManager.getHistory();
  }

  @Override
  public void deleteTaskById(int id) {
    //Удалить задачу из истории если она там есть
    if (historyManager.getHistoryMap().containsKey(id)) {
      historyManager.removeInHistory(id);
    }
    if (tasks.containsKey(id)) {
      prioritizedTask.remove(tasks.get(id));
      tasks.remove(id);
    } else if (subtasks.containsKey(id)) {
      Epic updateEpic = epics.get(subtasks.get(id).getEpicId());
      final Subtask deleteSubtask = subtasks.get(id);
      prioritizedTask.remove(deleteSubtask);
      subtasks.remove(id);
      epics.get(deleteSubtask.getEpicId()).deleteSubTaskInEpic(id);
      Epic newEpic = epicUpdater.updateEpicOnSubtaskOperation(deleteSubtask, updateEpic, subtasks);
      updateEpic(newEpic);
    } else if (epics.containsKey(id)) {
      for (int subTasksId : epics.get(id).getSubTasksId()) {
        prioritizedTask.remove(subtasks.get(subTasksId));
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
      prioritizedTask.removeIf(t -> t.getId() == task.getId());
      checkTheTaskCompletionTime(task);
      tasks.put(task.getId(), task);
    } else {
      throw new NullPointerException("Обновляемая таска ещё не создана");
    }
  }

  @Override
  public void updateSubTask(Subtask subTask) {
    if (subtasks.containsKey(subTask.getId())) {
      prioritizedTask.removeIf(t -> t.getId() == subTask.getId());
      checkTheTaskCompletionTime(subTask);
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
    prioritizedTask.removeIf(tasks::containsValue);
    tasks.clear();
  }

  @Override
  public void deleteAllEpic() {
    prioritizedTask.removeIf(subtasks::containsValue);
    epics.clear();
    subtasks.clear();
  }

  @Override
  public void deleteAllSubTasksInEpic(Epic epic) {
    if (epics.containsKey(epic.getId())) {
      List<Integer> subtasksInEpic = epic.getSubTasksId();
      prioritizedTask.removeIf(subtasks::containsValue);
      if (subtasksInEpic != null) {
        for (int id : subtasksInEpic) {
          subtasks.remove(id);
        }
      }
      epic.reset();
    } else {
      throw new NullPointerException("Такой сабтаски не существует");
    }
  }

  @Override
  public List<Subtask> getSubtasksInEpic(Epic epic) {
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