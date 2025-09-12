package com.addi.crm.adapter.messaging.kafka;

import com.addi.crm.adapters.messaging.kafka.KafkaListenersAdapter;
import com.addi.crm.domain.model.CriminalExternalResponse;
import com.addi.crm.domain.model.IdentityExternalResponse;
import com.addi.crm.dto.*;
import com.addi.crm.orchestrator.OrchestratorService;
import com.addi.crm.ports.outbound.EventPublisherPort;
import com.addi.crm.ports.outbound.ExternalCriminalPort;
import com.addi.crm.ports.outbound.ExternalIdentityPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KafkaListenersAdapterTest {

  @Mock
  private OrchestratorService orchestrator;

  @Mock
  private ExternalCriminalPort externalCriminalPort;

  @Mock
  private ExternalIdentityPort externalIdentityPort;

  @Mock
  private EventPublisherPort eventPublisherPort;

  @InjectMocks
  private KafkaListenersAdapter kafkaListenersAdapter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void identityListener_ShouldCallOrchestrator() {
    IdentityValidationResult result = new IdentityValidationResult(1L, true, "OK");

    kafkaListenersAdapter.identityListener(result);

    verify(orchestrator, times(1)).onIdentityResult(result);
  }
  @Test
  void criminalListener_ShouldCallOrchestrator() {
    CriminalRecordResult result = new CriminalRecordResult(1L, true, "Clear");

    kafkaListenersAdapter.criminalListener(result);

    verify(orchestrator, times(1)).onCriminalResult(result);
  }

  @Test
  void scoringListener_ShouldCallOrchestrator() {
    ScoringResult result = new ScoringResult(1L, 85);

    kafkaListenersAdapter.scoringListener(result);

    verify(orchestrator, times(1)).onScoringResult(result);
  }
  @Test
  void criminalValidation_ShouldPublishEvent_WhenNoCriminalRecords() {
    CheckCriminalRecordCommand cmd = new CheckCriminalRecordCommand(1L, "123456789");
    CriminalExternalResponse response = new CriminalExternalResponse();
    response.setHasRecords(false);
    response.setMessage("No criminal records");

    when(externalCriminalPort.verify(cmd.getNationalId())).thenReturn(response);
    kafkaListenersAdapter.criminalValidation(cmd);
    ArgumentCaptor<CriminalRecordResult> captor = ArgumentCaptor.forClass(CriminalRecordResult.class);
    verify(eventPublisherPort, times(1)).publish(eq("events.criminal-result"),
            captor.capture());

    CriminalRecordResult publishedResult = captor.getValue();
    assertThat(publishedResult.getLeadId()).isEqualTo(1L);
    assertThat(publishedResult.isSuccess()).isTrue();
    assertThat(publishedResult.getMessage()).isEqualTo("No criminal records");
  }

  @Test
  void criminalValidation_ShouldHandleNullResponse() {
    CheckCriminalRecordCommand cmd = new CheckCriminalRecordCommand(1L, "123456789");

    when(externalCriminalPort.verify(cmd.getNationalId())).thenReturn(null);

    kafkaListenersAdapter.criminalValidation(cmd);

    ArgumentCaptor<CriminalRecordResult> captor = ArgumentCaptor.forClass(CriminalRecordResult.class);
    verify(eventPublisherPort).publish(eq("events.criminal-result"), captor.capture());

    CriminalRecordResult publishedResult = captor.getValue();
    assertThat(publishedResult.isSuccess()).isFalse();
    assertThat(publishedResult.getMessage()).isEqualTo("no response");
  }

  @Test
  void identityValidation_ShouldPublishEvent_WhenIdentityMatches() {
    ValidateIdentityCommand cmd = new ValidateIdentityCommand(1L, "987654321", "rocke", "malva",
            LocalDate.now());
    IdentityExternalResponse response = new IdentityExternalResponse();
    response.setExists(true);
    response.setMatches(true);
    response.setMessage("Identity valid");

    when(externalIdentityPort.verify(cmd.getNationalId())).thenReturn(response);

    kafkaListenersAdapter.intentityValidation(cmd);

    ArgumentCaptor<IdentityValidationResult> captor = ArgumentCaptor.forClass(IdentityValidationResult.class);
    verify(eventPublisherPort, times(1)).publish(eq("events.identity-result"),
            captor.capture());

    IdentityValidationResult publishedResult = captor.getValue();
    assertThat(publishedResult.getLeadId()).isEqualTo(1L);
    assertThat(publishedResult.isSuccess()).isTrue();
    assertThat(publishedResult.getMessage()).isEqualTo("Identity valid");
  }

  @Test
  void identityValidation_ShouldHandleNullResponse() {
    ValidateIdentityCommand cmd = new ValidateIdentityCommand(1L, "987654321", "yac",
            "rode", LocalDate.now());

    when(externalIdentityPort.verify(cmd.getNationalId())).thenReturn(null);

    kafkaListenersAdapter.intentityValidation(cmd);

    ArgumentCaptor<IdentityValidationResult> captor = ArgumentCaptor.forClass(IdentityValidationResult.class);
    verify(eventPublisherPort).publish(eq("events.identity-result"), captor.capture());

    IdentityValidationResult publishedResult = captor.getValue();
    assertThat(publishedResult.isSuccess()).isFalse();
    assertThat(publishedResult.getMessage()).isEqualTo("no response");
  }

  @Test
  void scoreValidation_ShouldPublishScoringResult() {
    RunScoringCommand cmd = new RunScoringCommand(1L);

    kafkaListenersAdapter.scoreValidation(cmd);

    ArgumentCaptor<ScoringResult> captor = ArgumentCaptor.forClass(ScoringResult.class);
    verify(eventPublisherPort, times(1)).publish(eq("events.scoring-result"),
            captor.capture());

    ScoringResult publishedResult = captor.getValue();
    assertThat(publishedResult.getLeadId()).isEqualTo(1L);
    assertThat(publishedResult.getScore()).isBetween(45, 100);
  }
}
