package com.addi.crm.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SagaState {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long leadId;
  private boolean identityDone;
  private boolean identityOk;
  private boolean criminalDone;
  private boolean criminalOk;
  private boolean scoringDone;
  private boolean completed;
  private String lastMessage;
  private Instant createdAt = Instant.now();

}
