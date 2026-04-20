package catalog.ingredient.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import catalog.ingredient.domain.FormulaIngredient;
import catalog.ingredient.domain.Ingredient;
import catalog.ingredient.domain.IngredientComponent;
import catalog.ingredient.domain.IngredientIdentifier;
import catalog.ingredient.domain.IngredientKind;
import catalog.ingredient.domain.IngredientName;
import catalog.ingredient.domain.IngredientRequirement;
import catalog.ingredient.domain.IngredientSolubility;
import catalog.ingredient.domain.IngredientSolvent;
import catalog.ingredient.domain.IngredientSourceLink;
import catalog.ingredient.domain.IngredientTestLog;
import catalog.ingredient.domain.IngredientWaxProperty;
import catalog.ingredient.service.IngredientService;
import catalog.ingredient.service.dto.IngredientDetailDto;
import catalog.ingredient.service.dto.RelatedFormulationDto;
import catalog.ingredient.service.dto.RelatedFormulationIngredientDto;
import catalog.ingredient.service.dto.RelatedFormulationIngredientMatchDto;
import catalog.ingredient.service.dto.RelatedFormulationPropertyDto;
import catalog.ingredient.service.dto.RelatedFormulationTagDto;
import catalog.ingredient.service.dto.SpecialchemKeyValueRow;
import catalog.ingredient.service.dto.SpecialchemValueRow;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

@PageTitle("Карточка ингредиента")
@Route(value = "ingredients/:id", layout = MainLayout.class)
public class IngredientDetailView extends VerticalLayout implements BeforeEnterObserver {

    private static final int FULL_WIDTH_COLSPAN = 2;
    private static final String SECOND_COLUMN_BREAKPOINT = "900px";
    private static final String DEG_C = "degC";
    private static final String SDS_LABEL = "SDS / паспорт безопасности";
    private static final List<String> SOLUBILITY_MEDIA = List.of(
            "water",
            "oil",
            "alcohol",
            "glycol",
            "silicone",
            "hydrocarbon",
            "other"
    );
    private static final List<String> SOLUBILITY_CLASSES = List.of(
            "soluble",
            "partially_soluble",
            "insoluble",
            "dispersible",
            "swells"
    );
    private static final List<String> WAX_PROPERTY_TYPES = List.of(
            "dropping_point",
            "melting_point"
    );

    private final IngredientService ingredientService;
    private Long ingredientId;
    private boolean editMode;
    private boolean showWaxProperties;

    private enum RowActionKind {
        NONE,
        SOLUBILITY,
        SOLVENT,
        WAX_PROPERTY
    }

    private record KeyValueRow(String parameter, String value, RowActionKind actionKind, Object source) {
    }

    public IngredientDetailView(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
        setSizeFull();
        setWidthFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        ingredientId = Long.parseLong(event.getRouteParameters().get("id").orElseThrow());
        editMode = false;
        showWaxProperties = false;
        renderView();
    }

    private void renderView() {
        removeAll();

        IngredientDetailDto detail = ingredientService.getDetail(ingredientId);
        Ingredient ingredient = detail.ingredient();

        add(buildHeader(ingredient));
        add(editMode ? buildEditCard(ingredient) : buildReadCard(ingredient));
        add(
                sectionNames(detail),
                sectionIdentifiers(detail),
                sectionProperties(detail),
                sectionTests(detail)
        );

        if (ingredient.isMixture()) {
            add(sectionComposition(detail));
        }

        add(
                sectionSourceLinks(detail)
        );
        addSpecialchemSections(detail);
    }

    private HorizontalLayout buildHeader(Ingredient ingredient) {
        Button back = new Button("Назад", e -> getUI().ifPresent(ui -> ui.getPage().getHistory().back()));

        H2 title = new H2(ingredient.getDisplayIdentity());
        title.getStyle().set("margin", "0");

        HorizontalLayout actions = new HorizontalLayout();
        if (!editMode) {
            Button edit = new Button("Редактировать", e -> enableEditMode());
            actions.add(edit, buildDeleteButton(ingredient));
        }

        HorizontalLayout header = new HorizontalLayout(back, title, actions);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.expand(title);
        return header;
    }

    private VerticalLayout buildEditCard(Ingredient source) {
        Ingredient edited = source.createEditableCopy();
        Binder<Ingredient> binder = new Binder<>(Ingredient.class);
        binder.setBean(edited);

        VerticalLayout form = createIngredientCardLayout();
        form.add(
                pairRow(
                        readOnlyTextField("ID", String.valueOf(source.getIngredientId())),
                        kindField(binder)
                ),
                pairRow(
                        requiredTextField(
                                binder,
                                "Название",
                                "Название обязательно",
                                Ingredient::getPrimaryName,
                                Ingredient::setPrimaryName
                        ),
                        boundTextField(binder, "INCI", Ingredient::getInciName, Ingredient::setInciName)
                ),
                pairRow(
                        boundTextField(binder, "CAS", Ingredient::getCasNo, Ingredient::setCasNo),
                        boundTextField(binder, "EC", Ingredient::getEcNo, Ingredient::setEcNo)
                ),
                pairRow(
                        boundTextField(binder, "CI", Ingredient::getCiNo, Ingredient::setCiNo),
                        readOnlyTextField("Функция", source.getFunctionDisplay())
                ),
                pairRow(
                        boundTextField(binder, "Поставщик", Ingredient::getSupplierName, Ingredient::setSupplierName),
                        boundTextField(binder, "Код поставщика", Ingredient::getSupplierCode, Ingredient::setSupplierCode)
                ),
                pairRow(
                        boundTextField(binder, SDS_LABEL, Ingredient::getSdsUrl, Ingredient::setSdsUrl),
                        boundTextField(binder, "Источник SpecialChem", Ingredient::getSpecialchemUrl, Ingredient::setSpecialchemUrl)
                ),
                pairRow(
                        boundTextArea(binder, "Описание EN", 6, Ingredient::getDescriptionEn, Ingredient::setDescriptionEn),
                        boundTextArea(binder, "Примечание", 4, Ingredient::getNote, Ingredient::setNote)
                ),
                pairRow(
                        boundTextArea(
                                binder,
                                "Происхождение RU",
                                5,
                                Ingredient::getSpecialchemOriginRu,
                                Ingredient::setSpecialchemOriginRu
                        ),
                        boundTextArea(
                                binder,
                                "Происхождение EN",
                                5,
                                Ingredient::getSpecialchemOriginEn,
                                Ingredient::setSpecialchemOriginEn
                        )
                ),
                pairRow(
                        boundTextArea(
                                binder,
                                "Профиль безопасности RU",
                                5,
                                Ingredient::getSpecialchemSafetyProfileRu,
                                Ingredient::setSpecialchemSafetyProfileRu
                        ),
                        boundTextArea(
                                binder,
                                "Профиль безопасности EN",
                                5,
                                Ingredient::getSpecialchemSafetyProfileEn,
                                Ingredient::setSpecialchemSafetyProfileEn
                        )
                ),
                pairRow(
                        boundTextArea(
                                binder,
                                "IUPAC-наименование RU",
                                3,
                                Ingredient::getSpecialchemChemIupacNameRu,
                                Ingredient::setSpecialchemChemIupacNameRu
                        ),
                        boundTextArea(
                                binder,
                                "IUPAC-наименование EN",
                                3,
                                Ingredient::getSpecialchemChemIupacNameEn,
                                Ingredient::setSpecialchemChemIupacNameEn
                        )
                ),
                pairRow(
                        boundTextArea(
                                binder,
                                "Назначение и применение RU",
                                6,
                                Ingredient::getSpecialchemUsageTextRu,
                                Ingredient::setSpecialchemUsageTextRu
                        ),
                        boundTextArea(
                                binder,
                                "Назначение и применение EN",
                                6,
                                Ingredient::getSpecialchemUsageTextEn,
                                Ingredient::setSpecialchemUsageTextEn
                        )
                )
        );

        VerticalLayout wrapper = new VerticalLayout(form, buildEditActions(source, binder, edited));
        wrapper.setPadding(false);
        wrapper.setSpacing(true);
        wrapper.setWidthFull();
        return wrapper;
    }

