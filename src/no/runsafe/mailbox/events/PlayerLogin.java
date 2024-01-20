package no.runsafe.mailbox.events;

import no.runsafe.framework.api.event.player.IPlayerJoinEvent;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerJoinEvent;
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
		IPlayer player = event.getPlayer();
		int mailCount = mailHandler.getInboxCount(player);
		player.sendColouredMessage("%sYou have %d magic parcels in your mailbox!", (mailCount > 0 ? "&a" : "&c"), mailCount);
	}

	private final MailHandler mailHandler;
}
