package com.cjemison.spothero.service;

import com.cjemison.spothero.domain.RateDO;
import com.cjemison.spothero.model.RateEO;
import java.util.Optional;

public interface IRateEOFactory {

  Optional<RateEO> build(final RateDO rateDO);
}
