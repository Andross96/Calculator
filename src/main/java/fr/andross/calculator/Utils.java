package fr.andross.calculator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.text.DecimalFormat;

class Utils {
    private final static ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    private final static DecimalFormat format = new DecimalFormat("#.##");
    private static final char COLOR_CHAR = '\u00A7';

    static String color(final String text) {
        char[] b = text.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i+1] = Character.toLowerCase(b[i+1]);
            }
        }
        return new String(b);
    }

    static String calculate(final Player p, final String calculation) {
        try {
            // Locations?
            if (calculation.startsWith("loc")) {
                final World w = p.getLocation().getWorld();
                final Location from;
                final Location to;
                String inside = calculation.replace("loc(", "");
                inside = inside.substring(0, inside.length() - 1);
                if (inside.contains(":")) {
                    final String[] s = inside.split(":");
                    from = getLocation(w, s[0]);
                    to = getLocation(w, s[1]);
                } else {
                    from = p.getLocation();
                    to = getLocation(w, inside);
                }
                return (from == null || to == null) ? null : format.format(from.distance(to));
            }

            // Calculating
            return format.format(engine.eval(calculation.toLowerCase().replace("e", "Math.E")
                    .replace("pi", "Math.PI")
                    .replace("sqrt2", "Math.SQRT2")
                    .replace("sqrt1_2", "Math.SQRT1_2")
                    .replace("ln2", "Math.LN2")
                    .replace("ln10", "Math.LN10")
                    .replace("log2e", "Math.LOG2E")
                    .replace("log10e", "Math.LOG10E")
                    .replace("abs", "Math.abs")
                    .replace("acos", "Math.acos")
                    .replace("asin", "Math.asin")
                    .replace("atan", "Math.atan")
                    .replace("atan2", "Math.atan2")
                    .replace("ceil", "Math.ceil")
                    .replace("cos", "Math.cos")
                    .replace("exp", "Math.exp")
                    .replace("floor", "Math.floor")
                    .replace("log", "Math.log")
                    .replace("max", "Math.max")
                    .replace("min", "Math.min")
                    .replace("pow", "Math.pow")
                    .replace("random", "Math.random")
                    .replace("round", "Math.round")
                    .replace("sin", "Math.sin")
                    .replace("sqrt", "Math.sqrt")
                    .replace("tan", "Math.tan")
                    .replace(",", ".")));
        } catch (final Exception e) {
            return null;
        }
    }

    private static Location getLocation(final World w, final String coords) {
        try {
            final String[] s = coords.split(";");
            if (s.length != 3) return null;
            final Double x = toDouble(s[0]);
            if (x == null) return null;
            final Double y = toDouble(s[1]);
            if (y == null) return null;
            final Double z = toDouble(s[2]);
            if (z == null) return null;
            return new Location(w, x, y, z);
        } catch (final Exception e) {
            return null;
        }
    }

    private static Double toDouble(final String str) {
        try {
            return Double.parseDouble(str.replace(',', '.'));
        } catch (final Exception e) {
            return null;
        }
    }

}
