package catalog.ingredient.repo;

import catalog.ingredient.domain.IngredientEntryLink;
import catalog.ingredient.domain.IngredientEntryLinkId;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientEntryLinkRepository extends JpaRepository<IngredientEntryLink, IngredientEntryLinkId> {

    @EntityGraph(attributePaths = {"entry"})
    List<IngredientEntryLink> findByIngredient_IngredientIdOrderByEntry_EntryId(Long ingredientId);
}

