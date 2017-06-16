package no.runsafe.mailbox;

import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.api.command.Command;
import no.runsafe.framework.features.Commands;
import no.runsafe.framework.features.Database;
import no.runsafe.framework.features.Events;
import no.runsafe.mailbox.commands.*;
import no.runsafe.mailbox.events.CloseInventory;
import no.runsafe.mailbox.events.InventoryClick;
import no.runsafe.mailbox.events.PlayerInteract;
import no.runsafe.mailbox.events.PlayerLogin;
import no.runsafe.mailbox.repositories.MailPackageRepository;
import no.runsafe.mailbox.repositories.MailboxObjectRepository;
import no.runsafe.mailbox.repositories.MailboxRepository;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void pluginSetup()
	{
		// Framework features
		addComponent(Commands.class);
		addComponent(Events.class);
		addComponent(Database.class);

		// Repositories
		this.addComponent(MailboxRepository.class);
		this.addComponent(MailPackageRepository.class);
		addComponent(MailboxObjectRepository.class);

		// Plugin components
		addComponent(MailboxBlocks.class);
		this.addComponent(MailHandler.class);

		// Events
		this.addComponent(CloseInventory.class);
		this.addComponent(PlayerInteract.class);
		this.addComponent(PlayerLogin.class);
		this.addComponent(InventoryClick.class);

		// Make the MailSender API accessible globally
		exportAPI(getInstance(MailSender.class));

		// Commands
		Command mailCommand = new Command("mail", "Mail related commands", null);
		mailCommand.addSubCommand(getInstance(ViewMailbox.class));
		mailCommand.addSubCommand(getInstance(ViewOtherMailbox.class));
		mailCommand.addSubCommand(getInstance(SendMail.class));
		mailCommand.addSubCommand(getInstance(SendBook.class));
		this.addComponent(mailCommand);
	}
}
