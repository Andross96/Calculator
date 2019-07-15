package fr.andross;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyCalculator extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    private final Pattern allP = Pattern.compile("=\\-?\\d+([\\.|\\,]\\d+)?(([+\\-x*/:%])\\-?\\d+([\\.|\\,]\\d+)?)+");
    private final Pattern firstP = Pattern.compile("=(\\-?\\d+([\\.|\\,]\\d+)?)");
    private final Pattern othersP = Pattern.compile("([+\\-x*/:%])(\\-?\\d+([\\.|\\,]\\d+)?)");
    private final Pattern functionsP = Pattern.compile("(?i)=(sin|asin|cos|acos|tan|atan|sqrt|ln|log|exp|pow2|pow3|fac|rnd)\\((-?\\d*[\\.|\\,]?\\d+)\\)");

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        try {
            // Checking message
            String message = e.getMessage();
            if(message.isEmpty() || (!message.startsWith("=") && !message.startsWith("@="))) return;

            // Checking permission
            final boolean operations = e.getPlayer().hasPermission("easycalculator.operations");
            final boolean functions = e.getPlayer().hasPermission("easycalculator.functions");
            if(!operations && !functions) return;

            // Checking silent
            final boolean silent = message.startsWith("@=");
            if(silent) message = message.substring(1);

            Matcher m;
            final DecimalFormat formatter = new DecimalFormat("#.##");

            if(operations) {
                // Checking normal calculations:
                // Getting the calculation: "The result is =5+10+5" -> =5+10+5
                m = allP.matcher(message);
                if(m.matches()) {
                    // calcul: =5+10+5
                    final String calcul = m.group(0);

                    // firstNumber;
                    // group(0): =5 | group(1): 5
                    m = firstP.matcher(calcul);
                    if(!m.find()) return;
                    Double result = toDouble(m.group(1));
                    if(result == null) return;

                    // Calculing the others numbers, on firstNumber
                    m = othersP.matcher(calcul);
                    // Matches: +10 & +5
                    // group(0): +10 | group(1): + | group(2): 10
                    while (m.find()) {
                        final Double number = toDouble(m.group(2));
                        if(number == null) return;

                        switch(m.group(1)) {
                            case "+": result += number; break;
                            case "-": result -= number; break;
                            case "*":case "x": result *= number; break;
                            case "/":case ":": result /= number; break;
                            case "%": result %= number; break;
                            default: return;
                        }
                    }
                    message = message.replace(calcul, formatter.format(result));
                }
            }

            if(functions) {
                // Checking functions calculations:
                // example: "The result is =cos(15)"
                // group0: =cos(15) | group1: cos | group2: 15
                m = functionsP.matcher(message);
                while (m.find()) {
                    Double result;
                    final Double n = toDouble(m.group(2));
                    if(n == null) continue;

                    switch(m.group(1)) {
                        case "sin": result = Math.sin(Math.toRadians(n)); break;
                        case "asin": result = Math.asin(Math.toRadians(n)); break;
                        case "cos": result = Math.cos(Math.toRadians(n)); break;
                        case "acos": result = Math.acos(Math.toRadians(n)); break;
                        case "tan": result = Math.tan(Math.toRadians(n)); break;
                        case "atan": result = Math.atan(Math.toRadians(n)); break;
                        case "sqrt": result = Math.sqrt(n); break;
                        case "ln": result = Math.log(n); break;
                        case "log": result = Math.log(n)/Math.log(10); break;
                        case "exp": result = Math.exp(n); break;
                        case "pow2": result = Math.pow(n, 2); break;
                        case "pow3": result = Math.pow(n, 3); break;
                        case "fac": result = fac(n); break;
                        case "rnd": result = (double) new Random().nextInt(n.intValue()); break;
                        default: continue;
                    }

                    if(result != null && !Double.isNaN(result)) message = message.replace(m.group(0), formatter.format(result));
                }
            }

            // Checking if it's a silent calculation
            if (silent) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&r\u2211&e] &l&a" + message));
            } else e.setMessage(message);
        } catch (Exception ex) { /* Nothing */ }
    }

    private Double toDouble(String str) {
        try {
            return Double.parseDouble(str.replace(',', '.'));
        } catch(Exception e) {
            return null;
        }
    }

    private Double fac(Double number) {
        try {
            if(number > 20) return null;
            double result = 1d;
            for (int factor = 2; factor <= number; factor++) result *= factor;
            return result;
        } catch(Exception e) {
            return null;
        }
    }

}