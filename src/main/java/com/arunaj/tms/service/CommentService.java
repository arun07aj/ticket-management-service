package com.arunaj.tms.service;

import com.arunaj.tms.model.Comment;
import com.arunaj.tms.repository.CommentRepository;
import com.arunaj.tms.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private static final Logger logger = LoggerUtil.getLogger(CommentService.class);
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> fetchAllCommentsOfTicketId(Long ticketId) {
        return commentRepository.findCommentsByTicketId(ticketId);
    }
}
