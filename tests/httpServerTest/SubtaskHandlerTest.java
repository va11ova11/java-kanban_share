package httpServerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import HttpServer.Util.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import models.business.Epic;
import models.business.Subtask;
import models.business.enums.TaskStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SubtaskHandlerTest {

  private static URI url;
  private static HttpClient client;
  private static Gson gson;

  @BeforeAll
  public static void beforeAll() throws IOException, InterruptedException {
    url = URI.create("http://localhost:8080/tasks/subtask/");
    client = HttpClient.newHttpClient();
    gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();
    createEpic();
  }

  private static void createEpic() throws IOException, InterruptedException {
    Gson gsonForEpic = new Gson();
    Epic epic = new Epic("Epic1", "Epic1_desc");
    URI createEpicUri = URI.create("http://localhost:8080/tasks/epic/");
    String json = gsonForEpic.toJson(epic);
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
    HttpRequest request = HttpRequest.newBuilder().uri(createEpicUri).POST(body).header("Content-Type", "application/json").build();
    client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  @Test
  public void shouldRequestBodyNotEmptyAndContentTypeEqualsJson() throws IOException, InterruptedException {
    Subtask subtask = new Subtask("First_Subtask", "FirstSubtask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 10, 0), 60, 8);

    String json = gson.toJson(subtask);
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
    HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).header("Content-Type", "application/json").build();

    assertTrue(request.headers().map().get("Content-Type").contains("application/json"),
        "Передан не верный формат данных");

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertNotNull(response.body(), "Тело запроса пусто");
  }

  @Test
  public void shouldStatusCode400WhenIncorrectJson() throws IOException, InterruptedException {
    String incorrectJson = "{\"1\":{\"id\":1,\"incorrectTaskType\":\"incorrect\",\"taskName\":\"Create_Task\",\"taskStatus\":\"NEW\",\"taskDescription\":\"CreteTask_description\",\"startTime\":{\"date\":{\"year\":2022,\"month\":1,\"day\":2}}";
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(incorrectJson);
    HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).header("Content-Type", "application/json").build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 400, "Передан не верный Json");
  }

  @Test
  public void shouldStatusCode404WhenCreatingSubtaskForTheSameTime()
      throws IOException, InterruptedException {
    Subtask subtask1 = new Subtask("First_Task", "FirstTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 10, 0), 60, 8);
    Subtask subtask2 = new Subtask("SameTimeTask", "FirstTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 10, 0), 60, 8);

    String subTaskOne = gson.toJson(subtask1);
    String subTaskSameTime = gson.toJson(subtask2);

    final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(subTaskOne);
    final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(subTaskSameTime);
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).header("Content-Type", "application/json").build();
    HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).header("Content-Type", "application/json").build();
    client.send(request1, HttpResponse.BodyHandlers.ofString());
    HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 404, "Задача на это время уже существует");
  }
  @Test
  public void shouldGetAllSubtask() throws IOException, InterruptedException {
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 200, "Задачи не возвращаются");
  }

  @Test
  public void shouldGetTaskByIdIfSubtaskExistsAndIdIsCorrect() throws IOException, InterruptedException {
    URI url = URI.create("http://localhost:8080/tasks/subtask/?id=9");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response2.statusCode(), 200, "Задача не возвращается");
  }

  @Test
  public void shouldNumberFormatExceptionWhenIncorrectIdWhenGetSubtaskById()
      throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks/subtask/?id=incorrect");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 400, "Обрабатывается задача с некорректным Id");
  }

  @Test
  public void shouldNullPointerExceptionWhenGetSubtaskWhichIsNot()
      throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks/subtask/?id=100");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 404, "Обрабатывается задача с некорректным Id");
  }
}
