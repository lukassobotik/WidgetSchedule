package lukas.sobotik.widgetschedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrawableParser {
    public static HashMap<String, Integer> drawableMap;

    public static void initialize() {
        drawableMap = new HashMap<>();
        drawableMap.put("red", R.drawable.rounded_background_red);
        drawableMap.put("pink", R.drawable.rounded_background_pink);
        drawableMap.put("orange", R.drawable.rounded_background_orange);
        drawableMap.put("lime", R.drawable.rounded_background_lime);
        drawableMap.put("green", R.drawable.rounded_background_green);
        drawableMap.put("teal", R.drawable.rounded_background_teal);
        drawableMap.put("cyan", R.drawable.rounded_background_cyan);
        drawableMap.put("light_blue", R.drawable.rounded_background_light_blue);
        drawableMap.put("blue", R.drawable.rounded_background_blue);
        drawableMap.put("purple", R.drawable.rounded_background_purple);
        drawableMap.put("indigo", R.drawable.rounded_background_indigo);
        drawableMap.put("deep_pink", R.drawable.rounded_background_deep_pink);
        drawableMap.put("coral", R.drawable.rounded_background_coral);
        drawableMap.put("gold", R.drawable.rounded_background_gold);
        drawableMap.put("silver", R.drawable.rounded_background_silver);
        drawableMap.put("yellow", R.drawable.rounded_background_yellow);
    }

    public static int getDrawableId(String s) {
        initialize();

        int drawableId = -1;
        if (drawableMap.containsKey(s)) {
            drawableId = drawableMap.get(s);
        }
        return drawableId;
    }

    public static String getDrawableName(int drawableId) {
        initialize();

        String drawableName = "";
        for (String key : drawableMap.keySet()) {
            if (drawableMap.get(key) == drawableId) {
                drawableName = key;
            }
        }
        return drawableName;
    }

    public static List<Integer> getAllDrawables() {
        initialize();
        return new ArrayList<>(drawableMap.values());
    }
}
