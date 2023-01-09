package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import searchengine.response.StatisticResponseService;
import searchengine.services.StatisticService;


@Controller
@RequestMapping("/api")
public class StatisticController {

    private final StatisticService statisticsService;

    public StatisticController(StatisticService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<Object> getStatistics() {
        StatisticResponseService stat = statisticsService.getStatistic();
        return ResponseEntity.ok(stat);
    }
}
