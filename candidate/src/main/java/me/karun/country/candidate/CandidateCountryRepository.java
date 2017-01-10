package me.karun.country.candidate;

import me.karun.country.Country;
import me.karun.country.Currency;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.karun.country.Currency.*;

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

      final Country oldCountry = result.get();
      return Optional.of(buildCountry(oldCountry.getName(), "Invalid value", oldCountry.getCurrency(), oldCountry.getPopulation()));
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