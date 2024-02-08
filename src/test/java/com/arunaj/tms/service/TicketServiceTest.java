package com.arunaj.tms.service;

import com.arunaj.tms.model.Account;
import com.arunaj.tms.model.AccountRole;
import com.arunaj.tms.model.Ticket;
import com.arunaj.tms.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TicketServiceTest {
    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckAccessOfTicketIdByAccountId() {
        Account account = new Account(10L,"sa","sa@sa.com","sa", AccountRole.USER,true,null);
        Ticket ticket = new Ticket(15L,"sa_sub", "sa_desc",new Date(), new Date(), "OPEN", account, null);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        Mockito.when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        Mockito.when(ticketRepository.findTicketsByAccount_Id(account.getId())).thenReturn(tickets);
        assertTrue(ticketService.checkAccessOfTicketIdByAccountId(15L,10L), "test fail as the ticket is created by the account");
        assertFalse(ticketService.checkAccessOfTicketIdByAccountId(15L,101L), "test fail as the ticket is not created by the account");

    }
}
