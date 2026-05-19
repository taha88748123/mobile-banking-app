package com.banking.controller;

import com.banking.dto.StatisticsResponse;
import com.banking.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(originPatterns = "*")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<StatisticsResponse> get(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(statisticsService.compute(email));
    }
}
