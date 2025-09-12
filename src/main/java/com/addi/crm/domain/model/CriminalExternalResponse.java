package com.addi.crm.domain.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CriminalExternalResponse {
    private boolean hasRecords; private String message;
    public boolean isHasRecords(){return hasRecords;} public void setHasRecords(boolean hasRecords){this.hasRecords=hasRecords;}
    public String getMessage(){return message;} public void setMessage(String message){this.message=message;}
}
