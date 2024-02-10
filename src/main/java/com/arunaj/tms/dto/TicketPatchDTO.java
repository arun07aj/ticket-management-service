package com.arunaj.tms.dto;

public class TicketPatchDTO {
    private String updatedDescription;
    private String updatedStatus;
    private String comment;

    public TicketPatchDTO() {
    }

    public TicketPatchDTO(String updatedDescription, String updatedStatus, String comment) {
        this.updatedDescription = updatedDescription;
        this.updatedStatus = updatedStatus;
        this.comment = comment;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
