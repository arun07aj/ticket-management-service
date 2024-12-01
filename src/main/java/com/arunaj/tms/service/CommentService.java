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

    public Comment addComment(String comment, Ticket ticket) throws Exception {
        Comment commentObj = new Comment();
        if(comment != null && !comment.isBlank()) {
            commentObj.setContent(comment);
            commentObj.setCommentTime(new Date(System.currentTimeMillis()));
            commentObj.setTicket(ticket);
            if(accountService.getCurrentLoggedInUser().isPresent()) {
                commentObj.setUsername(accountService.getCurrentLoggedInUser().get().getUsername());
            }
            else
                throw new Exception("current logged-in user returned null, cannot create ticket without valid user");
        }

        if(isValidComment(commentObj)) {
            return commentRepository.save(commentObj);
        }

        logger.info("invalid comment received: comment content may be null");
        throw new InvalidDataException("invalid comment");
    }

    private boolean isValidComment(Comment comment) {
        return comment.getContent() != null && !comment.getContent().isBlank();
    }

    public List<Comment> fetchAllCommentsOfTicketId(Long ticketId) {
        return commentRepository.findCommentsByTicketId(ticketId);
    }
}
