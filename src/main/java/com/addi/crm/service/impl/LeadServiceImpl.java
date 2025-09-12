package com.addi.crm.service.impl;

import com.addi.crm.domain.Lead;
import com.addi.crm.repository.LeadRepository;
import com.addi.crm.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

  private final LeadRepository leadRepo;

  @Override
  public Lead createLead(Lead lead) {
    return leadRepo.save(lead);
  }

  @Override
  public Optional<Lead> getLeadById(Long id) {
    return leadRepo.findById(id);
  }
}
