package com.addi.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateIdentityCommand {
  private Long leadId;
  private String nationalId;
  private String firstName;
  private String lastName;
  private LocalDate birthDate;
}
