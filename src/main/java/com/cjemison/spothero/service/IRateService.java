package com.cjemison.spothero.service;

import com.cjemison.spothero.domain.RateDO;
import com.cjemison.spothero.domain.ResponseDO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public interface IRateService {

  Flux<RateDO> findAll();

  Mono<RateDO> findOne(final Context context);

  Mono<ResponseDO> query(final Context context);

  Flux<RateDO> create(final Context context);

  Flux<RateDO> update(final Context context);

  Mono<Boolean> delete(final Context context);
}
