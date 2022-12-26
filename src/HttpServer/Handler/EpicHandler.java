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
  private final Gson gsonForGetEpic;


  public EpicHandler(FileBackedTasksManager manager, Gson gsonForGetEpic) {
    this.manager = manager;
    this.gsonForGetEpic = gsonForGetEpic;
  }

  public void deleteEpics(HttpExchange exchange) throws IOException {
    manager.deleteAllEpic();
    String response = "Все эпики удалены";
    ResponseWriter.writeResponse(exchange, response, 200);
  }

  public void getEpics(HttpExchange exchange) throws IOException {
    ResponseWriter.writeResponse(exchange, gsonForGetEpic.toJson(manager.getEpics()), 200);
  }

  public void postEpic(HttpExchange exchange) throws IOException {
    Headers headers = exchange.getRequestHeaders();
    InputStream is = exchange.getRequestBody();
    if (is == null || !headers.get("Content-Type").contains("application/json")) {
      String response = "Формат должен данных быть - JSON";
      ResponseWriter.writeResponse(exchange, response, 400);
      return;
    }
    String requestString = new String(is.readAllBytes(), DEFAULT_CHARSET);
    try {
      Epic epic = gsonForGetEpic.fromJson(requestString, Epic.class);
      manager.createEpic(epic);
      String response = "Задача " + epic.getName() + " создана";
      ResponseWriter.writeResponse(exchange, response, 200);
    } catch (JsonSyntaxException ex) {
      String response = "Не верный формат JSON";
      ResponseWriter.writeResponse(exchange, response, 400);
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
      ResponseWriter.writeResponse(exchange, gsonForGetEpic.toJson(epic), 200);
    } catch (NullPointerException ex) {
      ResponseWriter.writeResponse(exchange, ex.getMessage(), 404);
    }
  }

  public void getSubtasksInEpic(HttpExchange exchange) throws IOException {
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
      List<Subtask> subtasksInEpic = manager.getSubtasksInEpic(epic);
      ResponseWriter.writeResponse(exchange, gsonForGetEpic.toJson(subtasksInEpic), 200);
    } catch (NullPointerException ex) {
      ResponseWriter.writeResponse(exchange, ex.getMessage(), 404);
    }
  }

  public void deleteAllSubtaskInEpic(HttpExchange exchange) throws IOException{
    Headers headers = exchange.getRequestHeaders();
    InputStream is = exchange.getRequestBody();
    if (is == null || !headers.get("Content-Type").contains("application/json")) {
      String response = "Формат должен данных быть - JSON";
      ResponseWriter.writeResponse(exchange, response, 400);
      return;
    }

    String requestString = new String(is.readAllBytes(), DEFAULT_CHARSET);
    final Epic epic;
    try {
      epic = gsonForGetEpic.fromJson(requestString, Epic.class);
    } catch (JsonSyntaxException ex) {
      String response = "Не верный формат JSON";
      ResponseWriter.writeResponse(exchange, response, 400);
      return;
    }

    try {
      manager.deleteAllSubTasksInEpic(epic);
      String response = "Все задачи у эпика: " + epic.getName() + " удалены";
      ResponseWriter.writeResponse(exchange, response, 200);
    } catch (NullPointerException ex) {
      ResponseWriter.writeResponse(exchange, ex.getMessage(), 404);
    }
  }
}
