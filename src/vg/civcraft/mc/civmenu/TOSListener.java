package vg.civcraft.mc.civmenu;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import vg.civcraft.mc.civmenu.database.TOSManager;
import vg.civcraft.mc.civmodcore.Config;
import vg.civcraft.mc.civmodcore.annotations.CivConfig;
import vg.civcraft.mc.civmodcore.annotations.CivConfigType;
import vg.civcraft.mc.civmodcore.annotations.CivConfigs;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;

public class TOSListener implements Listener {

	private CivMenu plugin = CivMenu.getInstance();
	private Map<UUID, Location> locations;
	private Config config = plugin.GetConfig();
	private CivMenuAPI api = CivMenuAPI.getInstance();

	public TOSListener() {
		locations = new ConcurrentHashMap<UUID, Location>();
	}
	
	@CivConfigs({
		@CivConfig(name = "terms.kickDelay", def = "6000", type = CivConfigType.Int),
		@CivConfig(name = "terms.kickMessage", def = "You must accept the terms in order to play", type = CivConfigType.String)
	})
	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		if (!TOSManager.isTermPlayer(p, "CivMenu Agreement")) {
			sendTOS(p);
			locations.put(p.getUniqueId(), p.getLocation());
			new BukkitRunnable() {
				
				@Override
				public void run() {
					locations.remove(p.getUniqueId());
					if(!TOSManager.isTermPlayer(p, "CivMenu Agreement")){
						p.kickPlayer(plugin.GetConfig().get("terms.kickMessage").getString());
					}
					
				}
			}.runTaskLater(this.plugin, plugin.GetConfig().get("terms.kickDelay").getInt());
		}
	}
	
	@CivConfig(name = "terms.MovementRange", def = "15", type = CivConfigType.Int)
	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (!TOSManager.isTermPlayer(p, "CivMenu Agreement")) {
			if(event.getTo().distance(locations.get(p.getUniqueId())) > plugin.GetConfig().get("terms.MovementRange").getInt()){
				p.sendMessage(ChatColor.RED + "You must accept the terms in order to play.");
				sendTOS(p);
				event.setTo(locations.get(p.getUniqueId()));
			}
		}
	}

	@CivConfigs({
		@CivConfig(name = "terms.link", def = "http://www.google.com", type = CivConfigType.String),
		@CivConfig(name = "terms.message", def = "You can click this message to open up the terms of service.", type = CivConfigType.String),
		@CivConfig(name = "terms.welcome" , def = "Welcome!", type = CivConfigType.String),
		@CivConfig(name = "terms.confirm", def = "Once you've read it, you can click this message to agree to the terms", type = CivConfigType.String),
	})
	public void sendTOS(Player p) {

		Menu menu = new Menu();

		TextComponent welcome = new TextComponent(config.get("terms.welcome").getString());
		welcome.setColor(ChatColor.RED);
		welcome.setBold(true);
		menu.setTitle(welcome);

		TextComponent agree = new TextComponent(
				config.get("terms.message").getString());

		agree.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, config.get("terms.link").getString()));
		menu.setSubTitle(agree);
		
		TextComponent confirm = new TextComponent(config.get("terms.confirm").getString());
		confirm.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
				"/sign"));
		
		menu.addPart(confirm);
		
		api.performAction(p, menu);
	}
}