package com.cjemison.spothero.controller.impl;

import com.cjemison.spothero.controller.IRateController;
import com.cjemison.spothero.domain.RateDO;
import com.cjemison.spothero.domain.RatesDO;
import com.cjemison.spothero.domain.RequestDO;
import com.cjemison.spothero.service.IRateService;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

@RestController
@RequestMapping(value = "/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DefaultRateControllerImpl implements IRateController {

  private final static Logger LOGGER = LoggerFactory.getLogger(DefaultRateControllerImpl.class);
  private final IRateService rateService;
  private final Executor executor;

  @Autowired
  public DefaultRateControllerImpl(final IRateService rateService,
      @Qualifier("threadPoolTaskExecutor") final Executor executor) {
    this.rateService = rateService;
    this.executor = executor;
  }

  @Override
  @RequestMapping(value = "/rate", method = RequestMethod.GET)
  public Flux<ResponseEntity<?>> findAll() {
    LOGGER.debug("findAll()");
    return rateService.findAll()
        .subscribeOn(Schedulers.fromExecutor(executor))
        .flatMap(rateDO -> Mono.<ResponseEntity<?>>just(ResponseEntity.ok(rateDO)));
  }

  @Override
  @RequestMapping(value = "/rate/{id}", method = RequestMethod.GET)
  public Mono<ResponseEntity<?>> findOne(@PathVariable("id") final String id) {
    LOGGER.debug("findOne - id: {}", id);
    return Mono.subscriberContext()
        .subscriberContext(Context.of("id", id))
        .flatMap(rateService::findOne)
        .flatMap(rateDO1 -> Mono.<ResponseEntity<?>>just(ResponseEntity.ok(rateDO1)));
  }

  @Override
  @RequestMapping(value = "/rate",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Flux<ResponseEntity<?>> create(@RequestBody final RatesDO ratesDO) {
    LOGGER.debug("create - rateDO: {}", ratesDO);
    return Flux.just(Context.of("ratesDO", ratesDO))
        .flatMap(rateService::create)
        .flatMap(rateDO1 -> Mono.<ResponseEntity<?>>just(ResponseEntity.accepted().body(rateDO1)));
  }

  @Override
  @RequestMapping(value = "/rate/{id}",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Flux<ResponseEntity<?>> update(@PathVariable("id") final String id,
      @RequestBody final RateDO rateDO) {
    LOGGER.debug("update - id: {} rateDO: {}", id, rateDO);
    return Flux.just(Context.of("rateDO", rateDO))
        .subscriberContext(Context.of("id", id))
        .subscriberContext(Context.of("rateDO", rateDO))
        .flatMap(rateService::update)
        .flatMap(rateDO1 -> Mono.<ResponseEntity<?>>just(ResponseEntity.accepted().body(rateDO1)));
  }

  @Override
  @RequestMapping(value = "/rate/{id}", method = RequestMethod.DELETE)
  public Mono<ResponseEntity<?>> delete(@PathVariable("id") final String id) {
    LOGGER.debug("delete - id: {}", id);
    return Mono.subscriberContext()
        .subscriberContext(Context.of("id", id))
        .flatMap(rateService::delete)
        .flatMap(aBoolean -> {
          Mono<ResponseEntity<?>> mono = Mono.just(ResponseEntity.accepted().build());
          if (aBoolean) {
            mono = Mono.just(ResponseEntity.ok().body(""));
          }
          return mono;
        });
  }

  @Override
  @RequestMapping(value = "/query",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Mono<ResponseEntity<?>> query(@RequestBody final RequestDO requestDO) {
    LOGGER.debug("query - requestDO: {}", requestDO);
    return Mono.subscriberContext()
        .subscriberContext(Context.of("requestDO", requestDO))
        .flatMap(rateService::query)
        .flatMap(responseDO -> Mono.<ResponseEntity<?>>just(ResponseEntity.ok(requestDO)));
  }
}
