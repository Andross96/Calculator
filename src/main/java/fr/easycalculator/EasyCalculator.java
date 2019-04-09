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
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("EasyCalculator: Enabled."); 
	}
	
	@Override
    public void onDisable() { getLogger().info("EasyCalculator: Disabled."); }
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerChat(final AsyncPlayerChatEvent e) { 
		try {
			if(!e.getPlayer().hasPermission("easycalculator.access")) return;
			
			// Creating variables
			String message = e.getMessage();
			if(message == null || message.isEmpty() || !message.contains("=")) return;
			Pattern p;
			Matcher m;
			final DecimalFormat formatter = new DecimalFormat("#.##");
			
			
	
			// Checking normal calculations:
			// example: "The result is =5+10"
			// group0: =5+10 | group1: 5 | group2: + | group3: 10
			p = Pattern.compile("=(-?\\d*[\\.|\\,]?\\d+)(\\+|\\-|x|\\*|\\/|\\:|\\%)(-?\\d+[\\.|\\,]?\\d*)");
			m = p.matcher(message);
			while (m.find()) {
		    	Double result = null;
				final Double n1 = isNumeric(m.group(1));
				final Double n2 = isNumeric(m.group(3));
				if(n1 == null || n2 == null) continue;
				
				switch(m.group(2)) {
					case "+": result = n1 + n2; break;
					case "-": result = n1 - n2; break;
					case "*":case "x": result = n1 * n2; break;
					case "/":case ":": result = n1 / n2; break;
					case "%": result = n1 % n2; break;
					default: continue;
				}
				
				if(result != null) message = message.replace(m.group(0), formatter.format(result));
		    }
			
			
			
			// Checking functions calculations:
			// example: "The result is =cos(15)"
			// group0: =cos(15) | group1: cos | group2: 15
			p = Pattern.compile("(?i)=(cos|sin|tan|sqrt|ln|log|exp)\\((\\d+)\\)");
			m = p.matcher(message);
			while (m.find()) {
		    	Double result = null;
				final Double n = isNumeric(m.group(2));
				if(n == null) continue;
				
				switch(m.group(1)) {
					case "cos": result = Math.cos(Math.toRadians(n)); break;
					case "sin": result = Math.sin(Math.toRadians(n)); break;
					case "tan": result = Math.tan(Math.toRadians(n)); break;
					case "sqrt": result = Math.sqrt(n); break;
					case "ln": result = Math.log(n); break;
					case "log": result = Math.log(n)/Math.log(10); break;
					case "exp": result = Math.exp(n); break;
					default: continue;
				}
				
				if(result != null) message = message.replace(m.group(0), formatter.format(result));
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
