package catalog.ingredient.ui;

import catalog.ingredient.domain.IngredientKind;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import catalog.ingredient.domain.IngredientComponent;
import catalog.ingredient.repo.IngredientComponentRepository;

@PageTitle("Композиции")
@Route(value = "compositions", layout = MainLayout.class)
public class CompositionListView extends VerticalLayout {

    public CompositionListView(IngredientComponentRepository compositionRepository) {
        setSizeFull();
        add(new H2("Композиции ингредиентов"));

        Grid<IngredientComponent> grid = new Grid<>(IngredientComponent.class, false);
        grid.addColumn(IngredientComponent::getIngredientComponentId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(c -> c.getParentIngredient() == null ? "" : c.getParentIngredient().getIngredientId())
                .setHeader("ID владельца").setAutoWidth(true);
        grid.addColumn(c -> c.getParentIngredient() == null ? "" : c.getParentIngredient().getPrimaryName())
                .setHeader("Ингредиент / смесь").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(c -> c.getComponentIngredient() == null ? "" : c.getComponentIngredient().getIngredientId())
                .setHeader("ID компонента").setAutoWidth(true);
        grid.addColumn(c -> c.getComponentIngredient() != null ? c.getComponentIngredient().getPrimaryName() : c.getComponentNameRaw())
                .setHeader("Компонент").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(IngredientComponent::getInciRaw).setHeader("INCI").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getCasRaw).setHeader("CAS").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getEcRaw).setHeader("EC").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getFunctionRaw).setHeader("Функция").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getInputPctRaw).setHeader("Доля").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getSourceSystem).setHeader("Источник").setAutoWidth(true);
        grid.setItems(compositionRepository.findAllWithLinksByParentKind(IngredientKind.MIXTURE));
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null && e.getValue().getParentIngredient() != null) {
                getUI().ifPresent(ui -> ui.navigate(
                        IngredientDetailView.class,
                        new RouteParameters("id", e.getValue().getParentIngredient().getIngredientId().toString())
                ));
            }
        });

        add(grid);
        expand(grid);
    }
}
