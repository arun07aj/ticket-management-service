package com.arunaj.tms.service;

import com.arunaj.tms.dto.TicketPatchDTO;
import com.arunaj.tms.exception.TicketNotFoundException;
import com.arunaj.tms.model.Account;
import com.arunaj.tms.model.AccountRole;
import com.arunaj.tms.model.Ticket;
import com.arunaj.tms.repository.TicketRepository;
import com.arunaj.tms.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TicketService {
    private static final Logger logger = LoggerUtil.getLogger(TicketService.class);
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AccountService accountService;

    public Ticket createTicket(Ticket ticket) throws Exception {

        if(ticket != null) {
            ticket.setStatus("OPEN");
            ticket.setCreatedDate(new Date(System.currentTimeMillis()));
            ticket.setLastUpdatedDate(ticket.getCreatedDate());

            if(accountService.getCurrentLoggedInUser().isPresent()) {
                ticket.setAccount(accountService.getCurrentLoggedInUser().get());
            }
            else
                throw new Exception("current logged-in user returned null, cannot create ticket without valid user");

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

    public List<Ticket> getAllTicketsOfCurrentLoggedInUser() throws Exception {
        Optional<Account> account = accountService.getCurrentLoggedInUser();
        if(account.isPresent()) {
            logger.info("retrieved all tickets created by current logged-in user");
            return ticketRepository.findTicketsByAccount_Id(account.get().getId());
        }
        else
            throw new Exception("current logged-in user returned null, cannot fetch ticket list without valid user");

    }

    public boolean checkTicketAccess(Ticket ticket) throws Exception {
        if(accountService.getCurrentLoggedInUser().isPresent()) {
            if(!accountService.getCurrentLoggedInUser().get().getRole().equals(AccountRole.ADMIN)) {
                Long currentAccountId = accountService.getCurrentLoggedInUser().get().getId();
                return checkAccessOfTicketIdByAccountId(ticket.getId(), currentAccountId);
            }
            // admin has access to all tickets
            return true;
        }
        else {
            throw new Exception("cannot check ticket access of user due to error while fetching current logged-in user");
        }
    }

    public boolean checkAccessOfTicketIdByAccountId(Long ticketId, Long accountId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        return ticket.filter(value -> Objects.equals(value.getAccount().getId(), accountId)).isPresent();
    }

    public Optional<Ticket> getTicketById(long id) {
        return ticketRepository.findById(id);
    }

    public Ticket updateTicket(long id, TicketPatchDTO ticketPatchDTO) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found for ID: " + id));

        // Update lastUpdatedDate
        existingTicket.setLastUpdatedDate(new Date(System.currentTimeMillis()));

        // Update description if provided
        if (ticketPatchDTO.getUpdatedDescription() != null) {
            existingTicket.setDescription(existingTicket.getDescription() + "<br>< " + existingTicket.getLastUpdatedDate() + " ><br>" + ticketPatchDTO.getUpdatedDescription());
        }

        // Update ticket status
        if(ticketPatchDTO.getUpdatedStatus() != null) {
            existingTicket.setStatus(ticketPatchDTO.getUpdatedStatus());
        }
        // if none mentioned then set it as OPEN
        else{
            existingTicket.setStatus("OPEN");
        }

        return ticketRepository.save(existingTicket);
    }

}
