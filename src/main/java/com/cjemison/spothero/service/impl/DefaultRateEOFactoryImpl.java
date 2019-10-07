package com.cjemison.spothero.service.impl;

import com.cjemison.spothero.domain.RateDO;
import com.cjemison.spothero.exception.InvalidRateDOException;
import com.cjemison.spothero.model.RateEO;
import com.cjemison.spothero.service.IRateEOFactory;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultRateEOFactoryImpl implements IRateEOFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRateEOFactoryImpl.class);
  private final Set<String> daysOfWeek;

  public DefaultRateEOFactoryImpl() {
    this.daysOfWeek = Set.of(new String[]{
        "mon",
        "tues",
        "wed",
        "thurs",
        "fri",
        "sat",
        "sun"
    });
  }

  @Override
  public Optional<RateEO> build(final RateDO rateDO) {
    LOGGER.debug("rateDO: {}", rateDO);
    if (rateDO != null) {
      validateDays(rateDO);
      final var times = validTime(rateDO);
      validateTimeZone(rateDO);
      validatePrice(rateDO);

      final RateEO rateEO = RateEO.builder()
          .id(UUID.randomUUID().toString())
          .createdEpoch(DateTime.now(DateTimeZone.UTC))
          .start(times.get(0))
          .end(times.get(1))
          .days(rateDO.getDays())
          .price(rateDO.getPrice())
          .timeZone(rateDO.getTimeZone())
          .build();
      return Optional.of(rateEO);
    }
    return Optional.empty();
  }

  private void validatePrice(final RateDO rateDO) {
    if (StringUtils.isBlank(rateDO.getPrice()) || !NumberUtils.isParsable(rateDO.getPrice())) {
      final var ex = new InvalidRateDOException("Invalid price");
      ex.setRateDO(rateDO);
      throw ex;
    }
  }

  private void validateTimeZone(final RateDO rateDO) {

    if (StringUtils.isBlank(rateDO.getTimeZone())) {
      final var ex = new InvalidRateDOException("Missing time zone.");
      ex.setRateDO(rateDO);
      throw ex;
    }

    // wrapped to produce right error
    try {
      final DateTimeZone dateTimeZone = DateTimeZone.forID(rateDO.getTimeZone());
      if (dateTimeZone == null) {
        final var ex = new InvalidRateDOException("Invalid time zone.");
        ex.setRateDO(rateDO);
        throw ex;
      }
    } catch (Exception e) {
      final var ex = new InvalidRateDOException(e.getMessage());
      ex.setRateDO(rateDO);
      throw ex;
    }
  }

  private List<String> validTime(final RateDO rateDO) {

    if (StringUtils.isBlank(rateDO.getTimes())) {
      final var ex = new InvalidRateDOException("times attribute is null or empty. ex. 0600-1800");
      ex.setRateDO(rateDO);
      throw ex;
    }

    final List<String> times = Splitter.on("-")
        .trimResults()
        .splitToList(rateDO.getTimes())
        .stream()
        .map(String::trim)
        .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(times) || times.size() != 2) {
      final var ex = new InvalidRateDOException("times attribute invalid. ex. 0600-1800");
      ex.setRateDO(rateDO);
      throw ex;
    }

    // wrapped to produce right error
    try {
      final var start = LocalTime.parse(times.get(0), DateTimeFormat.forPattern("HHmm"));
      final var end = LocalTime.parse(times.get(1), DateTimeFormat.forPattern("HHmm"));

      if (end.isBefore(start)) {
        throw new RuntimeException(String.format("%s is before %s", end.toString(),
            start.toString()));
      }
    } catch (Exception e) {
      final var ex = new InvalidRateDOException(e.getMessage() + " ex. 0600-1800");
      ex.setRateDO(rateDO);
      throw ex;
    }

    return times;
  }

  private void validateDays(final RateDO rateDO) {
    if (StringUtils.isBlank(rateDO.getDays())) {
      final var ex = new InvalidRateDOException("days attribute is null or empty. ex, fri,sat,sun");
      ex.setRateDO(rateDO);
      throw ex;
    }

    final List<String> days = Splitter.on(",")
        .trimResults()
        .splitToList(rateDO.getDays())
        .stream()
        .map(String::toLowerCase)
        .map(String::trim)
        .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(days)) {
      final var ex = new InvalidRateDOException("days attribute is null or empty. ex. fri,sat,sun");
      ex.setRateDO(rateDO);
      throw ex;
    }

    days.forEach(s -> {
      if (!daysOfWeek.contains(s.trim())) {
        final var ex = new InvalidRateDOException(
            String.format("Invalid day of the week: %s ex. %s",
                s, Joiner.on(",").join(daysOfWeek)));
        ex.setRateDO(rateDO);
        throw ex;
      }
    });

  }
}
