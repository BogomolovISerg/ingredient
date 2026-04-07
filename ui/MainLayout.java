package catalog.ingredient.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        H1 title = new H1("Каталог ингредиентов");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), title);
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.getStyle().set("padding", "0 var(--lumo-space-m)");
        addToNavbar(header);

        Nav nav = new Nav();
        VerticalLayout links = new VerticalLayout();
        links.setPadding(false);
        links.setSpacing(false);
        links.add(
                createLink("Обзор", DashboardView.class, VaadinIcon.DASHBOARD),
                createLink("Ингредиенты", IngredientListView.class, VaadinIcon.SEARCH),
                createLink("Смеси", MixtureListView.class, VaadinIcon.CLUSTER),
                createLink("Композиции", CompositionListView.class, VaadinIcon.CUBE),
                createLink("Регуляторика", RegulatoryListView.class, VaadinIcon.FILE_TABLE),
                createLink("Продукты и формы", ProductListView.class, VaadinIcon.PACKAGE),
                createLink("Качество данных", DataQualityView.class, VaadinIcon.WARNING)
        );
        nav.add(links);
        addToDrawer(new Scroller(nav));
    }

    private RouterLink createLink(String text, Class<? extends com.vaadin.flow.component.Component> navigationTarget, VaadinIcon icon) {
        RouterLink link = new RouterLink();
        link.setRoute(navigationTarget);
        HorizontalLayout row = new HorizontalLayout(icon.create(), new Span(text));
        row.setDefaultVerticalComponentAlignment(HorizontalLayout.Alignment.CENTER);
        row.setPadding(true);
        link.add(row);
        link.getStyle().set("text-decoration", "none").set("color", "inherit");
        return link;
    }
}

