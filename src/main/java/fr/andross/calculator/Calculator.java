package fr.andross.calculator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Calculator extends JavaPlugin {
    private final static String prefix = Utils.color("&e[&r\u2211&e] ");

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvent(AsyncPlayerChatEvent.class, new Listener() {}, EventPriority.LOWEST, (l, event) -> {
            final AsyncPlayerChatEvent e = (AsyncPlayerChatEvent) event;

            try {
                // Is a calculation?
                String calculation = e.getMessage();
                if(calculation.isEmpty() || (!calculation.startsWith("=") && !calculation.startsWith("@="))) return;

                // Checking permission?
                final Player p = e.getPlayer();
                if (!p.hasPermission("calculator.use")) return;

                // Checking broadcast?
                final boolean broadcast = calculation.startsWith("@");
                if (broadcast) calculation = calculation.substring(1);

                // Removing equal
                calculation = calculation.substring(1);

                // Calculating
                final String result = Utils.calculate(p, calculation);
                if (result == null) {
                    p.sendMessage(prefix + Utils.color("&cInvalid calculation entered."));
                    e.setCancelled(true);
                    return;
                }

                // Result
                final String finalMessage = prefix + Utils.color("&7" + calculation + " = &a" + result);
                if (broadcast) e.setMessage(finalMessage);
                else {
                    e.setCancelled(true);
                    p.sendMessage(finalMessage);
                }
            } catch (final Exception ignored) { }
        }, this, true);
    }
}
