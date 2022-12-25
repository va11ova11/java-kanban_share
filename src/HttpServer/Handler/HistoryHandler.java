package HttpServer.Handler;

import HttpServer.Util.ResponseWriter;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import services.manager.FileBackedTasksManager;

public class HistoryHandler {

  private final Gson gson;
  private final FileBackedTasksManager manager;
  private final ResponseWriter responseWriter;

  public HistoryHandler (Gson gson, FileBackedTasksManager manager, ResponseWriter responseWriter) {
    this.gson = gson;
    this.manager = manager;
    this.responseWriter = responseWriter;
  }


  public void getPrioritizedTask(HttpExchange exchange) throws IOException {
    if(manager.getPrioritizedTasks().isEmpty()) {
      String response = "Список задач по приоритету пуст";
      responseWriter.writeResponse(exchange, response, 404);
    }
    responseWriter.writeResponse(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
  }

  public void getHistory(HttpExchange exchange) throws IOException {
    if(manager.getHistory().isEmpty()) {
      String response = "История просмотренных задач пуста";
      responseWriter.writeResponse(exchange, response, 404);
    }
    responseWriter.writeResponse(exchange, gson.toJson(manager.getHistory()), 200);
  }
}
