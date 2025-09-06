package com.kh.controller;

import com.kh.dto.CalendarResponse;
import com.kh.dto.EventDto;
import com.kh.dto.EventListResponse;
import com.kh.service.EventDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place")
@CrossOrigin(origins = "*")
public class PlaceController {

    private final EventDbService svc;

    // ex) /api/place/events/list?date=2025-08-21&gu=&q=&page=1&size=12
    @GetMapping(value = "/events/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public EventListResponse list(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "") String gu,
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "12") int size
    ) {
        return svc.listByDate(date, gu, q, page, size);
    }

    // ex) /api/place/events/featured?from=2025-08-01&to=2025-08-31&gu=&limit=4
    @GetMapping(value = "/events/featured", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventDto> featured(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "") String gu,
            @RequestParam(required = false, defaultValue = "4") int limit
    ) {
        return svc.featured(from, to, gu, limit);
    }

    // ex) /api/place/events/calendar?month=2025-09&gu=&q=
    @GetMapping(value = "/events/calendar", produces = MediaType.APPLICATION_JSON_VALUE)
    public CalendarResponse calendar(
            @RequestParam String month,
            @RequestParam(required = false, defaultValue = "") String gu,
            @RequestParam(required = false, defaultValue = "") String q
    ) {
        YearMonth ym = YearMonth.parse(month);
        return svc.monthDots(ym, gu, q);
    }

    /* ======= 진단용 ======= */
    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> health() {
        Map<String, Object> m = new HashMap<>();
        m.put("ok", true);
        m.put("module", "place");
        return m;
    }
    @GetMapping(value = "/events/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EventDto detail(@PathVariable Long id) {
        var row = svc.getOne(id);
        if (row == null) {
            // 404로 처리하려면:
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "event not found");
        }
        System.out.println("ssssssssssssssssssss"+ row);
        System.out.println("id=" + row.getDescription()
                + ", desc.len=" + (row.getDescription() == null ? 0 : row.getDescription().length()));


        return row;
    }


}
