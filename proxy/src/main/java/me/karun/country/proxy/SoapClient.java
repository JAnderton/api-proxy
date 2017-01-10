package me.karun.country.proxy;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

class SoapClient {
  private final String serverUri;
  private final Map<String, String> headers;
  private String body;

  private SoapClient(final String serverUri) {
    this.serverUri = serverUri;
    headers = new HashMap<>();
  }

  static SoapClient testClient(final String serverUri) {
    return new SoapClient(serverUri);
  }

  SoapClient withHeader(final String key, final String value) {
    headers.put(key, value);
    return this;
  }

  SoapClient post(final String body) {
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

      assert entity != null;
      try (final InputStream stream = entity.getContent()) {
        return IOUtils.toString(stream);
      }
    }
  }
}