    private VerticalLayout buildReadCard(Ingredient ingredient) {
        VerticalLayout card = createIngredientCardLayout();
        card.add(
                pairRow(
                        readTextItem("ID", String.valueOf(ingredient.getIngredientId())),
                        readTextItem("Тип", formatIngredientKind(ingredient.getKind()))
                ),
                pairRow(
                        readTextItem("Название", ingredient.getPrimaryName()),
                        readTextItem("INCI", ingredient.getInciName())
                ),
                pairRow(
                        readTextItem("CAS", ingredient.getCasNo()),
                        readTextItem("EC", ingredient.getEcNo())
                ),
                pairRow(
                        readTextItem("CI", ingredient.getCiNo()),
                        readTextItem("Функция", ingredient.getFunctionDisplay())
                ),
                pairRow(
                        readTextItem("Поставщик", ingredient.getSupplierName()),
                        readTextItem("Код поставщика", ingredient.getSupplierCode())
                ),
                pairRow(
                        readTextItem(SDS_LABEL, ingredient.getSdsUrl()),
                        readComponentItem("Источник SpecialChem", buildExternalLink(ingredient.getSpecialchemUrl()))
                ),
                pairRow(
                        readWrappedTextItem("Описание EN", ingredient.getDescriptionEn()),
                        readWrappedTextItem("Примечание", ingredient.getNote())
                ),
                pairRow(
                        readWrappedTextItem("Происхождение RU", ingredient.getSpecialchemOriginRu()),
                        readWrappedTextItem("Происхождение EN", ingredient.getSpecialchemOriginEn())
                ),
                pairRow(
                        readWrappedTextItem("Профиль безопасности RU", ingredient.getSpecialchemSafetyProfileRu()),
                        readWrappedTextItem("Профиль безопасности EN", ingredient.getSpecialchemSafetyProfileEn())
                ),
                pairRow(
                        readWrappedTextItem("IUPAC-наименование RU", ingredient.getSpecialchemChemIupacNameRu()),
                        readWrappedTextItem("IUPAC-наименование EN", ingredient.getSpecialchemChemIupacNameEn())
                ),
                pairRow(
                        readWrappedTextItem("Назначение и применение RU", ingredient.getSpecialchemUsageTextRu()),
                        readWrappedTextItem("Назначение и применение EN", ingredient.getSpecialchemUsageTextEn())
                )
        );
        return card;
    }

