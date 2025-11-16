package org.example.mcpspringserver.repository;

import org.example.mcpspringserver.entities.FragenHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FragenHistoryrepository extends JpaRepository<FragenHistory,Long> {

    List<FragenHistory> findAllByOrderByIdAsc();
    List<FragenHistory> findAllByOrderByIdDesc();

}
