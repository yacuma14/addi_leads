package com.addi.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriminalStubResponse {
  private boolean hasRecords;
  private String message;
}