    private FormLayout createCardLayout() {
        FormLayout layout = new FormLayout();
        layout.setWidthFull();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep(SECOND_COLUMN_BREAKPOINT, FULL_WIDTH_COLSPAN)
        );
        return layout;
    }

    private void enableEditMode() {
        editMode = true;
        renderView();
    }

    private HorizontalLayout buildEditActions(Ingredient source, Binder<Ingredient> binder, Ingredient edited) {
        Button save = new Button("Сохранить", e -> saveIngredient(binder, edited));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Отмена", e -> cancelEditing());

        return new HorizontalLayout(save, cancel, buildDeleteButton(source));
    }

    private void saveIngredient(Binder<Ingredient> binder, Ingredient edited) {
        if (!binder.validate().isOk()) {
            return;
        }

        ingredientService.updateIngredient(ingredientId, edited);
        editMode = false;
        Notification.show("Изменения сохранены");
        renderView();
    }

    private void cancelEditing() {
        editMode = false;
        renderView();
    }

    private Button buildDeleteButton(Ingredient ingredient) {
        Button delete = new Button("Удалить", e -> deleteIngredient(ingredient));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return delete;
    }

    private void deleteIngredient(Ingredient ingredient) {
        ingredientService.markDeleted(ingredient.getIngredientId());
        Notification.show("Ингредиент помечен на удаление");

        Class<? extends Component> targetView =
                ingredient.isMixture() ? CompositionListView.class : IngredientListView.class;

        getUI().ifPresent(ui -> ui.navigate(targetView));
    }

    private Details sectionNames(IngredientDetailDto detail) {
        Grid<IngredientName> grid = new Grid<>(IngredientName.class, false);
        grid.addColumn(IngredientName::getName).setHeader("Имя").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(IngredientName::getNameType).setHeader("Тип").setAutoWidth(true);
        grid.addColumn(IngredientName::getLang).setHeader("Язык").setAutoWidth(true);
        grid.addColumn(IngredientName::isPrimary).setHeader("Основное").setAutoWidth(true);
        return details("Синонимы и названия", grid, detail.names());
    }

    private Details sectionIdentifiers(IngredientDetailDto detail) {
        Grid<IngredientIdentifier> grid = new Grid<>(IngredientIdentifier.class, false);
        grid.addColumn(IngredientIdentifier::getIdType).setHeader("Тип").setAutoWidth(true);
        grid.addColumn(IngredientIdentifier::getIdValue).setHeader("Значение").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(IngredientIdentifier::isPrimary).setHeader("Основной").setAutoWidth(true);
        grid.addColumn(IngredientIdentifier::getSourceSheet).setHeader("Лист").setAutoWidth(true);
        return details("Идентификаторы", grid, detail.identifiers());
    }

    private Details sectionProperties(IngredientDetailDto detail) {
        List<KeyValueRow> rows = buildPropertyRows(detail);
        Grid<KeyValueRow> grid = keyValueGrid(rows.stream().anyMatch(row -> row.actionKind() != RowActionKind.NONE));

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        HorizontalLayout actions = sectionActions();
        actions.add(primaryActionButton("Добавить растворимость", this::openSolubilityDialog));
        actions.add(primaryActionButton("Добавить растворитель", this::openSolventDialog));
        actions.add(isWaxSectionEnabled(detail)
                ? primaryActionButton("Добавить физическое свойство", this::openWaxPropertyDialog)
                : primaryActionButton("Добавить физическое свойство воска", () -> enableWaxProperties(true)));

        grid.setItems(rows);
        grid.setAllRowsVisible(true);

        content.add(actions, grid);
        return new Details("Свойства", content);
    }

    private List<KeyValueRow> buildPropertyRows(IngredientDetailDto detail) {
        List<KeyValueRow> rows = new ArrayList<>();

        for (IngredientRequirement requirement : detail.requirements()) {
            rows.add(new KeyValueRow(
                    hasText(requirement.getRequirementType()) ? requirement.getRequirementType() : "Требование",
                    nullToEmpty(requirement.getRequirementText()),
                    RowActionKind.NONE,
                    requirement
            ));
        }

        for (SpecialchemKeyValueRow technicalProfileRow : detail.technicalProfile()) {
            rows.add(new KeyValueRow(
                    nullToEmpty(technicalProfileRow.name()),
                    nullToEmpty(technicalProfileRow.value()),
                    RowActionKind.NONE,
                    technicalProfileRow
            ));
        }

        for (IngredientSolubility solubility : detail.solubilities()) {
            rows.add(new KeyValueRow(
                    hasText(solubility.getMediumType())
                            ? "Растворимость: " + formatMediumType(solubility.getMediumType())
                            : "Растворимость",
                    formatSolubilityValue(solubility),
                    RowActionKind.SOLUBILITY,
                    solubility
            ));
        }

        for (IngredientSolvent solvent : detail.solvents()) {
            rows.add(new KeyValueRow(
                    "Растворитель",
                    formatSolventValue(solvent),
                    RowActionKind.SOLVENT,
                    solvent
            ));
        }

        for (IngredientWaxProperty waxProperty : detail.waxProperties()) {
            rows.add(new KeyValueRow(
                    formatWaxPropertyType(waxProperty.getPropertyType()),
                    formatWaxPropertyValue(waxProperty),
                    RowActionKind.WAX_PROPERTY,
                    waxProperty
            ));
        }

        return rows;
    }

    private Details sectionTests(IngredientDetailDto detail) {
        Grid<IngredientTestLog> grid = new Grid<>(IngredientTestLog.class, false);
        grid.addColumn(IngredientTestLog::getTestResult).setHeader("Результат").setAutoWidth(true);
        grid.addColumn(IngredientTestLog::getTestNote).setHeader("Комментарий").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(IngredientTestLog::getSourceSheet).setHeader("Лист").setAutoWidth(true);
        return details("Испытания", grid, detail.testLogs());
    }

    private Details sectionComposition(IngredientDetailDto detail) {
        Grid<IngredientComponent> grid = new Grid<>(IngredientComponent.class, false);
        grid.addColumn(this::formatComponentName)
                .setHeader("Компонент")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(component -> component.getComponentIngredient() == null
                        ? ""
                        : String.valueOf(component.getComponentIngredient().getIngredientId()))
                .setHeader("ID ингредиента")
                .setAutoWidth(true);
        grid.addColumn(IngredientComponent::getInciRaw).setHeader("INCI").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getCasRaw).setHeader("CAS").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getEcRaw).setHeader("EC").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getPurposeRaw).setHeader("Назначение").setAutoWidth(true);
        grid.addColumn(IngredientComponent::getInputPctRaw).setHeader("Доля").setAutoWidth(true);
        grid.addColumn(row -> formatSourceRef(row.getSourceSheet(), row.getSourceRowNum())).setHeader("Лист/строка").setAutoWidth(true);
        grid.addComponentColumn(row -> buildRowActions(
                () -> openComponentDialog(row),
                () -> deleteComponent(row)
        )).setHeader("Действия").setAutoWidth(true);
        return crudDetails("Состав", grid, detail.components(), this::openComponentDialog);
    }

    private boolean isWaxSectionEnabled(IngredientDetailDto detail) {
        return showWaxProperties || !detail.waxProperties().isEmpty() || looksLikeWax(detail);
    }

    private boolean looksLikeWax(IngredientDetailDto detail) {
        if (containsWaxFragment(detail.ingredient().getPrimaryName())) {
            return true;
        }
        if (containsWaxFragment(detail.ingredient().getInciName())) {
            return true;
        }
        return detail.names().stream().anyMatch(name -> containsWaxFragment(name.getName()));
    }

    private boolean containsWaxFragment(String value) {
        if (!hasText(value)) {
            return false;
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        return normalized.contains("wax") || normalized.contains("воск");
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

        return details("Источники данных", grid, detail.sourceLinks());
    }

    private Details sectionFormulaUsage(IngredientDetailDto detail) {
        Grid<FormulaIngredient> grid = new Grid<>(FormulaIngredient.class, false);
        grid.addColumn(row -> row.getFormula() == null || row.getFormula().getProduct() == null
                        ? ""
                        : row.getFormula().getProduct().getDisplayName())
                .setHeader("Продукт")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(row -> row.getFormula() == null ? "" : row.getFormula().getFormulaId())
                .setHeader("Формула")
                .setAutoWidth(true);
        grid.addColumn(row -> row.getFormula() == null ? "" : row.getFormula().getVersionNo())
                .setHeader("Версия")
                .setAutoWidth(true);
        grid.addColumn(FormulaIngredient::getPercentWw).setHeader("% масс./масс.").setAutoWidth(true);
        grid.addColumn(FormulaIngredient::getFunctionRole).setHeader("Роль").setAutoWidth(true).setFlexGrow(1);
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null && event.getValue().getFormula() != null) {
                getUI().ifPresent(ui -> ui.navigate(
                        FormulaDetailView.class,
                        new RouteParameters("id", event.getValue().getFormula().getFormulaId().toString())
                ));
            }
        });
        return details("Использование в формулах", grid, detail.formulaUsages());
    }

    private void addSpecialchemSections(IngredientDetailDto detail) {
        if (!detail.products().isEmpty()) {
            add(sectionSingleValueList("Products", "Продукт", detail.products()));
        }
        if (!detail.formulations().isEmpty()) {
            add(sectionRelatedFormulations(detail));
        }
        if (!detail.alternatives().isEmpty()) {
            add(sectionSingleValueList("Alternatives", "Альтернатива", detail.alternatives()));
        }
        if (!detail.potentialUse().isEmpty()) {
            add(sectionSingleValueList("Potential Use", "Потенциальное применение", detail.potentialUse()));
        }
    }

    private Details sectionRelatedFormulations(IngredientDetailDto detail) {
        Grid<RelatedFormulationDto> grid = new Grid<>(RelatedFormulationDto.class, false);
        grid.addColumn(RelatedFormulationDto::formulationName).setHeader("Formulation").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(row -> nullToEmpty(row.title())).setHeader("Title").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(row -> nullToEmpty(row.supplierName())).setHeader("Supplier").setAutoWidth(true);
        grid.addColumn(row -> formatRelationType(row.relationType())).setHeader("Связь").setAutoWidth(true);
        grid.addColumn(row -> nullToEmpty(row.lastUpdatedRaw())).setHeader("Updated").setAutoWidth(true);
        grid.addComponentColumn(row -> buildOptionalExternalLink(row.downloadFormulationUrl(), "Download"))
                .setHeader("Download")
                .setAutoWidth(true);
        grid.addComponentColumn(row -> {
                    Button open = new Button("Подробнее", event -> openRelatedFormulationDialog(row));
                    open.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                    return open;
                })
                .setHeader("Просмотр")
                .setAutoWidth(true);
        return details("Formulations", grid, detail.formulations());
    }

    private Details sectionSingleValueList(String title, String columnHeader, List<SpecialchemValueRow> rows) {
        Grid<SpecialchemValueRow> grid = new Grid<>(SpecialchemValueRow.class, false);
        grid.addColumn(SpecialchemValueRow::value).setHeader(columnHeader).setAutoWidth(true).setFlexGrow(1);
        return details(title, grid, rows);
    }

    private void openRelatedFormulationDialog(RelatedFormulationDto formulation) {
        Dialog dialog = new Dialog();
        dialog.setWidth("1180px");
        dialog.setMaxWidth("95vw");
        dialog.setMaxHeight("90vh");

        H2 header = new H2(nullToEmpty(formulation.formulationName()));
        header.getStyle().set("margin", "0");

        Button close = new Button("Закрыть", event -> dialog.close());

        VerticalLayout content = new VerticalLayout(
                header,
                buildRelatedFormulationOverview(formulation),
                buildRelatedFormulationTexts(formulation)
        );
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        if (!formulation.properties().isEmpty()) {
            content.add(openedDetails("Properties", createRelatedFormulationPropertyGrid(formulation.properties())));
        }
        if (!formulation.tags().isEmpty()) {
            content.add(openedDetails("Tags", createRelatedFormulationTagGrid(formulation.tags())));
        }
        if (!formulation.ingredients().isEmpty()) {
            content.add(openedDetails("Composition", createRelatedFormulationIngredientGrid(formulation.ingredients())));
        }

        content.add(close);
        dialog.add(content);
        dialog.open();
    }

    private FormLayout buildRelatedFormulationOverview(RelatedFormulationDto formulation) {
        FormLayout card = createCardLayout();
        addTextItem(card, "Formulation", formulation.formulationName());
        addTextItem(card, "Title", formulation.title());
        addTextItem(card, "Supplier", formulation.supplierName());
        addTextItem(card, "Тип связи", formatRelationType(formulation.relationType()));
        addTextItem(card, "Updated", formulation.lastUpdatedRaw());
        addComponentItem(card, "Formulation URL", buildOptionalExternalLink(formulation.formulationUrl(), "Open"));
        addComponentItem(card, "Download", buildOptionalExternalLink(formulation.downloadFormulationUrl(), "Download"));
        addWrappedTextItem(card, "Note", formulation.note());
        return card;
    }

    private Component buildRelatedFormulationTexts(RelatedFormulationDto formulation) {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        if (hasText(formulation.description())) {
            content.add(openedDetails("Description", wrapColumnText(formulation.description())));
        }
        if (hasText(formulation.propertiesText())) {
            content.add(openedDetails("Properties Text", wrapColumnText(formulation.propertiesText())));
        }
        if (hasText(formulation.procedureText())) {
            content.add(openedDetails("Procedure", wrapColumnText(formulation.procedureText())));
        }

        return content;
    }

    private Grid<RelatedFormulationPropertyDto> createRelatedFormulationPropertyGrid(List<RelatedFormulationPropertyDto> rows) {
        Grid<RelatedFormulationPropertyDto> grid = new Grid<>(RelatedFormulationPropertyDto.class, false);
        grid.addColumn(RelatedFormulationPropertyDto::propertyName).setHeader("Параметр").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(RelatedFormulationPropertyDto::propertyValue).setHeader("Значение").setAutoWidth(true).setFlexGrow(2);
        prepareGrid(grid, rows);
        return grid;
    }

    private Grid<RelatedFormulationTagDto> createRelatedFormulationTagGrid(List<RelatedFormulationTagDto> rows) {
        Grid<RelatedFormulationTagDto> grid = new Grid<>(RelatedFormulationTagDto.class, false);
        grid.addColumn(row -> formatFormulationTagType(row.tagType())).setHeader("Категория").setAutoWidth(true);
        grid.addColumn(RelatedFormulationTagDto::tagName).setHeader("Тег").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(row -> row.tagCount() == null ? "" : String.valueOf(row.tagCount())).setHeader("Count").setAutoWidth(true);
        grid.addComponentColumn(row -> buildOptionalExternalLink(row.tagUrl(), "Open")).setHeader("URL").setAutoWidth(true);
        prepareGrid(grid, rows);
        return grid;
    }

    private Grid<RelatedFormulationIngredientDto> createRelatedFormulationIngredientGrid(List<RelatedFormulationIngredientDto> rows) {
        Grid<RelatedFormulationIngredientDto> grid = new Grid<>(RelatedFormulationIngredientDto.class, false);
        grid.addColumn(row -> row.rowNum() == null ? "" : String.valueOf(row.rowNum())).setHeader("Row").setAutoWidth(true);
        grid.addColumn(row -> nullToEmpty(row.phase())).setHeader("Phase").setAutoWidth(true);
        grid.addColumn(RelatedFormulationIngredientDto::ingredientName).setHeader("Ingredient").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(row -> nullToEmpty(row.ingredientType())).setHeader("Type").setAutoWidth(true);
        grid.addColumn(this::formatRelatedFormulationQuantity).setHeader("Quantity").setAutoWidth(true);
        grid.addColumn(row -> nullToEmpty(row.matchStatus())).setHeader("Match status").setAutoWidth(true);
        grid.addComponentColumn(this::buildRelatedFormulationMatches).setHeader("Matched ingredients").setAutoWidth(true).setFlexGrow(1);
        prepareGrid(grid, rows);
        return grid;
    }

    private Component buildRelatedFormulationMatches(RelatedFormulationIngredientDto row) {
        if (row.matches() == null || row.matches().isEmpty()) {
            return new Span();
        }

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setWidthFull();

        for (RelatedFormulationIngredientMatchDto match : row.matches()) {
            if (match.ingredientId() != null) {
                RouterLink link = new RouterLink(
                        formatRelatedFormulationMatch(match),
                        IngredientDetailView.class,
                        new RouteParameters("id", String.valueOf(match.ingredientId()))
                );
                content.add(link);
            } else {
                content.add(wrapColumnText(formatRelatedFormulationMatch(match)));
            }
        }

        return content;
    }

    private void openSolubilityDialog() {
        openSolubilityDialog(null);
    }

    private void openComponentDialog() {
        openComponentDialog(null);
    }

    private void openComponentDialog(IngredientComponent source) {
        IngredientComponent edited = source == null ? new IngredientComponent() : source.createEditableCopy();
        Binder<IngredientComponent> binder = new Binder<>(IngredientComponent.class);
        binder.setBean(edited);
        binder.withValidator(
                component -> component.getComponentIngredient() != null || hasText(component.getComponentNameRaw()),
                "Выберите связанный ингредиент или укажите название компонента"
        );
        binder.withValidator(
                component -> component.getComponentIngredient() == null
                        || !ingredientId.equals(component.getComponentIngredient().getIngredientId()),
                "Смесь не может входить в собственный состав"
        );

        FormLayout form = createCardLayout();

        ComboBox<Ingredient> linkedIngredientField = ingredientPickerField("Связанный ингредиент");
        linkedIngredientField.setDataProvider(
                DataProvider.fromFilteringCallbacks(
                        query -> ingredientService
                                .searchComponentCandidates(
                                        ingredientId,
                                        query.getFilter().orElse(null),
                                        query.getOffset(),
                                        query.getLimit()
                                )
                                .stream(),
                        query -> ingredientService.countComponentCandidates(
                                ingredientId,
                                query.getFilter().orElse(null)
                        )
                ),
                filterText -> filterText == null ? null : filterText
        );
        binder.bind(linkedIngredientField, IngredientComponent::getComponentIngredient, IngredientComponent::setComponentIngredient);
        addField(form, linkedIngredientField, true);

        TextField componentNameField = textField("Название компонента");
        binder.bind(componentNameField, IngredientComponent::getComponentNameRaw, IngredientComponent::setComponentNameRaw);
        addField(form, componentNameField, true);

        TextField inciField = textField("INCI");
        binder.bind(inciField, IngredientComponent::getInciRaw, IngredientComponent::setInciRaw);
        addField(form, inciField, false);

        TextField chemicalNameField = textField("Химическое наименование");
        binder.bind(chemicalNameField, IngredientComponent::getChemicalNameRaw, IngredientComponent::setChemicalNameRaw);
        addField(form, chemicalNameField, false);

        TextField purposeField = textField("Назначение");
        binder.bind(purposeField, IngredientComponent::getPurposeRaw, IngredientComponent::setPurposeRaw);
        addField(form, purposeField, false);

        TextField inputPctField = textField("Доля");
        binder.bind(inputPctField, IngredientComponent::getInputPctRaw, IngredientComponent::setInputPctRaw);
        addField(form, inputPctField, false);

        TextField casField = textField("CAS");
        binder.bind(casField, IngredientComponent::getCasRaw, IngredientComponent::setCasRaw);
        addField(form, casField, false);

        TextField ecField = textField("EC");
        binder.bind(ecField, IngredientComponent::getEcRaw, IngredientComponent::setEcRaw);
        addField(form, ecField, false);

        TextArea specField = textArea("Спецификация", 3);
        binder.bind(specField, IngredientComponent::getSpecText, IngredientComponent::setSpecText);
        addField(form, specField, true);

        TextField calcValueField = textField("Расчётное значение");
        binder.bind(calcValueField, IngredientComponent::getCalcValueRaw, IngredientComponent::setCalcValueRaw);
        addField(form, calcValueField, false);

        TextField resultField = textField("Результат");
        binder.bind(resultField, IngredientComponent::getResultRaw, IngredientComponent::setResultRaw);
        addField(form, resultField, false);

        TextField sourceSystemField = textField("Источник");
        binder.bind(sourceSystemField, IngredientComponent::getSourceSystem, IngredientComponent::setSourceSystem);
        addField(form, sourceSystemField, false);

        TextField sourceSheetField = textField("Лист");
        binder.bind(sourceSheetField, IngredientComponent::getSourceSheet, IngredientComponent::setSourceSheet);
        addField(form, sourceSheetField, false);

        IntegerField sourceRowField = integerField("Строка");
        binder.bind(sourceRowField, IngredientComponent::getSourceRowNum, IngredientComponent::setSourceRowNum);
        addField(form, sourceRowField, false);

        linkedIngredientField.addValueChangeListener(event -> populateComponentFieldsFromIngredient(
                event.getValue(),
                componentNameField,
                inciField,
                casField,
                ecField
        ));
        populateComponentFieldsFromIngredient(edited.getComponentIngredient(), componentNameField, inciField, casField, ecField);

        openEditorDialog(
                source == null ? "Добавить компонент состава" : "Редактировать компонент состава",
                form,
                dialog -> {
                    if (!binder.validate().isOk()) {
                        return;
                    }
                    try {
                        ingredientService.saveComponent(ingredientId, edited);
                    } catch (IllegalArgumentException ex) {
                        Notification.show(ex.getMessage());
                        return;
                    }
                    dialog.close();
                    Notification.show("Компонент состава сохранен");
                    renderView();
                }
        );
    }

    private void openSolubilityDialog(IngredientSolubility source) {
        IngredientSolubility edited = source == null ? new IngredientSolubility() : source.createEditableCopy();
        Binder<IngredientSolubility> binder = new Binder<>(IngredientSolubility.class);
        binder.setBean(edited);

        FormLayout form = createCardLayout();

        Select<String> mediumField = selectField("Среда", SOLUBILITY_MEDIA, this::formatMediumType);
        binder.forField(mediumField)
                .asRequired("Среда обязательна")
                .bind(IngredientSolubility::getMediumType, IngredientSolubility::setMediumType);
        addField(form, mediumField, false);

        Select<String> classField = selectField("Тип", SOLUBILITY_CLASSES, this::formatSolubilityClass);
        binder.bind(classField, IngredientSolubility::getSolubilityClass, IngredientSolubility::setSolubilityClass);
        addField(form, classField, false);

        TextArea solubilityTextField = textArea("Описание", 4);
        binder.bind(solubilityTextField, IngredientSolubility::getSolubilityText, IngredientSolubility::setSolubilityText);
        addField(form, solubilityTextField, true);

        TextField concentrationField = textField("Ограничение по концентрации");
        binder.bind(concentrationField, IngredientSolubility::getConcentrationText, IngredientSolubility::setConcentrationText);
        addField(form, concentrationField, false);

        BigDecimalField temperatureField = bigDecimalField("Температура, °C");
        binder.bind(temperatureField, IngredientSolubility::getTemperatureC, IngredientSolubility::setTemperatureC);
        addField(form, temperatureField, false);

        TextField sourceSystemField = textField("Источник");
        binder.bind(sourceSystemField, IngredientSolubility::getSourceSystem, IngredientSolubility::setSourceSystem);
        addField(form, sourceSystemField, false);

        TextField sourceSheetField = textField("Лист");
        binder.bind(sourceSheetField, IngredientSolubility::getSourceSheet, IngredientSolubility::setSourceSheet);
        addField(form, sourceSheetField, false);

        IntegerField sourceRowField = integerField("Строка");
        binder.bind(sourceRowField, IngredientSolubility::getSourceRowNum, IngredientSolubility::setSourceRowNum);
        addField(form, sourceRowField, false);

        openEditorDialog(
                source == null ? "Добавить растворимость" : "Редактировать растворимость",
                form,
                dialog -> {
                    if (!binder.validate().isOk()) {
                        return;
                    }
                    ingredientService.saveSolubility(ingredientId, edited);
                    dialog.close();
                    Notification.show("Растворимость сохранена");
                    renderView();
                }
        );
    }

    private void openSolventDialog() {
        openSolventDialog(null);
    }

    private void openSolventDialog(IngredientSolvent source) {
        IngredientSolvent edited = source == null ? new IngredientSolvent() : source.createEditableCopy();
        Binder<IngredientSolvent> binder = new Binder<>(IngredientSolvent.class);
        binder.setBean(edited);

        FormLayout form = createCardLayout();

        TextField solventNameField = textField("Растворитель");
        binder.forField(solventNameField)
                .asRequired("Название растворителя обязательно")
                .bind(IngredientSolvent::getSolventName, IngredientSolvent::setSolventName);
        addField(form, solventNameField, true);

        TextArea noteField = textArea("Примечание", 3);
        binder.bind(noteField, IngredientSolvent::getNote, IngredientSolvent::setNote);
        addField(form, noteField, true);

        TextField sourceSystemField = textField("Источник");
        binder.bind(sourceSystemField, IngredientSolvent::getSourceSystem, IngredientSolvent::setSourceSystem);
        addField(form, sourceSystemField, false);

        TextField sourceSheetField = textField("Лист");
        binder.bind(sourceSheetField, IngredientSolvent::getSourceSheet, IngredientSolvent::setSourceSheet);
        addField(form, sourceSheetField, false);

        IntegerField sourceRowField = integerField("Строка");
        binder.bind(sourceRowField, IngredientSolvent::getSourceRowNum, IngredientSolvent::setSourceRowNum);
        addField(form, sourceRowField, false);

        openEditorDialog(
                source == null ? "Добавить растворитель" : "Редактировать растворитель",
                form,
                dialog -> {
                    if (!binder.validate().isOk()) {
                        return;
                    }
                    ingredientService.saveSolvent(ingredientId, edited);
                    dialog.close();
                    Notification.show("Растворитель сохранен");
                    renderView();
                }
        );
    }

    private void openWaxPropertyDialog() {
        openWaxPropertyDialog(null);
    }

    private void openWaxPropertyDialog(IngredientWaxProperty source) {
        IngredientWaxProperty edited = source == null ? new IngredientWaxProperty() : source.createEditableCopy();
        Binder<IngredientWaxProperty> binder = new Binder<>(IngredientWaxProperty.class);
        binder.setBean(edited);
        binder.withValidator(
                value -> value.getValueNum() != null || hasText(value.getValueText()),
                "Укажите числовое значение или текст"
        );

        FormLayout form = createCardLayout();

        Select<String> propertyTypeField = selectField("Свойство", WAX_PROPERTY_TYPES, this::formatWaxPropertyType);
        binder.forField(propertyTypeField)
                .asRequired("Свойство обязательно")
                .bind(IngredientWaxProperty::getPropertyType, IngredientWaxProperty::setPropertyType);
        addField(form, propertyTypeField, false);

        BigDecimalField valueNumField = bigDecimalField("Числовое значение");
        binder.bind(valueNumField, IngredientWaxProperty::getValueNum, IngredientWaxProperty::setValueNum);
        addField(form, valueNumField, false);

        TextField unitField = textField("Единица измерения");
        binder.forField(unitField)
                .asRequired("Единица измерения обязательна")
                .bind(IngredientWaxProperty::getUnitName, IngredientWaxProperty::setUnitName);
        if (!hasText(edited.getUnitName())) {
            unitField.setValue(DEG_C);
        }
        addField(form, unitField, false);

        TextField valueTextField = textField("Текстовое значение");
        binder.bind(valueTextField, IngredientWaxProperty::getValueText, IngredientWaxProperty::setValueText);
        addField(form, valueTextField, true);

        TextArea methodField = textArea("Метод", 3);
        binder.bind(methodField, IngredientWaxProperty::getMethodText, IngredientWaxProperty::setMethodText);
        addField(form, methodField, true);

        TextField sourceSystemField = textField("Источник");
        binder.bind(sourceSystemField, IngredientWaxProperty::getSourceSystem, IngredientWaxProperty::setSourceSystem);
        addField(form, sourceSystemField, false);

        TextField sourceSheetField = textField("Лист");
        binder.bind(sourceSheetField, IngredientWaxProperty::getSourceSheet, IngredientWaxProperty::setSourceSheet);
        addField(form, sourceSheetField, false);

        IntegerField sourceRowField = integerField("Строка");
        binder.bind(sourceRowField, IngredientWaxProperty::getSourceRowNum, IngredientWaxProperty::setSourceRowNum);
        addField(form, sourceRowField, false);

        openEditorDialog(
                source == null ? "Добавить свойство воска" : "Редактировать свойство воска",
                form,
                dialog -> {
                    if (!binder.validate().isOk()) {
                        return;
                    }
                    ingredientService.saveWaxProperty(ingredientId, edited);
                    dialog.close();
                    Notification.show("Свойство воска сохранено");
                    renderView();
                }
        );
    }

    private void deleteSolubility(IngredientSolubility solubility) {
        ingredientService.deleteSolubility(ingredientId, solubility.getSolubilityId());
        Notification.show("Запись растворимости удалена");
        renderView();
    }

    private void deleteComponent(IngredientComponent component) {
        ingredientService.deleteComponent(ingredientId, component.getIngredientComponentId());
        Notification.show("Компонент состава удален");
        renderView();
    }

    private void deleteSolvent(IngredientSolvent solvent) {
        ingredientService.deleteSolvent(ingredientId, solvent.getIngredientSolventId());
        Notification.show("Растворитель удален");
        renderView();
    }

    private void deleteWaxProperty(IngredientWaxProperty waxProperty) {
        ingredientService.deleteWaxProperty(ingredientId, waxProperty.getWaxPropertyId());
        Notification.show("Свойство воска удалено");
        renderView();
    }

    private void enableWaxProperties(boolean openDialog) {
        showWaxProperties = true;
        renderView();
        if (openDialog) {
            openWaxPropertyDialog();
        }
    }

    private Grid<KeyValueRow> keyValueGrid(boolean includeActions) {
        Grid<KeyValueRow> grid = new Grid<>(KeyValueRow.class, false);
        grid.addColumn(KeyValueRow::parameter)
                .setHeader("Параметр")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setResizable(true);
        grid.addColumn(KeyValueRow::value)
                .setHeader("Значение")
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setResizable(true);
        if (includeActions) {
            grid.addComponentColumn(this::buildKeyValueActions)
                    .setHeader("Действия")
                    .setWidth("210px")
                    .setFlexGrow(0);
        }
        return grid;
    }

    private Component buildKeyValueActions(KeyValueRow row) {
        return switch (row.actionKind()) {
            case SOLUBILITY -> buildRowActions(
                    () -> openSolubilityDialog((IngredientSolubility) row.source()),
                    () -> deleteSolubility((IngredientSolubility) row.source())
            );
            case SOLVENT -> buildRowActions(
                    () -> openSolventDialog((IngredientSolvent) row.source()),
                    () -> deleteSolvent((IngredientSolvent) row.source())
            );
            case WAX_PROPERTY -> buildRowActions(
                    () -> openWaxPropertyDialog((IngredientWaxProperty) row.source()),
                    () -> deleteWaxProperty((IngredientWaxProperty) row.source())
            );
            case NONE -> new Span();
        };
    }

    private HorizontalLayout sectionActions(Button... buttons) {
        HorizontalLayout actions = new HorizontalLayout(buttons);
        actions.setPadding(false);
        actions.setSpacing(true);
        actions.setWidthFull();
        actions.setWrap(true);
        return actions;
    }

    private Button primaryActionButton(String text, Runnable action) {
        Button button = new Button(text, event -> action.run());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return button;
    }

    private void openEditorDialog(String title, FormLayout form, Consumer<Dialog> onSave) {
        Dialog dialog = new Dialog();
        dialog.setWidth("920px");

        H2 header = new H2(title);
        header.getStyle().set("margin", "0");

        Button save = new Button("Сохранить", event -> onSave.accept(dialog));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Отмена", event -> dialog.close());

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        VerticalLayout content = new VerticalLayout(header, form, actions);
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        dialog.add(content);
        dialog.open();
    }

    private <T> Details details(String title, Grid<T> grid, Collection<T> items) {
        prepareGrid(grid, items);

        Details details = new Details(title, grid);
        details.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                grid.recalculateColumnWidths();
            }
        });
        return details;
    }

    private <T> Details crudDetails(String title, Grid<T> grid, Collection<T> items, Runnable onAdd) {
        Button add = new Button("Добавить", event -> onAdd.run());
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        prepareGrid(grid, items);

        VerticalLayout content = new VerticalLayout(add, grid);
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        Details details = new Details(title, content);
        details.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                grid.recalculateColumnWidths();
            }
        });
        return details;
    }

    private <T> void prepareGrid(Grid<T> grid, Collection<T> items) {
        grid.setWidthFull();
        grid.setItems(items);
        grid.setAllRowsVisible(true);
    }

    private HorizontalLayout buildRowActions(Runnable onEdit, Runnable onDelete) {
        Button edit = new Button("Изменить", event -> onEdit.run());
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);

        Button delete = new Button("Удалить", event -> onDelete.run());
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);

        HorizontalLayout actions = new HorizontalLayout(edit, delete);
        actions.setPadding(false);
        actions.setSpacing(true);
        actions.setAlignItems(Alignment.CENTER);
        actions.getThemeList().add("spacing-xs");
        return actions;
    }

    private TextField textField(String label) {
        TextField field = new TextField(label);
        field.setWidthFull();
        return field;
    }

    private ComboBox<Ingredient> ingredientPickerField(String label) {
        ComboBox<Ingredient> field = new ComboBox<>(label);
        field.setWidthFull();
        field.setPlaceholder("Начните вводить название, INCI, CAS, EC, CI");
        field.setClearButtonVisible(true);
        field.setAllowCustomValue(false);
        field.setItemLabelGenerator(this::formatIngredientOption);
        return field;
    }

    private TextArea textArea(String label, int minRows) {
        TextArea field = new TextArea(label);
        field.setWidthFull();
        field.setMinHeight((minRows * 24 + 48) + "px");
        return field;
    }

    private BigDecimalField bigDecimalField(String label) {
        BigDecimalField field = new BigDecimalField(label);
        field.setWidthFull();
        return field;
    }

    private IntegerField integerField(String label) {
        IntegerField field = new IntegerField(label);
        field.setWidthFull();
        return field;
    }

    private Select<String> selectField(String label, List<String> items, java.util.function.Function<String, String> labelProvider) {
        Select<String> field = new Select<>();
        field.setLabel(label);
        field.setWidthFull();
        field.setItems(items);
        field.setItemLabelGenerator(item -> item == null ? "" : labelProvider.apply(item));
        return field;
    }

    private TextField addReadOnlyTextField(FormLayout form, String label, String value) {
        TextField field = textField(label);
        field.setValue(value);
        field.setReadOnly(true);
        return addField(form, field, false);
    }

    private Select<IngredientKind> addKindField(FormLayout form, Binder<Ingredient> binder) {
        Select<IngredientKind> field = new Select<>();
        field.setLabel("Тип");
        field.setWidthFull();
        field.setItems(IngredientKind.values());
        field.setItemLabelGenerator(this::formatIngredientKind);
        binder.bind(field, Ingredient::getKind, Ingredient::setKind);
        return addField(form, field, false);
    }

    private VerticalLayout createIngredientCardLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setWidthFull();
        return layout;
    }

    private HorizontalLayout pairRow(Component left, Component right) {
        HorizontalLayout row = new HorizontalLayout(
                pairCell(left),
                pairCell(right)
        );
        row.setPadding(false);
        row.setSpacing(true);
        row.setWidthFull();
        row.getStyle().set("flex-wrap", "wrap");
        return row;
    }

    private Div pairCell(Component content) {
        Div cell = new Div(content);
        cell.setWidthFull();
        cell.getStyle().set("flex", "1 1 320px");
        cell.getStyle().set("min-width", "0");
        return cell;
    }

    private Div readTextItem(String label, String value) {
        return readComponentItem(label, wrapColumnText(value));
    }

    private Div readWrappedTextItem(String label, String value) {
        return readComponentItem(label, wrapColumnText(value));
    }

    private Div readComponentItem(String label, Component component) {
        Span caption = new Span(label);
        caption.getStyle().set("display", "block");
        caption.getStyle().set("font-size", "var(--lumo-font-size-s)");
        caption.getStyle().set("font-weight", "600");
        caption.getStyle().set("color", "var(--lumo-secondary-text-color)");
        caption.getStyle().set("margin-bottom", "var(--lumo-space-xs)");

        Div cell = new Div(caption, component);
        cell.setWidthFull();
        cell.getStyle().set("min-width", "0");
        return cell;
    }

    private TextField readOnlyTextField(String label, String value) {
        TextField field = textField(label);
        field.setValue(nullToEmpty(value));
        field.setReadOnly(true);
        return field;
    }

    private Select<IngredientKind> kindField(Binder<Ingredient> binder) {
        Select<IngredientKind> field = new Select<>();
        field.setLabel("Тип");
        field.setWidthFull();
        field.setItems(IngredientKind.values());
        field.setItemLabelGenerator(this::formatIngredientKind);
        binder.bind(field, Ingredient::getKind, Ingredient::setKind);
        return field;
    }

    private TextField requiredTextField(
            Binder<Ingredient> binder,
            String label,
            String requiredMessage,
            ValueProvider<Ingredient, String> getter,
            Setter<Ingredient, String> setter
    ) {
        TextField field = textField(label);
        binder.forField(field)
                .asRequired(requiredMessage)
                .bind(getter, setter);
        return field;
    }

    private TextField boundTextField(
            Binder<Ingredient> binder,
            String label,
            ValueProvider<Ingredient, String> getter,
            Setter<Ingredient, String> setter
    ) {
        TextField field = textField(label);
        binder.bind(field, getter, setter);
        return field;
    }

    private TextArea boundTextArea(
            Binder<Ingredient> binder,
            String label,
            int minRows,
            ValueProvider<Ingredient, String> getter,
            Setter<Ingredient, String> setter
    ) {
        TextArea field = textArea(label, minRows);
        binder.bind(field, getter, setter);
        return field;
    }

    private TextField addRequiredTextField(
            FormLayout form,
            Binder<Ingredient> binder,
            String label,
            String requiredMessage,
            ValueProvider<Ingredient, String> getter,
            Setter<Ingredient, String> setter
    ) {
        TextField field = textField(label);
        binder.forField(field)
                .asRequired(requiredMessage)
                .bind(getter, setter);
        return addField(form, field, false);
    }

    private TextField addTextField(
            FormLayout form,
            Binder<Ingredient> binder,
            String label,
            boolean fullWidth,
            ValueProvider<Ingredient, String> getter,
            Setter<Ingredient, String> setter
    ) {
        TextField field = textField(label);
        binder.bind(field, getter, setter);
        return addField(form, field, fullWidth);
    }

    private TextArea addTextArea(
            FormLayout form,
            Binder<Ingredient> binder,
            String label,
            int minRows,
            boolean fullWidth,
            ValueProvider<Ingredient, String> getter,
            Setter<Ingredient, String> setter
    ) {
        TextArea field = textArea(label, minRows);
        binder.bind(field, getter, setter);
        return addField(form, field, fullWidth);
    }

    private <T extends Component> T addField(FormLayout form, T field, boolean fullWidth) {
        form.add(field);
        if (fullWidth) {
            form.setColspan(field, FULL_WIDTH_COLSPAN);
        }
        return field;
    }

    private void addTextItem(FormLayout form, String label, String value) {
        form.addFormItem(new Span(nullToEmpty(value)), label);
    }

    private void addWrappedTextItem(FormLayout form, String label, String value) {
        form.addFormItem(wrapColumnText(value), label);
    }

    private void addComponentItem(FormLayout form, String label, Component component) {
        form.addFormItem(component, label);
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

    private Anchor buildExternalLink(String url) {
        String safeUrl = SpecialchemUrlResolver.normalize(url);
        Anchor anchor = new Anchor(safeUrl, safeUrl.isBlank() ? "" : safeUrl);
        anchor.setTarget("_blank");
        anchor.getStyle().set("display", "block");
        anchor.getStyle().set("max-width", "100%");
        anchor.getStyle().set("overflow-wrap", "anywhere");
        anchor.getStyle().set("word-break", "break-word");
        return anchor;
    }

    private Component buildOptionalExternalLink(String url, String label) {
        String safeUrl = SpecialchemUrlResolver.normalize(url);
        if (!hasText(safeUrl)) {
            return new Span();
        }
        Anchor anchor = new Anchor(safeUrl, label);
        anchor.setTarget("_blank");
        return anchor;
    }

    private String formatIngredientKind(IngredientKind kind) {
        if (kind == null) {
            return "";
        }
        return switch (kind) {
            case SUBSTANCE -> "ингредиент";
            case MIXTURE -> "смесь";
            case MATERIAL -> "материал";
        };
    }

    private Details openedDetails(String title, Component content) {
        Details details = new Details(title, content);
        details.setOpened(true);
        return details;
    }

    private String formatComponentName(IngredientComponent component) {
        if (component.getComponentIngredient() != null) {
            return formatIngredientOption(component.getComponentIngredient());
        }
        return nullToEmpty(component.getComponentNameRaw());
    }

    private String formatIngredientOption(Ingredient ingredient) {
        if (ingredient == null) {
            return "";
        }

        StringBuilder label = new StringBuilder();
        label.append('#').append(ingredient.getIngredientId()).append(" ");
        label.append(nullToEmpty(ingredient.getPrimaryName()));

        if (hasText(ingredient.getInciName())
                && !ingredient.getInciName().equalsIgnoreCase(nullToEmpty(ingredient.getPrimaryName()))) {
            label.append(" / ").append(ingredient.getInciName());
        }

        if (ingredient.getKind() != null) {
            label.append(" [").append(formatIngredientKind(ingredient.getKind())).append(']');
        }
        return label.toString();
    }

    private String formatMediumType(String value) {
        return switch (nullToEmpty(value)) {
            case "water" -> "Вода";
            case "oil" -> "Масло";
            case "alcohol" -> "Спирт";
            case "glycol" -> "Гликоль";
            case "silicone" -> "Силикон";
            case "hydrocarbon" -> "Углеводород";
            case "other" -> "Другое";
            default -> nullToEmpty(value);
        };
    }

    private String formatSolubilityClass(String value) {
        return switch (nullToEmpty(value)) {
            case "soluble" -> "Растворим";
            case "partially_soluble" -> "Частично растворим";
            case "insoluble" -> "Нерастворим";
            case "dispersible" -> "Диспергируем";
            case "swells" -> "Набухает";
            default -> nullToEmpty(value);
        };
    }

    private String formatWaxPropertyType(String value) {
        return switch (nullToEmpty(value)) {
            case "dropping_point" -> "Температура каплепадения";
            case "melting_point" -> "Температура плавления";
            default -> nullToEmpty(value);
        };
    }

    private String formatRelationType(String value) {
        return switch (nullToEmpty(value)) {
            case "source_ingredient" -> "Source ingredient";
            case "matched_component" -> "Matched component";
            default -> nullToEmpty(value);
        };
    }

    private String formatFormulationTagType(String value) {
        return switch (nullToEmpty(value)) {
            case "end_consumer_benefit" -> "End consumer benefit";
            case "application" -> "Application";
            case "application_format" -> "Application format";
            case "application_market" -> "Application market";
            default -> nullToEmpty(value);
        };
    }

    private String formatSolubilityValue(IngredientSolubility solubility) {
        List<String> parts = new ArrayList<>();
        appendPart(parts, formatSolubilityClass(solubility.getSolubilityClass()));
        appendPart(parts, solubility.getSolubilityText());
        if (hasText(solubility.getConcentrationText())) {
            parts.add("Ограничение: " + solubility.getConcentrationText());
        }
        if (solubility.getTemperatureC() != null) {
            parts.add("T " + formatNumber(solubility.getTemperatureC()) + " °C");
        }
        return String.join("; ", parts);
    }

    private String formatSolventValue(IngredientSolvent solvent) {
        List<String> parts = new ArrayList<>();
        appendPart(parts, solvent.getSolventName());
        if (hasText(solvent.getNote())) {
            parts.add("Примечание: " + solvent.getNote());
        }
        return String.join("; ", parts);
    }

    private String formatWaxPropertyValue(IngredientWaxProperty waxProperty) {
        List<String> parts = new ArrayList<>();
        if (waxProperty.getValueNum() != null) {
            String value = formatNumber(waxProperty.getValueNum());
            if (hasText(waxProperty.getUnitName())) {
                value = value + " " + waxProperty.getUnitName();
            }
            parts.add(value);
        }
        appendPart(parts, waxProperty.getValueText());
        if (hasText(waxProperty.getMethodText())) {
            parts.add("Метод: " + waxProperty.getMethodText());
        }
        return String.join("; ", parts);
    }

    private String formatSourceRef(String sheet, Integer rowNum) {
        if (!hasText(sheet) && rowNum == null) {
            return "";
        }
        if (!hasText(sheet)) {
            return String.valueOf(rowNum);
        }
        if (rowNum == null) {
            return sheet;
        }
        return sheet + " / " + rowNum;
    }

    private String formatNumber(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }

    private String formatRelatedFormulationQuantity(RelatedFormulationIngredientDto row) {
        if (hasText(row.quantityRaw())) {
            return row.quantityRaw();
        }

        if (row.quantityValue() == null) {
            return "";
        }

        String value = formatNumber(row.quantityValue());
        return hasText(row.quantityUnit()) ? value + " " + row.quantityUnit() : value;
    }

    private String formatRelatedFormulationMatch(RelatedFormulationIngredientMatchDto match) {
        List<String> parts = new ArrayList<>();
        appendPart(parts, match.ingredientPrimaryName());

        if (hasText(match.matchedName())
                && !match.matchedName().equalsIgnoreCase(nullToEmpty(match.ingredientPrimaryName()))) {
            parts.add("matched as: " + match.matchedName());
        }

        if (parts.isEmpty()) {
            appendPart(parts, match.sourceRef());
        }
        return String.join(" | ", parts);
    }

    private void appendPart(List<String> parts, String value) {
        if (hasText(value)) {
            parts.add(value);
        }
    }

    private void populateComponentFieldsFromIngredient(
            Ingredient ingredient,
            TextField componentNameField,
            TextField inciField,
            TextField casField,
            TextField ecField
    ) {
        if (ingredient == null) {
            return;
        }
        populateIfBlank(componentNameField, ingredient.getPrimaryName());
        populateIfBlank(inciField, ingredient.getInciName());
        populateIfBlank(casField, ingredient.getCasNo());
        populateIfBlank(ecField, ingredient.getEcNo());
    }

    private void populateIfBlank(TextField field, String value) {
        if (field.isEmpty() && hasText(value)) {
            field.setValue(value);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
