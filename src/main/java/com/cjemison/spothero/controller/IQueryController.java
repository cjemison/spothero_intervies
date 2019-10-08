package com.cjemison.spothero.controller;

import com.cjemison.spothero.domain.RequestDO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IQueryController {

  Mono<ResponseEntity<?>> query(final RequestDO requestDO);
}
