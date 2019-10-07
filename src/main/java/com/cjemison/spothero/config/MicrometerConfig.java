package com.cjemison.spothero.config;

import static io.micrometer.core.instrument.Meter.Type.TIMER;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MicrometerConfig {

  @Bean
  public MeterRegistry meterRegistry() {
    return new CompositeMeterRegistry();
  }

  @Bean
  public MeterFilter histograms() {
    return new MeterFilter() {
      @Override
      public DistributionStatisticConfig configure(Meter.Id id,
          DistributionStatisticConfig config) {
        if (id.getType() == TIMER) {
          return DistributionStatisticConfig.builder()
              .percentiles(0.9, 0.95, 0.99)
              .build()
              .merge(config);
        }
        return config;
      }
    };
  }

  @Bean
  public MeterFilter tags(final @Autowired Environment env) {
    final String[] profiles = env.getActiveProfiles();
    String profile = "none";
    if (profiles.length > 0) {
      profile = profiles[0];
    }
    return MeterFilter.commonTags(Arrays.asList(
        Tag.of("component", "spot-hero-app"),
        Tag.of("environment", profile),
        Tag.of("version", "1.0")
    ));
  }
}
