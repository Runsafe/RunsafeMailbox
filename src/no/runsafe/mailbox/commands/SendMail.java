package no.runsafe.mailbox.commands;

import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.mailbox.MailHandler;
import no.runsafe.mailbox.MailSender;

public class SendMail extends PlayerCommand
{
	public SendMail(MailHandler mailHandler, MailSender mailSender)
	{
		super("send", "Sends mail to another player", "runsafe.mailbox.send", new Player().require());
		this.mailHandler = mailHandler;
		this.mailSender = mailSender;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		IPlayer player = parameters.getValue("player");

		if (player == null)
			return null;

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
}
