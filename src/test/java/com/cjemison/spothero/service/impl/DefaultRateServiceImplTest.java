package com.cjemison.spothero.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.cjemison.spothero.domain.RateDO;
import com.cjemison.spothero.domain.RatesDO;
import com.cjemison.spothero.domain.RequestDO;
import com.cjemison.spothero.domain.ResponseDO;
import com.cjemison.spothero.model.RateEO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;

public class DefaultRateServiceImplTest {

  private ConcurrentLinkedQueue<RateEO> rateEOList;
  private DefaultRateServiceImpl service;
  private final DateTimeFormatter parser = ISODateTimeFormat.dateTime();

  @Before
  public void setup() {
    rateEOList = new ConcurrentLinkedQueue<>();
    service = new DefaultRateServiceImpl(rateEOList,
        new DefaultRateEOFactoryImpl());
  }

  @Test
  public void findAll() {
    final RateEO rateEO = RateEO.builder()
        .id(UUID.randomUUID().toString())
        .days("mon,tues,thurs")
        .start("0900")
        .end("2100")
        .price("1500")
        .timeZone("America/Chicago")
        .build();
    rateEOList.add(rateEO);

    final RateDO rateDO = service.findAll().blockFirst();
    assertThat(rateDO, is(notNullValue()));
    assertThat(StringUtils.isNotBlank(rateDO.getId()), is(equalTo(true)));
    assertThat(rateDO.getDays(), is(equalTo(rateDO.getDays())));
    assertThat(rateDO.getTimes(),
        is(equalTo(String.format("%s-%s", rateEO.getStart(), rateEO.getEnd()))));
    assertThat(rateDO.getPrice(), is(equalTo(rateEO.getPrice())));
  }

  @Test
  public void findOne() {
    final RatesDO ratesDO = RatesDO.builder()
        .rates(RateDO.builder()
            .times("0900-2100")
            .days("mon,tues,thurs")
            .price("1500")
            .tz("America/Chicago")
            .build())
        .build();

    RateDO rateDO = service.create(Context.of("ratesDO", ratesDO)).blockFirst();
    assertThat(rateDO, is(notNullValue()));
    assertThat(StringUtils.isNotBlank(rateDO.getId()), is(equalTo(true)));
    assertThat(rateDO.getDays(), is(equalTo(rateDO.getDays())));
    assertThat(rateDO.getTimes(),
        is(equalTo(String.format("%s-%s", "0900", "2100"))));
    assertThat(rateDO.getPrice(), is(equalTo("1500")));

    RateDO found = service.findOne(Context.of("id", rateDO.getId())).block();
    assertThat(found, is(notNullValue()));
    assertThat(found, is(equalTo(rateDO)));
  }

  @Test
  public void create() {
    final RateEO rateEO = RateEO.builder()
        .id(UUID.randomUUID().toString())
        .days("mon,tues,thurs")
        .start("0900")
        .end("2100")
        .price("1500")
        .timeZone("America/Chicago")
        .build();
    rateEOList.add(rateEO);

    final RatesDO ratesDO = RatesDO.builder()
        .rates(RateDO.builder()
            .times("0900-2100")
            .days("mon,tues,thurs")
            .price("1500")
            .tz("America/Chicago")
            .build())
        .build();

    final RateDO rateDO = service.create(Context.of("ratesDO", ratesDO)).blockFirst();
    assertThat(rateDO, is(notNullValue()));
    assertThat(StringUtils.isNotBlank(rateDO.getId()), is(equalTo(true)));
    assertThat(rateDO.getDays(), is(equalTo(rateDO.getDays())));
    assertThat(rateDO.getTimes(),
        is(equalTo(String.format("%s-%s", rateEO.getStart(), rateEO.getEnd()))));
    assertThat(rateDO.getPrice(), is(equalTo(rateEO.getPrice())));
  }

