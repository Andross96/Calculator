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
			Matcher m;
			final DecimalFormat formatter = new DecimalFormat("#.##");
			
			
			// Checking normal calculations:
			// Getting the calculation: "The result is =5+10+5" -> =5+10+5
			m = Pattern.compile("=\\-?\\d+([\\.|\\,]\\d+)?((\\+|\\-|x|\\*|\\/|\\:|\\%)\\-?\\d+([\\.|\\,]\\d+)?)+").matcher(message);
			if(m.matches()) {
				// calcul: =5+10+5
				final String calcul = m.group(0);

				// firstNumber;
				// group(0): =5 | group(1): 5
				m = Pattern.compile("=(\\-?\\d+([\\.|\\,]\\d+)?)").matcher(calcul);
				if(!m.find()) return;
				Double result = toDouble(m.group(1));
				if(result == null) return;

				// Calculing the others numbers, on firstNumber
				m = Pattern.compile("(\\+|\\-|x|\\*|\\/|\\:|\\%)(\\-?\\d+([\\.|\\,]\\d+)?)").matcher(calcul);
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
			
			
			// Checking functions calculations:
			// example: "The result is =cos(15)"
			// group0: =cos(15) | group1: cos | group2: 15
			m = Pattern.compile("(?i)=(sin|asin|cos|acos|tan|atan|sqrt|ln|log|exp|pow2|pow3)\\((-?\\d*[\\.|\\,]?\\d+)\\)").matcher(message);
			while (m.find()) {
		    	Double result = null;
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
					default: continue;
				}
				
				if(result != null && !Double.isNaN(result)) message = message.replace(m.group(0), formatter.format(result));
		    }
			
			e.setMessage(message);
		}catch (Exception ex) { }
	}
	
	private Double toDouble(String str) { 
		try {
			str = str.replace(',', '.');
			return Double.parseDouble(str);
		} catch(Exception e) {  
			return null;  
		}  
	}
	
}
