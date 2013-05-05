package no.runsafe.mailbox.events;

import no.runsafe.framework.event.player.IPlayerJoinEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerJoinEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.mailbox.MailHandler;

public class PlayerLogin implements IPlayerJoinEvent
{
	public PlayerLogin(MailHandler mailHandler)
	{
		this.mailHandler = mailHandler;
	}

	@Override
	public void OnPlayerJoinEvent(RunsafePlayerJoinEvent event)
	{
		RunsafePlayer player = event.getPlayer();
		player.sendColouredMessage("&eYou have " + mailHandler.getInboxCount(player) + " packages in your mailbox!");
	}

	private MailHandler mailHandler;
}
