package no.runsafe.mailbox.repositories;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.database.ISchemaUpdate;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.api.database.SchemaUpdate;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.inventory.RunsafeInventory;

import javax.annotation.Nonnull;

public class MailboxRepository extends Repository
{
	public MailboxRepository(IServer server)
	{
		this.server = server;
	}

	@Nonnull
	@Override
	public String getTableName()
	{
		return "player_mailboxes";
	}

	public RunsafeInventory getMailbox(IPlayer player)
	{
		RunsafeInventory inventory = server.createInventory(null, 27, String.format("%s's Mailbox", player.getName()));

		String data = this.database.queryString("SELECT contents FROM player_mailboxes WHERE player = ?", player.getUniqueId().toString());
		if (data != null)
			inventory.unserialize(data);

		return inventory;
	}

	public void updateMailbox(IPlayer player, RunsafeInventory inventory)
	{
		String contents = inventory.serialize();
		this.database.execute(
			"INSERT INTO player_mailboxes (player, contents) VALUES(?, ?) ON DUPLICATE KEY UPDATE contents = ?",
			player.getUniqueId().toString(), contents, contents
		);
	}

	public void removeMailbox(IPlayer owner)
	{
		this.database.execute(
			"DELETE IGNORE FROM `player_mailboxes` WHERE player = ?", owner
		);
	}

	@Nonnull
	@Override
	public ISchemaUpdate getSchemaUpdateQueries()
	{
		ISchemaUpdate update = new SchemaUpdate();

		update.addQueries(
			"CREATE TABLE `player_mailboxes` (" +
				"`player` varchar(50) NOT NULL," +
				"`contents` longtext," +
				"PRIMARY KEY (`player`)" +
			")"
		);

		update.addQueries(
			String.format("DELETE FROM `%s` WHERE `contents` = 'contents: {}\n'", getTableName()),
			String.format( // Update UUIDs
				"UPDATE IGNORE `%s` SET `player` = " +
					"COALESCE((SELECT `uuid` FROM player_db WHERE `name`=`%s`.`player`), `player`) " +
					"WHERE length(`player`) != 36",
				getTableName(), getTableName()
			)
		);

		return update;
	}

	private final IServer server;
}
