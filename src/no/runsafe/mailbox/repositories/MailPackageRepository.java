package no.runsafe.mailbox.repositories;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.database.*;
import no.runsafe.framework.minecraft.inventory.RunsafeInventory;

public class MailPackageRepository extends Repository
{
	public MailPackageRepository(IServer server)
	{
		this.server = server;
	}

	@Override
	public String getTableName()
	{
		return "mail_packages";
	}

	public RunsafeInventory getMailPackage(int packageID)
	{
		RunsafeInventory inventory = server.createInventory(null, 54, "");

		String data = this.database.queryString("SELECT contents FROM mail_packages WHERE ID = ?", packageID);
		if (data != null)
			inventory.unserialize(data);

		return inventory;
	}

	public int newPackage(RunsafeInventory contents)
	{
		ITransaction transaction = database.isolate();
		transaction.execute("INSERT INTO mail_packages (contents) VALUES(?)", contents.serialize());
		Integer id = transaction.queryInteger("SELECT LAST_INSERT_ID()");
		transaction.Commit();
		return id == null ? 0 : id;
	}

	public void removePackage(int packageID)
	{
		this.database.execute("DELETE FROM mail_packages WHERE ID = ?", packageID);
	}

	@Override
	public ISchemaUpdate getSchemaUpdateQueries()
	{
		ISchemaUpdate update = new SchemaUpdate();

		update.addQueries(
			"CREATE TABLE `mail_packages` (" +
				"`ID` int(10) unsigned NOT NULL AUTO_INCREMENT," +
				"`contents` longtext," +
				"PRIMARY KEY (`ID`)" +
			")"
		);

		return update;
	}

	private final IServer server;
}
