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

import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class
 * @version 1.1
 * @author Andross
 */
public final class Calculator extends JavaPlugin {
    private final Utils utils = new Utils();
    private String lastresult = null;
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvent(AsyncPlayerChatEvent.class, new Listener() {}, EventPriority.LOWEST, (l, event) -> {
            final AsyncPlayerChatEvent e = (AsyncPlayerChatEvent) event;

            // Is a calculation?
            String calculation = e.getMessage();
            if (calculation.isEmpty() || (!calculation.startsWith("=") && !calculation.startsWith("@="))) return;

            // Checking permission?
            final Player p = e.getPlayer();
            if (!p.hasPermission("calculator.use")) return;

            // Checking broadcast?
            final boolean broadcast = calculation.startsWith("@");
            if (broadcast) calculation = calculation.substring(1);

            // Removing equal
            calculation = calculation.substring(1);

            try {
                // Calculating
                if(lastresult != null){
                    calculation = calculation.replace("ans",lastresult);
                }
                final String result = utils.calculate(p, calculation);
                if (result == null) {
                    p.sendMessage(utils.color("&cInvalid calculation entered."));
                    e.setCancelled(true);
                    return;
                }
                lastresult = result;
                
                // Result
                final String finalMessage = utils.getPrefix() + utils.color("&7" + calculation + " = &a" + result);
                if (broadcast) e.setMessage(finalMessage);
                else {
                    e.setCancelled(true);
                    p.sendMessage(finalMessage);
                }
            } catch (final Exception ignored) {
                // Ignoring calculation
            }
        }, this, true);
    }
}