  @Test
  public void update() {
    final RateEO rateEO = RateEO.builder()
        .id(UUID.randomUUID().toString())
        .days("mon,tues,thurs")
        .start("0900")
        .end("2100")
        .price("1500")
        .timeZone("America/Chicago")
        .build();
    rateEOList.add(rateEO);

    final RateDO updateRateDO = RateDO
        .builder()
        .id(rateEO.getId())
        .times("0900-2100")
        .days("tues,thurs")
        .price("1500")
        .tz("America/Chicago")
        .build();

    final Map<String, Object> map = new HashMap<>();
    map.put("rateDO", updateRateDO);
    map.put("id", rateEO.getId());

    final RateDO updatedResponseRateDO = service.update(Context.of(map)).blockFirst();
    assertThat(updatedResponseRateDO, is(notNullValue()));
    assertThat(StringUtils.isNotBlank(updatedResponseRateDO.getId()), is(equalTo(true)));
    assertThat(updatedResponseRateDO.getDays(), is(equalTo("mon,tues,thurs")));
    assertThat(updatedResponseRateDO.getTimes(),
        is(equalTo(String.format("%s-%s", rateEO.getStart(), rateEO.getEnd()))));
    assertThat(updatedResponseRateDO.getPrice(), is(equalTo(rateEO.getPrice())));
  }

  @Test
  public void query() {
    final RateEO rateEO = RateEO.builder()
        .id(UUID.randomUUID().toString())
        .days("mon,tues,thurs")
        .start("0900")
        .end("2100")
        .price("1500")
        .timeZone("America/Chicago")
        .build();
    rateEOList.add(rateEO);

    final DateTimeFormatter parser = ISODateTimeFormat.dateTime();

    final DateTime dateTime = new DateTime(2019, 10, 7, 9, 0,
        DateTimeZone.forID("America/Chicago"));

    final RequestDO requestDO = RequestDO.builder()
        .startTime(parser.print(dateTime.plusMinutes(20)))
        .endTime(parser.print(dateTime.plusMinutes(25)))
        .build();

    final ResponseDO responseDO = service.query(Context.of("requestDO", requestDO)).block();
    assertThat(responseDO, is(notNullValue()));
    assertThat(responseDO.getPrice(), is(equalTo(rateEO.getPrice())));
  }

  @Test
  public void queryDifferentTimeZone() {
    final RateEO rateEO = RateEO.builder()
        .id(UUID.randomUUID().toString())
        .days("mon,tues,thurs")
        .start("0900")
        .end("2100")
        .price("1500")
        .timeZone("America/Chicago")
        .build();
    rateEOList.add(rateEO);

    final DateTimeFormatter parser = ISODateTimeFormat.dateTime();

    final DateTime dateTime = new DateTime(2019, 10, 7, 9, 0,
        DateTimeZone.forID("America/Chicago"));

    final RequestDO requestDO = RequestDO.builder()
        .startTime(parser.print(dateTime.withZone(DateTimeZone.UTC).plusMinutes(20)))
        .endTime(parser.print(dateTime.withZone(DateTimeZone.UTC).plusMinutes(25)))
        .build();

    final ResponseDO responseDO = service.query(Context.of("requestDO", requestDO)).block();
    assertThat(responseDO, is(notNullValue()));
    assertThat(responseDO.getPrice(), is(equalTo(rateEO.getPrice())));
  }

  @Test
  public void queryDifferentTimeZone1() {
    final RateEO rateEO = RateEO.builder()
        .id(UUID.randomUUID().toString())
        .days("mon,tues,thurs")
        .start("0900")
        .end("2100")
        .price("1500")
        .timeZone("America/Chicago")
        .build();
    rateEOList.add(rateEO);

    final DateTimeFormatter parser = ISODateTimeFormat.dateTime();

    final DateTime dateTime = new DateTime(2019, 10, 7, 9, 0,
        DateTimeZone.forID("America/Chicago"));

    final RequestDO requestDO = RequestDO.builder()
        .startTime(parser.print(dateTime.withZone(DateTimeZone.UTC).plusMinutes(20)))
        .endTime(parser.print(dateTime.withZone(DateTimeZone.UTC).plusMinutes(25)))
        .build();

    final ResponseDO responseDO = service.query(Context.of("requestDO", requestDO)).block();
    assertThat(responseDO, is(notNullValue()));
    assertThat(responseDO.getPrice(), is(equalTo(rateEO.getPrice())));
  }

  @Test
  public void query1() {
    final RateEO rateEO = RateEO.builder()
        .id(UUID.randomUUID().toString())
        .days("mon,tues,thurs")
        .start("0900")
        .end("2100")
        .price("1500")
        .timeZone("America/Chicago")
        .build();
    rateEOList.add(rateEO);

    final DateTimeFormatter parser = ISODateTimeFormat.dateTime();

    final DateTime dateTime = new DateTime(2019, 10, 7, 9, 0,
        DateTimeZone.forID("America/Chicago"));

    final RequestDO requestDO = RequestDO.builder()
        .startTime(parser.print(dateTime.plusMinutes(20)))
        .endTime(parser.print(dateTime.plusHours(25)))
        .build();

    final ResponseDO responseDO = service.query(Context.of("requestDO", requestDO)).block();
    assertThat(responseDO, is(notNullValue()));
    assertThat(responseDO.getPrice(), is(equalTo("unavailable")));
  }

