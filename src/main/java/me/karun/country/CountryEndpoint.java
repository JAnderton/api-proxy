package me.karun.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CountryEndpoint {
  private static final String NAMESPACE_URI = "http://karun.me/country";

  private final CountryRepository countryRepository;

  @Autowired
  public CountryEndpoint(final CountryRepository countryRepository) {
    this.countryRepository = countryRepository;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
  @ResponsePayload
  public GetCountryResponse getCountry(@RequestPayload final GetCountryRequest request) {
    final GetCountryResponse response = new GetCountryResponse();
    countryRepository.findCountry(request.getName())
      .ifPresent(response::setCountry);

    return response;
  }
}
