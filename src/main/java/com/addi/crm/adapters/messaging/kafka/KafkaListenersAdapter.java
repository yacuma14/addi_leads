package com.addi.crm.adapters.messaging.kafka;

import com.addi.crm.domain.model.CriminalExternalResponse;
import com.addi.crm.domain.model.IdentityExternalResponse;
import com.addi.crm.dto.*;
import com.addi.crm.orchestrator.OrchestratorService;
import com.addi.crm.ports.outbound.EventPublisherPort;
import com.addi.crm.ports.outbound.ExternalCriminalPort;
import com.addi.crm.ports.outbound.ExternalIdentityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class KafkaListenersAdapter {

    private final OrchestratorService orchestrator;
    private final ExternalCriminalPort externalCriminalPort;
    private final ExternalIdentityPort externalIdentityPort;
    private final EventPublisherPort eventPub;



    @KafkaListener(topics = "events.identity-result", groupId = "crm-group")
    public void identityListener(IdentityValidationResult result) {
        orchestrator.onIdentityResult(result);
    }

    @KafkaListener(topics = "events.criminal-result", groupId = "crm-group")
    public void criminalListener(CriminalRecordResult result) {
        orchestrator.onCriminalResult(result);
    }

    @KafkaListener(topics = "events.scoring-result", groupId = "crm-group")
    public void scoringListener(ScoringResult result) {
        orchestrator.onScoringResult(result);
    }
    @KafkaListener(topics = "commands.check-criminal", groupId = "crm-group")
    public void criminalValidation(CheckCriminalRecordCommand cmd){
        CriminalExternalResponse resp = externalCriminalPort.verify(cmd.getNationalId());
        boolean success = resp != null && !resp.isHasRecords();
        eventPub.publish("events.criminal-result",
                new CriminalRecordResult(cmd.getLeadId(), success, resp != null ? resp.getMessage() : "no response"));
   }

    @KafkaListener(topics = "commands.validate-identity", groupId = "crm-group")
    public void intentityValidation(ValidateIdentityCommand cmd){
        IdentityExternalResponse resp = externalIdentityPort.verify(cmd.getNationalId());
        boolean success = resp != null && resp.isExists() && resp.isMatches();
        eventPub.publish("events.identity-result",
                new IdentityValidationResult(cmd.getLeadId(), success, resp != null ? resp.getMessage() : "no response"));

    }

    @KafkaListener(topics = "commands.run-scoring", groupId = "crm-group")
     public void scoreValidation(RunScoringCommand cmd){
        int score = ThreadLocalRandom.current().nextInt(35,101);
        eventPub.publish("events.scoring-result", new ScoringResult(cmd.getLeadId(), score));
    }

}
