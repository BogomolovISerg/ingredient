package catalog.ingredient.service.dto;

public record DuplicateCandidateRow(
        String kind,
        String primaryNameNorm,
        String inciNameNorm,
        long count,
        String ingredientIds
) {
}

