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
import models.business.enums.TaskStatus;
import services.manager.history.HistoryManager;


public class InMemoryTasksManager implements TasksManager {

  private int id;
  protected final HashMap<Integer, Task> tasks;
  protected final HashMap<Integer, Subtask> subtasks;
  protected final HashMap<Integer, Epic> epics;
  protected final HistoryManager historyManager;
  protected final Set<Task> prioritizedTask;
  private final EpicUpdater epicUpdater;


  public InMemoryTasksManager() {
    historyManager = Managers.getDefaultHistory();
    tasks = new HashMap<>();
    subtasks = new HashMap<>();
    epics = new HashMap<>();
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
  public int createTask(Task task) {
    final int taskId = ++id;
    task.setId(taskId);
    tasks.put(taskId, task);
    checkTheTaskCompletionTime(task);
    return taskId;
  }

  public void checkTheTaskCompletionTime(Task task) {
    if (prioritizedTask.size() == 0 || task.getStartTime() == null) {
      prioritizedTask.add(task);
    } else {
      for (Task t : prioritizedTask) {
        boolean equalsStartTime = t.getStartTime().equals(task.getStartTime());
        boolean notCorrectEndTime = task.getEndTime().isAfter(t.getStartTime()) && task.getEndTime().isBefore(t.getEndTime());
        boolean notCorrectStartTime = task.getStartTime().isAfter(t.getStartTime()) && task.getStartTime().isBefore(t.getEndTime());
        if ( equalsStartTime || notCorrectEndTime || notCorrectStartTime ){
          throw new RuntimeException("???????????? ???? ?????? ?????????? ?????? ????????????????????");
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
      checkTheTaskCompletionTime(subtask);
      Epic newEpic = epicUpdater.updateEpicOnSubtaskOperation(subtask, updateEpic, subtasks);
      updateEpic(newEpic);
      return subtaskId;
    }
    throw new RuntimeException("???? ???????????????????? ?????????? ?????? ??????????????????");
  }

  @Override
  public Task getTaskById(int id) {
    if (tasks.containsKey(id)) {
      historyManager.add(tasks.get(id));
      return tasks.get(id);
    }
    throw new NullPointerException("???? ???????????????????? ?????????? ???? ?????????????? ????????????????????????????");
  }

  @Override
  public Epic getEpicById(int epicId) {
    if (epics.containsKey(epicId)) {
      historyManager.add(epics.get(epicId));
      return epics.get(epicId);
    }
    throw new NullPointerException("???? ???????????????????? ?????????? ???? ?????????????? ????????????????????????????");
  }

  @Override
  public Subtask getSubTaskById(int subTaskId) {
    if (subtasks.containsKey(subTaskId)) {
      historyManager.add(subtasks.get(subTaskId));
      return subtasks.get(subTaskId);
    }
    throw new NullPointerException("???? ???????????????????? ???????????????? ???? ?????????????? ????????????????????????????");
  }

  @Override
  public List<Task> getHistory() {
    return historyManager.getHistory();
  }

  @Override
  public void deleteTaskById(int id) {
    //?????????????? ???????????? ???? ?????????????? ???????? ?????? ?????? ????????
    if (historyManager.getHistoryMap().containsKey(id)) {
      historyManager.remove(id);
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
      throw new NullPointerException("?????????????? ???? ???????????? ??????????????????????????");
    }
  }

  @Override
  public void updateTask(Task task) {
    if (tasks.containsKey(task.getId())) {
      prioritizedTask.removeIf(t -> t.getId() == task.getId());
      checkTheTaskCompletionTime(task);
      tasks.put(task.getId(), task);
    } else {
      throw new NullPointerException("?????????????????????? ?????????? ?????? ???? ??????????????");
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
      throw new NullPointerException("?????????????????????? ???????????????? ?????? ???? ??????????????");
    }
  }

  @Override
  public void updateEpic(Epic epic) {
    if (epics.containsKey(epic.getId())) {
      epics.put(epic.getId(), epic);
    } else {
      throw new NullPointerException("?????????????????????? ???????? ?????? ???? ????????????");
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
    checkTheTaskCompletionTime(task);
  }

  public void addEpicInMap(Epic epic) {
    epics.put(epic.getId(), epic);
  }

  public void addSubtaskInMap(Subtask subTask) {
    Epic updateEpic = epics.get(subTask.getEpicId());
    updateEpic.addSubTaskInEpicList(subTask.getId());
    checkTheTaskCompletionTime(subTask);
    subtasks.put(subTask.getId(), subTask);
    epicUpdater.updateEpicOnSubtaskOperation(subTask, updateEpic, subtasks);
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
      throw new NullPointerException("???????????? ?????????? ???? ????????????????????");
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
      throw new NullPointerException("???????????? ?????????? ???? ????????????????????");
    }
  }
}