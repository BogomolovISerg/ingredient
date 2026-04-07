package catalog.ingredient.repo;

import catalog.ingredient.service.dto.SpecialchemKeyValueRow;
import catalog.ingredient.service.dto.SpecialchemValueRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class SpecialchemViewRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    public SpecialchemViewRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
    }


    public List<SpecialchemKeyValueRow> findTechnicalProfileByIngredientId(long ingredientId) {
        List<String> payloads = jdbcClient.sql("""
                select technical_profile_json::text
                from std.v_specialchem_technical_profile_by_ingredient
                where ingredient_id = :ingredientId
                order by std_row_id
                """)
                .param("ingredientId", ingredientId)
                .query(String.class)
                .list();

        Set<SpecialchemKeyValueRow> rows = new LinkedHashSet<>();
        for (String payload : payloads) {
            rows.addAll(parseTechnicalProfile(payload));
        }
        return new ArrayList<>(rows);
    }

    public List<SpecialchemValueRow> findProductsByIngredientId(long ingredientId) {
        return readArrayValues(
                """
                select products_json::text
                from std.v_specialchem_products_by_ingredient
                where ingredient_id = :ingredientId
                order by std_row_id
                """,
                ingredientId,
                "product_name", "name", "title"
        );
    }

    public List<SpecialchemValueRow> findFormulationsByIngredientId(long ingredientId) {
        return readArrayValues(
                """
                select formulations_json::text
                from std.v_specialchem_formulations_by_ingredient
                where ingredient_id = :ingredientId
                order by std_row_id
                """,
                ingredientId,
                "formulation_name", "name", "title"
        );
    }

    public List<SpecialchemValueRow> findAlternativesByIngredientId(long ingredientId) {
        return readArrayValues(
                """
                select alternatives_json::text
                from std.v_specialchem_alternatives_by_ingredient
                where ingredient_id = :ingredientId
                order by std_row_id
                """,
                ingredientId,
                "alternative_name", "name", "title"
        );
    }

    public List<SpecialchemValueRow> findPotentialUseByIngredientId(long ingredientId) {
        return readArrayValues(
                """
                select potential_use_json::text
                from std.v_specialchem_potential_use_by_ingredient
                where ingredient_id = :ingredientId
                order by std_row_id
                """,
                ingredientId,
                "potential_use_name", "use_name", "name", "title", "potential_use"
        );
    }

    private List<SpecialchemValueRow> readArrayValues(String sql, long ingredientId, String... preferredKeys) {
        List<String> payloads = jdbcClient.sql(sql)
                .param("ingredientId", ingredientId)
                .query(String.class)
                .list();

        Set<SpecialchemValueRow> rows = new LinkedHashSet<>();
        for (String payload : payloads) {
            rows.addAll(parseArrayValues(payload, preferredKeys));
        }
        return new ArrayList<>(rows);
    }

    private List<SpecialchemKeyValueRow> parseTechnicalProfile(String payload) {
        List<SpecialchemKeyValueRow> rows = new ArrayList<>();
        if (payload == null || payload.isBlank()) {
            return rows;
        }
        try {
            JsonNode root = objectMapper.readTree(payload);
            if (root != null && root.isObject()) {
                root.fields().forEachRemaining(entry -> {
                    String value = jsonNodeToText(entry.getValue());
                    if (!value.isBlank()) {
                        rows.add(new SpecialchemKeyValueRow(entry.getKey(), value));
                    }
                });
            }
        } catch (Exception ignored) {
            // ignore malformed payloads
        }
        return rows;
    }

    private List<SpecialchemValueRow> parseArrayValues(String payload, String... preferredKeys) {
        List<SpecialchemValueRow> rows = new ArrayList<>();
        if (payload == null || payload.isBlank()) {
            return rows;
        }
        try {
            JsonNode root = objectMapper.readTree(payload);
            if (root == null || !root.isArray()) {
                return rows;
            }
            for (JsonNode item : root) {
                String value = extractValue(item, preferredKeys);
                if (!value.isBlank()) {
                    rows.add(new SpecialchemValueRow(value));
                }
            }
        } catch (Exception ignored) {
            // ignore malformed payloads
        }
        return rows;
    }

    private String extractValue(JsonNode item, String... preferredKeys) {
        if (item == null || item.isNull()) {
            return "";
        }
        if (item.isTextual()) {
            return item.asText("").trim();
        }
        if (item.isObject()) {
            for (String key : preferredKeys) {
                JsonNode preferred = item.get(key);
                if (preferred != null && !preferred.isNull()) {
                    String value = jsonNodeToText(preferred);
                    if (!value.isBlank()) {
                        return value;
                    }
                }
            }
            java.util.Iterator<java.util.Map.Entry<String, JsonNode>> it = item.fields();
            while (it.hasNext()) {
                java.util.Map.Entry<String, JsonNode> entry = it.next();
                JsonNode valueNode = entry.getValue();
                if (valueNode != null && !valueNode.isNull() && valueNode.isValueNode()) {
                    String value = jsonNodeToText(valueNode);
                    if (!value.isBlank()) {
                        return value;
                    }
                }
            }
            return item.toString();
        }
        return jsonNodeToText(item);
    }

    private String jsonNodeToText(JsonNode node) {
        if (node == null || node.isNull()) {
            return "";
        }
        if (node.isTextual()) {
            return node.asText("").trim();
        }
        if (node.isValueNode()) {
            return node.asText("").trim();
        }
        return node.toString();
    }
}
