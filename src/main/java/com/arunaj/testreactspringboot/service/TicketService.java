package com.arunaj.testreactspringboot.service;

import com.arunaj.testreactspringboot.model.Ticket;
import com.arunaj.testreactspringboot.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket createTicket(Ticket ticket) throws Exception {

        if(ticket != null) {
            ticket.setStatus("OPEN");
            ticket.setTime(new Date(System.currentTimeMillis()));

            if(isValidTicket(ticket)) {
                return ticketRepository.save(ticket);
            }
        }
        throw new Exception("invalid ticket");
    }

    private boolean isValidTicket(Ticket ticket) {
        // Check for non-null and non-empty values
        return ticket.getDescription() != null && !ticket.getDescription().isEmpty() &&
                ticket.getSubject() != null && !ticket.getSubject().isEmpty();
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(long id) {
        return ticketRepository.findById(id);
    }
}
