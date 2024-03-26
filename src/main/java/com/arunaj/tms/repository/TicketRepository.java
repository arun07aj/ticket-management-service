package com.arunaj.tms.repository;

import com.arunaj.tms.dto.TicketDetailsDTO;
import com.arunaj.tms.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT new com.arunaj.tms.dto.TicketDetailsDTO(t.id, t.subject, t.description, t.createdDate, t.lastUpdatedDate, t.status, t.account.email) FROM Ticket t")
    List<TicketDetailsDTO> findAllTicketDetailsDTO();
    @Query("SELECT new com.arunaj.tms.dto.TicketDetailsDTO(t.id, t.subject, t.description, t.createdDate, t.lastUpdatedDate, t.status, t.account.email) FROM Ticket t WHERE t.account.id = :id")
    List<TicketDetailsDTO> findAllTicketDetailsDTOByAccountId(@Param("id") long id);
    @Query("SELECT new com.arunaj.tms.dto.TicketDetailsDTO(t.id, t.subject, t.description, t.createdDate, t.lastUpdatedDate, t.status, t.account.email) FROM Ticket t WHERE t.id = :id")
    Optional<TicketDetailsDTO> findTicketDetailsDTOById(@Param("id") Long id);

    List<Ticket> findTicketsByAccount_Id(long id);
}