package com.arunaj.tms.repository;

import com.arunaj.tms.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findTicketsByAccount_Id(long id);
}