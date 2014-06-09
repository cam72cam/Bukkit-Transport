package me.cmesh.Transport;

import org.bukkit.plugin.java.JavaPlugin;

public class Transport extends JavaPlugin {
	private TransportListener listener;
	public static Transport Instance;
	
	public Transport() {
		Instance = this;
		listener = new TransportListener();
	}
	
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(listener, this);
	}
}
