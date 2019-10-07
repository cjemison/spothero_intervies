package com.cjemison.spothero.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.cjemison.spothero.domain.RateDO;
import com.cjemison.spothero.exception.InvalidRateDOException;
import com.cjemison.spothero.model.RateEO;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class DefaultRateEOFactoryImplTest {

  private DefaultRateEOFactoryImpl factory;

  @Before
  public void setUp() throws Exception {
    factory = new DefaultRateEOFactoryImpl();
  }

  @Test
  public void happyPath() {
    final RateEO rateEO = RateEO.builder()
        .id(UUID.randomUUID().toString())
        .days("mon,tues,thurs")
        .start("0900")
        .end("2100")
        .price("1500")
        .timeZone("America/Chicago")
        .build();

    final RateDO rateDO = RateDO.builder()
        .times("0900-2100")
        .days("mon,tues,thurs")
        .price("1500")
        .tz("America/Chicago")
        .build();

    final Optional<RateEO> optional = factory.build(rateDO);
    assertThat(optional, is(notNullValue()));
    assertThat(optional.isPresent(), is(equalTo(true)));
    assertThat(optional.get(), is(equalTo(rateEO)));
  }

  @Test
  public void happyPath1() {
    final RateEO rateEO = RateEO.builder()
        .id(UUID.randomUUID().toString())
        .days("mon,tues,thurs")
        .start("0900")
        .end("2100")
        .price("1500.50")
        .timeZone("America/Chicago")
        .build();

    final RateDO rateDO = RateDO.builder()
        .times("0900-2100")
        .days("mon,tues,thurs")
        .price("1500.50")
        .tz("America/Chicago")
        .build();

    final Optional<RateEO> optional = factory.build(rateDO);
    assertThat(optional, is(notNullValue()));
    assertThat(optional.isPresent(), is(equalTo(true)));
    assertThat(optional.get(), is(equalTo(rateEO)));
  }


  @Test(expected = InvalidRateDOException.class)
  public void missingTimes() {
    final RateDO rateDO = RateDO.builder()
        .days("mon,tues,thurs")
        .price("1500")
        .tz("America/Chicago")
        .build();
    final Optional<RateEO> optional = factory.build(rateDO);
    assertThat(optional, is(notNullValue()));
    assertThat(optional.isPresent(), is(equalTo(false)));
  }

  @Test(expected = InvalidRateDOException.class)
  public void partialTime() {
    final RateDO rateDO = RateDO.builder()
        .days("mon,tues,thurs")
        .times("0900")
        .price("1500")
        .tz("America/Chicago")
        .build();
    final Optional<RateEO> optional = factory.build(rateDO);
    assertThat(optional, is(notNullValue()));
    assertThat(optional.isPresent(), is(equalTo(false)));
  }

  @Test(expected = InvalidRateDOException.class)
  public void invalidStartTime() {
    final RateDO rateDO = RateDO.builder()
        .times("5000-2100")
        .days("mon,tues,thurs")
        .price("1500")
        .tz("America/Chicago")
        .build();
    final Optional<RateEO> optional = factory.build(rateDO);
    assertThat(optional, is(notNullValue()));
    assertThat(optional.isPresent(), is(equalTo(false)));
  }

  @Test(expected = InvalidRateDOException.class)
  public void invalidEndTime() {
    final RateDO rateDO = RateDO.builder()
        .times("0900-5000")
        .days("mon,tues,thurs")
        .price("1500")
        .tz("America/Chicago")
        .build();
    final Optional<RateEO> optional = factory.build(rateDO);
    assertThat(optional, is(notNullValue()));
    assertThat(optional.isPresent(), is(equalTo(false)));
  }

  @Test(expected = InvalidRateDOException.class)
  public void invalidDays() {
    final RateDO rateDO = RateDO.builder()
        .times("0900-2100")
        .days(UUID.randomUUID().toString())
        .price("1500")
        .tz("America/Chicago")
        .build();
    final Optional<RateEO> optional = factory.build(rateDO);
    assertThat(optional, is(notNullValue()));
    assertThat(optional.isPresent(), is(equalTo(false)));
  }

  @Test(expected = InvalidRateDOException.class)
  public void invalidDays1() {
    final RateDO rateDO = RateDO.builder()
        .times("0900-2100")
        .price("1500")
        .tz("America/Chicago")
        .build();
    final Optional<RateEO> optional = factory.build(rateDO);
    assertThat(optional, is(notNullValue()));
    assertThat(optional.isPresent(), is(equalTo(false)));
  }

  @Test(expected = InvalidRateDOException.class)
  public void invalidPrice() {
    final RateDO rateDO = RateDO.builder()
        .times("0900-2100")
        .days("mon,tues,thurs")
        .price(UUID.randomUUID().toString())
        .tz("America/Chicago")
        .build();
    final Optional<RateEO> optional = factory.build(rateDO);
    assertThat(optional, is(notNullValue()));
    assertThat(optional.isPresent(), is(equalTo(false)));
  }

  @Test(expected = InvalidRateDOException.class)
  public void invalidPrice1() {
    final RateDO rateDO = RateDO.builder()
        .times("0900-2100")
        .days("mon,tues,thurs")
        .tz("America/Chicago")
        .build();
    final Optional<RateEO> optional = factory.build(rateDO);
    assertThat(optional, is(notNullValue()));
    assertThat(optional.isPresent(), is(equalTo(false)));
  }

  @Test(expected = InvalidRateDOException.class)
  public void invalidTimeZone() {

    final RateDO rateDO = RateDO.builder()
        .times("0900-2100")
        .days("mon,tues,thurs")
        .price("1500")
        .build();

    final Optional<RateEO> optional = factory.build(rateDO);
    assertThat(optional, is(notNullValue()));
    assertThat(optional.isPresent(), is(equalTo(true)));
  }

  @Test(expected = InvalidRateDOException.class)
  public void invalidTimeZone1() {

    final RateDO rateDO = RateDO.builder()
        .times("0900-2100")
        .days("mon,tues,thurs")
        .price("1500")
        .tz(UUID.randomUUID().toString())
        .build();

    final Optional<RateEO> optional = factory.build(rateDO);
    assertThat(optional, is(notNullValue()));
    assertThat(optional.isPresent(), is(equalTo(true)));
  }
}