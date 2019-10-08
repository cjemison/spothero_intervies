package com.cjemison.spothero.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = RateDO.Builder.class)
public class RateDO implements IResponse {

  private final String id;
  private final String days;
  private final String times;
  private final String timeZone;
  private final String price;
  private final String error;

  protected RateDO(final String id,
      final String days,
      final String times,
      final String timeZone,
      final String price,
      final String error) {
    this.id = id;
    this.days = days;
    this.times = times;
    this.timeZone = timeZone;
    this.price = price;
    this.error = error;
  }

  public static RateDO.Builder builder() {
    return new RateDO.Builder();
  }

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("days")
  public String getDays() {
    return days;
  }

  @JsonProperty("times")
  public String getTimes() {
    return times;
  }

  @JsonProperty("tz")
  public String getTimeZone() {
    return timeZone;
  }

  @JsonProperty("price")
  public String getPrice() {
    return price;
  }

  @JsonProperty("error")
  public String getError() {
    return error;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RateDO)) {
      return false;
    }
    final RateDO rateDO = (RateDO) o;
    return Objects.equals(getId(), rateDO.getId()) &&
        Objects.equals(getDays(), rateDO.getDays()) &&
        Objects.equals(getTimes(), rateDO.getTimes()) &&
        Objects.equals(getTimeZone(), rateDO.getTimeZone()) &&
        Objects.equals(getPrice(), rateDO.getPrice());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getDays(), getTimes(), getTimeZone(), getPrice());
  }

  @Override
  public String toString() {
    return "RateDO{" +
        "id='" + id + '\'' +
        ", days='" + days + '\'' +
        ", times='" + times + '\'' +
        ", timeZone='" + timeZone + '\'' +
        ", price='" + price + '\'' +
        ", error='" + error + '\'' +
        '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Builder {

    private String id;
    private String days;
    private String times;
    private String timeZone;
    private String price;
    private String error;

    public Builder() {
      this.id = StringUtils.EMPTY;
      this.days = StringUtils.EMPTY;
      this.times = StringUtils.EMPTY;
      this.timeZone = StringUtils.EMPTY;
      this.price = StringUtils.EMPTY;
      this.error = null;
    }

    @JsonProperty("id")
    public Builder id(final String value) {
      this.id = defaultValue(value);
      return this;
    }

    @JsonProperty("days")
    public Builder days(final String value) {
      this.days = defaultValue(value);
      return this;
    }

    @JsonProperty("times")
    public Builder times(final String value) {
      this.times = defaultValue(value);
      return this;
    }

    @JsonProperty("tz")
    public Builder tz(final String value) {
      this.timeZone = defaultValue(value);
      return this;
    }

    @JsonProperty("price")
    public Builder price(final String value) {
      this.price = defaultValue(value);
      return this;
    }

    @JsonProperty("error")
    public Builder error(final String value) {
      this.error = defaultValue(value);
      return this;
    }

    public RateDO build() {
      return new RateDO(id,
          days,
          times,
          timeZone,
          price,
          error);
    }

    private String defaultValue(final String value) {
      if (StringUtils.isNotBlank(value)) {
        return value.trim();
      }
      return StringUtils.EMPTY;
    }
  }
}