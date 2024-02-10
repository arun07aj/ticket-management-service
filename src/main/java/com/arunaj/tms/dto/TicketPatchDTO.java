package com.arunaj.tms.dto;

import com.arunaj.tms.model.Comment;

public class TicketPatchDTO {
    private String updatedDescription;
    private String updatedStatus;
    private Comment comment;

    public TicketPatchDTO() {
    }

    public TicketPatchDTO(String updatedDescription, String updatedStatus, Comment comment) {
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

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
