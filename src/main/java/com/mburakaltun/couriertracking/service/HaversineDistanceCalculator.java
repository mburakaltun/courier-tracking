package com.mburakaltun.couriertracking.service;

import com.mburakaltun.couriertracking.common.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HaversineDistanceCalculator implements DistanceCalculator {

    private final AppProperties appProperties;

    @Override
    public double calculateDistanceInMeters(Double fromLatitude, Double fromLongitude, Double toLatitude, Double toLongitude) {
        double latitudeDistance = Math.toRadians(toLatitude - fromLatitude);
        double longitudeDistance = Math.toRadians(toLongitude - fromLongitude);
        double a = Math.sin(latitudeDistance / 2) * Math.sin(latitudeDistance / 2) +
                Math.cos(Math.toRadians(fromLatitude)) * Math.cos(Math.toRadians(toLatitude)) *
                        Math.sin(longitudeDistance / 2) * Math.sin(longitudeDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return appProperties.getEarthRadiusInKilometers() * c * 1000;
    }
}
