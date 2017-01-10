package me.karun.country.control;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.function.Predicate;

import static me.karun.country.control.Currency.EUR;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.assertj.core.api.Assertions.assertThat;

public class ControlCountryRepositoryTest {
  private ControlCountryRepository repository;

  @Before
  public void setUp() throws Exception {
    repository = new ControlCountryRepository();
    repository.initData();
  }

  @Test
  public void shouldFindKnownCountry() {
    final String inputCountryName = "Spain";
    final Country expected = new Country();
    expected.setName(inputCountryName);
    expected.setCapital("Madrid");
    expected.setCurrency(EUR);
    expected.setPopulation(46704314);

    final Optional<Country> actualCountry = repository.findCountry(inputCountryName);

    final Predicate<Optional<Country>> predicate = c -> c.isPresent() && reflectionEquals(c.get(), expected);
    final Condition<Optional<Country>> condition = new Condition<>(predicate, reflectionToString(expected));
    assertThat(actualCountry).isPresent()
      .describedAs(reflectionToString(actualCountry.orElseGet(Country::new)))
      .has(condition);
  }

  @Test
  public void shouldFailWhenSearchingForAnUnknownCountry() {
    assertThat(repository.findCountry("unknown-country")).isNotPresent();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailWhenSearchingForNull() {
    repository.findCountry(null);
  }
}