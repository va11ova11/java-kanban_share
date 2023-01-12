package services.manager.history;

import models.business.Task;

import java.util.List;
import java.util.Map;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
    void remove(int id);
    Map<Integer, Node<Task>> getHistoryMap();
    List<String> getHistoryToString();
}
