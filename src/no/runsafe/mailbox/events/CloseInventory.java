package no.runsafe.mailbox.events;

import no.runsafe.framework.event.inventory.IInventoryClosed;
import no.runsafe.framework.server.event.inventory.RunsafeInventoryCloseEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
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
		RunsafePlayer player = event.getPlayer();

		if (this.mailHandler.isViewingSendAgent(player))
			player.sendColouredMessage(this.mailHandler.sendOutstandingMail(player));

		if (this.mailHandler.isViewingMailbox(player))
			this.mailHandler.handleMailboxClose(player);
	}

	private MailHandler mailHandler;
}
