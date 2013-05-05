package no.runsafe.mailbox.commands;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.mailbox.MailHandler;

import java.util.HashMap;

public class SendMail extends PlayerCommand
{
	public SendMail(MailHandler mailHandler)
	{
		super("mail", "Sends mail to another player", "runsafe.mailbox.send", "player");
		this.mailHandler = mailHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		RunsafePlayer player = RunsafeServer.Instance.getPlayer(parameters.get("player"));

		if (player != null)
		{
			if (this.mailHandler.hasFreeMailboxSpace(player))
				this.mailHandler.openMailSender(executor, player);
			else
				executor.sendColouredMessage("&cThat player cannot receive mail right now.");
		}
		else
		{
			executor.sendColouredMessage(String.format("&cThe player %s does not exist.", parameters.get("player")));
		}

		return null;
	}

	private MailHandler mailHandler;
}
