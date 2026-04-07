package catalog.ingredient.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import catalog.ingredient.domain.Ingredient;
import catalog.ingredient.service.IngredientService;

@PageTitle("Ингредиенты")
@Route(value = "ingredients", layout = MainLayout.class)
public class IngredientListView extends VerticalLayout {

    private final IngredientService ingredientService;
    private final Grid<Ingredient> grid = new Grid<>(Ingredient.class, false);
    private final TextField query = new TextField("Поиск");
    private final ComboBox<String> function = new ComboBox<>("Функция");

    public IngredientListView(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
        setSizeFull();

        add(new H2("Ингредиенты"));

        query.setPlaceholder("Название, INCI, CAS, EC, CI, синоним");
        query.setClearButtonVisible(true);

        function.setPlaceholder("Начните вводить функцию");
        function.setClearButtonVisible(true);
        function.setAllowCustomValue(false);

        function.setDataProvider(
                DataProvider.fromFilteringCallbacks(
                        queryDef -> ingredientService
                                .listFunctions(queryDef.getFilter().orElse(null), queryDef.getOffset(), queryDef.getLimit())
                                .stream(),
                        queryDef -> ingredientService.countFunctions(queryDef.getFilter().orElse(null))
                ),
                filterText -> filterText == null ? null : filterText
        );

        Button refresh = new Button("Найти", e -> reload());

        HorizontalLayout filters = new HorizontalLayout(query, function, refresh);
        filters.setWidthFull();
        filters.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        add(filters);

        grid.addColumn(Ingredient::getIngredientId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Ingredient::getPrimaryName).setHeader("Название").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(Ingredient::getInciName).setHeader("INCI").setAutoWidth(true);
        grid.addColumn(Ingredient::getFunctionDisplay).setHeader("Функция").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(Ingredient::getCasNo).setHeader("CAS").setAutoWidth(true);
        grid.addColumn(Ingredient::getEcNo).setHeader("EC").setAutoWidth(true);
        grid.addColumn(Ingredient::getCiNo).setHeader("CI").setAutoWidth(true);
        grid.addColumn(Ingredient::getSupplierName).setHeader("Поставщик").setAutoWidth(true);

        grid.setSizeFull();

        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                getUI().ifPresent(ui -> ui.navigate(
                        IngredientDetailView.class,
                        new RouteParameters("id", e.getValue().getIngredientId().toString())
                ));
            }
        });

        add(grid);
        expand(grid);

        configureDataProvider();
    }

    private void configureDataProvider() {
        grid.setDataProvider(DataProvider.fromCallbacks(
                queryDef -> ingredientService
                        .searchIngredients(query.getValue(), function.getValue(), queryDef.getOffset(), queryDef.getLimit())
                        .stream(),
                queryDef -> ingredientService.countIngredientSearch(query.getValue(), function.getValue())
        ));
    }

    private void reload() {
        grid.getDataProvider().refreshAll();
    }
}