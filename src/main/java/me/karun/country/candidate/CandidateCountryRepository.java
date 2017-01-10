package me.karun.country.candidate;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.karun.country.candidate.Currency.*;

@Component
public class CandidateCountryRepository {
  private final List<Country> countries = new ArrayList<>();
  private final NumberProvider numberProvider;

  CandidateCountryRepository(final NumberProvider numberProvider) {
    this.numberProvider = numberProvider;
  }

  @PostConstruct
  public void initData() {
    countries.add(buildCountry("Spain", "Madrid", EUR, 46704314));
    countries.add(buildCountry("Poland", "Warsaw", PLN, 38186860));
    countries.add(buildCountry("United Kingdom", "London", GBP, 63705000));
  }

  Optional<Country> findCountry(final String name) {
    Assert.notNull(name);

    final Optional<Country> result = countries.stream()
      .filter(c -> name.equals(c.getName()))
      .findFirst();
    if (result.isPresent() && numberProvider.fetchRandomNumber() > 0.5) {
      System.out.println(">>> Candidate is returning a defective result");

      result.get().setCapital("Invalid value");
    }
    return result;
  }

  private static Country buildCountry(final String name, final String capital, final Currency currency, final int population) {
    final Country country = new Country();
    country.setName(name);
    country.setCapital(capital);
    country.setCurrency(currency);
    country.setPopulation(population);

    return country;
  }
}