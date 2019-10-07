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
@JsonDeserialize(builder = RequestDO.Builder.class)
public class RequestDO {

  private final String startTime;
  private final String endTime;

  protected RequestDO(final String startTime,
      final String endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public static RequestDO.Builder builder() {
    return new RequestDO.Builder();
  }

  @JsonProperty("startTime")
  public String getStartTime() {
    return startTime;
  }

  @JsonProperty("endTime")
  public String getEndTime() {
    return endTime;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RequestDO)) {
      return false;
    }
    final RequestDO requestDO = (RequestDO) o;
    return Objects.equals(getStartTime(), requestDO.getStartTime()) &&
        Objects.equals(getEndTime(), requestDO.getEndTime());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getStartTime(), getEndTime());
  }

  @Override
  public String toString() {
    return "RequestDO{" +
        "startTime=" + startTime +
        ", endTime=" + endTime +
        '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Builder {

    private String startTime;
    private String endTime;

    public Builder() {
      this.startTime = StringUtils.EMPTY;
      this.endTime = StringUtils.EMPTY;
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

    public RequestDO build() {
      return new RequestDO(startTime, endTime);
    }

    private String defaultValue(final String value) {
      if (StringUtils.isNotBlank(value)) {
        return value.trim();
      }
      return StringUtils.EMPTY;
    }
  }
}
