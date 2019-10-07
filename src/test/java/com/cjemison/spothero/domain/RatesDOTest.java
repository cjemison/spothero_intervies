package com.cjemison.spothero.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RatesDOTest {


  @Test
  public void builder() {
    assertThat(RatesDO.builder(), is(notNullValue()));

  }

  @Test
  public void getRates() {
    final var rateDO = RateDO.builder()
        .times("0900-2100")
        .days("mon,tues,thurs")
        .price("1500")
        .tz("America/Chicago")
        .build();

    final RatesDO ratesDO = RatesDO.builder()
        .rates(rateDO)
        .build();

    assertThat(ratesDO, is(notNullValue()));
  }

  @Test
  public void getRates1() {
    final var rateDO = RateDO.builder()
        .times("0900-2100")
        .days("mon,tues,thurs")
        .price("1500")
        .tz("America/Chicago")
        .build();

    final RatesDO ratesDO = RatesDO.builder()
        .rates(rateDO)
        .build();

    assertThat(ratesDO, is(notNullValue()));
    assertThat(ratesDO.getRates().contains(rateDO), is(equalTo(true)));
  }
}