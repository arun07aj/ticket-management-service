package com.arunaj.testreactspringboot.repository;

import com.arunaj.testreactspringboot.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
}