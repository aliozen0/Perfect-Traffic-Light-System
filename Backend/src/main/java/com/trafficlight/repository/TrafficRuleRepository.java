package com.trafficlight.repository;

import com.trafficlight.entity.TrafficRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrafficRuleRepository extends JpaRepository<TrafficRule, Long> {

    Optional<TrafficRule> findByRuleName(String ruleName);

    List<TrafficRule> findByActiveTrue();

    List<TrafficRule> findByRuleTypeAndActiveTrue(TrafficRule.RuleType ruleType);

    List<TrafficRule> findByActiveTrueOrderByPriorityAsc();

    @Query("SELECT r FROM TrafficRule r WHERE r.active = true AND " +
           "(:vehicleCount IS NULL OR " +
           "(r.minVehicleCount IS NULL OR :vehicleCount >= r.minVehicleCount) AND " +
           "(r.maxVehicleCount IS NULL OR :vehicleCount <= r.maxVehicleCount)) " +
           "ORDER BY r.priority ASC")
    List<TrafficRule> findApplicableRules(Integer vehicleCount);
}