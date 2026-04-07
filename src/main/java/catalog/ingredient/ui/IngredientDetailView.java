package catalog.ingredient.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import catalog.ingredient.domain.FormulaIngredient;
import catalog.ingredient.domain.IngredientComponent;
import catalog.ingredient.domain.IngredientEntryLink;
import catalog.ingredient.domain.IngredientIdentifier;
import catalog.ingredient.domain.IngredientName;
import catalog.ingredient.domain.IngredientRequirement;
import catalog.ingredient.domain.IngredientTestLog;
import catalog.ingredient.service.IngredientService;
import catalog.ingredient.service.dto.IngredientDetailDto;

@PageTitle("Карточка ингредиента")
@Route(value = "ingredients/:id", layout = MainLayout.class)
public class IngredientDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final IngredientService ingredientService;

    public IngredientDetailView(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();
        long ingredientId = Long.parseLong(event.getRouteParameters().get("id").orElseThrow());
        IngredientDetailDto detail = ingredientService.getDetail(ingredientId);

        add(new Button("Назад", e -> getUI().ifPresent(ui -> ui.navigate(IngredientListView.class))),
                new H2(detail.ingredient().getDisplayIdentity()));

        FormLayout card = new FormLayout();
        card.addFormItem(new Span(String.valueOf(detail.ingredient().getIngredientId())), "ID");
        card.addFormItem(new Span(detail.ingredient().getKind() == null ? "" : detail.ingredient().getKind().getDbValue()), "Тип");
        card.addFormItem(new Span(nullToEmpty(detail.ingredient().getPrimaryName())), "Название");
        card.addFormItem(new Span(nullToEmpty(detail.ingredient().getInciName())), "INCI");
        card.addFormItem(new Span(nullToEmpty(detail.ingredient().getCasNo())), "CAS");
        card.addFormItem(new Span(nullToEmpty(detail.ingredient().getEcNo())), "EC");
        card.addFormItem(new Span(nullToEmpty(detail.ingredient().getCiNo())), "CI");
        card.addFormItem(new Span(nullToEmpty(detail.ingredient().getSupplierName())), "Поставщик");
        card.addFormItem(new Span(nullToEmpty(detail.ingredient().getSupplierCode())), "Код поставщика");
        card.addFormItem(new Span(nullToEmpty(detail.ingredient().getSdsUrl())), "SDS URL");
        card.addFormItem(new Pre(nullToEmpty(detail.ingredient().getNote())), "Примечание");
        add(card);

        add(sectionNames(detail));
        add(sectionIdentifiers(detail));
        add(sectionRequirements(detail));
        add(sectionTests(detail));
        add(sectionComponents(detail));
        add(sectionRegulatory(detail));
        add(sectionFormulaUsage(detail));
    }

    private Details sectionNames(IngredientDetailDto detail) {
        Grid<IngredientName> grid = new Grid<>(IngredientName.class, false);
        grid.addColumn(IngredientName::getName).setHeader("Имя").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(IngredientName::getNameType).setHeader("Тип").setAutoWidth(true);
        grid.addColumn(IngredientName::getLang).setHeader("Язык").setAutoWidth(true);
        grid.addColumn(IngredientName::isPrimary).setHeader("Основное").setAutoWidth(true);
        grid.setItems(detail.names());
        grid.setAllRowsVisible(true);
        return new Details("Синонимы и названия", grid);
    }

    private Details sectionIdentifiers(IngredientDetailDto detail) {
        Grid<IngredientIdentifier> grid = new Grid<>(IngredientIdentifier.class, false);
        grid.addColumn(IngredientIdentifier::getIdType).setHeader("Тип").setAutoWidth(true);
        grid.addColumn(IngredientIdentifier::getIdValue).setHeader("Значение").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(IngredientIdentifier::isPrimary).setHeader("Основной").setAutoWidth(true);
        grid.addColumn(IngredientIdentifier::getSourceSheet).setHeader("Лист").setAutoWidth(true);
        grid.setItems(detail.identifiers());
        grid.setAllRowsVisible(true);
        return new Details("Идентификаторы", grid);
    }

    private Details sectionRequirements(IngredientDetailDto detail) {
        Grid<IngredientRequirement> grid = new Grid<>(IngredientRequirement.class, false);
        grid.addColumn(IngredientRequirement::getRequirementType).setHeader("Тип").setAutoWidth(true);
        grid.addColumn(IngredientRequirement::getRequirementText).setHeader("Текст").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(IngredientRequirement::getSourceSheet).setHeader("Лист").setAutoWidth(true);
        grid.setItems(detail.requirements());
        grid.setAllRowsVisible(true);
        return new Details("Требования", grid);
    }

    private Details sectionTests(IngredientDetailDto detail) {
        Grid<IngredientTestLog> grid = new Grid<>(IngredientTestLog.class, false);
        grid.addColumn(IngredientTestLog::getTestResult).setHeader("Результат").setAutoWidth(true);
        grid.addColumn(IngredientTestLog::getTestNote).setHeader("Комментарий").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(IngredientTestLog::getSourceSheet).setHeader("Лист").setAutoWidth(true);
        grid.setItems(detail.testLogs());
        grid.setAllRowsVisible(true);
        return new Details("Испытания", grid);
    }

    private Details sectionComponents(IngredientDetailDto detail) {
        Grid<IngredientComponent> grid = new Grid<>(IngredientComponent.class, false);
        grid.addColumn(c -> c.getComponentIngredient() != null ? c.getComponentIngredient().getPrimaryName() : c.getComponentNameRaw())
                .setHeader("Компонент").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(IngredientComponent::getInciRaw).setHeader("INCI").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getCasRaw).setHeader("CAS").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getEcRaw).setHeader("EC").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getFunctionRaw).setHeader("Функция").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getInputPctRaw).setHeader("Доля").setAutoWidth(true);
        grid.setItems(detail.components());
        grid.setAllRowsVisible(true);
        return new Details("Состав смеси", grid);
    }

    private Details sectionRegulatory(IngredientDetailDto detail) {
        Grid<IngredientEntryLink> grid = new Grid<>(IngredientEntryLink.class, false);
        grid.addColumn(link -> link.getEntry().getListType() == null ? "" : link.getEntry().getListType().getDbValue()).setHeader("Тип списка").setAutoWidth(true);
        grid.addColumn(link -> link.getEntry().getEuRefNo()).setHeader("Позиция").setAutoWidth(true);
        grid.addColumn(link -> link.getEntry().getDisplayName()).setHeader("Наименование").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(IngredientEntryLink::getMatchMethod).setHeader("Метод связи").setAutoWidth(true);
        grid.addColumn(link -> link.getConfidence() == null ? "" : link.getConfidence().toPlainString()).setHeader("Уверенность").setAutoWidth(true);
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                getUI().ifPresent(ui -> ui.navigate(RegulatoryDetailView.class,
                        new RouteParameters("id", e.getValue().getEntry().getEntryId().toString())));
            }
        });
        grid.setItems(detail.regulatoryLinks());
        grid.setAllRowsVisible(true);
        return new Details("Регуляторные связи", grid);
    }

    private Details sectionFormulaUsage(IngredientDetailDto detail) {
        Grid<FormulaIngredient> grid = new Grid<>(FormulaIngredient.class, false);
        grid.addColumn(fi -> fi.getFormula() == null || fi.getFormula().getProduct() == null ? "" : fi.getFormula().getProduct().getDisplayName())
                .setHeader("Продукт").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(fi -> fi.getFormula() == null ? "" : fi.getFormula().getFormulaId()).setHeader("Формула").setAutoWidth(true);
        grid.addColumn(fi -> fi.getFormula() == null ? "" : fi.getFormula().getVersionNo()).setHeader("Версия").setAutoWidth(true);
        grid.addColumn(FormulaIngredient::getPercentWw).setHeader("% масс./масс.").setAutoWidth(true);
        grid.addColumn(FormulaIngredient::getFunctionRole).setHeader("Роль").setAutoWidth(true).setFlexGrow(1);
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null && e.getValue().getFormula() != null) {
                getUI().ifPresent(ui -> ui.navigate(FormulaDetailView.class,
                        new RouteParameters("id", e.getValue().getFormula().getFormulaId().toString())));
            }
        });
        grid.setItems(detail.formulaUsages());
        grid.setAllRowsVisible(true);
        return new Details("Использование в формулах", grid);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}

