package com.cjemison.spothero.service.impl;

import com.cjemison.spothero.domain.RateDO;
import com.cjemison.spothero.domain.RatesDO;
import com.cjemison.spothero.domain.RequestDO;
import com.cjemison.spothero.domain.ResponseDO;
import com.cjemison.spothero.model.RateEO;
import com.cjemison.spothero.service.IRateEOFactory;
import com.cjemison.spothero.service.IRateService;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Service
public class DefaultRateServiceImpl implements IRateService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRateServiceImpl.class);
  private final DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
  private final ConcurrentLinkedQueue<RateEO> rateEOList;
  private final IRateEOFactory iRateEOFactory;

  @Autowired
  public DefaultRateServiceImpl(final ConcurrentLinkedQueue<RateEO> queue,
      final IRateEOFactory iRateEOFactory) {
    this.rateEOList = queue;
    this.iRateEOFactory = iRateEOFactory;
  }

  @Override
  public Flux<RateDO> findAll() {
    return Flux.fromIterable(rateEOList)
        .flatMap(this::toDomain);
  }

  @Override
  public Mono<RateDO> findOne(final Context context) {
    LOGGER.debug("context: {}", context);
    return Flux.just(context)
        .flatMap(c -> {
          final String id = c.get("id");
          return Flux.fromIterable(rateEOList)
              .filter(rateEO -> rateEO.getId().equals(id));
        }).collectList()
        .filter(CollectionUtils::isNotEmpty)
        .flatMap(rateEOS -> Mono.just(rateEOS.get(0)).flatMap(this::toDomain));
  }

  @Override
  public Mono<ResponseDO> query(final Context context) {
    LOGGER.debug("context: {}", context);
    final RequestDO requestDO = context.get("requestDO");
    return Mono.just(context)
        .flatMap(c -> {
          final var start = parser.parseDateTime(requestDO.getStartTime());
          final var end = parser.parseDateTime(requestDO.getEndTime());

          return Flux.fromIterable(rateEOList)
              .filter(rateEO -> rateEO.getDays()
                  .contains(getShortNameForDay(start.dayOfWeek().getAsText())))
              .filter(rateEO -> {
                final var startTime = LocalTime
                    .parse(rateEO.getStart(), DateTimeFormat.forPattern("HHmm"));

                final var endTime = LocalTime
                    .parse(rateEO.getEnd(), DateTimeFormat.forPattern("HHmm"));

                final var dateTimeZone = DateTimeZone.forID(rateEO.getTimeZone());

                final var currentTimeZoneDate = start.withZone(dateTimeZone);

                final var startDateTime = new DateTime(currentTimeZoneDate.getYear(),
                    currentTimeZoneDate.getMonthOfYear(),
                    currentTimeZoneDate.getDayOfMonth(),
                    startTime.getHourOfDay(),
                    startTime.getMinuteOfHour(),
                    DateTimeZone.forID(rateEO.getTimeZone()));

                final var endDateTime = new DateTime(currentTimeZoneDate.getYear(),
                    currentTimeZoneDate.getMonthOfYear(),
                    currentTimeZoneDate.getDayOfMonth(),
                    endTime.getHourOfDay(),
                    endTime.getMinuteOfHour(),
                    DateTimeZone.forID(rateEO.getTimeZone())).plusMillis(500);

                final Interval interval = new Interval(startDateTime, endDateTime);
                return interval.contains(start) && interval.contains(end);
              }).collectList();
        }).flatMap(rateEOS -> {
          if (CollectionUtils.isNotEmpty(rateEOS)) {
            return Mono.just(ResponseDO.builder()
                .startTime(requestDO.getStartTime())
                .endTime(requestDO.getEndTime())
                .price(rateEOS.get(0).getPrice())
                .build());
          }
          return Mono.just(ResponseDO.builder()
              .startTime(requestDO.getStartTime())
              .endTime(requestDO.getEndTime())
              .price("unavailable")
              .build());
        });
  }

  @Override
  public Flux<RateDO> create(final Context context) {
    LOGGER.debug("context: {}", context);
    return Flux.just(context)
        .flatMap(c -> {
          final RatesDO ratesDO = c.get("ratesDO");
          return Flux.fromIterable(ratesDO.getRates())
              .flatMap(rateDO -> {
                final RateEO rateEO = iRateEOFactory.build(rateDO)
                    .orElseThrow(() -> new RuntimeException("Couldn't generate RateEO"));
                if (!rateEOList.contains(rateEO)) {
                  rateEOList.add(rateEO);
                }
                return Mono.just(rateEO);
              });
        })
        .flatMap(this::toDomain);
  }

  @Override
  public Flux<RateDO> update(final Context context) {
    LOGGER.debug("context: {}", context);
    return Flux.just(context)
        .flatMap(c -> {
          final String id = c.get("id");
          final RateDO rateDO = c.get("rateDO");
          return Flux.fromIterable(rateEOList)
              .filter(rateEO -> rateEO.getId().equals(id.trim()))
              .flatMap(rateEO -> {
                final RateEO newRateEO = iRateEOFactory.build(rateDO)
                    .orElseThrow(() -> new RuntimeException("Couldn't generate RateEO"));
                rateEOList.remove(rateEO);
                rateEOList.add(newRateEO);
                return Mono.just(rateEO);
              });
        })
        .flatMap(this::toDomain);
  }

  @Override
  public Mono<Boolean> delete(final Context context) {
    LOGGER.debug("context: {}", context);
    return Flux.just(context)
        .flatMap(c -> {
          final String id = c.get("id");
          return Flux.fromIterable(rateEOList)
              .filter(rateEO -> rateEO.getId().equals(id));
        }).collectList()
        .flatMap(rateEOS -> {
          if (CollectionUtils.isNotEmpty(rateEOS)) {
            rateEOS.removeAll(rateEOS);
            return Mono.just(true);
          }
          return Mono.just(false);
        });
  }

  private Mono<RateDO> toDomain(final RateEO rateEO) {
    return Mono.just(RateDO.builder()
        .id(rateEO.getId())
        .days(rateEO.getDays())
        .times(String.format("%s-%s", rateEO.getStart(), rateEO.getEnd()))
        .tz(rateEO.getTimeZone())
        .price(rateEO.getPrice())
        .build());
  }

  private String getShortNameForDay(final String day) {
    switch (day) {
      case "Tuesday":
        return "tues";
      case "Thursday":
        return "thurs";
      default:
        return day.toLowerCase().substring(0, 3);
    }
  }
}
