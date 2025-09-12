package com.addi.crm.controller;

import com.addi.crm.domain.Lead;
import com.addi.crm.orchestrator.OrchestratorService;
import com.addi.crm.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leads")
@RequiredArgsConstructor
public class LeadController {

  private final LeadService leadService;

  private final OrchestratorService orchestrator;

  @PostMapping
  public ResponseEntity<Lead> create(@RequestBody Lead lead) {
    return ResponseEntity.ok(leadService.createLead(lead));
  }

  @PostMapping("/{id}/validate")
  public ResponseEntity<String> validate(@PathVariable("id") Long id) {
    orchestrator.startValidation(id);
    return ResponseEntity.accepted().body("Validation started");
  }

  @GetMapping("/{id}")
  public ResponseEntity<Lead> get(@PathVariable("id") Long id) {
    return ResponseEntity.ofNullable(leadService.getLeadById(id).orElseThrow());
  }
}
