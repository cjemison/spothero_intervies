package com.cjemison.spothero.exception;

import com.cjemison.spothero.domain.RateDO;

public class InvalidRateDOException extends RuntimeException {

  private RateDO rateDO;

  public RateDO getRateDO() {
    return rateDO;
  }

  public void setRateDO(final RateDO rateDO) {
    this.rateDO = rateDO;
  }

  public InvalidRateDOException(final String message) {
    super(message);
  }
}
