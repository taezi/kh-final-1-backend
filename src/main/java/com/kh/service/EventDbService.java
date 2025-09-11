// src/main/java/com/kh/service/EventDbService.java
package com.kh.service;

import com.kh.dto.CalendarResponse;
import com.kh.dto.EventDto;
import com.kh.dto.EventListResponse;
import com.kh.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventDbService {

    private final EventMapper mapper;

    @Transactional(readOnly = true)
    public EventListResponse listByDate(LocalDate date, String gu, String q, int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.max(size, 1);
        int offset = (p - 1) * s;

        String district = normDistrict(gu);
        String query = safe(q);

        var rows = mapper.selectPagedByDate(date, district, query, offset, s);
        int total = mapper.countByDate(date, district, query);
        boolean hasMore = offset + rows.size() < total;

        return EventListResponse.builder()
                .items(rows)
                .hasMore(hasMore)
                .build();
    }

    @Transactional(readOnly = true)
    public List<EventDto> featured(LocalDate from, LocalDate to, String gu, int limit) {
        return mapper.selectFeatured(from, to, normDistrict(gu), Math.max(limit, 1));
    }

    @Transactional(readOnly = true)
    public CalendarResponse monthDots(YearMonth ym, String gu, String q) {
        var start = ym.atDay(1);
        var end   = ym.atEndOfMonth();
        var list  = mapper.selectBetween(start, end, normDistrict(gu), safe(q));

        Map<String, Integer> map = new HashMap<>();
        for (var ev : list) {
            var s = ev.getStartDate().isBefore(start) ? start : ev.getStartDate();
            var e = ev.getEndDate().isAfter(end) ? end : ev.getEndDate();
            for (var d = s; !d.isAfter(e); d = d.plusDays(1)) {
                map.merge(d.toString(), 1, Integer::sum);
            }
        }
        return CalendarResponse.builder().eventsByDate(map).build();
    }

    // "서울시/서울특별시/공백" 정리 + 마지막에 '구' 붙이기
    private static String normDistrict(String raw) {
        if (raw == null) return "";
        String s = raw.replace("서울특별시", "")
                .replace("서울시", "")
                .replaceAll("\\s+", "")
                .trim();
        if (s.isEmpty()) return "";
        if (!s.endsWith("구")) s += "구";
        return s;
    }
    private static String safe(String s){ return s == null ? "" : s; }


    @Transactional(readOnly = true)
    public EventDto getOne(Long id) {
        return mapper.selectById(id);
    }



}