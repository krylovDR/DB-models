package graphics;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

public class FigureSettings {
    private final String path = "src\\graphics\\settings.json";
    private final ObjectMapper mapper;
    private final ObjectNode root;
    private final ArrayNode graphicsArray;

    /**
     * Конструктор, создаёт JSON файл
     * @param countGraphics число графиков, о которых будет хранится информация
     */
    public FigureSettings(int countGraphics) {
        if (countGraphics < 1) throw new RuntimeException("Count graphics must be >0");
        mapper = new ObjectMapper();

        root = mapper.createObjectNode();
        root.put("title", "title");
        root.put("axis_x_name", "values X");
        root.put("axis_y_name", "values Y");

        graphicsArray = mapper.createArrayNode();
        for (int i = 0; i < countGraphics; i++) {
            ObjectNode node = mapper.createObjectNode();
            node.put("graphic_name", "name");
            node.put("color", "color");
            node.put("style", "line");
            node.put("marker_style", "dot");
            node.put("dot_size", 2);

            graphicsArray.add(node);
        }

        root.set("graphics", graphicsArray);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), root);

        } catch (IOException e) {
            e.printStackTrace();
        }
        graphicsArray.removeAll();  // очистка массива с инфой графиков для дальнейшего редактирования
    }


    /**
     * Сеттер для названия заголовка
     * @param title название
     */
    public void setTitle(String title) {
        root.put("title", title);
    }


    /**
     * Сеттер для названия оси X
     * @param nameX название
     */
    public void setAxisX(String nameX) {
        root.put("axis_x_name", nameX);
    }


    /**
     * Сеттер для названия оси Y
     * @param nameY название
     */
    public void setAxisY(String nameY) {
        root.put("axis_y_name", nameY);
    }


    /**
     * Метод добавления информации о графике
     * @param name название графика
     * @param color цвет
     * @param style стиль
     * @param marker стиль маркера
     * @param dotSize размер точки
     */
    public void addGraphicParameters(String name, String color, String style, String marker, int dotSize) {
        ObjectNode node = mapper.createObjectNode();
        node.put("graphic_name", name);
        node.put("color", color);
        node.put("style", style);
        node.put("marker_style", marker);
        node.put("dot_size", dotSize);

        graphicsArray.add(node);
    }


    /**
     * Метод очистки массива с информацией о графиках
     */
    public void clearArrayNode() {
        graphicsArray.removeAll();
    }


    /**
     * Метод сохранения информации в JSON
     */
    public void saveJSON() {
        root.set("graphics", graphicsArray);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
