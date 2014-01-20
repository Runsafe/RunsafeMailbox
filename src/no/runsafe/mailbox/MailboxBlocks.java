package no.runsafe.mailbox;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.mailbox.repositories.MailboxObjectRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MailboxBlocks implements IConfigurationChanged
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
	public void OnConfigurationChanged(IConfiguration config)
	{
		for (ILocation addLocation : repository.getBlockLocations())
		{
			String worldName = addLocation.getWorld().getName();
			if (!blockLocations.containsKey(worldName))
				blockLocations.put(worldName, new ArrayList<ILocation>(0));

			blockLocations.get(worldName).add(addLocation);
		}
	}

	private HashMap<String, List<ILocation>> blockLocations = new HashMap<String, List<ILocation>>(0);
	private final MailboxObjectRepository repository;
}
