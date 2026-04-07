package catalog.ingredient.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import catalog.ingredient.domain.FormulaIngredient;
import catalog.ingredient.service.FormulaService;
import catalog.ingredient.service.dto.FormulaDetailDto;

@PageTitle("Карточка формулы")
@Route(value = "formulas/:id", layout = MainLayout.class)
public class FormulaDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final FormulaService formulaService;

    public FormulaDetailView(FormulaService formulaService) {
        this.formulaService = formulaService;
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();
        long formulaId = Long.parseLong(event.getRouteParameters().get("id").orElseThrow());
        FormulaDetailDto detail = formulaService.getFormula(formulaId);

        add(new Button("Назад", e -> getUI().ifPresent(ui -> {
                    if (detail.formula().getProduct() != null) {
                        ui.navigate(
                                FormulaListView.class,
                                new RouteParameters("id", detail.formula().getProduct().getProductId().toString())
                        );
                    } else {
                        ui.navigate(ProductListView.class);
                    }
                })),
                new H2(detail.formula().getDisplayName()));

        FormLayout card = new FormLayout();
        card.addFormItem(new Span(String.valueOf(detail.formula().getFormulaId())), "ID формулы");
        card.addFormItem(new Span(detail.formula().getProduct() == null ? "" : detail.formula().getProduct().getDisplayName()), "Продукт");
        card.addFormItem(new Span(detail.formula().getVersionNo() == null ? "" : detail.formula().getVersionNo().toString()), "Версия");
        card.addFormItem(new Span(nullToEmpty(detail.formula().getStatus())), "Статус");
        card.addFormItem(new Span(detail.formula().getValidFrom() == null ? "" : detail.formula().getValidFrom().toString()), "Действует с");
        card.addFormItem(new Pre(nullToEmpty(detail.formula().getNote())), "Примечание");
        add(card);

        Grid<FormulaIngredient> grid = new Grid<>(FormulaIngredient.class, false);
        grid.addColumn(fi -> fi.getIngredient() == null ? "" : fi.getIngredient().getPrimaryName()).setHeader("Ингредиент").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(fi -> fi.getIngredient() == null ? "" : fi.getIngredient().getInciName()).setHeader("INCI").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(FormulaIngredient::getPercentWw).setHeader("% масс./масс.").setAutoWidth(true);
        grid.addColumn(FormulaIngredient::getFunctionRole).setHeader("Роль").setAutoWidth(true);
        grid.addColumn(FormulaIngredient::getNote).setHeader("Примечание").setAutoWidth(true).setFlexGrow(1);
        grid.setItems(detail.ingredients());
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(e -> {
            var selected = e.getValue();
            if (selected != null && selected.getIngredient() != null) {
                String id = selected.getIngredient().getIngredientId().toString();
                getUI().ifPresent(ui -> ui.navigate(
                        IngredientDetailView.class,
                        new RouteParameters("id", id)
                ));
            }
        });
        add(grid);
        expand(grid);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}

