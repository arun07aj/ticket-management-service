package com.arunaj.tms.service;

import com.arunaj.tms.exception.InvalidDataException;
import com.arunaj.tms.model.Comment;
import com.arunaj.tms.model.Ticket;
import com.arunaj.tms.repository.CommentRepository;
import com.arunaj.tms.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentService {
    private static final Logger logger = LoggerUtil.getLogger(CommentService.class);
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AccountService accountService;

    public Comment addComment(Comment comment, Ticket ticket) throws Exception {
        if(comment != null) {
            comment.setCommentTime(new Date(System.currentTimeMillis()));
            comment.setTicket(ticket);
            if(accountService.getCurrentLoggedInUser().isPresent()) {
                comment.setUsername(accountService.getCurrentLoggedInUser().get().getUsername());
            }
            else
                throw new Exception("current logged-in user returned null, cannot create ticket without valid user");
        }
        if(isValidComment(comment)) {
            return commentRepository.save(comment);
        }

        logger.info("invalid comment received: comment content may be null");
        throw new InvalidDataException("invalid comment");
    }

    private boolean isValidComment(Comment comment) {
        return comment.getContent() != null && !comment.getContent().isEmpty();
    }

    public List<Comment> fetchAllCommentsOfTicketId(Long ticketId) {
        return commentRepository.findCommentsByTicketId(ticketId);
    }
}
