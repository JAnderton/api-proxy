package me.karun.country.candidate;

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
public class CandidateCountryEndpoint {
  private static final String NAMESPACE_URI = "http://karun.me/country";
  private final Logger logger = LoggerFactory.getLogger(CandidateCountryEndpoint.class);

  private final CandidateCountryRepository oldRepository;

  @Autowired
  public CandidateCountryEndpoint(final CandidateCountryRepository oldRepository) {
    this.oldRepository = oldRepository;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
  @ResponsePayload
  public GetCountryResponse getCountry(@RequestPayload final GetCountryRequest request) throws InterruptedException {
    final long start = currentTimeMillis();
    logger.debug("Candidate start");

    final GetCountryResponse response = new GetCountryResponse();
    oldRepository.findCountry(request.getName())
      .ifPresent(response::setCountry);

    final int sleepTime = 1000;
    logger.debug("Sleeping for {} ms", sleepTime);
    Thread.sleep(sleepTime);

    logger.debug("Control end {}", (currentTimeMillis() - start));
    return response;
  }
}
