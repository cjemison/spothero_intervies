package com.cjemison.spothero.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.UUID;
import org.junit.Test;

public class ResponseDOTest {

  @Test
  public void builder() {
    assertThat(ResponseDO.builder(), is(notNullValue()));
  }

  @Test
  public void getStartTime() {
    final var expected = UUID.randomUUID().toString();
    final var domain = ResponseDO.builder().startTime(expected).build();
    assertThat(domain, is(notNullValue()));
    assertThat(domain.getStartTime(), is(equalTo(expected)));
  }

  @Test
  public void getEndTime() {
    final var expected = UUID.randomUUID().toString();
    final var domain = ResponseDO.builder().endTime(expected).build();
    assertThat(domain, is(notNullValue()));
    assertThat(domain.getEndTime(), is(equalTo(expected)));
  }

  @Test
  public void getPrice() {
    final var expected = UUID.randomUUID().toString();
    final var domain = ResponseDO.builder().price(expected).build();
    assertThat(domain, is(notNullValue()));
    assertThat(domain.getPrice(), is(equalTo(expected)));
  }
}