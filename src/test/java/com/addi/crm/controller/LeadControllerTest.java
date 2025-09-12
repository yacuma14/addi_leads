package com.addi.crm.controller;

import com.addi.crm.domain.Lead;
import com.addi.crm.domain.LeadStatus;
import com.addi.crm.service.LeadService;
import com.addi.crm.orchestrator.OrchestratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadControllerTest {

  @Mock
  private LeadService leadService;

  @Mock
  private OrchestratorService orchestratorService;

  @InjectMocks
  private LeadController leadController;

  private Lead buildLead() {
    Lead lead = new Lead();
    lead.setId(1L);
    lead.setNationalId("123456789");
    lead.setFirstName("Juan");
    lead.setLastName("Perez");
    lead.setBirthDate(LocalDate.of(1990, 1, 1));
    lead.setEmail("juan@example.com");
    lead.setStatus(LeadStatus.LEAD);
    return lead;
  }

  @Test
  void testCreateLead() {
    Lead lead = buildLead();
    when(leadService.createLead(any(Lead.class))).thenReturn(lead);

    ResponseEntity<Lead> response = leadController.create(lead);

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Juan", response.getBody().getFirstName());

    verify(leadService, times(1)).createLead(any(Lead.class));
  }

  @Test
  void testValidateLeadStartsSaga() {
    Long leadId = 1L;

    ResponseEntity<String> response = leadController.validate(leadId);

    assertNotNull(response);
    assertEquals(202, response.getStatusCodeValue());
    assertEquals("Validation started", response.getBody());

    verify(orchestratorService, times(1)).startValidation(leadId);
  }

  @Test
  void testGetLeadById() {
    Lead lead = buildLead();
    when(leadService.getLeadById(1L)).thenReturn(Optional.of(lead));
   ResponseEntity<Lead> response = leadController.get(1L);

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("123456789", response.getBody().getNationalId());
    assertEquals(LeadStatus.LEAD, response.getBody().getStatus());

    verify(leadService, times(1)).getLeadById(1L);
  }

  @Test
  void testGetLeadById_NotFound() {
    when(leadService.getLeadById(99L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> leadController.get(99L));
    verify(leadService, times(1)).getLeadById(99L);
  }
}
