package no.runsafe.mailbox.repositories;

import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.internal.database.Repository;
import no.runsafe.framework.internal.database.Row;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.inventory.RunsafeInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MailPackageRepository extends Repository
{
	public MailPackageRepository(IDatabase database)
	{
		this.database = database;
	}

	@Override
	public String getTableName()
	{
		return "mail_packages";
	}

	public RunsafeInventory getMailPackage(int packageID)
	{
		Row data = this.database.QueryRow("SELECT contents FROM mail_packages WHERE ID = ?", packageID);
		RunsafeInventory inventory = RunsafeServer.Instance.createInventory(null, 54, "");

		if (data != null)
			inventory.unserialize(data.String("contents"));

		return inventory;
	}

	public int newPackage(RunsafeInventory contents)
	{
		this.database.Execute("INSERT INTO mail_packages (contents) VALUES(?)", contents.serialize());
		Row data = this.database.QueryRow("SELECT LAST_INSERT_ID() AS ID FROM mail_packages");
		if (data != null)
			return data.Integer("ID");

		return 0;
	}

	public void removePackage(int packageID)
	{
		this.database.Execute("DELETE FROM mail_packages WHERE ID = ?", packageID);
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> versions = new HashMap<Integer, List<String>>();
		ArrayList<String> sql = new ArrayList<String>();
		sql.add(
				"CREATE TABLE `mail_packages` (" +
					"`ID` int(10) unsigned NOT NULL AUTO_INCREMENT," +
					"`contents` longtext," +
					"PRIMARY KEY (`ID`)" +
				")"
		);
		versions.put(1, sql);
		return versions;
	}

	private final IDatabase database;
}
