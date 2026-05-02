package com.hospital.inventory.planning.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.inventory.planning.dto.CreateReorderRuleRequest;
import com.hospital.inventory.planning.dto.ReorderRecommendationResponse;
import com.hospital.inventory.planning.dto.ReorderRuleResponse;

public interface ReorderPlanningService {

	ReorderRuleResponse createRule(CreateReorderRuleRequest request);

	Page<ReorderRuleResponse> getRules(Pageable pageable, boolean activeOnly);

	List<ReorderRecommendationResponse> getRecommendations();
}
