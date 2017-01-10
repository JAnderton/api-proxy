package me.karun.country.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CandidateCountryEndpoint {
  private static final String NAMESPACE_URI = "http://karun.me/country/candidate";

  private final CandidateCountryRepository oldRepository;

  @Autowired
  public CandidateCountryEndpoint(final CandidateCountryRepository oldRepository) {
    this.oldRepository = oldRepository;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
  @ResponsePayload
  public GetCountryResponse getCountryV1(@RequestPayload final GetCountryRequest request) {
    final GetCountryResponse response = new GetCountryResponse();
    oldRepository.findCountry(request.getName())
      .ifPresent(response::setCountry);

    return response;
  }
}
