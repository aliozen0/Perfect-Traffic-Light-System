package com.trafficlight.repository;

import com.trafficlight.entity.RuleApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RuleApplicationRepository extends JpaRepository<RuleApplication, Long> {

    List<RuleApplication> findByRuleIdOrderByAppliedAtDesc(Long ruleId);

    List<RuleApplication> findByIntersectionIdOrderByAppliedAtDesc(Long intersectionId);

    @Query("SELECT ra FROM RuleApplication ra WHERE ra.appliedAt BETWEEN :start AND :end ORDER BY ra.appliedAt DESC")
    List<RuleApplication> findByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(ra) FROM RuleApplication ra WHERE ra.ruleId = :ruleId AND ra.appliedAt >= :since")
    Long countApplicationsSince(Long ruleId, LocalDateTime since);

    @Query("SELECT ra.ruleName, COUNT(ra) as count FROM RuleApplication ra " +
           "WHERE ra.appliedAt >= :since GROUP BY ra.ruleName ORDER BY count DESC")
    List<Object[]> findMostAppliedRulesSince(LocalDateTime since);
}