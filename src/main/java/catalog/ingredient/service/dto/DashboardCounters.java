package catalog.ingredient.service.dto;

public record DashboardCounters(
        long totalIngredients,
        long totalMixtures,
        long totalRegulatoryEntries,
        long totalProducts,
        long totalFormulas,
        long totalRequirements,
        long totalTests,
        long safeDuplicateGroups
) {
}

