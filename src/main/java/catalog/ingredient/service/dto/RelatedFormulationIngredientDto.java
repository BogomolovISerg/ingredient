package catalog.ingredient.service.dto;

import java.math.BigDecimal;
import java.util.List;

public record RelatedFormulationIngredientDto(
        Long relatedFormulationIngredientRowId,
        Integer rowNum,
        String phase,
        String ingredientName,
        String ingredientType,
        String quantityRaw,
        BigDecimal quantityValue,
        String quantityUnit,
        String matchStatus,
        List<RelatedFormulationIngredientMatchDto> matches
) {
}
