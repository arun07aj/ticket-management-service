package com.arunaj.tms.dto;

import com.arunaj.tms.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public class TicketDetailsDTO {
    private Long id;
    private String subject;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
    private String status;
    private String creatorEmail;
    private List<Comment> comments;

    public TicketDetailsDTO(Long id, String subject, String description, LocalDateTime createdDate, LocalDateTime lastUpdatedDate, String status, String creatorEmail) {
        this.id = id;
        this.subject = subject;
        this.description = description;
        this.createdDate = createdDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.status = status;
        this.creatorEmail = creatorEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
