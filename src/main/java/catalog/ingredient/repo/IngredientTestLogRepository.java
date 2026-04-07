package catalog.ingredient.repo;

import catalog.ingredient.domain.IngredientTestLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientTestLogRepository extends JpaRepository<IngredientTestLog, Long> {
    List<IngredientTestLog> findByIngredient_IngredientIdOrderByTestLogId(Long ingredientId);
}

