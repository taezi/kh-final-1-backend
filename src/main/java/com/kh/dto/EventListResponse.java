// src/main/java/com/kh/dto/EventListResponse.java
package com.kh.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventListResponse {
    private List<EventDto> items;
    private boolean hasMore;
}
