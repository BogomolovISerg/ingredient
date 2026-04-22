package catalog.ingredient.repo;

import catalog.ingredient.service.dto.RelatedProductDto;
import catalog.ingredient.service.dto.RelatedProductInciMatchDto;
import catalog.ingredient.service.dto.RelatedProductInciRowDto;
import catalog.ingredient.service.dto.RelatedProductPropertyDto;
import catalog.ingredient.service.dto.RelatedProductTagDto;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class RelatedProductRepository {

    private final JdbcClient jdbcClient;

    public RelatedProductRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<RelatedProductDto> findByIngredientId(long ingredientId) {
        List<RelatedProductBase> products = jdbcClient.sql("""
                with product_link as (
                    select related_product_id,
                           min(ingredient_related_product_id) as ingredient_related_product_id,
                           string_agg(distinct relation_type, ', ' order by relation_type) as relation_type,
                           string_agg(distinct nullif(btrim(note), ''), E'\n' order by nullif(btrim(note), ''))
                               filter (where note is not null and btrim(note) <> '') as note
                    from core.ingredient_related_product
                    where ingredient_id = :ingredientId
                    group by related_product_id
                )
                select irp.ingredient_related_product_id,
                       rp.related_product_id,
                       rp.product_name,
                       rp.product_url,
                       rp.title,
                       rp.supplier_name,
                       rp.brand_name,
                       rp.grade_name,
                       irp.relation_type,
                       rp.last_updated_raw,
                       rp.use_level,
                       rp.product_life_cycle_stage,
                       rp.appearance,
                       rp.physical_form,
                       rp.odor,
                       rp.color,
                       rp.bio_based,
                       rp.bio_based_content,
                       rp.chemical_composition,
                       rp.cas_no,
                       rp.ec_no,
                       irp.note,
                       rp.description
                from product_link irp
                join core.related_product rp
                  on rp.related_product_id = irp.related_product_id
                order by lower(rp.product_name), rp.related_product_id
                """)
                .param("ingredientId", ingredientId)
                .query((rs, rowNum) -> new RelatedProductBase(
                        rs.getLong("ingredient_related_product_id"),
                        rs.getLong("related_product_id"),
                        rs.getString("product_name"),
                        rs.getString("product_url"),
                        rs.getString("title"),
                        rs.getString("supplier_name"),
                        rs.getString("brand_name"),
                        rs.getString("grade_name"),
                        rs.getString("relation_type"),
                        rs.getString("last_updated_raw"),
                        rs.getString("use_level"),
                        rs.getString("product_life_cycle_stage"),
                        rs.getString("appearance"),
                        rs.getString("physical_form"),
                        rs.getString("odor"),
                        rs.getString("color"),
                        rs.getString("bio_based"),
                        rs.getString("bio_based_content"),
                        rs.getString("chemical_composition"),
                        rs.getString("cas_no"),
                        rs.getString("ec_no"),
                        rs.getString("note"),
                        rs.getString("description")
                ))
                .list();

        if (products.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = products.stream()
                .map(RelatedProductBase::relatedProductId)
                .distinct()
                .toList();

        Map<Long, List<RelatedProductTagDto>> tagsByProductId = loadTags(productIds);
        Map<Long, List<RelatedProductPropertyDto>> propertiesByProductId = loadProperties(productIds);
        Map<Long, List<RelatedProductInciRowDto>> inciRowsByProductId = loadInciRows(productIds);

        List<RelatedProductDto> rows = new ArrayList<>();
        for (RelatedProductBase product : products) {
            rows.add(new RelatedProductDto(
                    product.ingredientRelatedProductId(),
                    product.relatedProductId(),
                    product.productName(),
                    product.productUrl(),
                    product.title(),
                    product.supplierName(),
                    product.brandName(),
                    product.gradeName(),
                    product.relationType(),
                    product.lastUpdatedRaw(),
                    product.useLevel(),
                    product.productLifeCycleStage(),
                    product.appearance(),
                    product.physicalForm(),
                    product.odor(),
                    product.color(),
                    product.bioBased(),
                    product.bioBasedContent(),
                    product.chemicalComposition(),
                    product.casNo(),
                    product.ecNo(),
                    product.note(),
                    product.description(),
                    tagsByProductId.getOrDefault(product.relatedProductId(), List.of()),
                    propertiesByProductId.getOrDefault(product.relatedProductId(), List.of()),
                    inciRowsByProductId.getOrDefault(product.relatedProductId(), List.of())
            ));
        }
        return rows;
    }

    private Map<Long, List<RelatedProductTagDto>> loadTags(List<Long> productIds) {
        Map<Long, List<RelatedProductTagDto>> rowsByProductId = new LinkedHashMap<>();
        jdbcClient.sql("""
                select related_product_id,
                       tag_type,
                       tag_ord,
                       tag_name,
                       tag_url,
                       tag_count
                from core.related_product_tag
                where related_product_id in (:productIds)
                order by related_product_id, tag_type, tag_ord
                """)
                .param("productIds", productIds)
                .query((rs, rowNum) -> new TagRow(
                        rs.getLong("related_product_id"),
                        new RelatedProductTagDto(
                                rs.getString("tag_type"),
                                rs.getInt("tag_ord"),
                                rs.getString("tag_name"),
                                rs.getString("tag_url"),
                                rs.getObject("tag_count", Integer.class)
                        )
                ))
                .list()
                .forEach(row -> rowsByProductId
                        .computeIfAbsent(row.relatedProductId(), unused -> new ArrayList<>())
                        .add(row.tag()));
        return rowsByProductId;
    }

    private Map<Long, List<RelatedProductPropertyDto>> loadProperties(List<Long> productIds) {
        Map<Long, List<RelatedProductPropertyDto>> rowsByProductId = new LinkedHashMap<>();
        jdbcClient.sql("""
                select related_product_id,
                       property_type,
                       property_ord,
                       property_group,
                       property_name,
                       property_value,
                       si_value,
                       imperial_value,
                       test_condition,
                       test_method
                from core.related_product_property
                where related_product_id in (:productIds)
                order by related_product_id, property_type, property_ord
                """)
                .param("productIds", productIds)
                .query((rs, rowNum) -> new PropertyRow(
                        rs.getLong("related_product_id"),
                        new RelatedProductPropertyDto(
                                rs.getString("property_type"),
                                rs.getInt("property_ord"),
                                rs.getString("property_group"),
                                rs.getString("property_name"),
                                rs.getString("property_value"),
                                rs.getString("si_value"),
                                rs.getString("imperial_value"),
                                rs.getString("test_condition"),
                                rs.getString("test_method")
                        )
                ))
                .list()
                .forEach(row -> rowsByProductId
                        .computeIfAbsent(row.relatedProductId(), unused -> new ArrayList<>())
                        .add(row.property()));
        return rowsByProductId;
    }

    private Map<Long, List<RelatedProductInciRowDto>> loadInciRows(List<Long> productIds) {
        List<InciBase> inciRows = jdbcClient.sql("""
                select related_product_inci_row_id,
                       related_product_id,
                       row_num,
                       inci_name,
                       inci_url,
                       match_status
                from core.related_product_inci_row
                where related_product_id in (:productIds)
                order by related_product_id, row_num
                """)
                .param("productIds", productIds)
                .query((rs, rowNum) -> new InciBase(
                        rs.getLong("related_product_inci_row_id"),
                        rs.getLong("related_product_id"),
                        rs.getInt("row_num"),
                        rs.getString("inci_name"),
                        rs.getString("inci_url"),
                        rs.getString("match_status")
                ))
                .list();

        if (inciRows.isEmpty()) {
            return Map.of();
        }

        List<Long> inciRowIds = inciRows.stream()
                .map(InciBase::relatedProductInciRowId)
                .toList();

        Map<Long, List<RelatedProductInciMatchDto>> matchesByInciRowId = loadInciMatches(inciRowIds);
        Map<Long, List<RelatedProductInciRowDto>> rowsByProductId = new LinkedHashMap<>();

        for (InciBase inciRow : inciRows) {
            rowsByProductId
                    .computeIfAbsent(inciRow.relatedProductId(), unused -> new ArrayList<>())
                    .add(new RelatedProductInciRowDto(
                            inciRow.relatedProductInciRowId(),
                            inciRow.rowNum(),
                            inciRow.inciName(),
                            inciRow.inciUrl(),
                            inciRow.matchStatus(),
                            matchesByInciRowId.getOrDefault(inciRow.relatedProductInciRowId(), List.of())
                    ));
        }

        return rowsByProductId;
    }

    private Map<Long, List<RelatedProductInciMatchDto>> loadInciMatches(List<Long> inciRowIds) {
        Map<Long, List<RelatedProductInciMatchDto>> rowsByInciRowId = new LinkedHashMap<>();
        jdbcClient.sql("""
                select m.related_product_inci_row_id,
                       m.match_ord,
                       m.ingredient_id,
                       i.primary_name,
                       m.matched_name,
                       m.source_ref
                from core.related_product_inci_match m
                left join core.ingredient i
                  on i.ingredient_id = m.ingredient_id
                where m.related_product_inci_row_id in (:inciRowIds)
                order by m.related_product_inci_row_id, m.match_ord
                """)
                .param("inciRowIds", inciRowIds)
                .query((rs, rowNum) -> new InciMatchRow(
                        rs.getLong("related_product_inci_row_id"),
                        new RelatedProductInciMatchDto(
                                rs.getInt("match_ord"),
                                rs.getLong("ingredient_id"),
                                rs.getString("primary_name"),
                                rs.getString("matched_name"),
                                rs.getString("source_ref")
                        )
                ))
                .list()
                .forEach(row -> rowsByInciRowId
                        .computeIfAbsent(row.relatedProductInciRowId(), unused -> new ArrayList<>())
                        .add(row.match()));
        return rowsByInciRowId;
    }

    private record RelatedProductBase(
            Long ingredientRelatedProductId,
            Long relatedProductId,
            String productName,
            String productUrl,
            String title,
            String supplierName,
            String brandName,
            String gradeName,
            String relationType,
            String lastUpdatedRaw,
            String useLevel,
            String productLifeCycleStage,
            String appearance,
            String physicalForm,
            String odor,
            String color,
            String bioBased,
            String bioBasedContent,
            String chemicalComposition,
            String casNo,
            String ecNo,
            String note,
            String description
    ) {
    }

    private record TagRow(
            Long relatedProductId,
            RelatedProductTagDto tag
    ) {
    }

    private record PropertyRow(
            Long relatedProductId,
            RelatedProductPropertyDto property
    ) {
    }

    private record InciBase(
            Long relatedProductInciRowId,
            Long relatedProductId,
            Integer rowNum,
            String inciName,
            String inciUrl,
            String matchStatus
    ) {
    }

    private record InciMatchRow(
            Long relatedProductInciRowId,
            RelatedProductInciMatchDto match
    ) {
    }
}
