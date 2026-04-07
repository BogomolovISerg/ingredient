package catalog.ingredient.service;

import catalog.ingredient.domain.RegulatoryEntry;
import catalog.ingredient.domain.RegulatoryListType;
import catalog.ingredient.repo.RegulatoryEntryRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RegulatoryService {

    private final RegulatoryEntryRepository regulatoryEntryRepository;

    public RegulatoryService(RegulatoryEntryRepository regulatoryEntryRepository) {
        this.regulatoryEntryRepository = regulatoryEntryRepository;
    }

    public List<RegulatoryEntry> search(String query, RegulatoryListType type, int limit) {
        String normalized = query == null ? null : query.trim();
        PageRequest page = PageRequest.of(0, Math.max(1, limit));
        return type == null
                ? regulatoryEntryRepository.search(normalized, page)
                : regulatoryEntryRepository.searchByType(normalized, type, page);
    }

    public RegulatoryEntry get(long entryId) {
        return regulatoryEntryRepository.findByEntryId(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Регуляторная запись не найдена: " + entryId));
    }
}
