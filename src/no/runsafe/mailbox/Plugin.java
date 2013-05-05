package no.runsafe.mailbox;

import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.mailbox.commands.SendMail;
import no.runsafe.mailbox.commands.ViewMailbox;
import no.runsafe.mailbox.events.CloseInventory;
import no.runsafe.mailbox.events.PlayerInteract;
import no.runsafe.mailbox.events.PlayerLogin;
import no.runsafe.mailbox.repositories.MailPackageRepository;
import no.runsafe.mailbox.repositories.MailboxRepository;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(MailHandler.class);

		// Repositories
		this.addComponent(MailboxRepository.class);
		this.addComponent(MailPackageRepository.class);

		// Events
		this.addComponent(CloseInventory.class);
		this.addComponent(PlayerInteract.class);
		this.addComponent(PlayerLogin.class);

		// Commands
		this.addComponent(ViewMailbox.class);
		this.addComponent(SendMail.class);
	}
}
