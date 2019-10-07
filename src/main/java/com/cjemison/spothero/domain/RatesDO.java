package com.cjemison.spothero.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = RatesDO.Builder.class)
public class RatesDO {

  private final List<RateDO> rates;

  protected RatesDO(final List<RateDO> rates) {
    this.rates = rates;
  }

  public static RatesDO.Builder builder() {
    return new RatesDO.Builder();
  }


  @JsonProperty("rates")
  public List<RateDO> getRates() {
    return rates;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RatesDO)) {
      return false;
    }
    final RatesDO ratesDO = (RatesDO) o;
    return Objects.equals(getRates(), ratesDO.getRates());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRates());
  }

  @Override
  public String toString() {
    return "RatesDO{" +
        "rates=" + rates +
        '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Builder {

    private List<RateDO> rates;

    public Builder() {
      this.rates = new LinkedList<>();
    }

    @JsonProperty("rates")
    public Builder rates(final List<RateDO> value) {
      if (CollectionUtils.isNotEmpty(value)) {
        this.rates = value;
      }
      return this;
    }

    public Builder rates(final RateDO value) {
      if (value != null) {
        this.rates.add(value);
      }
      return this;
    }

    public RatesDO build() {
      return new RatesDO(rates);
    }
  }
}
