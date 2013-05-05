package no.runsafe.mailbox.commands;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.mailbox.MailHandler;

import java.util.HashMap;

public class ViewMailbox extends PlayerCommand
{
	public ViewMailbox(MailHandler mailHandler)
	{
		super("viewmail", "Views a players mailbox", "runsafe.mailbox.admin.view");
		this.mailHandler = mailHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		this.mailHandler.openMailbox(executor, executor);
		return null;
	}

	private MailHandler mailHandler;
}
