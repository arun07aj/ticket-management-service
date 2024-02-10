package com.arunaj.tms.service;

import com.arunaj.tms.dto.TicketDetailsDTO;
import com.arunaj.tms.dto.TicketPatchDTO;
import com.arunaj.tms.exception.InvalidDataException;
import com.arunaj.tms.exception.TicketNotFoundException;
import com.arunaj.tms.model.Account;
import com.arunaj.tms.model.Comment;
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

    @Autowired
    private CommentService commentService;

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
        logger.info("invalid ticket received: description or subject may be null");
        throw new InvalidDataException("invalid ticket");
    }

    private boolean isValidTicket(Ticket ticket) {
        // Check for non-null and non-empty values
        return ticket.getDescription() != null && !ticket.getDescription().isEmpty() &&
                ticket.getSubject() != null && !ticket.getSubject().isEmpty();
    }

    public Optional<TicketDetailsDTO> getTicketById(long id) {
        Optional<TicketDetailsDTO> ticketDetailsDTO = ticketRepository.findTicketDetailsDTOById(id);
        // fetch and set comments for ticket
        if(ticketDetailsDTO.isPresent()) {
            ticketDetailsDTO.get().setComments(commentService.fetchAllCommentsOfTicketId(id));
            logger.info("fetched and set comments for the ticket id: " + id);
            return ticketDetailsDTO;
        }

        logger.info("ticket not found for the given id: " + id);
        return Optional.empty();
    }

    public List<TicketDetailsDTO> getAllTickets() {
        return fetchAndSetCommentsForTickets(ticketRepository.findAllTicketDetailsDTO());
    }

    private List<TicketDetailsDTO> fetchAndSetCommentsForTickets(List<TicketDetailsDTO> ticketDetailsDTOList) {
        // fetch and set comments for each ticket one by one
        ticketDetailsDTOList
                .forEach(ticketDetailsDTO -> {
                    long ticketId = ticketDetailsDTO.getId();
                    ticketDetailsDTO.setComments(commentService.fetchAllCommentsOfTicketId(ticketId));
                });
        logger.info("fetched and set comments successfully for ticket list");
        return ticketDetailsDTOList;
    }

    public List<TicketDetailsDTO> getAllTicketsOfCurrentLoggedInUser() throws Exception {
        Optional<Account> account = accountService.getCurrentLoggedInUser();

        if(account.isPresent()) {
            logger.info("retrieved all tickets created by current logged-in user");
            return fetchAndSetCommentsForTickets(ticketRepository.findAllTicketDetailsDTOByAccountId(account.get().getId()));
        }
        else
            throw new Exception("current logged-in user returned null, cannot fetch ticket list without valid user");
    }

    public boolean checkTicketAccess(TicketDetailsDTO ticket) throws Exception {
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

    public TicketDetailsDTO updateTicket(long id, TicketPatchDTO ticketPatchDTO) throws Exception {
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

        // add new comment if provided
        if(ticketPatchDTO.getComment() != null) {
            List<Comment> comments = existingTicket.getComments();
            comments.add(commentService.addComment(ticketPatchDTO.getComment(), existingTicket));
            existingTicket.setComments(comments);
        }

        ticketRepository.save(existingTicket);
        logger.info("edit ticket successful and fetching ticket id: " + id);
        return getTicketById(id).get();
    }

}
