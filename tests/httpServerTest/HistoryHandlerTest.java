package httpServerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import httpServer.HttpTaskServer;
import httpServer.Util.LocalDateTimeAdapter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import kvServer.KVServer;
import models.business.Task;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HistoryHandlerTest {

  private static HttpClient client;
  private static KVServer kvServer;
  private static HttpTaskServer httpTaskServer;
  private static Gson gson;

  @BeforeAll
  public static void beforeAll() throws IOException, InterruptedException {
    gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();
    client = HttpClient.newHttpClient();
    kvServer = Managers.getDefaultKVServer();
    kvServer.start();
    httpTaskServer = new HttpTaskServer();
    httpTaskServer.start();
    getTaskById();
  }

  @AfterAll
  public static void afterAll(){
    kvServer.stop();
    httpTaskServer.stop();
  }

  private static void getTaskById() throws IOException, InterruptedException {
    Task task1 = new Task("First_Task", "FirstTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 10, 0), 60);
    Task task2 = new Task("SameTimeTask", "FirstTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 15, 10, 0), 60);

    String taskOne = gson.toJson(task1);
    String taskTwo = gson.toJson(task2);
    URI url = URI.create("http://localhost:8080/tasks/task/");

    final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(taskOne);
    final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(taskTwo);
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).header("Content-Type", "application/json").build();
    HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).header("Content-Type", "application/json").build();
    client.send(request1, HttpResponse.BodyHandlers.ofString());
    client.send(request2, HttpResponse.BodyHandlers.ofString());

    URI url2 = URI.create("http://localhost:8080/tasks/task/?id=1");
    HttpRequest request3 = HttpRequest.newBuilder().uri(url2).GET().build();
    client.send(request3, HttpResponse.BodyHandlers.ofString());

    URI url3 = URI.create("http://localhost:8080/tasks/task/?id=1");
    HttpRequest request4 = HttpRequest.newBuilder().uri(url3).GET().build();
    client.send(request4, HttpResponse.BodyHandlers.ofString());
  }



  @Test
  public void shouldStatusCode200WhenHistoryIsNotEmpty() throws IOException, InterruptedException {
    URI url = URI.create("http://localhost:8080/tasks/history/");
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 200, "Задачи не возвращаются");
  }

  @Test
  public void shouldStatusCode200WhenGetPrioritizedTaskIsNotEmpty()
      throws IOException, InterruptedException
  {
    URI url = URI.create("http://localhost:8080/tasks");
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 200, "Задачи не возвращаются");
  }
}
