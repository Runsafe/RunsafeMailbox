package no.runsafe.mailbox;

import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.api.command.Command;
import no.runsafe.framework.features.Commands;
import no.runsafe.framework.features.Database;
import no.runsafe.framework.features.Events;
import no.runsafe.mailbox.commands.SendBook;
import no.runsafe.mailbox.commands.SendMail;
import no.runsafe.mailbox.commands.ViewMailbox;
import no.runsafe.mailbox.events.CloseInventory;
import no.runsafe.mailbox.events.InventoryClick;
import no.runsafe.mailbox.events.PlayerInteract;
import no.runsafe.mailbox.events.PlayerLogin;
import no.runsafe.mailbox.repositories.MailPackageRepository;
import no.runsafe.mailbox.repositories.MailboxRepository;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void PluginSetup()
	{
		// Framework features
		addComponent(Commands.class);
		addComponent(Events.class);
		addComponent(Database.class);

		// Plugin components
		this.addComponent(MailHandler.class);

		// Repositories
		this.addComponent(MailboxRepository.class);
		this.addComponent(MailPackageRepository.class);

		// Events
		this.addComponent(CloseInventory.class);
		this.addComponent(PlayerInteract.class);
		this.addComponent(PlayerLogin.class);
		this.addComponent(InventoryClick.class);

		// Commands
		Command mailCommand = new Command("mail", "Mail related commands", null);
		mailCommand.addSubCommand(getInstance(ViewMailbox.class));
		mailCommand.addSubCommand(getInstance(SendMail.class));
		mailCommand.addSubCommand(getInstance(SendBook.class));
		this.addComponent(mailCommand);

		// Make the MailSender API accessible globally
		exportAPI(getInstance(MailSender.class));
	}
}
