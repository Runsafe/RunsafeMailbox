package no.runsafe.mailbox;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.inventory.RunsafeInventory;

public class MailSendAgent
{
	public MailSendAgent(IPlayer recipient, RunsafeInventory inventory)
	{
		this.recipient = recipient;
		this.inventory = inventory;
	}

	public RunsafeInventory getInventory()
	{
		return this.inventory;
	}

	public IPlayer getRecipient()
	{
		return this.recipient;
	}

	private final RunsafeInventory inventory;
	private final IPlayer recipient;
}
