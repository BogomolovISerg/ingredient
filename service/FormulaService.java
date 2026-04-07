package catalog.ingredient.service;

import catalog.ingredient.domain.Formula;
import catalog.ingredient.domain.FormulaIngredient;
import catalog.ingredient.domain.Product;
import catalog.ingredient.repo.FormulaIngredientRepository;
import catalog.ingredient.repo.FormulaRepository;
import catalog.ingredient.repo.ProductRepository;
import catalog.ingredient.service.dto.FormulaDetailDto;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FormulaService {

    private final ProductRepository productRepository;
    private final FormulaRepository formulaRepository;
    private final FormulaIngredientRepository formulaIngredientRepository;

    public FormulaService(ProductRepository productRepository,
                          FormulaRepository formulaRepository,
                          FormulaIngredientRepository formulaIngredientRepository) {
        this.productRepository = productRepository;
        this.formulaRepository = formulaRepository;
        this.formulaIngredientRepository = formulaIngredientRepository;
    }

    public List<Product> searchProducts(String query, int limit) {
        return productRepository.search(normalize(query), PageRequest.of(0, Math.max(1, limit)));
    }

    public List<Formula> searchFormulas(String query, int limit) {
        return formulaRepository.search(normalize(query), PageRequest.of(0, Math.max(1, limit)));
    }

    public List<Formula> findByProduct(long productId) {
        return formulaRepository.findByProduct_ProductIdOrderByVersionNoDesc(productId);
    }

    public FormulaDetailDto getFormula(long formulaId) {
        Formula formula = formulaRepository.findByFormulaId(formulaId)
                .orElseThrow(() -> new IllegalArgumentException("Формула не найдена: " + formulaId));
        List<FormulaIngredient> ingredients = formulaIngredientRepository
                .findByFormulaId(formulaId);
        return new FormulaDetailDto(formula, ingredients);
    }

    public List<FormulaIngredient> findUsageByIngredient(long ingredientId) {
        return formulaIngredientRepository
                .findUsageByIngredientId(ingredientId);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}

