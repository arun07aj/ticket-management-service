package com.arunaj.testreactspringboot.controller;

import com.arunaj.testreactspringboot.model.Ticket;
import com.arunaj.testreactspringboot.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private TicketService ticketService;

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

    @GetMapping("/list/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable long id) {
        try {
            Optional<Ticket> ticket = ticketService.getTicketById(id);
            if(ticket.isPresent()) {
                return new ResponseEntity<>(ticket, HttpStatus.OK);
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
}
