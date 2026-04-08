package catalog.ingredient.repo;

import catalog.ingredient.domain.IngredientWaxProperty;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientWaxPropertyRepository extends JpaRepository<IngredientWaxProperty, Long> {
    List<IngredientWaxProperty> findByIngredient_IngredientIdOrderByPropertyTypeAscWaxPropertyIdAsc(Long ingredientId);
}
