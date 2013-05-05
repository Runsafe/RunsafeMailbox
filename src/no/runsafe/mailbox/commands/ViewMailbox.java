package no.runsafe.mailbox.commands;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.mailbox.MailHandler;

import java.util.HashMap;

public class ViewMailbox extends PlayerCommand
{
	public ViewMailbox(MailHandler mailHandler)
	{
		super("viewmailbox", "Views a players mailbox", "runsafe.mailbox.admin.view", "player");
		this.mailHandler = mailHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		String playerName = parameters.get("player");
		RunsafePlayer player = RunsafeServer.Instance.getPlayer(playerName);

		if (player != null)
			this.mailHandler.openMailbox(executor, player);
		else
			executor.sendColouredMessage(String.format("&cThe player %s does not exist.", playerName));

		return null;
	}

	private MailHandler mailHandler;
}
