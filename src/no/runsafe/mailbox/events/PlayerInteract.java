package no.runsafe.mailbox.events;

import no.runsafe.framework.event.player.IPlayerInteractEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerInteractEvent;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.item.meta.RunsafeItemMeta;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.mailbox.MailHandler;
import org.bukkit.Material;

public class PlayerInteract implements IPlayerInteractEvent
{
	public PlayerInteract(MailHandler mailHandler)
	{
		this.mailHandler = mailHandler;
	}

	@Override
	public void OnPlayerInteractEvent(RunsafePlayerInteractEvent event)
	{
		if (event.hasItem())
		{
			RunsafeItemStack item = event.getItemStack();
			if (event.getItemStack().getItemId() == Material.CHEST.getId())
			{
				RunsafeItemMeta meta = item.getItemMeta();
				String displayName = meta.getDisplayName();

				if (displayName != null)
				{
					if (displayName.startsWith("Mail Package #"))
					{
						RunsafePlayer player = event.getPlayer();
						String[] split = displayName.split("#");
						this.mailHandler.openPackage(player, Integer.valueOf(split[1]));
						player.getInventory().remove(item);
					}
				}
			}
		}
	}

	private MailHandler mailHandler;
}