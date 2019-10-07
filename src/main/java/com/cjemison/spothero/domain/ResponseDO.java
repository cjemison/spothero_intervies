package com.cjemison.spothero.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = ResponseDO.Builder.class)
public class ResponseDO {

  private final String startTime;
  private final String endTime;
  private final String price;

  protected ResponseDO(final String startTime,
      final String endTime,
      final String price) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.price = price;
  }

  public static ResponseDO.Builder builder() {
    return new ResponseDO.Builder();
  }

  @JsonProperty("startTime")
  public String getStartTime() {
    return startTime;
  }

  @JsonProperty("endTime")
  public String getEndTime() {
    return endTime;
  }

  @JsonProperty("price")
  public String getPrice() {
    return price;
  }

  private String dateToIso(final DateTime val) {
    if (val != null) {
      final DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
      return fmt.print(val);
    }
    return StringUtils.EMPTY;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ResponseDO)) {
      return false;
    }
    final ResponseDO that = (ResponseDO) o;
    return Objects.equals(getStartTime(), that.getStartTime()) &&
        Objects.equals(getEndTime(), that.getEndTime()) &&
        Objects.equals(getPrice(), that.getPrice());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getStartTime(), getEndTime(), getPrice());
  }

  @Override
  public String toString() {
    return "ResponseDO{" +
        "startTime=" + startTime +
        ", endTime=" + endTime +
        ", price=" + price +
        '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Builder {

    private String startTime;
    private String endTime;
    private String price;

    public Builder() {
      this.startTime = StringUtils.EMPTY;
      this.endTime = StringUtils.EMPTY;
      this.price = StringUtils.EMPTY;
    }

    @JsonProperty("startTime")
    public Builder startTime(final String value) {
      this.startTime = defaultValue(value);
      return this;
    }

    @JsonProperty("endTime")
    public Builder endTime(final String value) {
      this.endTime = defaultValue(value);
      return this;
    }

    @JsonProperty("price")
    public Builder price(final String value) {
      this.price = defaultValue(value);
      return this;
    }

    public ResponseDO build() {
      return new ResponseDO(startTime,
          endTime,
          price);
    }

    private String defaultValue(final String value) {
      if (StringUtils.isNotBlank(value)) {
        return value.trim();
      }
      return StringUtils.EMPTY;
    }
  }
}
