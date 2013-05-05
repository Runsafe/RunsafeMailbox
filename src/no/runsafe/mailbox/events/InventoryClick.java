package no.runsafe.mailbox.events;

import no.runsafe.framework.event.inventory.IInventoryClick;
import no.runsafe.framework.server.event.inventory.RunsafeInventoryClickEvent;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.player.RunsafePlayer;

public class InventoryClick implements IInventoryClick
{
	@Override
	public void OnInventoryClickEvent(RunsafeInventoryClickEvent event)
	{
		RunsafeItemStack item = event.getCurrentItem();

		if (item != null)
		{
			String displayName = item.getItemMeta().getDisplayName();
			if (displayName != null)
			{
				if (displayName.startsWith("Mail Package #"))
				{
					RunsafePlayer player = event.getWhoClicked();
					player.sendColouredMessage("&cYou cannot do that.");
					event.setCancelled(true);
				}
			}
		}
	}
}
