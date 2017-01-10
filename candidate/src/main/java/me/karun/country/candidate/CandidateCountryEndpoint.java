package me.karun.country.candidate;

import me.karun.country.GetCountryRequest;
import me.karun.country.GetCountryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import static java.lang.System.currentTimeMillis;

@Endpoint
public class CandidateCountryEndpoint {
  private static final String NAMESPACE_URI = "http://karun.me/country";

  private final CandidateCountryRepository oldRepository;

  @Autowired
  public CandidateCountryEndpoint(final CandidateCountryRepository oldRepository) {
    this.oldRepository = oldRepository;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
  @ResponsePayload
  public GetCountryResponse getCountry(@RequestPayload final GetCountryRequest request) throws InterruptedException {
    final long start = currentTimeMillis();
    System.out.println(">> Candidate start");

    final GetCountryResponse response = new GetCountryResponse();
    oldRepository.findCountry(request.getName())
      .ifPresent(response::setCountry);
    Thread.sleep(1000);

    System.out.println(">> Control end " + (currentTimeMillis() - start));
    return response;
  }
}
