package no.runsafe.mailbox.events;

import no.runsafe.framework.api.event.inventory.IInventoryClosed;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.inventory.RunsafeInventoryCloseEvent;
import no.runsafe.mailbox.MailHandler;

public class CloseInventory implements IInventoryClosed
{
	public CloseInventory(MailHandler mailHandler)
	{
		this.mailHandler = mailHandler;
	}

	@Override
	public void OnInventoryClosed(RunsafeInventoryCloseEvent event)
	{
		IPlayer player = event.getPlayer();

		if (this.mailHandler.isViewingSendAgent(player))
			player.sendColouredMessage(this.mailHandler.sendOutstandingMail(player));

		if (this.mailHandler.isViewingMailbox(player))
			this.mailHandler.handleMailboxClose(player);
	}

	private final MailHandler mailHandler;
}
