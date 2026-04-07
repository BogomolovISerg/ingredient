package catalog.ingredient.repo;

import catalog.ingredient.domain.IngredientComponent;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientComponentRepository extends JpaRepository<IngredientComponent, Long> {

    @EntityGraph(attributePaths = {"componentIngredient"})
    List<IngredientComponent> findByParentIngredient_IngredientIdOrderByIngredientComponentId(Long ingredientId);
}
