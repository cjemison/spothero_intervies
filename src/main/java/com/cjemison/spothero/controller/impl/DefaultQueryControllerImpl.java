package com.cjemison.spothero.controller.impl;

import com.cjemison.spothero.controller.IQueryController;
import com.cjemison.spothero.domain.RequestDO;
import com.cjemison.spothero.domain.ResponseDO;
import com.cjemison.spothero.service.IRateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

@RestController
@RequestMapping(value = "/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api("Endpoints for querying rates")
public class DefaultQueryControllerImpl implements IQueryController {

  private final static Logger LOGGER = LoggerFactory.getLogger(DefaultQueryControllerImpl.class);
  private final IRateService rateService;
  private final Executor executor;

  @Autowired
  public DefaultQueryControllerImpl(final IRateService rateService,
      @Qualifier("threadPoolTaskExecutor") final Executor executor) {
    this.rateService = rateService;
    this.executor = executor;
  }

  @Override
  @RequestMapping(value = "/query",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation("An endpoint for querying rates.")
  @ApiResponse(code = 200, message = "An endpoint for querying rates.",
      response = ResponseDO.class)
  public Mono<ResponseEntity<?>> query(@RequestBody final RequestDO requestDO) {
    LOGGER.debug("query - requestDO: {}", requestDO);
    return Mono.subscriberContext()
        .name("com.cjemison.spothero.controller.impl.timer.query")
        .subscribeOn(Schedulers.fromExecutor(executor))
        .subscriberContext(Context.of("requestDO", requestDO))
        .flatMap(rateService::query)
        .flatMap(responseDO -> Mono.<ResponseEntity<?>>just(ResponseEntity.ok(requestDO)))
        .metrics();
  }
}
