package no.runsafe.mailbox.events;

import no.runsafe.framework.event.inventory.IInventoryClick;
import no.runsafe.framework.server.event.inventory.RunsafeInventoryClickEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.mailbox.MailHandler;
import org.bukkit.Material;

public class InventoryClick implements IInventoryClick
{
	public InventoryClick(MailHandler mailHandler)
	{
		this.mailHandler = mailHandler;
	}

	@Override
	public void OnInventoryClickEvent(RunsafeInventoryClickEvent event)
	{
		RunsafePlayer player = event.getWhoClicked();
		if (this.mailHandler.isViewingMailbox(player))
		{
			if (event.getCursor().getItemId() == Material.AIR.getId())
				this.mailHandler.removeItemFromMailbox(player, event.getSlot());
			else
			{
				player.sendColouredMessage("ItemID: " + event.getCursor().getItemId());
				event.setCancelled(true); // Prevent item switching.
			}
		}
	}

	private MailHandler mailHandler;
}