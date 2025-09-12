package com.addi.crm.orchestrator;

import com.addi.crm.domain.Lead;
import com.addi.crm.domain.LeadStatus;
import com.addi.crm.domain.SagaState;
import com.addi.crm.dto.*;
import com.addi.crm.ports.outbound.EventPublisherPort;
import com.addi.crm.repository.LeadRepository;
import com.addi.crm.repository.SagaStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrchestratorServiceTest {

  @Mock
  private LeadRepository leadRepo;

  @Mock
  private SagaStateRepository sagaRepo;

  @Mock
  private EventPublisherPort eventPub;

  @InjectMocks
  private OrchestratorService orchestratorService;

  private Lead lead;
  private SagaState sagaState;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    lead = new Lead();
    lead.setId(1L);
    lead.setNationalId("ABC123");
    lead.setFirstName("yacuma");
    lead.setLastName("rodrigue");
    lead.setBirthDate(LocalDate.of(1990, 1, 1));
    lead.setStatus(LeadStatus.LEAD);

    sagaState = new SagaState();
    sagaState.setLeadId(1L);
  }

  @Test
  void startValidation_ShouldPublishIdentityAndCriminalCommands() {
    when(leadRepo.findById(1L)).thenReturn(Optional.of(lead));

    orchestratorService.startValidation(1L);

    verify(sagaRepo, times(1)).save(any(SagaState.class));

    verify(eventPub).publish(eq("commands.validate-identity"), any(ValidateIdentityCommand.class));
    verify(eventPub).publish(eq("commands.check-criminal"), any(CheckCriminalRecordCommand.class));
  }

  @Test
  void onIdentityResult_ShouldSetIdentityDoneAndProceed() {
    sagaState.setCriminalDone(true);
    sagaState.setCriminalOk(true);

    when(sagaRepo.findByLeadId(1L)).thenReturn(sagaState);

    orchestratorService.onIdentityResult(new IdentityValidationResult(1L, true, "ok"));

    verify(sagaRepo, times(1)).save(sagaState);
    verify(eventPub, times(1))
            .publish(eq("commands.run-scoring"), any(RunScoringCommand.class));
  }

  @Test
  void onCriminalResult_ShouldSetCriminalDoneAndProceed() {
    sagaState.setIdentityDone(true);
    sagaState.setIdentityOk(true);

    when(sagaRepo.findByLeadId(1L)).thenReturn(sagaState);

    orchestratorService.onCriminalResult(new CriminalRecordResult(1L, true, "ok"));

    verify(sagaRepo, times(1)).save(sagaState);
    verify(eventPub, times(1))
            .publish(eq("commands.run-scoring"), any(RunScoringCommand.class));
  }

  @Test
  void checkAndProceed_ShouldRejectWhenIdentityOrCriminalFails() {
    sagaState.setIdentityDone(true);
    sagaState.setIdentityOk(false);
    sagaState.setCriminalDone(true);
    sagaState.setCriminalOk(true);

    when(sagaRepo.findByLeadId(1L)).thenReturn(sagaState);
    when(leadRepo.findById(1L)).thenReturn(Optional.of(lead));

    orchestratorService.onIdentityResult(new IdentityValidationResult(1L, false, "fail"));

    verify(eventPub).publish(eq("events.lead-validation-failed"), any(LeadValidationFailed.class));
    verify(leadRepo).save(argThat(savedLead -> savedLead.getStatus() == LeadStatus.REJECTED));
  }

  @Test
  void onScoringResult_ShouldConvertLeadToProspect_WhenScoreAbove60() {
    when(sagaRepo.findByLeadId(1L)).thenReturn(sagaState);
    when(leadRepo.findById(1L)).thenReturn(Optional.of(lead));

    ScoringResult result = new ScoringResult(1L, 85);
    orchestratorService.onScoringResult(result);

    verify(eventPub).publish(eq("events.lead-converted"), any(LeadConvertedEvent.class));
    verify(leadRepo).save(argThat(savedLead -> savedLead.getStatus() == LeadStatus.PROSPECT));
  }

  @Test
  void onScoringResult_ShouldRejectLead_WhenScore60OrBelow() {
    when(sagaRepo.findByLeadId(1L)).thenReturn(sagaState);
    when(leadRepo.findById(1L)).thenReturn(Optional.of(lead));

    ScoringResult result = new ScoringResult(1L, 60);
    orchestratorService.onScoringResult(result);

    verify(eventPub).publish(eq("events.lead-validation-failed"), any(LeadValidationFailed.class));
    verify(leadRepo).save(argThat(savedLead -> savedLead.getStatus() == LeadStatus.REJECTED));
  }
}
