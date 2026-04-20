package catalog.ingredient.repo;

import catalog.ingredient.service.dto.RelatedFormulationDto;
import catalog.ingredient.service.dto.RelatedFormulationIngredientDto;
import catalog.ingredient.service.dto.RelatedFormulationIngredientMatchDto;
import catalog.ingredient.service.dto.RelatedFormulationPropertyDto;
import catalog.ingredient.service.dto.RelatedFormulationTagDto;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class RelatedFormulationRepository {

    private final JdbcClient jdbcClient;

    public RelatedFormulationRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<RelatedFormulationDto> findByIngredientId(long ingredientId) {
        List<RelatedFormulationBase> formulations = jdbcClient.sql("""
                select irf.ingredient_related_formulation_id,
                       rf.related_formulation_id,
                       rf.formulation_name,
                       rf.title,
                       rf.formulation_url,
                       rf.supplier_name,
                       irf.relation_type,
                       rf.last_updated_raw,
                       rf.download_formulation_url,
                       irf.note,
                       rf.description,
                       rf.properties_text,
                       rf.procedure_text
                from core.ingredient_related_formulation irf
                join core.related_formulation rf
                  on rf.related_formulation_id = irf.related_formulation_id
                where irf.ingredient_id = :ingredientId
                order by lower(rf.formulation_name), rf.related_formulation_id, irf.ingredient_related_formulation_id
                """)
                .param("ingredientId", ingredientId)
                .query((rs, rowNum) -> new RelatedFormulationBase(
                        rs.getLong("ingredient_related_formulation_id"),
                        rs.getLong("related_formulation_id"),
                        rs.getString("formulation_name"),
                        rs.getString("title"),
                        rs.getString("formulation_url"),
                        rs.getString("supplier_name"),
                        rs.getString("relation_type"),
                        rs.getString("last_updated_raw"),
                        rs.getString("download_formulation_url"),
                        rs.getString("note"),
                        rs.getString("description"),
                        rs.getString("properties_text"),
                        rs.getString("procedure_text")
                ))
                .list();

        if (formulations.isEmpty()) {
            return List.of();
        }

        List<Long> formulationIds = formulations.stream()
                .map(RelatedFormulationBase::relatedFormulationId)
                .distinct()
                .toList();

        Map<Long, List<RelatedFormulationPropertyDto>> propertiesByFormulationId = loadProperties(formulationIds);
        Map<Long, List<RelatedFormulationTagDto>> tagsByFormulationId = loadTags(formulationIds);
        Map<Long, List<RelatedFormulationIngredientDto>> ingredientsByFormulationId = loadIngredients(formulationIds);

        List<RelatedFormulationDto> rows = new ArrayList<>();
        for (RelatedFormulationBase formulation : formulations) {
            rows.add(new RelatedFormulationDto(
                    formulation.ingredientRelatedFormulationId(),
                    formulation.relatedFormulationId(),
                    formulation.formulationName(),
                    formulation.title(),
                    formulation.formulationUrl(),
                    formulation.supplierName(),
                    formulation.relationType(),
                    formulation.lastUpdatedRaw(),
                    formulation.downloadFormulationUrl(),
                    formulation.note(),
                    formulation.description(),
                    formulation.propertiesText(),
                    formulation.procedureText(),
                    propertiesByFormulationId.getOrDefault(formulation.relatedFormulationId(), List.of()),
                    tagsByFormulationId.getOrDefault(formulation.relatedFormulationId(), List.of()),
                    ingredientsByFormulationId.getOrDefault(formulation.relatedFormulationId(), List.of())
            ));
        }
        return rows;
    }

    private Map<Long, List<RelatedFormulationPropertyDto>> loadProperties(List<Long> formulationIds) {
        Map<Long, List<RelatedFormulationPropertyDto>> rowsByFormulationId = new LinkedHashMap<>();
        jdbcClient.sql("""
                select related_formulation_id,
                       property_ord,
                       property_name,
                       property_value
                from core.related_formulation_property
                where related_formulation_id in (:formulationIds)
                order by related_formulation_id, property_ord
                """)
                .param("formulationIds", formulationIds)
                .query((rs, rowNum) -> new PropertyRow(
                        rs.getLong("related_formulation_id"),
                        new RelatedFormulationPropertyDto(
                                rs.getInt("property_ord"),
                                rs.getString("property_name"),
                                rs.getString("property_value")
                        )
                ))
                .list()
                .forEach(row -> rowsByFormulationId
                        .computeIfAbsent(row.relatedFormulationId(), unused -> new ArrayList<>())
                        .add(row.property()));
        return rowsByFormulationId;
    }

    private Map<Long, List<RelatedFormulationTagDto>> loadTags(List<Long> formulationIds) {
        Map<Long, List<RelatedFormulationTagDto>> rowsByFormulationId = new LinkedHashMap<>();
        jdbcClient.sql("""
                select related_formulation_id,
                       tag_type,
                       tag_ord,
                       tag_name,
                       tag_url,
                       tag_count
                from core.related_formulation_tag
                where related_formulation_id in (:formulationIds)
                order by related_formulation_id, tag_type, tag_ord
                """)
                .param("formulationIds", formulationIds)
                .query((rs, rowNum) -> new TagRow(
                        rs.getLong("related_formulation_id"),
                        new RelatedFormulationTagDto(
                                rs.getString("tag_type"),
                                rs.getInt("tag_ord"),
                                rs.getString("tag_name"),
                                rs.getString("tag_url"),
                                rs.getObject("tag_count", Integer.class)
                        )
                ))
                .list()
                .forEach(row -> rowsByFormulationId
                        .computeIfAbsent(row.relatedFormulationId(), unused -> new ArrayList<>())
                        .add(row.tag()));
        return rowsByFormulationId;
    }

    private Map<Long, List<RelatedFormulationIngredientDto>> loadIngredients(List<Long> formulationIds) {
        List<IngredientBase> ingredients = jdbcClient.sql("""
                select related_formulation_ingredient_row_id,
                       related_formulation_id,
                       row_num,
                       phase,
                       ingredient_name,
                       ingredient_type,
                       quantity_raw,
                       quantity_value,
                       quantity_unit,
                       match_status
                from core.related_formulation_ingredient_row
                where related_formulation_id in (:formulationIds)
                order by related_formulation_id, row_num
                """)
                .param("formulationIds", formulationIds)
                .query((rs, rowNum) -> new IngredientBase(
                        rs.getLong("related_formulation_ingredient_row_id"),
                        rs.getLong("related_formulation_id"),
                        rs.getInt("row_num"),
                        rs.getString("phase"),
                        rs.getString("ingredient_name"),
                        rs.getString("ingredient_type"),
                        rs.getString("quantity_raw"),
                        rs.getBigDecimal("quantity_value"),
                        rs.getString("quantity_unit"),
                        rs.getString("match_status")
                ))
                .list();

        if (ingredients.isEmpty()) {
            return Map.of();
        }

        List<Long> ingredientRowIds = ingredients.stream()
                .map(IngredientBase::relatedFormulationIngredientRowId)
                .toList();

        Map<Long, List<RelatedFormulationIngredientMatchDto>> matchesByIngredientRowId = loadIngredientMatches(ingredientRowIds);
        Map<Long, List<RelatedFormulationIngredientDto>> rowsByFormulationId = new LinkedHashMap<>();

        for (IngredientBase ingredient : ingredients) {
            rowsByFormulationId
                    .computeIfAbsent(ingredient.relatedFormulationId(), unused -> new ArrayList<>())
                    .add(new RelatedFormulationIngredientDto(
                            ingredient.relatedFormulationIngredientRowId(),
                            ingredient.rowNum(),
                            ingredient.phase(),
                            ingredient.ingredientName(),
                            ingredient.ingredientType(),
                            ingredient.quantityRaw(),
                            ingredient.quantityValue(),
                            ingredient.quantityUnit(),
                            ingredient.matchStatus(),
                            matchesByIngredientRowId.getOrDefault(ingredient.relatedFormulationIngredientRowId(), List.of())
                    ));
        }

        return rowsByFormulationId;
    }

    private Map<Long, List<RelatedFormulationIngredientMatchDto>> loadIngredientMatches(List<Long> ingredientRowIds) {
        Map<Long, List<RelatedFormulationIngredientMatchDto>> rowsByIngredientRowId = new LinkedHashMap<>();
        jdbcClient.sql("""
                select m.related_formulation_ingredient_row_id,
                       m.match_ord,
                       m.ingredient_id,
                       i.primary_name,
                       m.matched_name,
                       m.source_ref
                from core.related_formulation_ingredient_match m
                left join core.ingredient i
                  on i.ingredient_id = m.ingredient_id
                where m.related_formulation_ingredient_row_id in (:ingredientRowIds)
                order by m.related_formulation_ingredient_row_id, m.match_ord
                """)
                .param("ingredientRowIds", ingredientRowIds)
                .query((rs, rowNum) -> new IngredientMatchRow(
                        rs.getLong("related_formulation_ingredient_row_id"),
                        new RelatedFormulationIngredientMatchDto(
                                rs.getInt("match_ord"),
                                rs.getLong("ingredient_id"),
                                rs.getString("primary_name"),
                                rs.getString("matched_name"),
                                rs.getString("source_ref")
                        )
                ))
                .list()
                .forEach(row -> rowsByIngredientRowId
                        .computeIfAbsent(row.relatedFormulationIngredientRowId(), unused -> new ArrayList<>())
                        .add(row.match()));
        return rowsByIngredientRowId;
    }

    private record RelatedFormulationBase(
            Long ingredientRelatedFormulationId,
            Long relatedFormulationId,
            String formulationName,
            String title,
            String formulationUrl,
            String supplierName,
            String relationType,
            String lastUpdatedRaw,
            String downloadFormulationUrl,
            String note,
            String description,
            String propertiesText,
            String procedureText
    ) {
    }

    private record PropertyRow(
            Long relatedFormulationId,
            RelatedFormulationPropertyDto property
    ) {
    }

    private record TagRow(
            Long relatedFormulationId,
            RelatedFormulationTagDto tag
    ) {
    }

    private record IngredientBase(
            Long relatedFormulationIngredientRowId,
            Long relatedFormulationId,
            Integer rowNum,
            String phase,
            String ingredientName,
            String ingredientType,
            String quantityRaw,
            java.math.BigDecimal quantityValue,
            String quantityUnit,
            String matchStatus
    ) {
    }

    private record IngredientMatchRow(
            Long relatedFormulationIngredientRowId,
            RelatedFormulationIngredientMatchDto match
    ) {
    }
}
