package services.manager.history;

import models.business.Task;

import java.util.List;
import java.util.Map;

public interface HistoryManager {
    void addTaskInHistory(Task task);
    List<Task> getHistory();
    void removeInHistory(int id);
    Map<Integer, Node<Task>> getHistoryMap();
}
