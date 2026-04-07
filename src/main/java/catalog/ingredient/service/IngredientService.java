package catalog.ingredient.service;

import catalog.ingredient.domain.Ingredient;
import catalog.ingredient.domain.IngredientKind;
import catalog.ingredient.repo.FormulaIngredientRepository;
import catalog.ingredient.repo.IngredientComponentRepository;
import catalog.ingredient.repo.IngredientEntryLinkRepository;
import catalog.ingredient.repo.IngredientRepository;
import catalog.ingredient.repo.IngredientRequirementRepository;
import catalog.ingredient.repo.IngredientTestLogRepository;
import catalog.ingredient.repo.SpecialchemViewRepository;
import catalog.ingredient.service.dto.IngredientDetailDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
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
    private final SpecialchemViewRepository specialchemViewRepository;

    public IngredientService(
            IngredientRepository ingredientRepository,
            IngredientRequirementRepository requirementRepository,
            IngredientTestLogRepository testLogRepository,
            IngredientComponentRepository componentRepository,
            IngredientEntryLinkRepository linkRepository,
            FormulaIngredientRepository formulaIngredientRepository,
            SpecialchemViewRepository specialchemViewRepository
    ) {
        this.ingredientRepository = ingredientRepository;
        this.requirementRepository = requirementRepository;
        this.testLogRepository = testLogRepository;
        this.componentRepository = componentRepository;
        this.linkRepository = linkRepository;
        this.formulaIngredientRepository = formulaIngredientRepository;
        this.specialchemViewRepository = specialchemViewRepository;
    }

    public List<Ingredient> searchIngredients(String query, String function, int offset, int limit) {
        int pageSize = Math.max(1, limit);
        int pageNumber = Math.max(0, offset / pageSize);
        String normalizedQuery = normalize(query);
        String normalizedFunction = normalize(function);

        Page<Ingredient> page = normalizedFunction == null
                ? ingredientRepository.searchPageByKindAndQuery(
                normalizedQuery,
                IngredientKind.SUBSTANCE,
                PageRequest.of(pageNumber, pageSize)
        )
                : ingredientRepository.searchPageByKindAndFunction(
                normalizedQuery,
                IngredientKind.SUBSTANCE,
                normalizedFunction,
                PageRequest.of(pageNumber, pageSize)
        );

        List<Ingredient> ingredients = page.getContent();
        enrichFunctions(ingredients);
        return ingredients;
    }

    public int countIngredientSearch(String query, String function) {
        String normalizedQuery = normalize(query);
        String normalizedFunction = normalize(function);

        long count = normalizedFunction == null
                ? ingredientRepository.countByKindAndQuery(normalizedQuery, IngredientKind.SUBSTANCE)
                : ingredientRepository.countByKindAndFunction(normalizedQuery, IngredientKind.SUBSTANCE, normalizedFunction);

        return saturatingCount(count);
    }

    public List<Ingredient> searchByKind(IngredientKind kind, int offset, int limit) {
        int pageSize = Math.max(1, limit);
        int pageNumber = Math.max(0, offset / pageSize);
        return ingredientRepository
                .findByKindOrderByPrimaryNameAsc(kind, PageRequest.of(pageNumber, pageSize))
                .getContent();
    }

    public int countByKind(IngredientKind kind) {
        long count = ingredientRepository.countByKind(kind);
        return saturatingCount(count);
    }

    public List<String> listFunctions(String prefix, int offset, int limit) {
        int pageSize = Math.max(1, limit);
        int pageNumber = Math.max(0, offset / pageSize);
        String normalizedPrefix = normalize(prefix);

        return normalizedPrefix == null
                ? componentRepository.findDistinctFunctions(PageRequest.of(pageNumber, pageSize))
                : componentRepository.findDistinctFunctionsByPrefix(normalizedPrefix, PageRequest.of(pageNumber, pageSize));
    }

    public int countFunctions(String prefix) {
        String normalizedPrefix = normalize(prefix);
        long count = normalizedPrefix == null
                ? componentRepository.countDistinctFunctions()
                : componentRepository.countDistinctFunctionsByPrefix(normalizedPrefix);

        return saturatingCount(count);
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
                formulaIngredientRepository.findUsageByIngredientId(ingredientId),
                specialchemViewRepository.findTechnicalProfileByIngredientId(ingredientId),
                specialchemViewRepository.findProductsByIngredientId(ingredientId),
                specialchemViewRepository.findFormulationsByIngredientId(ingredientId),
                specialchemViewRepository.findAlternativesByIngredientId(ingredientId),
                specialchemViewRepository.findPotentialUseByIngredientId(ingredientId)
        );
    }

    private void enrichFunctions(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return;
        }

        List<Long> ids = ingredients.stream()
                .map(Ingredient::getIngredientId)
                .toList();

        Map<Long, String> functionByIngredientId = componentRepository.findFunctionRowsByIngredientIds(ids)
                .stream()
                .collect(Collectors.groupingBy(
                        IngredientComponentRepository.IngredientFunctionProjection::getIngredientId,
                        Collectors.mapping(
                                IngredientComponentRepository.IngredientFunctionProjection::getFunctionText,
                                Collectors.collectingAndThen(
                                        Collectors.toCollection(java.util.TreeSet::new),
                                        values -> String.join(", ", values)
                                )
                        )
                ));

        for (Ingredient ingredient : ingredients) {
            ingredient.setFunctionDisplay(functionByIngredientId.getOrDefault(ingredient.getIngredientId(), ""));
        }
    }
    private int saturatingCount(long count) {
        return count > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) count;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}