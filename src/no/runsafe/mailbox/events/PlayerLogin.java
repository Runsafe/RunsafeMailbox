package no.runsafe.mailbox.events;

import no.runsafe.framework.event.player.IPlayerLoginEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerLoginEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.mailbox.MailHandler;

public class PlayerLogin implements IPlayerLoginEvent
{
	public PlayerLogin(MailHandler mailHandler)
	{
		this.mailHandler = mailHandler;
	}

	@Override
	public void OnPlayerLogin(RunsafePlayerLoginEvent event)
	{
		RunsafePlayer player = event.getPlayer();
		player.sendColouredMessage("&eYou have " + mailHandler.getInboxCount(player) + " packages in your mailbox!");
	}

	private MailHandler mailHandler;
}
