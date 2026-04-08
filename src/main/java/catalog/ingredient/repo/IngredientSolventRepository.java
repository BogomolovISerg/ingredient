package catalog.ingredient.repo;

import catalog.ingredient.domain.IngredientSolvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientSolventRepository extends JpaRepository<IngredientSolvent, Long> {
    List<IngredientSolvent> findByIngredient_IngredientIdOrderBySolventNameAscIngredientSolventIdAsc(Long ingredientId);
}
