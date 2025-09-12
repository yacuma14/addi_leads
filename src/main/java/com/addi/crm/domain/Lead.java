package com.addi.crm.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lead {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String nationalId;
  private String firstName;
  private String lastName;
  private LocalDate birthDate;
  private String email;
  @Enumerated(EnumType.STRING)
  private LeadStatus status = LeadStatus.LEAD;
  private Integer score;

}
