package me.cmesh.Transport;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

@SuppressWarnings("deprecation")
class ItemMover implements Runnable {
	private Item item;
	private int task;
	private Block target;
	private Vector v;
	private BlockFace direction;
	private DyeColor color;
	
	public ItemMover(Item item) {
		this.item = item;
		target = item.getLocation().getBlock();
		color = DyeColor.getByWoolData(target.getData());
		item.setPickupDelay(100000);
		
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		task = scheduler.scheduleSyncRepeatingTask(Transport.Instance, this, 0l, 3l);
	}
	
	public void Cancel() {
		item.setPickupDelay(0);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.cancelTask(task);
	}
	
	private BlockFace FindNext(Block block, BlockFace exclude) {
		BlockFace[] around = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
		for (BlockFace f : around) {
			if (exclude == f) {
				continue;
			}
			
			Block next = block.getRelative(f);
			if (next.getType() == Material.CARPET && DyeColor.getByWoolData(next.getData()) == color) {
				return f;
			}
		}
		return null;
	}
	
	@Override
	public void run(){
		if (item == null) {
			Cancel();
			return;
		}
		if (!item.isValid()) {
			Cancel();
			return;
		}
		//We fell off
		if (item.getLocation().getBlock().getType() != Material.CARPET) {
			Cancel();
			return;
		}
		
		Block curr = item.getLocation().getBlock();
		
		if (curr.equals(target) || v == null) {
			//Time to choose a new target
			if (curr.getType() == Material.CARPET) {
				BlockFace nextFace = FindNext(curr, direction != null ? direction.getOppositeFace() : null);
				
				if (nextFace == null) {
					//We are at the end of the line
					Cancel();
					return;
				}
				
				target = target.getRelative(nextFace);
				double speed = 0.2;
				switch (nextFace) {
					case NORTH:
						v = new Vector(0,0,-speed);
						break;
					case EAST:
						v = new Vector(speed,0,0);
						break;
					case SOUTH:
						v = new Vector(0,0,speed);
						break;
					case WEST:
						v = new Vector(-speed,0,0);
						break;
					default:
						Cancel();
						return;
				}
				direction = nextFace;
			} else {
				Cancel();
				return;
			}
		}
		
		item.setVelocity(v);
	}
	
	public static ItemMover StartItem(ItemStack stack, Location loc) {
		//center loc
		loc = loc.getBlock().getLocation().add(0.5, 0, 0.5);
		
		return new ItemMover(loc.getWorld().dropItem(loc, stack));
	}
}