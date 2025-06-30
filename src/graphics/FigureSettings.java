package graphics;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

public class FigureSettings {
    private final String path = "src\\graphics\\settings.json";

    public FigureSettings(int countGraphics) {
        if (countGraphics < 2) throw new RuntimeException("Count graphics must be >1");
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();
        root.put("title", "title");
        root.put("axis_x_name", "values X");
        root.put("axis_y_name", "values Y");

        ArrayNode graphicsArray = mapper.createArrayNode();
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
    }

}
