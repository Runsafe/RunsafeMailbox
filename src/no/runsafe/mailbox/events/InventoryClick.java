package no.runsafe.mailbox.events;

import no.runsafe.framework.api.event.inventory.IInventoryClick;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.inventory.RunsafeInventoryClickEvent;
import no.runsafe.framework.minecraft.inventory.RunsafeAnvilInventory;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;

public class InventoryClick implements IInventoryClick
{
	@Override
	public void OnInventoryClickEvent(RunsafeInventoryClickEvent event)
	{
		RunsafeMeta item = event.getCurrentItem();

		if (item == null || !item.hasItemMeta() || !(event.getInventory() instanceof RunsafeAnvilInventory))
			return;

		String displayName = item.getDisplayName();
		if (displayName != null)
		{
			if (displayName.startsWith("Mail Package #"))
			{
				IPlayer player = event.getWhoClicked();
				player.sendColouredMessage("&cYou cannot do that.");
				event.cancel();
			}
		}
	}
}
