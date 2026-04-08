package catalog.ingredient.repo;

import catalog.ingredient.domain.IngredientSolubility;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientSolubilityRepository extends JpaRepository<IngredientSolubility, Long> {
    List<IngredientSolubility> findByIngredient_IngredientIdOrderByMediumTypeAscSolubilityIdAsc(Long ingredientId);
}