  @Test
  public void testCreateSample() throws Exception {
    final var is = DefaultRateServiceImplTest.class.getClassLoader()
        .getResource("sample_rates.json").openStream();
    assertThat(is, is(notNullValue()));
    final var json = IOUtils.toString(is);
    assertThat(StringUtils.isNotBlank(json), is(equalTo(true)));

    final var objectMapper = new ObjectMapper();
    final RatesDO ratesDO = objectMapper.readValue(json, RatesDO.class);
    assertThat(ratesDO, is(notNullValue()));
    assertThat(CollectionUtils.isNotEmpty(ratesDO.getRates()), is(equalTo(true)));

    final Flux<RateDO> create = service.create(Context.of("ratesDO", ratesDO));
    assertThat(create, is(notNullValue()));

    final var rateDOS = create.collectList().block();
    assertThat(CollectionUtils.isNotEmpty(rateDOS), is(equalTo(true)));
    assertThat(rateDOS.size(), is(equalTo(ratesDO.getRates().size())));
  }

  @Test
  public void testCreateSample1() throws Exception {
    final var is = DefaultRateServiceImplTest.class.getClassLoader()
        .getResource("sample_rates.json").openStream();
    assertThat(is, is(notNullValue()));
    final var json = IOUtils.toString(is);
    assertThat(StringUtils.isNotBlank(json), is(equalTo(true)));

    final var objectMapper = new ObjectMapper();
    final RatesDO ratesDO = objectMapper.readValue(json, RatesDO.class);
    assertThat(ratesDO, is(notNullValue()));
    assertThat(CollectionUtils.isNotEmpty(ratesDO.getRates()), is(equalTo(true)));

    final Flux<RateDO> create = service.create(Context.of("ratesDO", ratesDO));
    assertThat(create, is(notNullValue()));

    final var rateDOS = create.collectList().block();
    assertThat(CollectionUtils.isNotEmpty(rateDOS), is(equalTo(true)));
    assertThat(rateDOS.size(), is(equalTo(ratesDO.getRates().size())));

    for (RateDO rateDO : ratesDO.getRates()) {

      List<String> days = Splitter.on(",")
          .trimResults()
          .splitToList(rateDO.getDays());

      List<String> times = Splitter.on("-")
          .trimResults()
          .splitToList(rateDO.getTimes())
          .stream()
          .map(String::trim)
          .collect(Collectors.toList());

      var start = LocalTime.parse(times.get(0), DateTimeFormat.forPattern("HHmm"));
      var end = LocalTime.parse(times.get(1), DateTimeFormat.forPattern("HHmm"));

      for (String day : days) {
        DateTime startTime = getNextDay(day, rateDO.getTimeZone())
            .withHourOfDay(start.getHourOfDay()).withMinuteOfHour(start.getMinuteOfHour());

        DateTime endTime = getNextDay(day, rateDO.getTimeZone())
            .withHourOfDay(end.getHourOfDay()).withMinuteOfHour(end.getMinuteOfHour()).minusMinutes(5);

        RequestDO requestDO = RequestDO.builder()
            .startTime(parser.print(startTime))
            .endTime(parser.print(endTime))
            .build();

        ResponseDO created = service.query(Context.of("requestDO", requestDO)).block();
        assertThat(created, is(notNullValue()));
        assertThat(created.getPrice(), is(not(equalTo("unavailable"))));

      }
    }
  }

  private DateTime getNextDay(final String value, final String timeZone) {
    final DateTime today = DateTime.now(DateTimeZone.forID(timeZone));
    int old = today.getDayOfWeek();
    int day = getIntegerDayRepresentative(value);

    if (day <= old) {
      day += 7;
    }
    return today.plusDays(day - old);
  }

  private int getIntegerDayRepresentative(final String day) {
    switch (day) {
      case "mon":
        return 1;
      case "tues":
        return 2;
      case "wed":
        return 3;
      case "thurs":
        return 4;
      case "fri":
        return 5;
      case "sat":
        return 6;
      case "sun":
        return 7;
      default:
        return -1;
    }
  }
}