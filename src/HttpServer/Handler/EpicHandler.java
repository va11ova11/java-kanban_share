package HttpServer.Handler;

import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;

import HttpServer.Util.ResponseWriter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import models.business.Epic;
import models.business.Subtask;
import services.manager.FileBackedTasksManager;

public class EpicHandler {

  private final FileBackedTasksManager manager;
  private final Gson gson;
  private final ResponseWriter responseWriter;

  public EpicHandler(FileBackedTasksManager manager, Gson gson, ResponseWriter responseWriter) {
    this.responseWriter = responseWriter;
    this.manager = manager;
    this.gson = gson;
  }

  public void deleteEpics(HttpExchange exchange) throws IOException {
    manager.deleteAllEpic();
    String response = "Все эпики удалены";
    responseWriter.writeResponse(exchange, response, 200);
  }

  public void getEpics(HttpExchange exchange) throws IOException {
    responseWriter.writeResponse(exchange, gson.toJson(manager.getEpics()), 200);
  }

  public void postEpic(HttpExchange exchange) throws IOException {
    Headers headers = exchange.getRequestHeaders();
    InputStream is = exchange.getRequestBody();
    if (is == null || !headers.get("Content-Type").contains("application/json")) {
      String response = "Формат должен данных быть - JSON";
      responseWriter.writeResponse(exchange, response, 400);
      return;
    }
    String requestString = new String(is.readAllBytes(), DEFAULT_CHARSET);
    try {
      Epic epic = gson.fromJson(requestString, Epic.class);
      try {
        manager.addEpicInMap(epic);
      } catch (RuntimeException ex) {
        ResponseWriter.writeResponse(exchange, ex.getMessage(), 404);
      }
      String response = "Задача " + epic.getName() + " создана";
      responseWriter.writeResponse(exchange, response, 200);
    } catch (JsonSyntaxException ex) {
      String response = "Не верный формат JSON";
      responseWriter.writeResponse(exchange, response, 400);
    }
  }

  public void getEpicById(HttpExchange exchange) throws IOException {
    String query = exchange.getRequestURI().getQuery();
    int id;
    try {
      id = Integer.parseInt(query.split("=")[1]);
    } catch (NumberFormatException ex) {
      String response = "некорректный id";
      ResponseWriter.writeResponse(exchange, response, 400);
      return;
    }
    try {
      Epic epic = manager.getEpicById(id);
      responseWriter.writeResponse(exchange, gson.toJson(epic), 200);
    } catch (NullPointerException ex) {
      responseWriter.writeResponse(exchange, ex.getMessage(), 404);
    }
  }

  public void getSubtasksInEpic(HttpExchange exchange) throws IOException {
    String query = exchange.getRequestURI().getQuery();
    int id;
    try {
      id = Integer.parseInt(query.split("=")[1]);
    } catch (NumberFormatException ex) {
      String response = "некорректный id";
      responseWriter.writeResponse(exchange, response, 400);
      return;
    }

    try {
      Epic epic = manager.getEpicById(id);
      List<Subtask> subtasksInEpic = manager.getSubtasksInEpic(epic);
      responseWriter.writeResponse(exchange, gson.toJson(subtasksInEpic), 200);
    } catch (NullPointerException ex) {
      responseWriter.writeResponse(exchange, ex.getMessage(), 404);
    }
  }

  public void deleteAllSubtaskInEpic(HttpExchange exchange) throws IOException{
    Headers headers = exchange.getRequestHeaders();
    InputStream is = exchange.getRequestBody();
    if (is == null || !headers.get("Content-Type").contains("application/json")) {
      String response = "Формат должен данных быть - JSON";
      responseWriter.writeResponse(exchange, response, 400);
      return;
    }

    String requestString = new String(is.readAllBytes(), DEFAULT_CHARSET);
    final Epic epic;
    try {
      epic = gson.fromJson(requestString, Epic.class);
    } catch (JsonSyntaxException ex) {
      String response = "Не верный формат JSON";
      responseWriter.writeResponse(exchange, response, 400);
      return;
    }

    try {
      manager.deleteAllSubTasksInEpic(epic);
      String response = "Все задачи у эпика: " + epic.getName() + " удалены";
      responseWriter.writeResponse(exchange, response, 200);
    } catch (NullPointerException ex) {
      responseWriter.writeResponse(exchange, ex.getMessage(), 404);
    }
  }
}
