package catalog.ingredient.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import catalog.ingredient.domain.RegulatoryEntry;
import catalog.ingredient.domain.RegulatoryListType;
import catalog.ingredient.service.RegulatoryService;

@PageTitle("Регуляторика")
@Route(value = "regulatory", layout = MainLayout.class)
public class RegulatoryListView extends VerticalLayout {

    private final RegulatoryService service;
    private final Grid<RegulatoryEntry> grid = new Grid<>(RegulatoryEntry.class, false);
    private final TextField query = new TextField("Поиск");
    private final ComboBox<RegulatoryListType> type = new ComboBox<>("Тип списка");

    public RegulatoryListView(RegulatoryService service) {
        this.service = service;
        setSizeFull();
        add(new H2("Регуляторные записи"));

        query.setPlaceholder("Название, CAS, EC, CI");
        query.setClearButtonVisible(true);
        type.setItems(RegulatoryListType.values());
        type.setItemLabelGenerator(v -> switch (v) {
            case PROHIBITED -> "Запрещено";
            case RESTRICTED -> "Ограничено";
            case COLORANT -> "Краситель";
            case PRESERVATIVE -> "Консервант";
            case UV_FILTER -> "UV-фильтр";
        });
        Button find = new Button("Найти", e -> reload());
        add(new HorizontalLayout(query, type, find));

        grid.addColumn(RegulatoryEntry::getEntryId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(r -> r.getListType() == null ? "" : r.getListType().getDbValue()).setHeader("Тип").setAutoWidth(true);
        grid.addColumn(RegulatoryEntry::getEuRefNo).setHeader("Позиция").setAutoWidth(true);
        grid.addColumn(RegulatoryEntry::getDisplayName).setHeader("Наименование").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(RegulatoryEntry::getCasNo).setHeader("CAS").setAutoWidth(true);
        grid.addColumn(RegulatoryEntry::getEcNo).setHeader("EC").setAutoWidth(true);
        grid.addColumn(RegulatoryEntry::getCiNo).setHeader("CI").setAutoWidth(true);
        grid.addColumn(RegulatoryEntry::getMaxConcentration).setHeader("Макс. концентрация").setAutoWidth(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                getUI().ifPresent(ui -> ui.navigate(RegulatoryDetailView.class, e.getValue().getEntryId().toString()));
            }
        });
        add(grid);
        expand(grid);
        reload();
    }

    private void reload() {
        grid.setItems(service.search(query.getValue(), type.getValue(), 300));
    }
}
