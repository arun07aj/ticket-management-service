package com.arunaj.tms.controller;

import com.arunaj.tms.dto.TicketPatchDTO;
import com.arunaj.tms.exception.TicketNotFoundException;
import com.arunaj.tms.model.Ticket;
import com.arunaj.tms.service.TicketService;
import com.arunaj.tms.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"https://arunaj.co", "http://localhost:3000"})
@RequestMapping("/tickets")
public class TicketController {
    private static final Logger logger = LoggerUtil.getLogger(TicketController.class);

    @Autowired
    private TicketService ticketService;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createTicket(@RequestBody Ticket ticket) {
        try {
            Ticket newTicket = ticketService.createTicket(ticket);
            return new ResponseEntity<>(newTicket, HttpStatus.CREATED);
        }
        catch(Exception e) {
            logger.error("Error creating ticket:", e);
            return new ResponseEntity<>("Error creating ticket", HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> getAllTickets() {
        try {
            List<Ticket> ticketsList = ticketService.getAllTickets();
            return new ResponseEntity<>(ticketsList, HttpStatus.OK);
        }
        catch(Exception e) {
            logger.error("Error fetching ticket list:", e);
            return new ResponseEntity<>("Error fetching ticket list", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/list/my")
    public ResponseEntity<?> getAllTicketsOfCurrentLoggedInUser() {
        try {
            List<Ticket> ticketsList = ticketService.getAllTicketsOfCurrentLoggedInUser();
            return new ResponseEntity<>(ticketsList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching ticket list of current logged-in user:", e);
            return new ResponseEntity<>("Error fetching ticket list of user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/list/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable long id) {
        try {
            Optional<Ticket> ticket = ticketService.getTicketById(id);

            if(ticket.isPresent()) {
                // check if user is authorized to access the ticket
                if(ticketService.checkTicketAccess(ticket.get())) {
                    return new ResponseEntity<>(ticket, HttpStatus.OK);
                }
                return new ResponseEntity<>("You don't have access to this ticket.", HttpStatus.FORBIDDEN);
            }
            else {
                return new ResponseEntity<>("No ticket exists with the ID " + id, HttpStatus.NOT_FOUND);
            }
        }
        catch(Exception e) {
            logger.error("Error fetching ticket details:", e);
            return new ResponseEntity<>("Error fetching ticket details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable Long id, @RequestBody TicketPatchDTO ticketPatchDTO) {
        try {
            Ticket ticket = ticketService.updateTicket(id, ticketPatchDTO);
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        }
        catch(TicketNotFoundException e) {
            logger.info("No ticket exists with the ID " + id, e);
            return new ResponseEntity<>("No ticket exists with the ID " + id, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error occurred while updating ticket details:", e);
            return new ResponseEntity<>("Error occurred while updating ticket details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
