package com.mburakaltun.couriertracking.controller;

import com.mburakaltun.couriertracking.model.request.RequestCourierLocation;
import com.mburakaltun.couriertracking.model.request.RequestCourierTotalDistance;
import com.mburakaltun.couriertracking.model.response.ResponseCourierLocation;
import com.mburakaltun.couriertracking.model.response.ResponseCourierTotalDistance;
import com.mburakaltun.couriertracking.service.CourierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/couriers")
public class CourierController {

    private final CourierService courierService;

    @PostMapping("/log-courier-location")
    public ResponseEntity<ResponseCourierLocation> logCourierLocation(@RequestBody @Valid RequestCourierLocation requestCourierLocation) {
        ResponseCourierLocation responseCourierLocation = courierService.logCourierLocation(requestCourierLocation);
        return new ResponseEntity<>(responseCourierLocation, HttpStatus.CREATED);
    }

    @GetMapping("/calculate-total-distance")
    public ResponseEntity<ResponseCourierTotalDistance> calculateTotalDistance(@RequestBody @Valid RequestCourierTotalDistance requestCourierTotalDistance) {
        ResponseCourierTotalDistance responseCourierTotalDistance = courierService.calculateTotalDistance(requestCourierTotalDistance);
        return new ResponseEntity<>(responseCourierTotalDistance, HttpStatus.OK);
    }
}
