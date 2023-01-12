package httpServer.Handler;

import httpServer.Util.ResponseWriter;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import services.manager.TasksManager;

public class HistoryHandler {

  private final Gson gson;
  private final TasksManager manager;

  public HistoryHandler (Gson gson, TasksManager manager) {
    this.gson = gson;
    this.manager = manager;
  }


  public void getPrioritizedTask(HttpExchange exchange) throws IOException {
    if(manager.getPrioritizedTasks() == null | manager.getPrioritizedTasks().isEmpty()) {
      String response = "Список задач по приоритету пуст";
      ResponseWriter.writeResponse(exchange, response, 404);
    }
    ResponseWriter.writeResponse(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
  }

  public void getHistory(HttpExchange exchange) throws IOException {
    if(manager.getHistory() == null) {
      String response = "История просмотренных задач пуста";
      ResponseWriter.writeResponse(exchange, response, 404);
    }
    ResponseWriter.writeResponse(exchange, gson.toJson(manager.getHistory()), 200);
  }
}
