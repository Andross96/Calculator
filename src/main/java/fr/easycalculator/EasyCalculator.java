package fr.easycalculator;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyCalculator extends JavaPlugin implements Listener {
	
	@Override
    public void onEnable() { 
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("EasyCalculator: Enabled."); 
	}
	
	@Override
    public void onDisable() { getLogger().info("EasyCalculator: Disabled."); }
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerChat(final AsyncPlayerChatEvent e) { 
		if(!e.getPlayer().hasPermission("easycalculator.access")) return;
		
		try {
			String message = e.getMessage();
			if(message == null || message.isEmpty() || !message.contains("=")) return;
	
			final Pattern p = Pattern.compile("=(\\d*[\\.|\\,]?\\d+)(\\+|\\-|x|\\*|\\/|\\:)(\\d+[\\.|\\,]?\\d*)");
			final Matcher m = p.matcher(message);
			final DecimalFormat formatter = new DecimalFormat("#.##");
	
			// example: "The result is =5+10"
			// group0 = =5+10 | group1 = 5 | group2 = + | group3 = 10 |
			while (m.find()) {
		    	String result = "%f";
				Double n1 = isNumeric(m.group(1));
				Double n2 = isNumeric(m.group(3));
				if(n1 == null || n2 == null) continue;
				
				switch(m.group(2)) {
					case "+": result = formatter.format(n1 + n2); break;
					case "-": result = formatter.format(n1 - n2); break;
					case "*":case "x": result = formatter.format(n1 * n2); break;
					case "/":case ":": result = formatter.format(n1 / n2); break;
					default: continue;
				}
				
				message = message.replace(m.group(0), result);
		    }
			e.setMessage(message);
		}catch (Exception ex) { }
	}
	
	private Double isNumeric(String str) { 
		try { 
			str = str.replace(',', '.');
			return Double.parseDouble(str);
		} catch(Exception e) {  
			return null;  
		}  
	}
	
}
