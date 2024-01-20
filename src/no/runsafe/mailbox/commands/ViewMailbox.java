package no.runsafe.mailbox.commands;

import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.mailbox.MailHandler;

public class ViewMailbox extends PlayerCommand
{
	public ViewMailbox(MailHandler mailHandler)
	{
		super("view", "Views a players magic parcels", "runsafe.mailbox.admin.view");
		this.mailHandler = mailHandler;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		this.mailHandler.openMailbox(executor);
		return null;
	}

	private final MailHandler mailHandler;
}
