package me.karun.country.candidate;

import org.springframework.stereotype.Component;

@Component
class NumberProvider {
  double fetchRandomNumber() {
    return Math.random();
  }
}
