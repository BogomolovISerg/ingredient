package catalog.ingredient.service.dto;

import java.util.List;

public record RelatedProductInciRowDto(
        Long relatedProductInciRowId,
        Integer rowNum,
        String inciName,
        String inciUrl,
        String matchStatus,
        List<RelatedProductInciMatchDto> matches
) {
}
