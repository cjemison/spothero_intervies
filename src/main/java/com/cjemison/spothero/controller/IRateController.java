package com.cjemison.spothero.controller;

import com.cjemison.spothero.domain.RateDO;
import com.cjemison.spothero.domain.RatesDO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IRateController {

  Mono<ResponseEntity<?>> findAll();

  Mono<ResponseEntity<?>> findOne(final String id);

  Mono<ResponseEntity<?>> create(final RatesDO ratesDO);

  Mono<ResponseEntity<?>> update(final String id,
      final RateDO rateDO);

  Mono<ResponseEntity<?>> delete(final String id);
}
