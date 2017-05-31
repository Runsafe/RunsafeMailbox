package no.runsafe.mailbox.repositories;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.database.ISchemaUpdate;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.api.database.SchemaUpdate;

import javax.annotation.Nonnull;
import java.util.List;

public class MailboxObjectRepository extends Repository
{
	public MailboxObjectRepository()
	{
	}

	@Nonnull
	@Override
	public String getTableName()
	{
		return "mailbox_blocks";
	}

	public List<ILocation> getBlockLocations()
	{
		return database.queryLocations("SELECT `world`, `x`, `y`, `z` FROM `mailbox_blocks`");
	}

	public List<ILocation> getBlockLocations(IWorld world)
	{
		return database.queryLocations("SELECT `world`, `x`, `y`, `z` FROM `mailbox_blocks` WHERE world=?", world.getName());
	}

	@Nonnull
	@Override
	public ISchemaUpdate getSchemaUpdateQueries()
	{
		ISchemaUpdate update = new SchemaUpdate();

		update.addQueries("CREATE TABLE `mailbox_blocks` (" +
			"`world` VARCHAR(50) NULL," +
			"`x` DOUBLE NULL," +
			"`y` DOUBLE NULL," +
			"`z` DOUBLE NULL" +
		")");

		return update;
	}
}
