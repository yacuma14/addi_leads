package com.addi.crm.repository;

import com.addi.crm.domain.SagaState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaStateRepository extends JpaRepository<SagaState, Long> {
  SagaState findByLeadId(Long leadId);
}
