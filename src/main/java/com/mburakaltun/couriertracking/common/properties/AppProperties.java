package com.mburakaltun.couriertracking.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private int earthRadiusInKilometers;
    private int distanceThresholdInMeters;
    private int timeThresholdInSeconds;
}
