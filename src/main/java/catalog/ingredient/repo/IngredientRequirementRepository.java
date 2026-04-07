package catalog.ingredient.repo;

import catalog.ingredient.domain.IngredientRequirement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRequirementRepository extends JpaRepository<IngredientRequirement, Long> {
    List<IngredientRequirement> findByIngredient_IngredientIdOrderByRequirementId(Long ingredientId);
}

