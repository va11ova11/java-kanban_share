package httpServer;

import httpServer.Handler.MainHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import models.business.Util.Managers;
import services.manager.TasksManager;


public class HttpTaskServer {

  private static final int PORT = 8080;
  private final MainHandler mainHandler;
  private final HttpServer httpServer;

  public HttpTaskServer() throws IOException, InterruptedException {
    TasksManager manager = Managers.getDefault(new URL("http://localhost:8078"), false);
    httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
    httpServer.createContext("/tasks", this::handle);
    mainHandler = new MainHandler(manager);
  }

  private void handle(HttpExchange exchange) throws IOException {
    mainHandler.handle(exchange);
  }

  public void start() {
    httpServer.start();
    System.out.println("HttpTaskServer запущен");
  }

  public void stop() {
    httpServer.stop(0);
  }
}
