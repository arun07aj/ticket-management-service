package com.arunaj.testreactspringboot.service;

import com.arunaj.testreactspringboot.dto.TicketPatchDTO;
import com.arunaj.testreactspringboot.exception.TicketNotFoundException;
import com.arunaj.testreactspringboot.model.Account;
import com.arunaj.testreactspringboot.model.Ticket;
import com.arunaj.testreactspringboot.repository.TicketRepository;
import com.arunaj.testreactspringboot.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TicketService implements PermissionEvaluator {
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
        if(account.isPresent())
            return ticketRepository.findTicketsByAccount_Id(account.get().getId());
        else
            throw new Exception("current logged-in user returned null, cannot fetch ticket list without valid user");

    }

    public boolean checkAccessOfTicketIdByAccountId(Long ticketId, Long accountId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if(ticket.isPresent()) {
            return Objects.equals(ticket.get().getAccount().getId(), accountId);
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject instanceof Ticket ticket && "READ".equals(permission)) {
            Long accountId;
            try {
                accountId = accountService.getCurrentLoggedInUser().get().getId();
            }
            catch (Exception e) {
                logger.error("Error fetching current logged-in user:", e);
                return false;
            }
            return checkAccessOfTicketIdByAccountId(ticket.getId(), accountId);
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
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
