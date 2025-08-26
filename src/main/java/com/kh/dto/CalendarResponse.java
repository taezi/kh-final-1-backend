// src/main/java/com/kh/dto/CalendarResponse.java
package com.kh.dto;

import lombok.*;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarResponse {
    // "YYYY-MM-DD" -> 당일 이벤트 개수
    private Map<String, Integer> eventsByDate;
}
