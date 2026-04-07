package catalog.ingredient.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import catalog.ingredient.domain.Ingredient;
import catalog.ingredient.domain.IngredientKind;
import catalog.ingredient.service.IngredientService;

@PageTitle("Ингредиенты")
@Route(value = "ingredients", layout = MainLayout.class)
public class IngredientListView extends VerticalLayout {

    private final IngredientService ingredientService;
    private final Grid<Ingredient> grid = new Grid<>(Ingredient.class, false);
    private final TextField query = new TextField("Поиск");
    private final ComboBox<IngredientKind> kind = new ComboBox<>("Тип");

    public IngredientListView(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
        setSizeFull();
        add(new H2("Ингредиенты"));

        query.setPlaceholder("Название, INCI, CAS, EC, CI, синоним");
        query.setClearButtonVisible(true);
        kind.setItems(IngredientKind.values());
        kind.setItemLabelGenerator(value -> switch (value) {
            case SUBSTANCE -> "Вещество";
            case MIXTURE -> "Смесь";
            case MATERIAL -> "Материал";
        });
        Button refresh = new Button("Найти", e -> reload());

        HorizontalLayout filters = new HorizontalLayout(query, kind, refresh);
        filters.setWidthFull();
        filters.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        add(filters);

        grid.addColumn(Ingredient::getIngredientId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Ingredient::getPrimaryName).setHeader("Название").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(Ingredient::getInciName).setHeader("INCI").setAutoWidth(true);
        grid.addColumn(i -> i.getKind() == null ? "" : i.getKind().getDbValue()).setHeader("Тип").setAutoWidth(true);
        grid.addColumn(Ingredient::getCasNo).setHeader("CAS").setAutoWidth(true);
        grid.addColumn(Ingredient::getEcNo).setHeader("EC").setAutoWidth(true);
        grid.addColumn(Ingredient::getCiNo).setHeader("CI").setAutoWidth(true);
        grid.addColumn(Ingredient::getSupplierName).setHeader("Поставщик").setAutoWidth(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                getUI().ifPresent(ui -> ui.navigate(IngredientDetailView.class,
                        new RouteParameters("id", e.getValue().getIngredientId().toString())));
            }
        });
        add(grid);
        expand(grid);
        reload();
    }

    private void reload() {
        grid.setItems(ingredientService.search(query.getValue(), kind.getValue(), 300));
    }
}

