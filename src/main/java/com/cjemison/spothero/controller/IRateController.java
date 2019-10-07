package com.cjemison.spothero.controller;

import com.cjemison.spothero.domain.RateDO;
import com.cjemison.spothero.domain.RatesDO;
import com.cjemison.spothero.domain.RequestDO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IRateController {

  Flux<ResponseEntity<?>> findAll();

  Mono<ResponseEntity<?>> findOne(final String id);

  Flux<ResponseEntity<?>> create(final RatesDO ratesDO);

  Flux<ResponseEntity<?>> update(final String id,
      final RateDO rateDO);

  Mono<ResponseEntity<?>> delete(final String id);

  Mono<ResponseEntity<?>> query(final RequestDO requestDO);
}
