package httpServerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import models.business.Epic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EpicHandlerTest {

  private static URI url;
  private static HttpClient client;
  private static Gson gsonCreateEpic;

  @BeforeAll
  public static void beforeAll() {
    url = URI.create("http://localhost:8080/tasks/epic/");
    client = HttpClient.newHttpClient();
    gsonCreateEpic = new GsonBuilder()
        .setPrettyPrinting()
        .create();
  }

  @Test
  public void shouldRequestBodyNotEmptyAndContentTypeEqualsJson() throws IOException, InterruptedException {
    Epic epic1 = new Epic("Epic", "Epic_desc");

    String json = gsonCreateEpic.toJson(epic1);
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
  public void shouldGetAllEpic() throws IOException, InterruptedException {
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 200, "Задачи не возвращаются");
  }

  @Test
  public void shouldGetEpicByIdIfEpicExistsAndIdIsCorrect() throws IOException, InterruptedException {
    URI url = URI.create("http://localhost:8080/tasks/epic/?id=8");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response2.statusCode(), 200, "Задача не возвращается");
  }

  @Test
  public void shouldNumberFormatExceptionWhenIncorrectIdWhenGetEpicById()
      throws IOException, InterruptedException {
    URI url = URI.create("http://localhost:8080/tasks/epic/?id=incorrect");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 400, "Обрабатывается задача с некорректным Id");
  }

  @Test
  public void shouldNullPointerExceptionWhenGetEpicWhichIsNot()
      throws IOException, InterruptedException {
    URI url = URI.create("http://localhost:8080/tasks/epic/?id=100");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 404, "Обрабатывается задача с некорректным Id");
  }

  @Test
  public void shouldGetSubtaskInEpicIfIdIsCorrectAndEpicIsExist()
      throws IOException, InterruptedException {
    URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=8");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 200, "Не возвращаются подзадачи Эпика");
  }

  @Test
  public void shouldStatusCode404WhenGetSubtaskEpicWhenEpicIsNot()
      throws IOException, InterruptedException {
    URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=100");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 404, "Такого Эпика не существует");
  }

  @Test
  public void shouldStatusCode400WhenIncorrectIdWhenGetSubtasksInEpic()
      throws IOException, InterruptedException {
    URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=incorrect");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 400, "Такого Эпика не существует");
  }
}
