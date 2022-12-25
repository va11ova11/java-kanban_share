package HttpServer;

import HttpServer.Handler.MainHandler;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import models.business.Util.Managers;
import models.business.enums.Endpoint;
import services.manager.FileBackedTasksManager;


public class HttpTaskServer {

  private static final int PORT = 8080;
  private final MainHandler mainHandler;

  public HttpTaskServer() throws IOException {
    FileBackedTasksManager manager = Managers.getFailBackedTaskManager();
    HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
    httpServer.createContext("/tasks", this::handle);
    mainHandler = new MainHandler(manager);
    httpServer.start();
    System.out.println("Сервер запущен");
  }

  private void handle(HttpExchange exchange) throws IOException {
    mainHandler.handle(exchange);
  }
}
