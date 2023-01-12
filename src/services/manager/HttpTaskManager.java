package services.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import kvServer.KVTaskClient;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;


public class HttpTaskManager extends FileBackedTasksManager {

  private final KVTaskClient client;
  private final Gson gson;
  private final Type taskType = new TypeToken<ArrayList<Task>>() {
  }.getType();
  private final Type epicType = new TypeToken<ArrayList<Epic>>() {
  }.getType();
  private final Type subtaskType = new TypeToken<ArrayList<Subtask>>() {
  }.getType();
  private final Type historyType = new TypeToken<ArrayList<String>>() {
  }.getType();


  public HttpTaskManager(URL url, boolean isLoad) throws IOException, InterruptedException {
    this.client = new KVTaskClient(url);
    this.gson = new Gson();
    if (isLoad) {
      load();
    }
  }


  @Override
  public void save() {
    try {
      String tasksJson = gson.toJson(new ArrayList<>(tasks.values()), taskType);
      client.put("tasks", tasksJson);

      String epicsJson = gson.toJson(new ArrayList<>(epics.values()), epicType);
      client.put("epics", epicsJson);

      String subtasksJson = gson.toJson(new ArrayList<>(subtasks.values()), subtaskType);
      client.put("subtasks", subtasksJson);

      if (historyManager.getHistoryToString() != null) {
        String historyJson = gson.toJson(historyManager.getHistoryToString(), historyType);
        client.put("history", historyJson);
      }
    } catch (IOException | InterruptedException e) {
      e.getMessage();
    }
  }


  public void load() throws IOException, InterruptedException {
    String tasksJson = client.load("tasks");
    String epicsJson = client.load("epics");
    String subtasksJson = client.load("subtasks");
    String historyJson = client.load("history");

    if (!tasksJson.isEmpty()) {
      List<Task> tasksList = gson.fromJson(tasksJson, taskType);
      for (Task task : tasksList) {
        tasks.put(task.getId(), task);
      }
    }

    if (!epicsJson.isEmpty()) {
      List<Epic> epicsList = gson.fromJson(epicsJson, epicType);
      for (Epic epic : epicsList) {
        epics.put(epic.getId(), epic);
      }
    }

    if (!subtasksJson.isEmpty()) {
      List<Subtask> subtasksList = gson.fromJson(subtasksJson, subtaskType);
      for (Subtask subtask : subtasksList) {
        subtasks.put(subtask.getId(), subtask);
      }
    }

    if (!historyJson.isEmpty()) {
      List<String> historyIds = gson.fromJson(historyJson, historyType);

      for (String stringId : historyIds) {
        int id = Integer.parseInt(stringId);
        if (tasks.containsKey(id)) {
          historyManager.add(tasks.get(id));
        }

        if (subtasks.containsKey(id)) {
          historyManager.add(subtasks.get(id));
        }

        if (epics.containsKey(id)) {
          historyManager.add(epics.get(id));
        }
      }
    }
  }
}
