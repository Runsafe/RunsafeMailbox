package no.runsafe.mailbox;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.IServerReady;
import no.runsafe.framework.api.event.world.IWorldLoad;
import no.runsafe.mailbox.repositories.MailboxObjectRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MailboxBlocks implements IServerReady, IWorldLoad
{
	public MailboxBlocks(MailboxObjectRepository repository)
	{
		this.repository = repository;
	}

	public boolean isMailboxBlock(ILocation checkLocation)
	{
		String worldName = checkLocation.getWorld().getName();
		if (blockLocations.containsKey(worldName))
		{
			for (ILocation location : blockLocations.get(worldName))
				if (location.distance(checkLocation) <= 1)
					return true;
		}
		return false;
	}

	@Override
	public void OnServerReady()
	{
		for (ILocation addLocation : repository.getBlockLocations())
		{
			if (addLocation == null)
				continue;
			String worldName = addLocation.getWorld().getName();
			if (!blockLocations.containsKey(worldName))
				blockLocations.put(worldName, new ArrayList<ILocation>(0));
			blockLocations.get(worldName).add(addLocation);
		}
	}

	@Override
	public void OnWorldLoad(IWorld world)
	{
		String worldName = world.getName();
		if (!blockLocations.containsKey(worldName))
			blockLocations.put(worldName, new ArrayList<ILocation>(0));

		for (ILocation addLocation : repository.getBlockLocations(world))
			blockLocations.get(worldName).add(addLocation);
	}

	private HashMap<String, List<ILocation>> blockLocations = new HashMap<String, List<ILocation>>(0);
	private final MailboxObjectRepository repository;
}
