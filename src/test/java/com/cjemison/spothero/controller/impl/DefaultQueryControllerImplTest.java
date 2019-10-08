package com.cjemison.spothero.controller.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.cjemison.spothero.config.WebConfig;
import com.cjemison.spothero.domain.RateDO;
import com.cjemison.spothero.domain.RatesDO;
import com.cjemison.spothero.domain.RequestDO;
import com.cjemison.spothero.model.RateEO;
import com.cjemison.spothero.service.impl.DefaultRateServiceImpl;
import com.cjemison.spothero.service.impl.DefaultRateServiceImplTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import java.util.List;
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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {WebConfig.class})
public class DefaultQueryControllerImplTest {

  private final DateTimeFormatter parser = ISODateTimeFormat.dateTime();

  @Autowired
  private DefaultRateServiceImpl service;

  @Autowired
  private ConcurrentLinkedQueue<RateEO> queue;

  @Autowired
  private WebTestClient webTestClient;

  @Before
  public void setUp() throws Exception {
    queue.clear();
  }

  @Test
  public void query() throws Exception {
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
            .withHourOfDay(end.getHourOfDay()).withMinuteOfHour(end.getMinuteOfHour())
            .minusMinutes(5);

        RequestDO requestDO = RequestDO.builder()
            .startTime(parser.print(startTime))
            .endTime(parser.print(endTime))
            .build();

        webTestClient.post()
            .uri("/v1/query")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(BodyInserters.fromObject(requestDO))
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody()
            .jsonPath("$.price", is(not(equalTo("unavailable"))));

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