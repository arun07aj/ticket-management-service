package com.arunaj.tms.dto;

public class TicketPatchDTO {
    private String updatedDescription;
    private String updatedStatus;

    public TicketPatchDTO() {
    }

    public TicketPatchDTO(String updatedDescription, String updatedStatus) {
        this.updatedDescription = updatedDescription;
        this.updatedStatus = updatedStatus;
    }

    public String getUpdatedDescription() {
        return updatedDescription;
    }

    public void setUpdatedDescription(String updatedDescription) {
        this.updatedDescription = updatedDescription;
    }

    public String getUpdatedStatus() {
        return updatedStatus;
    }

    public void setUpdatedStatus(String updatedStatus) {
        this.updatedStatus = updatedStatus;
    }
}
