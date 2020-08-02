/*
 * Calculator - Powerful and lightweight calculator directly in chat
 * Copyright (C) 2020 André Sustac
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.andross.calculator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.text.DecimalFormat;

/**
 * Utility class
 * @version 1.1
 * @author Andross
 */
final class Utils {
    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    private final DecimalFormat format = new DecimalFormat("#.##");
    private final String prefix = color("&e[&r\u2211&e] ");

    /**
     * Colorize a text
     * @param text text to colorize
     * @return the colored text
     */
    @NotNull
    protected String color(@NotNull final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Try to evaluate & calculate from a String
     * @param p the player
     * @param calculation the calculation
     * @return the result, null if it's not a valid calculation
     */
    @Nullable
    protected String calculate(final Player p, final String calculation) {
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
            return format.format(engine.eval(calculation.toLowerCase()
                    .replace("e", "Math.E")
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
                    .replace(",", ".")
                    .replace("x", "*")
                    .replace(":", "/")));
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Trying to get a location object from coords
     * @param w the world from
     * @param coords coordinates string
     * @return a location object, null if not valid
     */
    @Nullable
    protected Location getLocation(final World w, final String coords) {
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

    /**
     * Trying to parse a double
     * @param str string
     * @return the double value from the string, otherwise null
     */
    @Nullable
    protected Double toDouble(final String str) {
        try {
            return Double.parseDouble(str.replace(',', '.'));
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Get the prefix of the plugin
     * @return prefix of the plugin
     */
    @NotNull
    public String getPrefix() {
        return prefix;
    }
}
