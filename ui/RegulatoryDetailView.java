package catalog.ingredient.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import catalog.ingredient.domain.RegulatoryEntry;
import catalog.ingredient.service.RegulatoryService;

@PageTitle("Карточка регуляторной записи")
@Route(value = "regulatory/:id", layout = MainLayout.class)
public class RegulatoryDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final RegulatoryService service;

    public RegulatoryDetailView(RegulatoryService service) {
        this.service = service;
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();
        long id = Long.parseLong(event.getRouteParameters().get("id").orElseThrow());
        RegulatoryEntry entry = service.get(id);

        Button back = new Button("Назад", e -> getUI().ifPresent(ui -> ui.navigate(RegulatoryListView.class)));
        add(back, new H2(entry.getDisplayName()));

        FormLayout form = new FormLayout();
        form.addFormItem(new com.vaadin.flow.component.html.Span(String.valueOf(entry.getEntryId())), "ID");
        form.addFormItem(new com.vaadin.flow.component.html.Span(entry.getListType() == null ? "" : entry.getListType().getDbValue()), "Тип списка");
        form.addFormItem(new com.vaadin.flow.component.html.Span(entry.getEuRefNo()), "Номер позиции");
        form.addFormItem(new com.vaadin.flow.component.html.Span(entry.getCasNo()), "CAS");
        form.addFormItem(new com.vaadin.flow.component.html.Span(entry.getEcNo()), "EC");
        form.addFormItem(new com.vaadin.flow.component.html.Span(entry.getCiNo()), "CI");
        form.addFormItem(new com.vaadin.flow.component.html.Span(entry.getColor()), "Цвет");
        form.addFormItem(new Pre(nullToEmpty(entry.getProductScope())), "Область применения");
        form.addFormItem(new Pre(nullToEmpty(entry.getMaxConcentration())), "Макс. концентрация");
        form.addFormItem(new Pre(nullToEmpty(entry.getOtherRestrictions())), "Прочие ограничения");
        form.addFormItem(new Pre(nullToEmpty(entry.getConsumerWarnings())), "Предупреждения");
        add(form);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}

