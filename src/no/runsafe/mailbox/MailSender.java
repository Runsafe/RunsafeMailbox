package no.runsafe.mailbox;

import no.runsafe.framework.server.inventory.RunsafeInventory;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.item.meta.RunsafeItemMeta;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.mailbox.repositories.MailPackageRepository;
import no.runsafe.mailbox.repositories.MailboxRepository;
import org.bukkit.Material;

public class MailSender
{
	public MailSender(MailboxRepository mailboxRepository, MailPackageRepository mailPackageRepository)
	{
		this.mailboxRepository = mailboxRepository;
		this.mailPackageRepository = mailPackageRepository;
	}

	public void sendMail(RunsafePlayer recipient, String sender, RunsafeInventory inventory)
	{
		RunsafeInventory mailbox = this.mailboxRepository.getMailbox(recipient);
		mailbox.addItems(this.packageMail(sender, inventory));
		this.mailboxRepository.updateMailbox(recipient, mailbox);

		if (recipient.isOnline())
			recipient.sendColouredMessage("&eYou just received mail!");
	}

	public boolean hasFreeMailboxSpace(RunsafePlayer mailboxOwner)
	{
		RunsafeInventory inventory = this.mailboxRepository.getMailbox(mailboxOwner);
		return inventory.getContents().size() < inventory.getSize();
	}

	private RunsafeItemStack packageMail(String sender, RunsafeInventory contents)
	{
		RunsafeItemStack mailPackage = new RunsafeItemStack(Material.CHEST.getId());
		int packageID = this.mailPackageRepository.newPackage(contents);
		mailPackage.setDisplayName("Mail Package #" + packageID).addLore("Sent by " + sender);
		return mailPackage;
	}

	private final MailboxRepository mailboxRepository;
	private final MailPackageRepository mailPackageRepository;
}
