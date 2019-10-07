package com.cjemison.spothero.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.UUID;
import org.junit.Test;

public class RateDOTest {

  @Test
  public void builder() {
    assertThat(RateDO.builder(), is(notNullValue()));
  }

  @Test
  public void getId() {
    final var expected = UUID.randomUUID().toString();
    final var domain = RateDO.builder().id(expected).build();
    assertThat(domain, is(notNullValue()));
    assertThat(domain.getId(), is(equalTo(expected)));
  }

  @Test
  public void getDays() {
    final var expected = UUID.randomUUID().toString();
    final var domain = RateDO.builder().days(expected).build();
    assertThat(domain, is(notNullValue()));
    assertThat(domain.getDays(), is(equalTo(expected)));
  }

  @Test
  public void getTimes() {
    final var expected = UUID.randomUUID().toString();
    final var domain = RateDO.builder().times(expected).build();
    assertThat(domain, is(notNullValue()));
    assertThat(domain.getTimes(), is(equalTo(expected)));
  }

  @Test
  public void getTimeZone() {
    final var expected = UUID.randomUUID().toString();
    final var domain = RateDO.builder().tz(expected).build();
    assertThat(domain, is(notNullValue()));
    assertThat(domain.getTimeZone(), is(equalTo(expected)));
  }

  @Test
  public void getPrice() {
    final var expected = UUID.randomUUID().toString();
    final var domain = RateDO.builder().price(expected).build();
    assertThat(domain, is(notNullValue()));
    assertThat(domain.getPrice(), is(equalTo(expected)));
  }
}