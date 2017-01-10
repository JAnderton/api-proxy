package me.karun.country;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static me.karun.country.proxy.SoapClient.testClient;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.http.protocol.HTTP.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CountryEndpointAppTest {

  @LocalServerPort
  private int serverPort;

  @Test
  public void shouldFetchDataFromControl() throws IOException {
    final String serverUri = "http://localhost:" + serverPort + "/ws";

    final String output = testClient(serverUri)
      .withHeader(CONTENT_TYPE, TEXT_XML_VALUE)
      .post(inputFromFile())
      .process();

    assertThat(output).contains("<ns2:name>Spain</ns2:name>")
      .contains("<ns2:population>46704314</ns2:population>")
      .contains("<ns2:capital>Madrid</ns2:capital>")
      .contains("<ns2:currency>EUR</ns2:currency>");
  }

  private String inputFromFile() throws IOException {
    final String absoluteFilePath = this.getClass()
      .getResource("/control-test-input.xml")
      .getFile();
    return readFileToString(new File(absoluteFilePath));
  }
}

