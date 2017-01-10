package me.karun.country.candidate;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;
import java.util.function.Predicate;

import static me.karun.country.candidate.Currency.EUR;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountryRepositoryTest {
  private CountryRepository repository;
  @Mock
  private NumberProvider numberProvider;

  @Before
  public void setUp() throws Exception {
    repository = new CountryRepository(numberProvider);
    repository.initData();
  }

  @Test
  public void shouldFindKnownCountry() {
    when(numberProvider.fetchRandomNumber()).thenReturn(0.1);

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
  public void shouldFindInvalidCountryWhenNumberProvidedIsGreaterThanHalf() {
    when(numberProvider.fetchRandomNumber()).thenReturn(0.9);

    final String inputCountryName = "Spain";
    final Country expected = new Country();
    expected.setName(inputCountryName);
    expected.setCapital("Invalid value");
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