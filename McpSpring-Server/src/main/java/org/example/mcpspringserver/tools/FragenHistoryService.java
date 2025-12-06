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
        // 1. Neue Frage + Antwort speichern
        FragenHistory entry = repo.save(new FragenHistory(frage, antwort));

        // 2. Aufräumen (nur die letzten 3 behalten)
        // Wir packen das in ein try-catch, damit ein Fehler beim Löschen NICHT die Antwort verhindert!
        try {
            List<FragenHistory> all = repo.findAllByOrderByIdAsc();

            if (all != null && all.size() > 3) {
                // Wir löschen so viele, bis nur noch 3 übrig sind
                int anzahlZuLoeschen = all.size() - 3;

                for (int i = 0; i < anzahlZuLoeschen; i++) {
                    FragenHistory item = all.get(i);
                    // WICHTIG: Null-Check verhindert den Absturz "Entity must not be null"
                    if (item != null) {
                        repo.delete(item);
                    }
                }
            }
        } catch (Exception e) {
            // Fehler beim Aufräumen loggen, aber ignorieren.
            // Der Chatbot soll trotzdem antworten!
            System.err.println("Warnung: Alte Einträge konnten nicht gelöscht werden: " + e.getMessage());
            e.printStackTrace();
        }

        return entry;
    }

    public FragenHistory getLast1() {
        return repo.findAllByOrderByIdDesc().stream().findFirst().orElse(null);
    }

    public List<FragenHistory> getLast3() {
        return repo.findAllByOrderByIdDesc().stream().limit(3).toList();
    }
}
