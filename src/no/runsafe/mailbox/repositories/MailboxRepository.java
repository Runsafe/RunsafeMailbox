package no.runsafe.mailbox.repositories;

import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.inventory.RunsafeInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MailboxRepository extends Repository
{
	public MailboxRepository(IDatabase database)
	{
		this.database = database;
	}

	@Override
	public String getTableName()
	{
		return "player_mailboxes";
	}

	public RunsafeInventory getMailbox(IPlayer player)
	{
		String playerName = player.getName();
		RunsafeInventory inventory = RunsafeServer.Instance.createInventory(null, 27, String.format("%s's Mailbox", playerName));

		String data = this.database.QueryString("SELECT contents FROM player_mailboxes WHERE player = ?", playerName);
		if (data != null)
			inventory.unserialize(data);

		return inventory;
	}

	public void updateMailbox(IPlayer player, RunsafeInventory inventory)
	{
		String contents = inventory.serialize();
		this.database.Execute(
			"INSERT INTO player_mailboxes (player, contents) VALUES(?, ?) ON DUPLICATE KEY UPDATE contents = ?",
			player.getName(), contents, contents
		);
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> versions = new HashMap<Integer, List<String>>();
		ArrayList<String> sql = new ArrayList<String>();
		sql.add(
			"CREATE TABLE `player_mailboxes` (" +
				"`player` varchar(50) NOT NULL," +
				"`contents` longtext," +
				"PRIMARY KEY (`player`)" +
				")"
		);
		versions.put(1, sql);
		return versions;
	}

	private final IDatabase database;
}
