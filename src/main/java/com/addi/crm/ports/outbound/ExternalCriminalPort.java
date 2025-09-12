package com.addi.crm.ports.outbound;

import com.addi.crm.domain.model.CriminalExternalResponse;

public interface ExternalCriminalPort {
    CriminalExternalResponse verify(String nationalId);
}
