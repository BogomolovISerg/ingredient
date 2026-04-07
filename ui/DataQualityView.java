package catalog.ingredient.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import catalog.ingredient.service.DataQualityService;
import catalog.ingredient.service.dto.DuplicateCandidateRow;

@PageTitle("Качество данных")
@Route(value = "data-quality", layout = MainLayout.class)
public class DataQualityView extends VerticalLayout {

    public DataQualityView(DataQualityService dataQualityService) {
        setSizeFull();
        add(new H2("Качество данных"), new Paragraph("Здесь показываются безопасные кандидаты на слияние дублей по нормализованным имени и INCI без конфликта по CAS/EC/CI."));

        Grid<DuplicateCandidateRow> grid = new Grid<>(DuplicateCandidateRow.class, false);
        grid.addColumn(DuplicateCandidateRow::kind).setHeader("Тип").setAutoWidth(true);
        grid.addColumn(DuplicateCandidateRow::primaryNameNorm).setHeader("Норм. имя").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(DuplicateCandidateRow::inciNameNorm).setHeader("Норм. INCI").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(DuplicateCandidateRow::count).setHeader("Кол-во").setAutoWidth(true);
        grid.addColumn(DuplicateCandidateRow::ingredientIds).setHeader("ID").setAutoWidth(true).setFlexGrow(1);
        grid.setItems(dataQualityService.loadSafeDuplicateCandidates(500));
        grid.setSizeFull();
        add(grid);
        expand(grid);
    }
}
