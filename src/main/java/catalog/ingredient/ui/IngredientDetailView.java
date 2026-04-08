package catalog.ingredient.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import catalog.ingredient.domain.FormulaIngredient;
import catalog.ingredient.domain.Ingredient;
import catalog.ingredient.domain.IngredientComponent;
import catalog.ingredient.domain.IngredientSourceLink;
import catalog.ingredient.domain.IngredientIdentifier;
import catalog.ingredient.domain.IngredientName;
import catalog.ingredient.domain.IngredientRequirement;
import catalog.ingredient.domain.IngredientTestLog;
import catalog.ingredient.domain.IngredientKind;
import catalog.ingredient.service.IngredientService;
import catalog.ingredient.service.dto.IngredientDetailDto;
import catalog.ingredient.service.dto.SpecialchemKeyValueRow;
import catalog.ingredient.service.dto.SpecialchemValueRow;
import com.vaadin.flow.component.Component;

@PageTitle("Карточка ингредиента")
@Route(value = "ingredients/:id", layout = MainLayout.class)
public class IngredientDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final IngredientService ingredientService;
    private Long ingredientId;
    private boolean editMode;

    public IngredientDetailView(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
        setSizeFull();
        setWidthFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        ingredientId = Long.parseLong(event.getRouteParameters().get("id").orElseThrow());
        editMode = false;
        renderView();
    }

    private void renderView() {
        removeAll();

        IngredientDetailDto detail = ingredientService.getDetail(ingredientId);
        Ingredient ingredient = detail.ingredient();

        add(buildHeader(ingredient));
        add(editMode ? buildEditCard(ingredient) : buildReadCard(ingredient));

        add(sectionNames(detail));
        add(sectionIdentifiers(detail));
        add(sectionRequirements(detail));
        add(sectionTests(detail));

        if (ingredient.isMixture()) {
            add(sectionComposition(detail));
        }

        add(sectionSourceLinks(detail));
        add(sectionFormulaUsage(detail));
        addSpecialchemSections(detail);
    }

    private HorizontalLayout buildHeader(Ingredient ingredient) {
        Button back = new Button("Назад", e -> getUI().ifPresent(ui -> ui.getPage().getHistory().back()));

        H2 title = new H2(ingredient.getDisplayIdentity());
        title.getStyle().set("margin", "0");

        HorizontalLayout actions = new HorizontalLayout();
        if (!editMode) {
            Button edit = new Button("Редактировать", e -> {
                editMode = true;
                renderView();
            });
            Button delete = new Button("Удалить", e -> deleteIngredient(ingredient));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(edit, delete);
        }

        HorizontalLayout header = new HorizontalLayout(back, title, actions);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.expand(title);
        return header;
    }

    private VerticalLayout buildEditCard(Ingredient source) {
        Ingredient edited = copyIngredient(source);
        Binder<Ingredient> binder = new Binder<>(Ingredient.class);
        binder.setBean(edited);

        FormLayout form = new FormLayout();
        form.setWidthFull();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("900px", 2)
        );

        TextField idField = new TextField("ID");
        idField.setValue(String.valueOf(source.getIngredientId()));
        idField.setReadOnly(true);

        Select<IngredientKind> kindField = new Select<>();
        kindField.setLabel("Тип");
        kindField.setItems(IngredientKind.values());
        kindField.setItemLabelGenerator(kind -> kind == null ? "" : kind.getDbValue());
        binder.bind(kindField, Ingredient::getKind, Ingredient::setKind);

        TextField primaryNameField = textField("Название");
        binder.forField(primaryNameField)
                .asRequired("Название обязательно")
                .bind(Ingredient::getPrimaryName, Ingredient::setPrimaryName);

        TextField inciNameField = textField("INCI");
        binder.bind(inciNameField, Ingredient::getInciName, Ingredient::setInciName);

        TextField casNoField = textField("CAS");
        binder.bind(casNoField, Ingredient::getCasNo, Ingredient::setCasNo);

        TextField ecNoField = textField("EC");
        binder.bind(ecNoField, Ingredient::getEcNo, Ingredient::setEcNo);

        TextField ciNoField = textField("CI");
        binder.bind(ciNoField, Ingredient::getCiNo, Ingredient::setCiNo);

        TextField supplierNameField = textField("Поставщик");
        binder.bind(supplierNameField, Ingredient::getSupplierName, Ingredient::setSupplierName);

        TextField supplierCodeField = textField("Код поставщика");
        binder.bind(supplierCodeField, Ingredient::getSupplierCode, Ingredient::setSupplierCode);

        TextField sdsUrlField = textField("SDS URL");
        binder.bind(sdsUrlField, Ingredient::getSdsUrl, Ingredient::setSdsUrl);

        TextArea descriptionRuField = textArea("Описание RU", 6);
        binder.bind(descriptionRuField, Ingredient::getDescriptionRu, Ingredient::setDescriptionRu);

        TextArea descriptionEnField = textArea("Описание EN", 6);
        binder.bind(descriptionEnField, Ingredient::getDescriptionEn, Ingredient::setDescriptionEn);

        TextArea noteField = textArea("Примечание", 4);
        binder.bind(noteField, Ingredient::getNote, Ingredient::setNote);

        TextField specialchemUrlField = textField("Источник SpecialChem");
        binder.bind(specialchemUrlField, Ingredient::getSpecialchemUrl, Ingredient::setSpecialchemUrl);

        TextArea specialchemOriginRuField = textArea("Происхождение RU", 5);
        binder.bind(specialchemOriginRuField, Ingredient::getSpecialchemOriginRu, Ingredient::setSpecialchemOriginRu);

        TextArea specialchemOriginEnField = textArea("Происхождение EN", 5);
        binder.bind(specialchemOriginEnField, Ingredient::getSpecialchemOriginEn, Ingredient::setSpecialchemOriginEn);

        TextArea specialchemSafetyProfileRuField = textArea("Профиль безопасности RU", 5);
        binder.bind(specialchemSafetyProfileRuField, Ingredient::getSpecialchemSafetyProfileRu, Ingredient::setSpecialchemSafetyProfileRu);

        TextArea specialchemSafetyProfileEnField = textArea("Профиль безопасности EN", 5);
        binder.bind(specialchemSafetyProfileEnField, Ingredient::getSpecialchemSafetyProfileEn, Ingredient::setSpecialchemSafetyProfileEn);

        TextArea specialchemChemIupacNameRuField = textArea("IUPAC-наименование RU", 3);
        binder.bind(specialchemChemIupacNameRuField, Ingredient::getSpecialchemChemIupacNameRu, Ingredient::setSpecialchemChemIupacNameRu);

        TextArea specialchemChemIupacNameEnField = textArea("IUPAC-наименование EN", 3);
        binder.bind(specialchemChemIupacNameEnField, Ingredient::getSpecialchemChemIupacNameEn, Ingredient::setSpecialchemChemIupacNameEn);

        TextArea specialchemUsageTextRuField = textArea("Назначение и применение RU", 6);
        binder.bind(specialchemUsageTextRuField, Ingredient::getSpecialchemUsageTextRu, Ingredient::setSpecialchemUsageTextRu);

        TextArea specialchemUsageTextEnField = textArea("Назначение и применение EN", 6);
        binder.bind(specialchemUsageTextEnField, Ingredient::getSpecialchemUsageTextEn, Ingredient::setSpecialchemUsageTextEn);

        form.add(idField, kindField, primaryNameField, inciNameField, casNoField, ecNoField, ciNoField,
                supplierNameField, supplierCodeField, sdsUrlField,
                descriptionRuField, descriptionEnField, noteField,
                specialchemUrlField,
                specialchemOriginRuField, specialchemOriginEnField,
                specialchemSafetyProfileRuField, specialchemSafetyProfileEnField,
                specialchemChemIupacNameRuField, specialchemChemIupacNameEnField,
                specialchemUsageTextRuField, specialchemUsageTextEnField);

        form.setColspan(descriptionRuField, 2);
        form.setColspan(descriptionEnField, 2);
        form.setColspan(noteField, 2);
        form.setColspan(specialchemUrlField, 2);
        form.setColspan(specialchemOriginRuField, 2);
        form.setColspan(specialchemOriginEnField, 2);
        form.setColspan(specialchemSafetyProfileRuField, 2);
        form.setColspan(specialchemSafetyProfileEnField, 2);
        form.setColspan(specialchemChemIupacNameRuField, 2);
        form.setColspan(specialchemChemIupacNameEnField, 2);
        form.setColspan(specialchemUsageTextRuField, 2);
        form.setColspan(specialchemUsageTextEnField, 2);

        Button save = new Button("Сохранить", e -> {
            if (binder.validate().isOk()) {
                ingredientService.updateIngredient(ingredientId, edited);
                editMode = false;
                Notification.show("Изменения сохранены");
                renderView();
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Отмена", e -> {
            editMode = false;
            renderView();
        });

        Button delete = new Button("Удалить", e -> deleteIngredient(source));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttons = new HorizontalLayout(save, cancel, delete);

        VerticalLayout wrapper = new VerticalLayout(form, buttons);
        wrapper.setPadding(false);
        wrapper.setSpacing(true);
        wrapper.setWidthFull();
        return wrapper;
    }

    private FormLayout buildReadCard(Ingredient ingredient) {
        FormLayout card = new FormLayout();
        card.setWidthFull();
        card.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("900px", 2)
        );

        card.addFormItem(new Span(String.valueOf(ingredient.getIngredientId())), "ID");
        card.addFormItem(new Span(ingredient.getKind() == null ? "" : ingredient.getKind().getDbValue()), "Тип");
        card.addFormItem(new Span(nullToEmpty(ingredient.getPrimaryName())), "Название");
        card.addFormItem(new Span(nullToEmpty(ingredient.getInciName())), "INCI");
        card.addFormItem(new Span(nullToEmpty(ingredient.getCasNo())), "CAS");
        card.addFormItem(new Span(nullToEmpty(ingredient.getEcNo())), "EC");
        card.addFormItem(new Span(nullToEmpty(ingredient.getCiNo())), "CI");
        card.addFormItem(new Span(nullToEmpty(ingredient.getSupplierName())), "Поставщик");
        card.addFormItem(new Span(nullToEmpty(ingredient.getSupplierCode())), "Код поставщика");
        card.addFormItem(new Span(nullToEmpty(ingredient.getSdsUrl())), "SDS URL");
        card.addFormItem(wrapColumnText(ingredient.getDescriptionRu()), "Описание RU");
        card.addFormItem(wrapColumnText(ingredient.getDescriptionEn()), "Описание EN");
        card.addFormItem(wrapColumnText(ingredient.getNote()), "Примечание");
        card.addFormItem(buildExternalLink(ingredient.getSpecialchemUrl()), "Источник SpecialChem");
        card.addFormItem(wrapColumnText(ingredient.getSpecialchemOriginRu()), "Происхождение RU");
        card.addFormItem(wrapColumnText(ingredient.getSpecialchemOriginEn()), "Происхождение EN");
        card.addFormItem(wrapColumnText(ingredient.getSpecialchemSafetyProfileRu()), "Профиль безопасности RU");
        card.addFormItem(wrapColumnText(ingredient.getSpecialchemSafetyProfileEn()), "Профиль безопасности EN");
        card.addFormItem(wrapColumnText(ingredient.getSpecialchemChemIupacNameRu()), "IUPAC-наименование RU");
        card.addFormItem(wrapColumnText(ingredient.getSpecialchemChemIupacNameEn()), "IUPAC-наименование EN");
        card.addFormItem(wrapColumnText(ingredient.getSpecialchemUsageTextRu()), "Назначение и применение RU");
        card.addFormItem(wrapColumnText(ingredient.getSpecialchemUsageTextEn()), "Назначение и применение EN");
        return card;
    }

    private Ingredient copyIngredient(Ingredient source) {
        Ingredient copy = new Ingredient();
        copy.setIngredientId(source.getIngredientId());
        copy.setKind(source.getKind());
        copy.setPrimaryName(source.getPrimaryName());
        copy.setInciName(source.getInciName());
        copy.setCasNo(source.getCasNo());
        copy.setEcNo(source.getEcNo());
        copy.setCiNo(source.getCiNo());
        copy.setSupplierName(source.getSupplierName());
        copy.setSupplierCode(source.getSupplierCode());
        copy.setSdsUrl(source.getSdsUrl());
        copy.setDescriptionRu(source.getDescriptionRu());
        copy.setDescriptionEn(source.getDescriptionEn());
        copy.setNote(source.getNote());
        copy.setSpecialchemUrl(source.getSpecialchemUrl());
        copy.setSpecialchemOriginRu(source.getSpecialchemOriginRu());
        copy.setSpecialchemOriginEn(source.getSpecialchemOriginEn());
        copy.setSpecialchemSafetyProfileRu(source.getSpecialchemSafetyProfileRu());
        copy.setSpecialchemSafetyProfileEn(source.getSpecialchemSafetyProfileEn());
        copy.setSpecialchemChemIupacNameRu(source.getSpecialchemChemIupacNameRu());
        copy.setSpecialchemChemIupacNameEn(source.getSpecialchemChemIupacNameEn());
        copy.setSpecialchemUsageTextRu(source.getSpecialchemUsageTextRu());
        copy.setSpecialchemUsageTextEn(source.getSpecialchemUsageTextEn());
        copy.setDeleted(source.getDeleted());
        return copy;
    }

    private void deleteIngredient(Ingredient ingredient) {
        ingredientService.markDeleted(ingredient.getIngredientId());
        Notification.show("Ингредиент помечен на удаление");

        Class<? extends Component> targetView =
                ingredient.isMixture() ? CompositionListView.class : IngredientListView.class;

        getUI().ifPresent(ui -> ui.navigate(targetView));
    }

    private TextField textField(String label) {
        TextField field = new TextField(label);
        field.setWidthFull();
        return field;
    }

    private TextArea textArea(String label, int minRows) {
        TextArea field = new TextArea(label);
        field.setWidthFull();
        field.setMinHeight((minRows * 24 + 48) + "px");
        return field;
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

    private Details sectionComposition(IngredientDetailDto detail) {
        Grid<IngredientComponent> grid = new Grid<>(IngredientComponent.class, false);
        grid.addColumn(c -> {
                    if (c.getComponentIngredient() != null) {
                        return nullToEmpty(c.getComponentIngredient().getPrimaryName());
                    }
                    return nullToEmpty(c.getComponentNameRaw());
                })
                .setHeader("Компонент")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(IngredientComponent::getInciRaw).setHeader("INCI").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getCasRaw).setHeader("CAS").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getEcRaw).setHeader("EC").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getFunctionRaw).setHeader("Функция").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getInputPctRaw).setHeader("Доля").setAutoWidth(true);
        grid.setItems(detail.components());
        grid.setAllRowsVisible(true);
        return new Details("Состав", grid);
    }

    private void addSpecialchemSections(IngredientDetailDto detail) {
        if (!detail.technicalProfile().isEmpty()) {
            add(sectionTechnicalProfile(detail));
        }
        if (!detail.products().isEmpty()) {
            add(sectionSingleValueList("Products", "Продукт", detail.products()));
        }
        if (!detail.formulations().isEmpty()) {
            add(sectionSingleValueList("Formulations", "Формула", detail.formulations()));
        }
        if (!detail.alternatives().isEmpty()) {
            add(sectionSingleValueList("Alternatives", "Альтернатива", detail.alternatives()));
        }
        if (!detail.potentialUse().isEmpty()) {
            add(sectionSingleValueList("Potential Use", "Потенциальное применение", detail.potentialUse()));
        }
    }

    private Details sectionTechnicalProfile(IngredientDetailDto detail) {
        Grid<SpecialchemKeyValueRow> grid = new Grid<>(SpecialchemKeyValueRow.class, false);
        grid.addColumn(SpecialchemKeyValueRow::name).setHeader("Параметр").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(SpecialchemKeyValueRow::value).setHeader("Значение").setAutoWidth(true).setFlexGrow(2);
        grid.setItems(detail.technicalProfile());
        grid.setAllRowsVisible(true);
        return new Details("Technical profile", grid);
    }

    private Details sectionSingleValueList(String title, String columnHeader, java.util.List<SpecialchemValueRow> rows) {
        Grid<SpecialchemValueRow> grid = new Grid<>(SpecialchemValueRow.class, false);
        grid.addColumn(SpecialchemValueRow::value).setHeader(columnHeader).setAutoWidth(true).setFlexGrow(1);
        grid.setItems(rows);
        grid.setAllRowsVisible(true);
        return new Details(title, grid);
    }

    private Details sectionSourceLinks(IngredientDetailDto detail) {
        Grid<IngredientSourceLink> grid = new Grid<>(IngredientSourceLink.class, false);

        grid.addColumn(IngredientSourceLink::getSourceSystem)
                .setHeader("Источник")
                .setAutoWidth(true);

        grid.addColumn(IngredientSourceLink::getSourceTable)
                .setHeader("Таблица источника")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(IngredientSourceLink::getSourceRowId)
                .setHeader("ID строки")
                .setAutoWidth(true);

        grid.addColumn(IngredientSourceLink::getSourceBusinessKey)
                .setHeader("Бизнес-ключ")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(IngredientSourceLink::getMatchMethod)
                .setHeader("Метод сопоставления")
                .setAutoWidth(true);

        grid.addColumn(link -> link.getConfidence() == null ? "" : link.getConfidence().toPlainString())
                .setHeader("Уверенность")
                .setAutoWidth(true);

        grid.addColumn(IngredientSourceLink::getNote)
                .setHeader("Примечание")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.setItems(detail.sourceLinks());
        grid.setAllRowsVisible(true);

        return new Details("Источники данных", grid);
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
                getUI().ifPresent(ui -> ui.navigate(
                        FormulaDetailView.class,
                        new RouteParameters("id", e.getValue().getFormula().getFormulaId().toString())
                ));
            }
        });
        grid.setItems(detail.formulaUsages());
        grid.setAllRowsVisible(true);
        return new Details("Использование в формулах", grid);
    }

    private Span wrapColumnText(String text) {
        Span span = new Span(nullToEmpty(text));
        span.getStyle().set("display", "block");
        span.getStyle().set("white-space", "normal");
        span.getStyle().set("overflow-wrap", "anywhere");
        span.getStyle().set("word-break", "break-word");
        span.getStyle().set("max-width", "100%");
        return span;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private Anchor buildExternalLink(String url) {
        String safeUrl = nullToEmpty(url);
        Anchor anchor = new Anchor(safeUrl, safeUrl.isBlank() ? "" : safeUrl);
        anchor.setTarget("_blank");
        anchor.getStyle().set("display", "block");
        anchor.getStyle().set("max-width", "100%");
        anchor.getStyle().set("overflow-wrap", "anywhere");
        anchor.getStyle().set("word-break", "break-word");
        return anchor;
    }
}
