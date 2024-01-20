package no.runsafe.mailbox.commands;

import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.mailbox.MailHandler;

public class ViewOtherMailbox extends PlayerCommand
{
	public ViewOtherMailbox(MailHandler mailHandler)
	{
		super(
			"viewOther",
			"Views a players magic parcels",
			"runsafe.mailbox.admin.viewOther",
			new Player(PLAYER).require()
		);
		this.mailHandler = mailHandler;
	}

	private static final String PLAYER = "player";

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		IPlayer player = parameters.getValue(PLAYER);
		if (player == null)
			return "&cInvalid player.";

		this.mailHandler.openMailbox(executor, player);
		return null;
	}

	private final MailHandler mailHandler;
}
