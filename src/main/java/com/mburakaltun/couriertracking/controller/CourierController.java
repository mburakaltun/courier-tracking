package com.mburakaltun.couriertracking.controller;

import com.mburakaltun.couriertracking.model.request.RequestLogCourierLocation;
import com.mburakaltun.couriertracking.model.request.RequestQueryCourierTotalDistance;
import com.mburakaltun.couriertracking.model.response.ResponseLogCourierLocation;
import com.mburakaltun.couriertracking.model.response.ResponseQueryCourierTotalDistance;
import com.mburakaltun.couriertracking.service.CourierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/couriers")
public class CourierController {

    private final CourierService courierService;

    @PostMapping("/log-courier-location")
    public ResponseEntity<ResponseLogCourierLocation> logCourierLocation(@RequestBody @Valid RequestLogCourierLocation requestLogCourierLocation) {
        ResponseLogCourierLocation responseLogCourierLocation = courierService.logCourierLocation(requestLogCourierLocation);
        return new ResponseEntity<>(responseLogCourierLocation, HttpStatus.CREATED);
    }

    @GetMapping("/query-total-distance")
    public ResponseEntity<ResponseQueryCourierTotalDistance> queryTotalDistance(@ModelAttribute @Valid RequestQueryCourierTotalDistance requestQueryCourierTotalDistance) {
        ResponseQueryCourierTotalDistance responseQueryCourierTotalDistance = courierService.queryTotalDistance(requestQueryCourierTotalDistance);
        return new ResponseEntity<>(responseQueryCourierTotalDistance, HttpStatus.OK);
    }
}
