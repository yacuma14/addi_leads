package com.addi.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriminalRecordResult {
  private Long leadId;
  private boolean success;
  private String message;
}
