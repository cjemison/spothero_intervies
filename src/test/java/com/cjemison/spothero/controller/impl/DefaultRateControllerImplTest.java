package com.cjemison.spothero.controller.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.cjemison.spothero.config.WebConfig;
import com.cjemison.spothero.domain.RateDO;
import com.cjemison.spothero.domain.RatesDO;
import com.cjemison.spothero.model.RateEO;
import com.cjemison.spothero.service.impl.DefaultRateServiceImpl;
import com.cjemison.spothero.service.impl.DefaultRateServiceImplTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
public class DefaultRateControllerImplTest {

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
  public void findAll() throws Exception {
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

    webTestClient.get()
        .uri("/v1/rate")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBody(RatesDO.class);
  }

  @Test
  public void findOne() throws Exception {
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

    rateDOS.forEach(rateDO -> {
      webTestClient.get()
          .uri("/v1/rate/" + rateDO.getId())
          .accept(MediaType.APPLICATION_JSON_UTF8)
          .exchange()
          .expectStatus().is2xxSuccessful()
          .expectBody(RateDO.class);
    });
  }

  @Test
  public void create() throws Exception {
    final var is = DefaultRateServiceImplTest.class.getClassLoader()
        .getResource("sample_rates.json").openStream();
    assertThat(is, is(notNullValue()));
    final var json = IOUtils.toString(is);
    assertThat(StringUtils.isNotBlank(json), is(equalTo(true)));

    final var objectMapper = new ObjectMapper();
    final RatesDO ratesDO = objectMapper.readValue(json, RatesDO.class);
    assertThat(ratesDO, is(notNullValue()));
    assertThat(CollectionUtils.isNotEmpty(ratesDO.getRates()), is(equalTo(true)));

    webTestClient.post()
        .uri("/v1/rate")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .body(BodyInserters.fromObject(ratesDO))
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBody(RatesDO.class);
  }

  @Test
  public void update() throws Exception {
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

    final RateDO update = RateDO.builder()
        .id(rateDOS.get(0).getId())
        .days("mon")
        .times(rateDOS.get(0).getTimes())
        .tz(rateDOS.get(0).getTimeZone())
        .price(rateDOS.get(0).getPrice())
        .build();

    webTestClient.put()
        .uri("/v1/rate/" + rateDOS.get(0).getId())
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .body(BodyInserters.fromObject(update))
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBody()
        .jsonPath("$.days", is(equalTo("mon")));
  }

  @Test
  public void delete() throws Exception {
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

    webTestClient.delete()
        .uri("/v1/rate/" + rateDOS.get(0).getId())
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().is2xxSuccessful();
  }
}