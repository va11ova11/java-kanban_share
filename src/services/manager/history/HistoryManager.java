package services.manager.history;

import java.util.ArrayList;
import models.business.Task;

import java.util.List;
import java.util.Map;

public interface HistoryManager {
    void addTaskInHistory(Task task);
    List<Task> getHistory();
    void removeInHistory(int id);
    Map<Integer, Node<Task>> getHistoryMap();
    String historyToString();

    static List<Integer> historyFromString(String value) {
        List<Integer> historyIds = new ArrayList<>();
        String[] ids = value.split(",");
        for (String id : ids) {
            historyIds.add(Integer.parseInt(id));
        }
        return historyIds;
    }
}
