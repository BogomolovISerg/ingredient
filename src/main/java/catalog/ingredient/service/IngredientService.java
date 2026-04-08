package catalog.ingredient.service;

import catalog.ingredient.domain.Ingredient;
import catalog.ingredient.domain.IngredientComponent;
import catalog.ingredient.domain.IngredientKind;
import catalog.ingredient.domain.IngredientSolubility;
import catalog.ingredient.domain.IngredientSolvent;
import catalog.ingredient.domain.IngredientWaxProperty;
import catalog.ingredient.repo.FormulaIngredientRepository;
import catalog.ingredient.repo.IngredientComponentRepository;
import catalog.ingredient.repo.IngredientSourceLinkRepository;
import catalog.ingredient.repo.IngredientRepository;
import catalog.ingredient.repo.IngredientRequirementRepository;
import catalog.ingredient.repo.IngredientSolubilityRepository;
import catalog.ingredient.repo.IngredientSolventRepository;
import catalog.ingredient.repo.IngredientTestLogRepository;
import catalog.ingredient.repo.IngredientWaxPropertyRepository;
import catalog.ingredient.repo.SpecialchemViewRepository;
import catalog.ingredient.service.dto.IngredientDetailDto;
import java.time.OffsetDateTime;
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
    private final IngredientSolubilityRepository solubilityRepository;
    private final IngredientSolventRepository solventRepository;
    private final IngredientWaxPropertyRepository waxPropertyRepository;
    private final IngredientSourceLinkRepository sourceLinkRepository;
    private final FormulaIngredientRepository formulaIngredientRepository;
    private final SpecialchemViewRepository specialchemViewRepository;

    public IngredientService(
            IngredientRepository ingredientRepository,
            IngredientRequirementRepository requirementRepository,
            IngredientTestLogRepository testLogRepository,
            IngredientComponentRepository componentRepository,
            IngredientSolubilityRepository solubilityRepository,
            IngredientSolventRepository solventRepository,
            IngredientWaxPropertyRepository waxPropertyRepository,
            IngredientSourceLinkRepository sourceLinkRepository,
            FormulaIngredientRepository formulaIngredientRepository,
            SpecialchemViewRepository specialchemViewRepository
    ) {
        this.ingredientRepository = ingredientRepository;
        this.requirementRepository = requirementRepository;
        this.testLogRepository = testLogRepository;
        this.componentRepository = componentRepository;
        this.solubilityRepository = solubilityRepository;
        this.solventRepository = solventRepository;
        this.waxPropertyRepository = waxPropertyRepository;
        this.sourceLinkRepository = sourceLinkRepository;
        this.formulaIngredientRepository = formulaIngredientRepository;
        this.specialchemViewRepository = specialchemViewRepository;
    }

    public List<Ingredient> searchIngredients(String query, String function, int offset, int limit) {
        String normalizedQuery = normalize(query);
        String normalizedFunction = normalize(function);
        PageRequest pageRequest = pageRequest(offset, limit);

        Page<Ingredient> page = normalizedFunction == null
                ? ingredientRepository.searchPageByKindAndQuery(
                normalizedQuery,
                IngredientKind.SUBSTANCE,
                pageRequest
        )
                : ingredientRepository.searchPageByKindAndFunction(
                normalizedQuery,
                IngredientKind.SUBSTANCE,
                normalizedFunction,
                pageRequest
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
        return ingredientRepository
                .findByKindAndDeletedFalseOrderByPrimaryNameAsc(kind, pageRequest(offset, limit))
                .getContent();
    }

    public List<Ingredient> searchComponentCandidates(long ownerIngredientId, String query, int offset, int limit) {
        return ingredientRepository
                .searchPageExcludingIngredientId(normalize(query), ownerIngredientId, pageRequest(offset, limit))
                .getContent();
    }

    public int countByKind(IngredientKind kind) {
        long count = ingredientRepository.countByKindAndDeletedFalse(kind);
        return saturatingCount(count);
    }

    public int countComponentCandidates(long ownerIngredientId, String query) {
        return saturatingCount(ingredientRepository.countByQueryExcludingIngredientId(normalize(query), ownerIngredientId));
    }

    public int countVisibleIngredients() {
        return saturatingCount(ingredientRepository.countByDeletedFalse());
    }

    public List<String> listFunctions(String prefix, int offset, int limit) {
        String normalizedPrefix = normalize(prefix);
        PageRequest pageRequest = pageRequest(offset, limit);

        return normalizedPrefix == null
                ? componentRepository.findDistinctFunctions(pageRequest)
                : componentRepository.findDistinctFunctionsByPrefix(normalizedPrefix, pageRequest);
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
                solubilityRepository.findByIngredient_IngredientIdOrderByMediumTypeAscSolubilityIdAsc(ingredientId),
                solventRepository.findByIngredient_IngredientIdOrderBySolventNameAscIngredientSolventIdAsc(ingredientId),
                waxPropertyRepository.findByIngredient_IngredientIdOrderByPropertyTypeAscWaxPropertyIdAsc(ingredientId),
                sourceLinkRepository.findByIngredient_IngredientIdOrderByIngredientSourceLinkId(ingredientId),
                formulaIngredientRepository.findUsageByIngredientId(ingredientId),
                specialchemViewRepository.findTechnicalProfileByIngredientId(ingredientId),
                specialchemViewRepository.findProductsByIngredientId(ingredientId),
                specialchemViewRepository.findFormulationsByIngredientId(ingredientId),
                specialchemViewRepository.findAlternativesByIngredientId(ingredientId),
                specialchemViewRepository.findPotentialUseByIngredientId(ingredientId)
        );
    }

    @Transactional
    public Ingredient updateIngredient(long ingredientId, Ingredient edited) {
        Ingredient current = requireIngredient(ingredientId);

        current.applyEditableChangesFrom(edited);
        current.setUpdatedAt(OffsetDateTime.now());

        return ingredientRepository.save(current);
    }

    @Transactional
    public void markDeleted(long ingredientId) {
        Ingredient current = requireIngredient(ingredientId);
        current.setDeleted(Boolean.TRUE);
        current.setUpdatedAt(OffsetDateTime.now());
        ingredientRepository.save(current);
    }

    @Transactional
    public IngredientComponent saveComponent(long ingredientId, IngredientComponent edited) {
        Ingredient mixture = requireMixture(ingredientId);
        IngredientComponent current = edited.getIngredientComponentId() == null
                ? new IngredientComponent()
                : requireComponent(ingredientId, edited.getIngredientComponentId());

        current.setParentIngredient(mixture);
        current.applyEditableChangesFrom(edited);
        current.setComponentIngredient(resolveComponentIngredient(ingredientId, edited.getComponentIngredient()));
        fillComponentFieldsFromLinkedIngredient(current);
        validateComponent(current);
        return componentRepository.save(current);
    }

    @Transactional
    public void deleteComponent(long ingredientId, long ingredientComponentId) {
        requireMixture(ingredientId);
        componentRepository.delete(requireComponent(ingredientId, ingredientComponentId));
    }

    @Transactional
    public IngredientSolubility saveSolubility(long ingredientId, IngredientSolubility edited) {
        IngredientSolubility current = edited.getSolubilityId() == null
                ? new IngredientSolubility()
                : requireSolubility(ingredientId, edited.getSolubilityId());

        current.setIngredient(requireIngredient(ingredientId));
        current.applyEditableChangesFrom(edited);
        return solubilityRepository.save(current);
    }

    @Transactional
    public void deleteSolubility(long ingredientId, long solubilityId) {
        solubilityRepository.delete(requireSolubility(ingredientId, solubilityId));
    }

    @Transactional
    public IngredientSolvent saveSolvent(long ingredientId, IngredientSolvent edited) {
        IngredientSolvent current = edited.getIngredientSolventId() == null
                ? new IngredientSolvent()
                : requireSolvent(ingredientId, edited.getIngredientSolventId());

        current.setIngredient(requireIngredient(ingredientId));
        current.applyEditableChangesFrom(edited);
        return solventRepository.save(current);
    }

    @Transactional
    public void deleteSolvent(long ingredientId, long ingredientSolventId) {
        solventRepository.delete(requireSolvent(ingredientId, ingredientSolventId));
    }

    @Transactional
    public IngredientWaxProperty saveWaxProperty(long ingredientId, IngredientWaxProperty edited) {
        IngredientWaxProperty current = edited.getWaxPropertyId() == null
                ? new IngredientWaxProperty()
                : requireWaxProperty(ingredientId, edited.getWaxPropertyId());

        current.setIngredient(requireIngredient(ingredientId));
        current.applyEditableChangesFrom(edited);
        return waxPropertyRepository.save(current);
    }

    @Transactional
    public void deleteWaxProperty(long ingredientId, long waxPropertyId) {
        waxPropertyRepository.delete(requireWaxProperty(ingredientId, waxPropertyId));
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

    private PageRequest pageRequest(int offset, int limit) {
        int pageSize = Math.max(1, limit);
        int pageNumber = Math.max(0, offset / pageSize);
        return PageRequest.of(pageNumber, pageSize);
    }

    private Ingredient requireIngredient(long ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ингредиент не найден: " + ingredientId));
    }

    private Ingredient requireMixture(long ingredientId) {
        Ingredient ingredient = requireIngredient(ingredientId);
        if (!ingredient.isMixture()) {
            throw new IllegalArgumentException("Состав можно редактировать только у смесей: " + ingredientId);
        }
        return ingredient;
    }

    private IngredientComponent requireComponent(long ingredientId, long ingredientComponentId) {
        IngredientComponent component = componentRepository.findById(ingredientComponentId)
                .orElseThrow(() -> new IllegalArgumentException("Компонент состава не найден: " + ingredientComponentId));
        validateOwnership(ingredientId, component.getParentIngredient(), "Компонент состава");
        return component;
    }

    private IngredientSolubility requireSolubility(long ingredientId, long solubilityId) {
        IngredientSolubility solubility = solubilityRepository.findById(solubilityId)
                .orElseThrow(() -> new IllegalArgumentException("Растворимость не найдена: " + solubilityId));
        validateOwnership(ingredientId, solubility.getIngredient(), "Растворимость");
        return solubility;
    }

    private IngredientSolvent requireSolvent(long ingredientId, long ingredientSolventId) {
        IngredientSolvent solvent = solventRepository.findById(ingredientSolventId)
                .orElseThrow(() -> new IllegalArgumentException("Растворитель не найден: " + ingredientSolventId));
        validateOwnership(ingredientId, solvent.getIngredient(), "Растворитель");
        return solvent;
    }

    private IngredientWaxProperty requireWaxProperty(long ingredientId, long waxPropertyId) {
        IngredientWaxProperty waxProperty = waxPropertyRepository.findById(waxPropertyId)
                .orElseThrow(() -> new IllegalArgumentException("Свойство воска не найдено: " + waxPropertyId));
        validateOwnership(ingredientId, waxProperty.getIngredient(), "Свойство воска");
        return waxProperty;
    }

    private Ingredient resolveComponentIngredient(long ownerIngredientId, Ingredient selected) {
        Long componentIngredientId = selected == null ? null : selected.getIngredientId();
        if (componentIngredientId == null) {
            return null;
        }
        if (componentIngredientId == ownerIngredientId) {
            throw new IllegalArgumentException("Смесь не может содержать саму себя");
        }
        return requireIngredient(componentIngredientId);
    }

    private void fillComponentFieldsFromLinkedIngredient(IngredientComponent component) {
        Ingredient linkedIngredient = component.getComponentIngredient();
        if (linkedIngredient == null) {
            return;
        }

        if (!hasText(component.getComponentNameRaw())) {
            component.setComponentNameRaw(linkedIngredient.getPrimaryName());
        }
        if (!hasText(component.getInciRaw())) {
            component.setInciRaw(linkedIngredient.getInciName());
        }
        if (!hasText(component.getCasRaw())) {
            component.setCasRaw(linkedIngredient.getCasNo());
        }
        if (!hasText(component.getEcRaw())) {
            component.setEcRaw(linkedIngredient.getEcNo());
        }
    }

    private void validateComponent(IngredientComponent component) {
        if (component.getComponentIngredient() == null && !hasText(component.getComponentNameRaw())) {
            throw new IllegalArgumentException("Укажите связанный ингредиент или название компонента");
        }
    }

    private void validateOwnership(long ingredientId, Ingredient owner, String entityName) {
        Long ownerId = owner == null ? null : owner.getIngredientId();
        if (ownerId == null || ownerId != ingredientId) {
            throw new IllegalArgumentException(entityName + " не принадлежит ингредиенту: " + ingredientId);
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
