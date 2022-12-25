package HttpServer.Handler;

import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;

import HttpServer.Util.ResponseWriter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import models.business.Subtask;
import services.manager.FileBackedTasksManager;

public class SubtaskHandler {

  private final FileBackedTasksManager manager;
  private final Gson gson;
  private final ResponseWriter responseWriter;

  public SubtaskHandler(Gson gson, FileBackedTasksManager manager, ResponseWriter responseWriter) {
    this.responseWriter = responseWriter;
    this.manager = manager;
    this.gson = gson;
  }

  public void handleGetSubtasks(HttpExchange exchange) throws IOException {
    ResponseWriter.writeResponse(exchange, gson.toJson(manager.getSubTasks()), 200);
  }

  public void handlePostSubtask(HttpExchange exchange) throws IOException {
    Headers headers = exchange.getRequestHeaders();
    InputStream is = exchange.getRequestBody();

    if(is == null || !headers.get("Content-Type").contains("application/json")) {
      String response = "Формат должен данных быть - JSON";
      ResponseWriter.writeResponse(exchange, response, 400);
      return;
    }
    String requestString = new String(is.readAllBytes(), DEFAULT_CHARSET);
    try {
      Subtask subtask = gson.fromJson(requestString, Subtask.class);
      try {
        manager.addSubtaskInMap(subtask);
      } catch (RuntimeException ex) {
        ResponseWriter.writeResponse(exchange, ex.getMessage(), 404);
      }
      String response = "Задача " + subtask.getName() + " создана";
      ResponseWriter.writeResponse(exchange, response,200);
    } catch (JsonSyntaxException ex) {
      String response = "Не верный формат JSON";
      ResponseWriter.writeResponse(exchange, response, 400);
    }
  }


  public void getSubtaskById(HttpExchange exchange) throws IOException {
    String query = exchange.getRequestURI().getQuery();
    int id;
    try{
      id = Integer.parseInt(query.split("=")[1]);
    } catch (NumberFormatException ex) {
      String response = "некорректный id";
      ResponseWriter.writeResponse(exchange, response, 400);
      return;
    }
    try {
      Subtask subtask = manager.getSubTaskById(id);
      ResponseWriter.writeResponse(exchange, gson.toJson(subtask), 200);
    }catch (NullPointerException ex) {
      ResponseWriter.writeResponse(exchange, ex.getMessage(), 404);
    }
  }
}
