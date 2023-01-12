package httpServer.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import httpServer.Util.LocalDateTimeAdapter;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import models.business.enums.Endpoint;
import services.manager.TasksManager;

public class MainHandler implements HttpHandler {

  private final EpicHandler epicHandler;
  private final TaskHandler taskHandler;
  private final SubtaskHandler subtaskHandler;
  private final HistoryHandler historyHandler;


  public MainHandler(TasksManager manager) {
    Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();
    Gson gsonForEpic = new GsonBuilder()
        .serializeNulls()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .create();
    taskHandler = new TaskHandler(manager, gson);
    subtaskHandler = new SubtaskHandler(gson, manager);
    epicHandler = new EpicHandler(manager, gsonForEpic);
    historyHandler = new HistoryHandler(gson, manager);
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    System.out.println("Началась обработка запроса по эндпоинту\n");
    Endpoint endpoint = getEndpoint(exchange.getRequestURI(), exchange.getRequestMethod());

    switch (endpoint) {
      case GET_TASKS:
        taskHandler.getTasks(exchange);
        break;
      case POST_TASK:
        taskHandler.postTask(exchange);
        break;
      case DELETE_TASKS:
        taskHandler.deleteTasks(exchange);
        break;
      case GET_TASK_BY_ID:
        taskHandler.getTaskById(exchange);
        break;

      case GET_EPICS:
        epicHandler.getEpics(exchange);
        break;
      case POST_EPIC:
        epicHandler.postEpic(exchange);
        break;
      case DELETE_EPICS:
        epicHandler.deleteEpics(exchange);
        break;
      case GET_EPIC_BY_ID:
        epicHandler.getEpicById(exchange);
        break;
      case GET_SUBTASK_IN_EPIC:
        epicHandler.getSubtasksInEpic(exchange);
        break;
      case DELETE_SUBTASKS_IN_EPIC:
        epicHandler.deleteAllSubtaskInEpic(exchange);
        break;

      case GET_SUBTASKS:
        subtaskHandler.handleGetSubtasks(exchange);
        break;
      case POST_SUBTASK:
        subtaskHandler.handlePostSubtask(exchange);
        break;
      case GET_SUBTASK_BY_ID:
        subtaskHandler.getSubtaskById(exchange);
        break;

      case DELETE_TASK_BY_ID:
      case DELETE_EPIC_BY_ID:
      case DELETE_SUBTASK_BY_ID:
        taskHandler.deleteTaskById(exchange);
        break;
      case GET_HISTORY:
        historyHandler.getHistory(exchange);
        break;
      case GET_PRIORITIZED_TASK:
        historyHandler.getPrioritizedTask(exchange);
        break;

      case UNKNOWN:
        System.out.println("Неправильный URI: " + exchange.getRequestURI());
    }
  }

  private Endpoint getEndpointForDeleteAndGetById(String[] path, String requestMethod) {
    if (path[2].equals("task")) {
      if (requestMethod.equals("GET")) {
        return Endpoint.GET_TASK_BY_ID;
      }

      if (requestMethod.equals("DELETE")) {
        return Endpoint.DELETE_TASK_BY_ID;
      }
    }

    if (path[2].equals("epic")) {
      if (requestMethod.equals("GET")) {
        return Endpoint.GET_EPIC_BY_ID;
      }

      if (requestMethod.equals("DELETE")) {
        return Endpoint.DELETE_EPIC_BY_ID;
      }
    }

    if (path[2].equals("subtask")) {
      if (path.length > 3 && path[3].equals("epic")) {
        return Endpoint.GET_SUBTASK_IN_EPIC;
      }

      if (requestMethod.equals("GET")) {
        return Endpoint.GET_SUBTASK_BY_ID;
      }

      if (requestMethod.equals("DELETE")) {
        return Endpoint.DELETE_SUBTASK_BY_ID;
      }
    }
    return Endpoint.UNKNOWN;
  }

  private Endpoint getEndpointForTask(String requestMethod) {
    if (requestMethod.equals("GET")) {
      return Endpoint.GET_TASKS;
    }
    if (requestMethod.equals("POST")) {
      return Endpoint.POST_TASK;
    }
    if (requestMethod.equals("DELETE")) {
      return Endpoint.DELETE_TASKS;
    }
    return Endpoint.UNKNOWN;
  }

  private Endpoint getEndpointForEpic(String requestMethod, String[] path) {
    if (path.length > 3 && path[3].equals("subtasks")) {
      return Endpoint.DELETE_SUBTASKS_IN_EPIC;
    }
    if (requestMethod.equals("GET")) {
      return Endpoint.GET_EPICS;
    }
    if (requestMethod.equals("POST")) {
      return Endpoint.POST_EPIC;
    }
    if (requestMethod.equals("DELETE")) {
      return Endpoint.DELETE_EPICS;
    }
    return Endpoint.UNKNOWN;
  }

  private Endpoint getEndpointForSubtask(String requestMethod) {
    if (requestMethod.equals("GET")) {
      return Endpoint.GET_SUBTASKS;
    }
    if (requestMethod.equals("POST")) {
      return Endpoint.POST_SUBTASK;
    }
    return Endpoint.UNKNOWN;
  }

  private Endpoint getEndpoint(URI uri, String requestMethod) {
    String[] path = uri.getPath().split("/");

    if (path[1].equals("tasks") && requestMethod.equals("GET") && path.length < 3) {
      return Endpoint.GET_PRIORITIZED_TASK;
    }

    if (uri.getQuery() != null) {
      return getEndpointForDeleteAndGetById(path, requestMethod);
    }
    if (path[2].equals("task")) {
      return getEndpointForTask(requestMethod);
    }
    if (path[2].equals("epic")) {
      return getEndpointForEpic(requestMethod, path);
    }
    if (path[2].equals("subtask")) {
      return getEndpointForSubtask(requestMethod);
    }

    if (path[2].equals("history") && requestMethod.equals("GET")) {
      return Endpoint.GET_HISTORY;
    }

    return Endpoint.UNKNOWN;
  }
}
