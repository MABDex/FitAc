package org.example.mcpspringserver.tools;

import org.example.mcpspringserver.entities.FragenHistory;
import org.example.mcpspringserver.repository.FragenHistoryrepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
@Transactional    
public class FragenHistoryService {

    private final FragenHistoryrepository repo;

    public FragenHistoryService(FragenHistoryrepository repo) {
        this.repo = repo;
    }

    // Speichert die neue Frage und löscht ALLES andere
    public FragenHistory save(String frage, String antwort) {
        repo.deleteAllInBatch(); // Tabelle leeren
        return repo.save(new FragenHistory(frage, antwort)); // Nur das Neueste speichern
    }
    

    public FragenHistory getLast1() {
        return repo.findAll().stream().findFirst().orElse(null);
    }

  
}
