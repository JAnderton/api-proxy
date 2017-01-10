package me.karun.country.proxy;

import org.apache.http.protocol.HTTP;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.readFileToString;

@Controller
@RequestMapping("/proxy")
public class ProxyEndpoint {

  @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_XML_VALUE)
  @ResponseBody
  public String proxyRequest() throws IOException {
    final String type = "control";

    final String output = evaluateSoapRequest(type);

    System.out.println(output);
    return output;
  }

  private String evaluateSoapRequest(final String type) throws IOException {
    return SoapClient.testClient(String.format("http://localhost:8080/ws/%s", type))
      .withHeader(HTTP.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
      .post(inputFromFile(String.format("%s-request.xml", type)))
      .process();
  }

  private String inputFromFile(final String fileName) throws IOException {
    final String absoluteFilePath = this.getClass()
      .getResource("/" + fileName)
      .getFile();
    return readFileToString(new File(absoluteFilePath));
  }
}
