package catalog.ingredient.ui;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import catalog.ingredient.service.DashboardService;
import catalog.ingredient.service.dto.DashboardCounters;

@PageTitle("Обзор")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    public DashboardView(DashboardService dashboardService) {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Обзор данных"), new Paragraph("Вторая итерация интерфейса: поиск ингредиентов, смесей, регуляторики и рецептур."));

        DashboardCounters counters = dashboardService.loadCounters();
        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.setSpacing(true);
        cards.setWrap(true);
        cards.add(
                card("Ингредиенты", String.valueOf(counters.totalIngredients())),
                card("Смеси", String.valueOf(counters.totalMixtures())),
                card("Регуляторные записи", String.valueOf(counters.totalRegulatoryEntries())),
                card("Продукты", String.valueOf(counters.totalProducts())),
                card("Формулы", String.valueOf(counters.totalFormulas())),
                card("Требования", String.valueOf(counters.totalRequirements())),
                card("Испытания", String.valueOf(counters.totalTests())),
                card("Безопасные дубли", String.valueOf(counters.safeDuplicateGroups()))
        );
        add(cards);
    }

    private Div card(String title, String value) {
        Div card = new Div();
        card.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "16px")
                .set("padding", "16px")
                .set("min-width", "180px")
                .set("background", "var(--lumo-base-color)");
        card.setWidth(220, Unit.PIXELS);
        card.add(new H2(title), new Paragraph(value));
        return card;
    }
}

