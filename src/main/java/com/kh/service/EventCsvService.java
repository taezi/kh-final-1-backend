// src/main/java/com/kh/service/EventCsvService.java
package com.kh.service;

import com.kh.dto.CalendarResponse;
import com.kh.dto.EventDto;
import com.kh.dto.EventListResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class EventCsvService {

    private static final String CSV_PATH = "data/culture.csv"; // ← 파일명/경로 고정
    private static final Pattern CSV_SPLIT =
            Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

    private final List<EventDto> store = new ArrayList<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @PostConstruct
    public void loadCsv() {
        store.clear();
        // 1차 UTF-8, 0건이면 MS949로 재시도
        int n = loadCsvWithCharset(StandardCharsets.UTF_8);
        if (n == 0) loadCsvWithCharset(Charset.forName("MS949"));

        System.out.println("[EventCsvService] loaded rows: " + store.size());
        store.stream().limit(3).forEach(ev ->
                System.out.printf("  - %s | %s | %s~%s%n",
                        ev.getGu(), ev.getTitle(), ev.getDateStart(), ev.getDateEnd()));
    }

    private int loadCsvWithCharset(Charset cs) {
        try {
            var res = new ClassPathResource(CSV_PATH);
            if (!res.exists()) {
                System.err.println("[EventCsvService] CSV not found: " + CSV_PATH);
                return 0;
            }
            try (var br = new BufferedReader(new InputStreamReader(res.getInputStream(), cs))) {
                String headerLine = br.readLine();
                if (headerLine == null) return 0;

                String[] headers = CSV_SPLIT.split(headerLine, -1);
                Map<String, Integer> idx = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    idx.put(headers[i].trim().toLowerCase(), i);
                }
                System.out.println("[EventCsvService] header(" + cs + "): " +
                        Arrays.stream(headers).map(String::trim).collect(Collectors.joining(" | ")));

                String line;
                while ((line = br.readLine()) != null) {
                    if (line.isBlank()) continue;
                    String[] cols = CSV_SPLIT.split(line, -1);
                    for (int i = 0; i < cols.length; i++) {
                        String c = cols[i];
                        if (c != null) c = c.trim();
                        if (c != null && c.startsWith("\"") && c.endsWith("\"") && c.length() >= 2) {
                            c = c.substring(1, c.length() - 1);
                        }
                        cols[i] = c == null ? "" : c;
                    }

                    String title = val(cols, idx, "culutename", "title", "name");
                    if (isBlank(title)) continue;

                    String rawGu = val(cols, idx, "district", "gu", "region");
                    String gu = normalizeGu(rawGu);

                    String address = val(cols, idx, "cultureaddress", "address");
                    String category = val(cols, idx, "category", "type");
                    String thumb = val(cols, idx, "thumbnailimage", "image", "thumb", "thumbnail");
                    if (isBlank(thumb)) {
                        thumb = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?q=80&w=1200&auto=format&fit=crop";
                    }

                    LocalDate ds = parseDate(val(cols, idx, "startdate", "start_date", "sdate", "datestart", "from"));
                    LocalDate de = parseDate(val(cols, idx, "enddate", "end_date", "edate", "dateend", "to"));
                    if (ds == null && de != null) ds = de;
                    if (de == null && ds != null) de = ds;
                    if (ds == null || de == null) continue; // 날짜 없으면 스킵

                    String fee  = val(cols, idx, "fee", "price");
                    String aud  = val(cols, idx, "targetaudience", "audience", "target");
                    String host = val(cols, idx, "organizationname", "host", "organizer");
                    String url  = val(cols, idx, "portalurl", "url", "link");
                    String desc = val(cols, idx, "description", "desc");
                    Boolean isFree = toFree(val(cols, idx, "isfree", "free"));

                    store.add(EventDto.builder()
                            .id(idGen.getAndIncrement())
                            .title(title)
                            .place(address)      // 장소/주소 동일 열 사용
                            .gu(gu)
                            .category(category)
                            .dateStart(ds)
                            .dateEnd(de)
                            .time("")
                            .thumbUrl(thumb)
                            .fee(fee)
                            .audience(aud)
                            .host(host)
                            .address(address)
                            .url(url)
                            .description(desc)
                            .isFree(isFree)
                            .build());
                }
                return store.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return store.size();
        }
    }

    /* ================== 공개 API ================== */

    public EventListResponse listByDate(LocalDate date, String gu, String q, int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.max(size, 1);
        int offset = (p - 1) * s;
        String guNorm = normalizeGu(gu);

        var filtered = store.stream()
                .filter(ev -> !date.isBefore(ev.getDateStart()) && !date.isAfter(ev.getDateEnd()))
                .filter(ev -> isBlank(guNorm) || guNorm.equals(ev.getGu()))
                .filter(ev -> {
                    if (isBlank(q)) return true;
                    String t = (safe(ev.getTitle()) + " " + safe(ev.getPlace())).toLowerCase();
                    return t.contains(q.toLowerCase());
                })
                .sorted(Comparator
                        .comparing(EventDto::getDateStart)
                        .thenComparing(e -> safe(e.getTitle())))
                .toList();

        var pageItems = filtered.stream().skip(offset).limit(s).toList();
        boolean hasMore = offset + pageItems.size() < filtered.size();
        return EventListResponse.builder().items(pageItems).hasMore(hasMore).build();
    }

    public List<EventDto> featured(LocalDate from, LocalDate to, String gu, int limit) {
        String guNorm = normalizeGu(gu);
        return store.stream()
                .filter(ev -> !ev.getDateStart().isAfter(to) && !ev.getDateEnd().isBefore(from))
                .filter(ev -> isBlank(guNorm) || guNorm.equals(ev.getGu()))
                .sorted(Comparator
                        .comparing((EventDto e) -> isBlank(e.getThumbUrl()))
                        .thenComparing(EventDto::getDateStart))
                .limit(Math.max(limit, 1))
                .toList();
    }

    public CalendarResponse monthDots(YearMonth ym, String gu, String q) {
        LocalDate start = ym.atDay(1);
        LocalDate end   = ym.atEndOfMonth();
        String guNorm = normalizeGu(gu);

        Map<String,Integer> map = new HashMap<>();
        store.stream()
                .filter(ev -> !ev.getDateStart().isAfter(end) && !ev.getDateEnd().isBefore(start))
                .filter(ev -> isBlank(guNorm) || guNorm.equals(ev.getGu()))
                .filter(ev -> {
                    if (isBlank(q)) return true;
                    String t = (safe(ev.getTitle()) + " " + safe(ev.getPlace())).toLowerCase();
                    return t.contains(q.toLowerCase());
                })
                .forEach(ev -> {
                    LocalDate s = ev.getDateStart().isBefore(start) ? start : ev.getDateStart();
                    LocalDate e = ev.getDateEnd().isAfter(end) ? end : ev.getDateEnd();
                    for (LocalDate d = s; !d.isAfter(e); d = d.plusDays(1)) {
                        map.merge(d.toString(), 1, Integer::sum);
                    }
                });

        return CalendarResponse.builder().eventsByDate(map).build();
    }

    /* ================== 유틸 ================== */

    private static String val(String[] cols, Map<String,Integer> idx, String... keys) {
        for (String k : keys) {
            Integer i = idx.get(k.toLowerCase());
            if (i != null && i >= 0 && i < cols.length) {
                String v = cols[i];
                if (v != null && !v.isBlank()) return v;
            }
        }
        return "";
    }
    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
    private static String safe(String s) { return s == null ? "" : s; }

    // "강남", "강남 구", "서울특별시 강남구" → "강남구"
    private static String normalizeGu(String raw) {
        if (raw == null) return "";
        String s = raw.replace("서울특별시", "")
                .replace("서울시", "")
                .replaceAll("\\s+", "")
                .trim();
        if (s.isEmpty()) return "";
        if (!s.endsWith("구")) s += "구";
        return s;
    }

    // 여러 포맷 지원: "2025-08-21", "2025. 08. 21", "2025.8.21", "20250821", "2025년8월21일"
    private static LocalDate parseDate(String s) {
        if (isBlank(s)) return null;
        String t = s.trim();

        Matcher m1 = Pattern.compile("(?<y>\\d{4})-(?<m>\\d{1,2})-(?<d>\\d{1,2})").matcher(t);
        if (m1.find()) return dateOf(m1.group("y"), m1.group("m"), m1.group("d"));

        String nospace = t.replace(" ", "");
        Matcher m2 = Pattern.compile("(?<y>\\d{4})[.\\-/년]?(?<m>\\d{1,2})[.\\-/월]?(?<d>\\d{1,2})").matcher(nospace);
        if (m2.find()) return dateOf(m2.group("y"), m2.group("m"), m2.group("d"));

        Matcher m3 = Pattern.compile("^(?<y>\\d{4})(?<m>\\d{2})(?<d>\\d{2})$").matcher(t);
        if (m3.find()) return dateOf(m3.group("y"), m3.group("m"), m3.group("d"));

        return null;
    }
    private static LocalDate dateOf(String y, String m, String d) {
        try { return LocalDate.of(Integer.parseInt(y), Integer.parseInt(m), Integer.parseInt(d)); }
        catch (Exception e) { return null; }
    }

    private static Boolean toFree(String v) {
        if (v == null) return null;
        String t = v.trim();
        if (t.isEmpty()) return null;
        t = t.replaceAll("\\s+", "");
        return switch (t) {
            case "무료","Y","y","YES","Yes","true","TRUE","1","예","가능" -> true;
            case "유료","N","n","NO","No","false","FALSE","0","아니오" -> false;
            default -> null;
        };
    }

    /* ========== optional: 상태 확인/리로드 ========== */
    public Map<String, Object> debugSnapshot() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("count", store.size());
        LocalDate min = store.stream().map(EventDto::getDateStart).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(null);
        LocalDate max = store.stream().map(EventDto::getDateEnd).filter(Objects::nonNull).max(LocalDate::compareTo).orElse(null);
        out.put("dateRange", Map.of("min", min, "max", max));
        out.put("guList", store.stream().map(EventDto::getGu).filter(Objects::nonNull).collect(Collectors.toCollection(TreeSet::new)));
        out.put("sample", store.stream().limit(3).toList());
        return out;
    }

    public int reload() {
        store.clear();
        idGen.set(1);
        return loadCsvWithCharset(StandardCharsets.UTF_8);
    }
}
