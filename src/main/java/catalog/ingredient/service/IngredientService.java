package catalog.ingredient.service;

import catalog.ingredient.domain.Ingredient;
import catalog.ingredient.domain.IngredientKind;
import catalog.ingredient.repo.FormulaIngredientRepository;
import catalog.ingredient.repo.IngredientComponentRepository;
import catalog.ingredient.repo.IngredientEntryLinkRepository;
import catalog.ingredient.repo.IngredientRepository;
import catalog.ingredient.repo.IngredientRequirementRepository;
import catalog.ingredient.repo.IngredientTestLogRepository;
import catalog.ingredient.service.dto.IngredientDetailDto;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientRequirementRepository requirementRepository;
    private final IngredientTestLogRepository testLogRepository;
    private final IngredientComponentRepository componentRepository;
    private final IngredientEntryLinkRepository linkRepository;
    private final FormulaIngredientRepository formulaIngredientRepository;

    public IngredientService(
            IngredientRepository ingredientRepository,
            IngredientRequirementRepository requirementRepository,
            IngredientTestLogRepository testLogRepository,
            IngredientComponentRepository componentRepository,
            IngredientEntryLinkRepository linkRepository,
            FormulaIngredientRepository formulaIngredientRepository
    ) {
        this.ingredientRepository = ingredientRepository;
        this.requirementRepository = requirementRepository;
        this.testLogRepository = testLogRepository;
        this.componentRepository = componentRepository;
        this.linkRepository = linkRepository;
        this.formulaIngredientRepository = formulaIngredientRepository;
    }

    public List<Ingredient> search(String query, IngredientKind kind, int limit) {
        return search(query, kind, 0, limit);
    }

    public List<Ingredient> search(String query, IngredientKind kind, int offset, int limit) {
        int pageSize = Math.max(1, limit);
        int pageNumber = Math.max(0, offset / pageSize);
        String normalized = normalize(query);

        return (kind == null
                ? ingredientRepository.searchPage(normalized, PageRequest.of(pageNumber, pageSize))
                : ingredientRepository.searchPageByKind(normalized, kind, PageRequest.of(pageNumber, pageSize)))
                .getContent();
    }

    public int countSearch(String query, IngredientKind kind) {
        String normalized = normalize(query);

        long count = (kind == null)
                ? ingredientRepository.countSearch(normalized)
                : ingredientRepository.countSearchByKind(normalized, kind);

        return count > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) count;
    }

    public IngredientDetailDto getDetail(long ingredientId) {
        Ingredient ingredient = ingredientRepository.findWithNamesAndIdentifiersByIngredientId(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ингредиент не найден: " + ingredientId));

        return new IngredientDetailDto(
                ingredient,
                ingredient.getNames().stream().sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName())).toList(),
                ingredient.getIdentifiers().stream().sorted((a, b) -> a.getIdValue().compareToIgnoreCase(b.getIdValue())).toList(),
                requirementRepository.findByIngredient_IngredientIdOrderByRequirementId(ingredientId),
                testLogRepository.findByIngredient_IngredientIdOrderByTestLogId(ingredientId),
                componentRepository.findByParentIngredient_IngredientIdOrderByIngredientComponentId(ingredientId),
                linkRepository.findByIngredient_IngredientIdOrderByEntry_EntryId(ingredientId),
                formulaIngredientRepository.findUsageByIngredientId(ingredientId)
        );
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}