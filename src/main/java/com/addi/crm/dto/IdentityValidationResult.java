package com.addi.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentityValidationResult {
  private Long leadId;
  private boolean success;
  private String message;
}
