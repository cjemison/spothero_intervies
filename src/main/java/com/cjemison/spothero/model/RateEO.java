package com.cjemison.spothero.model;


import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class RateEO {

  private final String id;
  private final String days;
  private final String start;
  private final String end;
  private final String timeZone;
  private final String price;
  private final DateTime createdEpoch;

  public RateEO(final String id,
      final String days,
      final String start,
      final String end,
      final String timeZone,
      final String price,
      final DateTime createdEpoch) {
    this.id = id;
    this.days = days;
    this.start = start;
    this.end = end;
    this.timeZone = timeZone;
    this.price = price;
    this.createdEpoch = createdEpoch;
  }

  public static Builder builder() {
    return new RateEO.Builder();
  }

  public String getId() {
    return id;
  }

  public String getDays() {
    return days;
  }

  public String getStart() {
    return start;
  }

  public String getEnd() {
    return end;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public String getPrice() {
    return price;
  }

  public DateTime getCreatedEpoch() {
    return createdEpoch;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RateEO)) {
      return false;
    }
    final RateEO rateEO = (RateEO) o;
    return Objects.equals(getDays(), rateEO.getDays()) &&
        Objects.equals(getStart(), rateEO.getStart()) &&
        Objects.equals(getEnd(), rateEO.getEnd()) &&
        Objects.equals(getTimeZone(), rateEO.getTimeZone()) &&
        Objects.equals(getPrice(), rateEO.getPrice());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getDays(), getStart(), getEnd(), getTimeZone(), getPrice());
  }

  @Override
  public String toString() {
    return "RateEO{" +
        "id='" + id + '\'' +
        ", day='" + days + '\'' +
        ", start='" + start + '\'' +
        ", end='" + end + '\'' +
        ", timeZone='" + timeZone + '\'' +
        ", price='" + price + '\'' +
        ", createdEpoch=" + createdEpoch +
        '}';
  }

  public static class Builder {

    private String id;
    private String days;
    private String start;
    private String end;
    private String timeZone;
    private String price;
    private DateTime createdEpoch;

    public Builder() {
      this.id = StringUtils.EMPTY;
      this.days = StringUtils.EMPTY;
      this.start = StringUtils.EMPTY;
      this.end = StringUtils.EMPTY;
      this.timeZone = StringUtils.EMPTY;
      this.price = StringUtils.EMPTY;
      this.createdEpoch = DateTime.now(DateTimeZone.UTC);
    }

    public Builder id(final String value) {
      this.id = defaultValue(value);
      return this;
    }

    public Builder days(final String value) {
      this.days = defaultValue(value);
      return this;
    }

    public Builder start(final String value) {
      this.start = defaultValue(value);
      return this;
    }

    public Builder end(final String value) {
      this.end = defaultValue(value);
      return this;
    }

    public Builder timeZone(final String value) {
      this.timeZone = defaultValue(value);
      return this;
    }

    public Builder price(final String value) {
      this.price = defaultValue(value);
      return this;
    }

    public Builder createdEpoch(final DateTime value) {
      if (value != null) {
        this.createdEpoch = value;
      }
      return this;
    }

    public RateEO build() {
      return new RateEO(id,
          days,
          start,
          end,
          timeZone,
          price,
          createdEpoch);
    }

    private String defaultValue(final String value) {
      if (StringUtils.isNotBlank(value)) {
        return value.trim();
      }
      return StringUtils.EMPTY;
    }
  }
}
