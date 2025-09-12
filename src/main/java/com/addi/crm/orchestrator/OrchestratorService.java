package com.addi.crm.orchestrator;


import com.addi.crm.domain.Lead;
import com.addi.crm.domain.LeadStatus;
import com.addi.crm.domain.SagaState;
import com.addi.crm.domain.model.*;
import com.addi.crm.dto.*;
import com.addi.crm.ports.outbound.EventPublisherPort;
import com.addi.crm.repository.LeadRepository;
import com.addi.crm.repository.SagaStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrchestratorService {
  private final LeadRepository leadRepo;
  private final SagaStateRepository sagaRepo;
  private final EventPublisherPort eventPub;

  @Transactional
  public void startValidation(Long leadId) {
    Lead lead = leadRepo.findById(leadId).orElseThrow();
    SagaState saga = new SagaState();
    saga.setLeadId(leadId);
    sagaRepo.save(saga);

    eventPub.publish("commands.validate-identity", new ValidateIdentityCommand(leadId, lead.getNationalId(),
            lead.getFirstName(), lead.getLastName(), lead.getBirthDate()));
    eventPub.publish("commands.check-criminal", new CheckCriminalRecordCommand(leadId, lead.getNationalId()));
  }

  public void onIdentityResult(IdentityValidationResult result) {
    SagaState saga = sagaRepo.findByLeadId(result.getLeadId());
    if (saga == null) return;
    saga.setIdentityDone(true);
    saga.setIdentityOk(result.isSuccess());
    sagaRepo.save(saga);
    checkAndProceed(saga);
  }

  public void onCriminalResult(CriminalRecordResult result) {
    SagaState saga = sagaRepo.findByLeadId(result.getLeadId());
    if (saga == null) return;
    saga.setCriminalDone(true);
    saga.setCriminalOk(result.isSuccess());
    sagaRepo.save(saga);
    checkAndProceed(saga);
  }

  private void checkAndProceed(SagaState saga) {
    if (saga.isIdentityDone() && saga.isCriminalDone()) {
      if (saga.isIdentityOk() && saga.isCriminalOk()) {
        eventPub.publish("commands.run-scoring", new RunScoringCommand(saga.getLeadId()));
      } else {
        eventPub.publish("events.lead-validation-failed", new LeadValidationFailed(saga.getLeadId(), "Identity or criminal failed"));
        saga.setCompleted(true);
        sagaRepo.save(saga);
        Lead lead = leadRepo.findById(saga.getLeadId()).orElseThrow();
        lead.setStatus(LeadStatus.REJECTED);
        leadRepo.save(lead);
      }
    }
  }

  public void onScoringResult(ScoringResult scoring) {
    SagaState saga = sagaRepo.findByLeadId(scoring.getLeadId());
    if (saga == null) return;
    saga.setScoringDone(true);
    sagaRepo.save(saga);

    Lead lead = leadRepo.findById(scoring.getLeadId()).orElseThrow();
    lead.setScore(scoring.getScore());
    if (scoring.getScore() > 60) {
      lead.setStatus(LeadStatus.PROSPECT);
      eventPub.publish("events.lead-converted", new LeadConvertedEvent(lead.getId()));
    } else {
      lead.setStatus(LeadStatus.REJECTED);
      eventPub.publish("events.lead-validation-failed", new LeadValidationFailed(lead.getId(), "Score too low"));
    }
    leadRepo.save(lead);
    saga.setCompleted(true);
    sagaRepo.save(saga);
  }
}