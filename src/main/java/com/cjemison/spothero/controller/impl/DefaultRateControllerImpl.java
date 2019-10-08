package com.cjemison.spothero.controller.impl;

import com.cjemison.spothero.controller.IRateController;
import com.cjemison.spothero.domain.RateDO;
import com.cjemison.spothero.domain.RatesDO;
import com.cjemison.spothero.service.IRateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import org.apache.commons.lang3.StringUtils;
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
@Api("Endpoints for finding, creating, updating, and deleting rates")
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
  @ApiOperation("An endpoint for finding all rates.")
  @ApiResponse(code = 200, message = "An endpoint for querying rates.",
      response = RatesDO.class)
  public Mono<ResponseEntity<?>> findAll() {
    LOGGER.debug("findAll()");
    return rateService.findAll()
        .subscribeOn(Schedulers.fromExecutor(executor))
        .onErrorResume(throwable -> Mono.just(RateDO.builder()
            .error(throwable.getMessage())
            .build()))
        .collectList()
        .flatMap(list -> Mono.<ResponseEntity<?>>just(
            ResponseEntity.ok(RatesDO.builder().rates(list).build())));
  }

  @Override
  @RequestMapping(value = "/rate/{id}", method = RequestMethod.GET)
  @ApiOperation("An endpoint for finding rates by id.")
  @ApiResponse(code = 200, message = "An endpoint for finding rates by id.",
      response = RateDO.class)
  public Mono<ResponseEntity<?>> findOne(@PathVariable("id") final String id) {
    LOGGER.debug("findOne - id: {}", id);
    return Mono.subscriberContext()
        .subscriberContext(Context.of("id", id))
        .flatMap(rateService::findOne)
        .onErrorResume(throwable -> Mono.just(RateDO.builder()
            .error(throwable.getMessage())
            .build()))
        .defaultIfEmpty(RateDO.builder().error("not found").build())
        .flatMap(rateDO -> {
          if (StringUtils.isNotBlank(rateDO.getError())) {
            return Mono.<ResponseEntity<?>>just(ResponseEntity.notFound().build());
          }
          return Mono.<ResponseEntity<?>>just(ResponseEntity.ok(rateDO));
        });
  }

  @Override
  @RequestMapping(value = "/rate",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation("An endpoint for creating rates.")
  @ApiResponse(code = 200, message = "An endpoint for creating rates.",
      response = RateDO.class)
  public Mono<ResponseEntity<?>> create(@RequestBody final RatesDO ratesDO) {

    LOGGER.debug("create - rateDO: {}", ratesDO);
    return Flux.just(Context.of("ratesDO", ratesDO))
        .flatMap(rateService::create)
        .onErrorResume(throwable -> Mono.just(RateDO.builder()
            .error(throwable.getMessage())
            .build()))
        .collectList()
        .flatMap(list -> {
          final long cnt = list.stream().filter(r -> StringUtils.isNotBlank(r.getError()))
              .count();

          final RatesDO r = RatesDO.builder().rates(list).build();

          if (cnt > 0) {
            return Mono.<ResponseEntity<?>>just(ResponseEntity.badRequest().body(r));
          }
          return Mono.<ResponseEntity<?>>just(ResponseEntity.accepted().body(r));
        });
  }

  @Override
  @RequestMapping(value = "/rate/{id}",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation("An endpoint for updating rates by id.")
  @ApiResponse(code = 200, message = "An endpoint for updating rates by id.",
      response = RateDO.class)
  public Mono<ResponseEntity<?>> update(@PathVariable("id") final String id,
      @RequestBody final RateDO rateDO) {
    LOGGER.debug("update - id: {} rateDO: {}", id, rateDO);

    final Map<String, Object> map = new HashMap<>();
    map.put("id", id);
    map.put("rateDO", rateDO);

    return Flux.just(Context.of(map))
        .flatMap(rateService::update)
        .onErrorResume(throwable -> Mono.just(RateDO.builder()
            .error(throwable.getMessage())
            .build()))
        .collectList()
        .flatMap(list -> {
          final long cnt = list.stream().filter(rate -> StringUtils.isNotBlank(rate.getError()))
              .count();
          final RatesDO r = RatesDO.builder().rates(list).build();

          if (cnt > 0) {
            return Mono.<ResponseEntity<?>>just(ResponseEntity.badRequest().body(r));
          }
          return Mono.<ResponseEntity<?>>just(ResponseEntity.accepted().body(r));
        });
  }

  @Override
  @RequestMapping(value = "/rate/{id}", method = RequestMethod.DELETE)
  @ApiOperation("An endpoint for deleting rates by id.")
  @ApiResponse(code = 200, message = "An endpoint for deleting rates by id.",
      response = RateDO.class)
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
}
