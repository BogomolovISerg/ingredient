package catalog.ingredient.repo;

import catalog.ingredient.domain.IngredientSourceLink;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientSourceLinkRepository extends JpaRepository<IngredientSourceLink, Long> {

    List<IngredientSourceLink> findByIngredient_IngredientIdOrderByIngredientSourceLinkId(Long ingredientId);
}