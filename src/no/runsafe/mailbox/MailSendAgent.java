package no.runsafe.mailbox;

import no.runsafe.framework.server.inventory.RunsafeInventory;
import no.runsafe.framework.server.player.RunsafePlayer;

public class MailSendAgent
{
	public MailSendAgent(RunsafePlayer recipient, RunsafeInventory inventory)
	{
		this.recipient = recipient;
		this.inventory = inventory;
	}

	public RunsafeInventory getInventory()
	{
		return this.inventory;
	}

	public RunsafePlayer getRecipient()
	{
		return this.recipient;
	}

	private final RunsafeInventory inventory;
	private final RunsafePlayer recipient;
}
