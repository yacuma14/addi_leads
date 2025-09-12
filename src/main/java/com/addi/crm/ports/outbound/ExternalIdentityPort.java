package com.addi.crm.ports.outbound;

import com.addi.crm.domain.model.IdentityExternalResponse;

public interface ExternalIdentityPort {
    IdentityExternalResponse verify(String nationalId);
}
