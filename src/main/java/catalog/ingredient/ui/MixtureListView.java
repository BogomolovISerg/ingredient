package catalog.ingredient.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import catalog.ingredient.domain.Ingredient;
import catalog.ingredient.domain.IngredientKind;
import catalog.ingredient.service.IngredientService;
import com.vaadin.flow.router.RouteParameters;

@PageTitle("Смеси")
@Route(value = "mixtures", layout = MainLayout.class)
public class MixtureListView extends VerticalLayout {

    public MixtureListView(IngredientService ingredientService) {
        setSizeFull();
        add(new H2("Смеси"));

        Grid<Ingredient> grid = new Grid<>(Ingredient.class, false);
        grid.addColumn(Ingredient::getIngredientId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Ingredient::getPrimaryName).setHeader("Название").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(Ingredient::getInciName).setHeader("INCI").setAutoWidth(true);
        grid.addColumn(Ingredient::getCasNo).setHeader("CAS").setAutoWidth(true);
        grid.addColumn(Ingredient::getEcNo).setHeader("EC").setAutoWidth(true);
        grid.setDataProvider(DataProvider.fromCallbacks(
                query -> ingredientService.search(null, IngredientKind.MIXTURE, query.getOffset(), query.getLimit()).stream(),
                query -> ingredientService.countSearch(null, IngredientKind.MIXTURE)
        ));
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
    }
}
