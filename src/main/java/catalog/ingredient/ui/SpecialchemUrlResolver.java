package catalog.ingredient.ui;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

final class SpecialchemUrlResolver {

    private static final String SPECIALCHEM_BASE_URL = "https://www.specialchem.com";
    private static final String COSMETICS_BASE_URL = SPECIALCHEM_BASE_URL + "/cosmetics";

    private SpecialchemUrlResolver() {
    }

    static String normalize(String url) {
        if (url == null) {
            return "";
        }

        String trimmed = url.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        if (trimmed.startsWith("//")) {
            return "https:" + trimmed;
        }
        if (trimmed.startsWith("~/")) {
            return SPECIALCHEM_BASE_URL + "/" + trimmed.substring(2);
        }
        if (trimmed.startsWith("/")) {
            return SPECIALCHEM_BASE_URL + trimmed;
        }
        if (trimmed.startsWith("mk-")) {
            return COSMETICS_BASE_URL + "/" + trimmed + "/all-formulations";
        }
        if (trimmed.startsWith("ap-")) {
            return COSMETICS_BASE_URL + "/all-formulations?ap=" + encodeQueryValue(trimmed.substring(3));
        }
        if (trimmed.startsWith("af-")) {
            return COSMETICS_BASE_URL + "/all-formulations?af=" + encodeQueryValue(trimmed.substring(3));
        }
        if (trimmed.startsWith("pr-")) {
            return COSMETICS_BASE_URL + "/all-formulations?pr=" + encodeQueryValue(trimmed.substring(3));
        }
        if (trimmed.startsWith("cosmetics/")) {
            return SPECIALCHEM_BASE_URL + "/" + trimmed;
        }
        return SPECIALCHEM_BASE_URL + "/" + trimmed;
    }

    private static String encodeQueryValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
