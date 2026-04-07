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
        return ingredientRepository.search(normalize(query), kind, PageRequest.of(0, Math.max(1, limit)));
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
        return value == null ? null : value.trim();
    }
}

