package com.addi.crm.domain.model;
public class IdentityExternalResponse {
    private boolean exists;
    private boolean matches;
    private String message;
    public boolean isExists(){return exists;} public void setExists(boolean exists){this.exists=exists;}
    public boolean isMatches(){return matches;} public void setMatches(boolean matches){this.matches=matches;}
    public String getMessage(){return message;} public void setMessage(String message){this.message=message;}
}
