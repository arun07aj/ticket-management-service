package com.arunaj.tms.service;

import com.arunaj.tms.model.Account;
import com.arunaj.tms.model.AccountRole;
import com.arunaj.tms.model.Comment;
import com.arunaj.tms.model.Ticket;
import com.arunaj.tms.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkFetchCommentsByTicketId() {
        Account userAccount = new Account(10L,"sa","sa@sa.com","sa", AccountRole.USER,true,null);
        Account adminAccount = new Account(1L,"admin1","admin1@sa.com","a1", AccountRole.ADMIN,true,null);

        Ticket ticket = new Ticket(1L, "dummyticket", "dummydesc", LocalDateTime.now(), LocalDateTime.now(), "OPEN", userAccount, null );

        List<Comment> comments = new ArrayList<>();
        Comment userComment = new Comment(1L, "please fix asap", LocalDateTime.now(), userAccount.getUsername(), ticket);
        Comment adminComment = new Comment(2L, "looking on it", LocalDateTime.now(), adminAccount.getUsername(), ticket);
        comments.add(userComment);
        comments.add(adminComment);

        Mockito.when(commentRepository.findCommentsByTicketId(1L)).thenReturn(comments);
        assertEquals(2, commentService.fetchAllCommentsOfTicketId(1L).size());
    }
}
