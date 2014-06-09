package me.cmesh.Transport;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.event.inventory.*;

public class TransportListener implements Listener {
	@EventHandler(priority = EventPriority.LOW)
	public void test2(org.bukkit.event.player.PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.CARPET && !event.getPlayer().isSneaking()) {
			ItemStack inhand = event.getPlayer().getItemInHand();
			ItemMover.StartItem(inhand, block.getLocation());
			event.setCancelled(true);
			event.getPlayer().setItemInHand(null);
		}
	}
	
	private void StartInv(final Inventory inv) {
		if (inv == null || !(inv.getHolder() instanceof Hopper)) {
			return;
		}
		
		Hopper h = (Hopper)inv.getHolder();
		final Block output = getHopperBlock(h);
		if (output.getType() == Material.CARPET) {
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.runTaskLater(Transport.Instance, new Runnable() {

				@Override
				public void run() {
					for(ItemStack i : inv.getContents()) {
						if (i != null) {
							ItemMover.StartItem(i, output.getLocation());
						}
					}
					inv.clear();
				} }, 1);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void test3(InventoryMoveItemEvent ev) {
		StartInv(ev.getDestination());
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void test4(InventoryClickEvent ev) {
		StartInv(ev.getInventory());
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void test4(InventoryPickupItemEvent ev) {
		StartInv(ev.getInventory());
	}
	
	
	private static final BlockFace[] hopperMap = {
		BlockFace.DOWN,		//0
		BlockFace.SELF, 	//none
		BlockFace.NORTH,	//2
		BlockFace.SOUTH,	//3
		BlockFace.WEST,		//4
		BlockFace.EAST,		//5
	};
	private Block getHopperBlock(Hopper h) {
		int i = h.getData().toString().charAt(7) - '0';
		if (i <= 5) {
			return h.getBlock().getRelative(hopperMap[i]);
		} else {
			//Something strange happened, I keep getting the number 8  when I look inside a chest above a hopper?
			return h.getBlock();
		}
	}
}
 