package com.kh.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

// JPA 엔티티임을 명시
@Entity
public class Rest {

    // 기본 키를 지정하고 자동으로 생성되도록 설정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restId;

    @Column(nullable = false)
    private String restName;

    private String restBranch;
    private String restAddress;
    private String restWebsite;
    private String restRating;
    private String restMapUrl;
    private String restOpen;
    private String restPhonNumber;
    private String restImgAddress;

    // 기본 생성자 (JPA 필수)
    public Rest() {}

    // Getter 및 Setter 메서드
    public Long getRestId() {
        return restId;
    }

    public void setRestId(Long restId) {
        this.restId = restId;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String rest) {
        this.restName = restName;
    }

    public String getCafeBranch() {
        return restBranch;
    }

    public void setCafeBranch(String restBranch) {
        this.restBranch = restBranch;
    }

    public String getCafeAddress() {
        return restAddress;
    }

    public void setCafeAddress(String restAddress) {
        this.restAddress = restAddress;
    }

    public String getCafeWebsite() {
        return restWebsite;
    }

    public void setCafeWebsite(String restWebsite) {
        this.restWebsite = restWebsite;
    }

    public String getCafeRating() {
        return restRating;
    }

    public void setCafeRating(String restRating) {
        this.restRating = restRating;
    }

    public String getCafeMapUrl() {
        return restMapUrl;
    }

    public void setCafeMapUrl(String restMapUrl) {
        this.restMapUrl = restMapUrl;
    }

    public String getCafeOpen() {
        return restOpen;
    }

    public void setCafeOpen(String restOpen) {
        this.restOpen = restOpen;
    }

    public String getCafePhonNumber() {
        return restPhonNumber;
    }

    public void setCafePhonNumber(String restPhonNumber) {
        this.restPhonNumber = restPhonNumber;
    }

    public String getCafeImgAddress() {
        return restImgAddress;
    }

    public void setCafeImgAddress(String restImgAddress) {
        this.restImgAddress = restImgAddress;
    }
}
