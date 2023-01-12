package httpServer.Handler;

import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;
import httpServer.Util.ResponseWriter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import models.business.Task;
import services.manager.TasksManager;

public class TaskHandler {

  private final TasksManager manager;
  private final Gson gson;

  public TaskHandler (TasksManager manager, Gson gson) {
    this.manager = manager;
    this.gson = gson;
  }
  public void deleteTaskById(HttpExchange exchange) throws IOException {
    String query = exchange.getRequestURI().getQuery();
    int id;
    try{
      id = Integer.parseInt(query.split("=")[1]);
    } catch (NumberFormatException ex) {
      String response = "Некорректный id";
      ResponseWriter.writeResponse(exchange, response, 400);
      return;
    }
    try {
      manager.deleteTaskById(id);
    } catch (NullPointerException ex) {
      ResponseWriter.writeResponse(exchange, ex.getMessage(), 404);
      return;
    }

    String response = "Задача с идентификатором" + id + "удалена";
    ResponseWriter.writeResponse(exchange, response, 200);
  }

  public void deleteTasks(HttpExchange exchange) throws IOException {
    manager.deleteAllTask();
    String response = "Все задачи удалены";
    ResponseWriter.writeResponse(exchange, response, 200);
  }

  public void getTasks(HttpExchange exchange) throws IOException {
    ResponseWriter.writeResponse(exchange, gson.toJson(manager.getTasks()), 200);
  }


  public void postTask(HttpExchange exchange) throws IOException {
    Headers headers = exchange.getRequestHeaders();
    InputStream is = exchange.getRequestBody();

    if (is == null || !headers.get("Content-Type").contains("application/json")) {
      String response = "Формат должен данных быть - JSON";
      ResponseWriter.writeResponse(exchange, response, 400);
      return;
    }
    try {
      String requestString = new String(is.readAllBytes(), DEFAULT_CHARSET);
      Task task = gson.fromJson(requestString, Task.class);
      manager.createTask(task);
      String response = "Задача " + task.getName() + " создана";
      ResponseWriter.writeResponse(exchange, response, 200);
    } catch (JsonSyntaxException ex) {
      String response = "Не верный формат JSON";
      ResponseWriter.writeResponse(exchange, response, 400);
    } catch (RuntimeException ex) {
      ResponseWriter.writeResponse(exchange, ex.getMessage(), 404);
    }
  }

  public void getTaskById(HttpExchange exchange) throws IOException {
    String query = exchange.getRequestURI().getQuery();
    int id;
    try {
      id = Integer.parseInt(query.split("=")[1]);
      Task task = manager.getTaskById(id);
      ResponseWriter.writeResponse(exchange, gson.toJson(task), 200);
    }
    catch (NumberFormatException ex) {
      String response = "Некорректный id";
      ResponseWriter.writeResponse(exchange, response, 400);
    } catch (NullPointerException ex) {
      ResponseWriter.writeResponse(exchange, ex.getMessage(), 404);
    }
  }
}
