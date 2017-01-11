package me.karun.country.control;

import me.karun.country.GetCountryRequest;
import me.karun.country.GetCountryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import static java.lang.System.currentTimeMillis;

@Endpoint
public class ControlCountryEndpoint {
  private static final String NAMESPACE_URI = "http://karun.me/country";
  private final Logger logger = LoggerFactory.getLogger(ControlCountryEndpoint.class);

  private final ControlCountryRepository oldRepository;

  @Autowired
  public ControlCountryEndpoint(final ControlCountryRepository oldRepository) {
    this.oldRepository = oldRepository;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
  @ResponsePayload
  public GetCountryResponse getCountryV1(@RequestPayload final GetCountryRequest request) {
    final long start = currentTimeMillis();
    System.out.println("Control start");

    final GetCountryResponse response = new GetCountryResponse();
    oldRepository.findCountry(request.getName())
      .ifPresent(response::setCountry);

    logger.debug("Control end {}", (currentTimeMillis() - start));
    return response;
  }
}
