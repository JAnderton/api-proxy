package me.karun.country.control;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.karun.country.control.Currency.*;

@Component
public class ControlCountryRepository {
  private final List<Country> countries = new ArrayList<>();

  @PostConstruct
  public void initData() {
    countries.add(buildCountry("Spain", "Madrid", EUR, 46704314));
    countries.add(buildCountry("Poland", "Warsaw", PLN, 38186860));
    countries.add(buildCountry("United Kingdom", "London", GBP, 63705000));
  }

  Optional<Country> findCountry(final String name) {
    Assert.notNull(name);

    return countries.stream()
      .filter(c -> name.equals(c.getName()))
      .findFirst();
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
