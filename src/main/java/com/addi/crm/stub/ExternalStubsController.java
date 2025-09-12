package com.addi.crm.stub;

import com.addi.crm.dto.CriminalStubResponse;
import com.addi.crm.dto.IdentityStubResponse;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/stub")
public class ExternalStubsController {

  @GetMapping("/identity/verify")
  public IdentityStubResponse verify(@RequestParam("nationalId") String nationalId) throws InterruptedException {
    Thread.sleep(ThreadLocalRandom.current().nextLong(200,1200));
    boolean contains = nationalId.contains("8");

    return new IdentityStubResponse(contains, contains, contains ? "Found and matches" : "Not found or mismatch");
  }

  @GetMapping("/criminal/check")
  public CriminalStubResponse check(@RequestParam("nationalId") String nationalId) throws InterruptedException {
    Thread.sleep(ThreadLocalRandom.current().nextLong(150,900));
    boolean hasRecords = nationalId.contains("9");
    return new CriminalStubResponse(hasRecords, hasRecords ? "Has records" : "Clear");
  }
}
