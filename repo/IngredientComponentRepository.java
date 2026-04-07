package catalog.ingredient.repo;

import catalog.ingredient.domain.IngredientComponent;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IngredientComponentRepository extends JpaRepository<IngredientComponent, Long> {

    @EntityGraph(attributePaths = {"componentIngredient"})
    List<IngredientComponent> findByParentIngredient_IngredientIdOrderByIngredientComponentId(Long ingredientId);

    @EntityGraph(attributePaths = {"parentIngredient", "componentIngredient"})
    @Query("""
            select c from IngredientComponent c
            order by c.parentIngredient.ingredientId asc, c.ingredientComponentId asc
            """)
    List<IngredientComponent> findAllWithLinks();
}
