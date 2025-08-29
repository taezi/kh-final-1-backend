package com.kh.repository;

import com.kh.dto.Rest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// JpaRepository를 상속받아 기본 CRUD 기능을 자동으로 사용
// 첫 번째 매개변수는 엔티티 타입, 두 번째는 엔티티의 ID 타입
public interface RestRepository extends JpaRepository<Rest, Long> {

    // Spring Data JPA의 메서드 명명 규칙을 따라 특정 필드로 조회하는 메서드 정의
    Optional<Rest> findByRestNameAndRestBranch(String restName, String restBranch);
}