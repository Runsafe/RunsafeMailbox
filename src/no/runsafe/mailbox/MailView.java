package no.runsafe.mailbox;

import no.runsafe.framework.minecraft.inventory.RunsafeInventory;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

public class MailView
{
	public MailView(String owner, RunsafeInventory mailbox, RunsafePlayer viewer)
	{
		this.owner = owner;
		this.mailbox = mailbox;
		this.viewer = viewer;
	}

	public String getOwner()
	{
		return this.owner;
	}

	public RunsafeInventory getMailbox()
	{
		return this.mailbox;
	}

	public RunsafePlayer getViewer()
	{
		return this.viewer;
	}

	private final String owner;
	private final RunsafeInventory mailbox;
	private final RunsafePlayer viewer;
}
