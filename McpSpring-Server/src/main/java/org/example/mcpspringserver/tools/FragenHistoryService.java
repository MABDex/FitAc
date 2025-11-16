package org.example.mcpspringserver.tools;


import org.example.mcpspringserver.entities.FragenHistory;
import org.example.mcpspringserver.repository.FragenHistoryrepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FragenHistoryService {

    private final FragenHistoryrepository repo;

    public FragenHistoryService(FragenHistoryrepository repo) {
        this.repo = repo;
    }

    public FragenHistory save(String frage, String antwort) {
        // neue Frage + Antwort speichern
        FragenHistory entry = repo.save(new FragenHistory(frage, antwort));

        // alle Einträge nach ID sortiert (älteste zuerst)
        List<FragenHistory> all = repo.findAllByOrderByIdAsc();

        // wenn mehr als 3 Einträge → die ältesten löschen
        if (all.size() > 3) {
            for (int i = 0; i < all.size() - 3; i++) {
                repo.delete(all.get(i));
            }
        }

        return entry;
    }

    public FragenHistory getLast1() {
        return repo.findAllByOrderByIdDesc()  .stream().findFirst().orElse(null);
    }

    public List<FragenHistory> getLast3() {
        return repo.findAllByOrderByIdDesc().stream().limit(3).toList();
    }


}