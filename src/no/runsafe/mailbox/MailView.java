package no.runsafe.mailbox;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.inventory.RunsafeInventory;

public class MailView
{
	public MailView(IPlayer owner, RunsafeInventory mailbox, IPlayer viewer)
	{
		this.owner = owner;
		this.mailbox = mailbox;
		this.viewer = viewer;
	}

	public IPlayer getOwner()
	{
		return this.owner;
	}

	public RunsafeInventory getMailbox()
	{
		return this.mailbox;
	}

	public IPlayer getViewer()
	{
		return this.viewer;
	}

	private final IPlayer owner;
	private final RunsafeInventory mailbox;
	private final IPlayer viewer;
}
