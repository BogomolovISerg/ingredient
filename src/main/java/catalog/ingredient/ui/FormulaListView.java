package catalog.ingredient.ui;


import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import catalog.ingredient.domain.Formula;
import catalog.ingredient.service.FormulaService;

@PageTitle("Формулы продукта")
@Route(value = "products/:id/formulas", layout = MainLayout.class)
public class FormulaListView extends VerticalLayout implements BeforeEnterObserver {

    private final FormulaService formulaService;

    public FormulaListView(FormulaService formulaService) {
        this.formulaService = formulaService;
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();
        long productId = Long.parseLong(event.getRouteParameters().get("id").orElseThrow());

        add(new H2("Формулы продукта"), new Paragraph("Выберите версию рецептуры для просмотра состава."));

        Grid<Formula> grid = new Grid<>(Formula.class, false);
        grid.addColumn(Formula::getFormulaId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(f -> f.getProduct() == null ? "" : f.getProduct().getDisplayName()).setHeader("Продукт").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(Formula::getVersionNo).setHeader("Версия").setAutoWidth(true);
        grid.addColumn(Formula::getStatus).setHeader("Статус").setAutoWidth(true);
        grid.addColumn(Formula::getValidFrom).setHeader("Действует с").setAutoWidth(true);
        grid.addColumn(Formula::getNote).setHeader("Примечание").setAutoWidth(true).setFlexGrow(1);
        grid.setItems(formulaService.findByProduct(productId));
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                getUI().ifPresent(ui -> ui.navigate(FormulaDetailView.class,
                        new RouteParameters("id", e.getValue().getFormulaId().toString())));
            }
        });
        add(grid);
        expand(grid);
    }
}

