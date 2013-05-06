package no.runsafe.mailbox.events;

import no.runsafe.framework.event.inventory.IInventoryClick;
import no.runsafe.framework.server.event.inventory.RunsafeInventoryClickEvent;
import no.runsafe.framework.server.inventory.RunsafeAnvilInventory;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.item.meta.RunsafeItemMeta;
import no.runsafe.framework.server.player.RunsafePlayer;

public class InventoryClick implements IInventoryClick
{
	@Override
	public void OnInventoryClickEvent(RunsafeInventoryClickEvent event)
	{
		RunsafeItemStack item = event.getCurrentItem();

		if (item != null)
		{
			RunsafeItemMeta meta = item.getItemMeta();
			if (meta != null)
			{
				String displayName = meta.getDisplayName();
				if (displayName != null)
				{
					RunsafePlayer player = event.getWhoClicked();
					if (event.getInventory() instanceof RunsafeAnvilInventory)
					{
						if (displayName.startsWith("Mail Package #"))
						{
							player.sendColouredMessage("&cYou cannot do that.");
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
}
