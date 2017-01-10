package me.karun.country;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static me.karun.country.TestHttpClient.testClient;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.http.protocol.HTTP.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CountryEndpointAppTest {

  @LocalServerPort
  private int serverPort;

  @Test
  public void shouldFetchDataForSpain() throws IOException {
    final String input = inputFromFile();
    final String serverUri = "http://localhost:" + serverPort + "/ws";

    final String output = testClient(serverUri)
      .withHeader(CONTENT_TYPE, TEXT_XML_VALUE)
      .post(input)
      .process();

    assertThat(output).contains("<ns2:name>Spain</ns2:name>")
      .contains("<ns2:population>46704314</ns2:population>")
      .contains("<ns2:capital>Madrid</ns2:capital>")
      .contains("<ns2:currency>EUR</ns2:currency>");
  }

  private String inputFromFile() throws IOException {
    final String absoluteFilePath = this.getClass()
      .getResource("/input.xml")
      .getFile();
    return readFileToString(new File(absoluteFilePath));
  }
}

class TestHttpClient {
  private final String serverUri;
  private final Map<String, String> headers;
  private String body;

  private TestHttpClient(final String serverUri) {
    this.serverUri = serverUri;
    headers = new HashMap<>();
  }

  static TestHttpClient testClient(final String serverUri) {
    return new TestHttpClient(serverUri);
  }

  TestHttpClient withHeader(final String key, final String value) {
    headers.put(key, value);
    return this;
  }

  TestHttpClient post(final String body) {
    this.body = body;
    return this;
  }

  String process() throws IOException {
    try (final CloseableHttpClient client = HttpClients.createDefault()) {
      final HttpPost post = new HttpPost(serverUri);
      post.setEntity(new StringEntity(body));
      headers.entrySet()
        .forEach(e -> post.addHeader(e.getKey(), e.getValue()));

      final HttpEntity entity = client.execute(post).getEntity();

      if (entity == null) {
        fail("No response from HTTP POST");
      }

      assert entity != null;
      try (final InputStream stream = entity.getContent()) {
        return IOUtils.toString(stream);
      }
    }
  }
}