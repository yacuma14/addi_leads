package com.addi.crm.service;

import com.addi.crm.domain.Lead;

import java.util.Optional;

public interface LeadService {

  Lead createLead(Lead lead);

  Optional<Lead> getLeadById(Long id);
}
