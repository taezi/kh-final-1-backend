// src/main/java/com/kh/dto/EventDto.java
package com.kh.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventDto {
    private Long cultureNo;          // CULUTENO (NUMBER)
    private String category;         // CATEGORY
    private String district;         // DISTRICT
    private String cultureName;      // CULUTENAME
    private String cultureAddress;   // CULTUREADDRESS
    private String organizationName; // ORGANIZATIONNAME
    private String targetAudience;   // TARGETAUDIENCE
    private String fee;              // FEE
    private String description;      // DESCRIPTION
    private String thumbnailImage;   // THUMBNAILIMAGE
    private String portalUrl;        // PORTALURL
    private String isFree;           // ISFREE (VARCHAR2(10) → 'Y'/'N' 등 문자열로 둠)
    private LocalDate startDate;     // STARTDATE (DATE)
    private LocalDate endDate;       // ENDDATE (DATE)


}
