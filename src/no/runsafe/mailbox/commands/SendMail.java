package no.runsafe.mailbox.commands;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.command.argument.PlayerArgument;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IAmbiguousPlayer;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.mailbox.MailHandler;
import no.runsafe.mailbox.MailSender;

import java.util.Map;

public class SendMail extends PlayerCommand
{
	public SendMail(MailHandler mailHandler, MailSender mailSender, IServer server)
	{
		super("send", "Sends mail to another player", "runsafe.mailbox.send", new PlayerArgument());
		this.mailHandler = mailHandler;
		this.mailSender = mailSender;
		this.server = server;
	}

	@Override
	public String OnExecute(IPlayer executor, Map<String, String> parameters)
	{
		IPlayer player = server.getPlayer(parameters.get("player"));

		if (player == null)
			return "&cThat player does not exist.";

		if (player instanceof IAmbiguousPlayer)
			return player.toString();

		if (player.getName().equals(executor.getName()))
			return "&cYou cannot send mail to yourself.";

		if (!this.mailHandler.hasMailCost(executor))
			return "&cYou do not have enough money to send mail. Sending mail costs " + this.mailHandler.getMailCostText() + ".";

		if (!this.mailSender.hasFreeMailboxSpace(player))
			return "&cThat player cannot receive mail right now.";

		this.mailHandler.openMailSender(executor, player);
		return null;
	}

	private final MailHandler mailHandler;
	private final MailSender mailSender;
	private final IServer server;
}
