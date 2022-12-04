package services.manager;

import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;
import services.manager.history.HistoryManager;

import java.util.HashMap;
import java.util.List;


public class InMemoryTasksManager implements TasksManager {
    private int id;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager;

    public InMemoryTasksManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        id = 0;
    }

    public HistoryManager getHistoryManager () {
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
    public int createSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            final int subTaskId = ++id;
            subTask.setId(subTaskId);
            subtasks.put(subTaskId, subTask);

            Epic epic = epics.get(subTask.getEpicId());
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
    public Epic getEpicById(int epicId)  {
        if (epics.containsKey(epicId)) {
            historyManager.addTaskInHistory(epics.get(epicId));
            return epics.get(epicId);
        }
        return null;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId)  {
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
        if(historyManager.getHistoryMap().containsKey(id)) {
            historyManager.removeInHistory(id);
        }
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            SubTask subTask = subtasks.get(id);
            subtasks.remove(id);
            epics.get(subTask.getEpicId()).deleteSubTaskInEpic(id);
            checkEpicStatus(subTask);
        } else if (epics.containsKey(id)) {
            for (Integer subTasksId : epics.get(id).getSubTasksId()) {
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
    public void updateSubTask(SubTask subTask) {
        if (subtasks.containsKey(subTask.getId())) {
            subtasks.put(subTask.getId(), subTask);
            checkEpicStatus(subTask);
        }
    }


    @Override
    public void checkEpicStatus(SubTask subTask) {
        int newCount = 0;
        int doneCount = 0;
        Epic epic = epics.get(subTask.getEpicId());
        for (Integer subTaskIdInEpic : epic.getSubTasksId()) {
            if (subtasks.get(subTaskIdInEpic).getTaskStatus() == TaskStatus.NEW) {
                newCount++;
            } else if (subtasks.get(subTaskIdInEpic).getTaskStatus() == TaskStatus.DONE) {
                doneCount++;
            }
        }
        updateEpicStatus(newCount, doneCount, epic);
    }

    @Override
    public void updateEpicStatus(int newCount, int doneCount, Epic epic) {
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
            for (SubTask subTask : subtasks.values()) {
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
    public HashMap<Integer, SubTask> getSubTasks() {
        return new HashMap<>(subtasks);
    }
    public void addTaskInMap(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addEpicInMap(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void addSubTaskInMap(SubTask subTask) {
        subtasks.put(subTask.getId(), subTask);
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }
    public Epic getEpic(int id) {
        return epics.get(id);
    }
    public SubTask getSubTask(int id) {
        return subtasks.get(id);
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
        epic.deleteAllSubTasksInEpic();
        epic.setTaskStatus(TaskStatus.NEW);
        subtasks.clear();
    }

    public void setSubtaskStatus(SubTask subTask, TaskStatus taskStatus) {
        subTask.setTaskStatus(taskStatus);
        subtasks.put(subTask.getId(), subTask);
        checkEpicStatus(subTask);
    }
}