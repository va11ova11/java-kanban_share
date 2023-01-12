package httpServer.Util;

import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

public class ResponseWriter {

  public static void writeResponse(HttpExchange exchange, String responseString,
      int statusCode) throws IOException {

    if (responseString.isBlank()) {
      exchange.sendResponseHeaders(statusCode, 0);
    } else {
      byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
      exchange.sendResponseHeaders(statusCode, bytes.length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(bytes);
      }
    }
    exchange.close();
  }
}
