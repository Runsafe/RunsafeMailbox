package no.runsafe.mailbox.commands;

import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mailbox.MailHandler;

import java.util.HashMap;

public class ViewMailbox extends PlayerCommand
{
	public ViewMailbox(MailHandler mailHandler)
	{
		super("view", "Views a players mailbox", "runsafe.mailbox.admin.view");
		this.mailHandler = mailHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		this.mailHandler.openMailbox(executor, executor);
		return null;
	}

	private final MailHandler mailHandler;
}
