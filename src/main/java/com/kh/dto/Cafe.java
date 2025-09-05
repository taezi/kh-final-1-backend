package com.kh.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

// JPA 엔티티임을 명시
@Entity
public class Cafe {

    // 기본 키를 지정하고 자동으로 생성되도록 설정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cafeId;

    @Column(nullable = false)
    private String cafeName;

    private String cafeBranch;
    private String cafeAddress;
    private String cafeWebsite;
    private String cafeRating;
    private String cafeMapUrl;
    private String cafeOpen;
    private String cafePhonNumber;
    private String cafeImgAddress;

    // 기본 생성자 (JPA 필수)
    public Cafe() {}

    // Getter 및 Setter 메서드
    public Long getCafeId() {
        return cafeId;
    }

    public void setCafeId(Long cafeId) {
        this.cafeId = cafeId;
    }

    public String getCafeName() {
        return cafeName;
    }

    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
    }

    public String getCafeBranch() {
        return cafeBranch;
    }

    public void setCafeBranch(String cafeBranch) {
        this.cafeBranch = cafeBranch;
    }

    public String getCafeAddress() {
        return cafeAddress;
    }

    public void setCafeAddress(String cafeAddress) {
        this.cafeAddress = cafeAddress;
    }

    public String getCafeWebsite() {
        return cafeWebsite;
    }

    public void setCafeWebsite(String cafeWebsite) {
        this.cafeWebsite = cafeWebsite;
    }

    public String getCafeRating() {
        return cafeRating;
    }

    public void setCafeRating(String cafeRating) {
        this.cafeRating = cafeRating;
    }

    public String getCafeMapUrl() {
        return cafeMapUrl;
    }

    public void setCafeMapUrl(String cafeMapUrl) {
        this.cafeMapUrl = cafeMapUrl;
    }

    public String getCafeOpen() {
        return cafeOpen;
    }

    public void setCafeOpen(String cafeOpen) {
        this.cafeOpen = cafeOpen;
    }

    public String getCafePhonNumber() {
        return cafePhonNumber;
    }

    public void setCafePhonNumber(String cafePhonNumber) {
        this.cafePhonNumber = cafePhonNumber;
    }

    public String getCafeImgAddress() {
        return cafeImgAddress;
    }

    public void setCafeImgAddress(String cafeImgAddress) {
        this.cafeImgAddress = cafeImgAddress;
    }
}
