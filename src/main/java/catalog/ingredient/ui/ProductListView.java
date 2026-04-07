package catalog.ingredient.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import catalog.ingredient.domain.Product;
import catalog.ingredient.service.FormulaService;

@PageTitle("Продукты и формулы")
@Route(value = "products", layout = MainLayout.class)
public class ProductListView extends VerticalLayout {

    private final FormulaService formulaService;
    private final Grid<Product> grid = new Grid<>(Product.class, false);
    private final TextField query = new TextField("Поиск");

    public ProductListView(FormulaService formulaService) {
        this.formulaService = formulaService;
        setSizeFull();
        add(new H2("Продукты и рецептуры"));

        query.setPlaceholder("Название продукта, SKU, категория");
        query.setClearButtonVisible(true);
        Button find = new Button("Найти", e -> reload());
        add(new HorizontalLayout(query, find));

        grid.addColumn(Product::getProductId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Product::getSku).setHeader("SKU").setAutoWidth(true);
        grid.addColumn(Product::getName).setHeader("Название").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(Product::getCategory).setHeader("Категория").setAutoWidth(true);
        grid.addColumn(Product::getDescription).setHeader("Описание").setAutoWidth(true).setFlexGrow(1);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                getUI().ifPresent(ui -> ui.navigate(FormulaListView.class,
                        new RouteParameters("id", e.getValue().getProductId().toString())));
            }
        });

        add(grid);
        expand(grid);
        reload();
    }

    private void reload() {
        grid.setItems(formulaService.searchProducts(query.getValue(), 300));
    }
}
