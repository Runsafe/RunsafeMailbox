package no.runsafe.mailbox;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.inventory.RunsafeInventory;
import no.runsafe.framework.server.item.meta.RunsafeMeta;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.mailbox.repositories.MailPackageRepository;
import no.runsafe.mailbox.repositories.MailboxRepository;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class MailHandler implements IConfigurationChanged
{
	public MailHandler(MailSender mailSender, MailboxRepository mailboxRepository, MailPackageRepository mailPackageRepository)
	{
		this.mailSender = mailSender;
		this.mailboxRepository = mailboxRepository;
		this.mailPackageRepository = mailPackageRepository;
	}

	public void openMailbox(RunsafePlayer viewer, RunsafePlayer mailboxOwner)
	{
		RunsafeInventory inventory = this.mailboxRepository.getMailbox(mailboxOwner);

		if (inventory.getContents().size() > 0)
		{
			this.openMailboxes.put(viewer.getName(), new MailView(mailboxOwner.getName(), inventory, viewer));
			viewer.openInventory(inventory);
		}
		else
		{
			viewer.sendColouredMessage("&eYour mailbox is empty!");
		}
	}

	public void openMailSender(RunsafePlayer sender, RunsafePlayer recipient)
	{
		sender.sendColouredMessage("&3Sending mail will cost " + this.getMailCostText() + ".");
		RunsafeInventory inventory = RunsafeServer.Instance.createInventory(null, 54, "Mail to " + recipient.getName());
		this.openSendAgents.put(sender.getName(), new MailSendAgent(recipient, inventory));
		sender.openInventory(inventory);
	}

	public boolean isViewingSendAgent(RunsafePlayer sender)
	{
		return this.openSendAgents.containsKey(sender.getName());
	}

	public boolean isViewingMailbox(RunsafePlayer viewer)
	{
		return this.openMailboxes.containsKey(viewer.getName());
	}

	public void closeMailbox(RunsafePlayer viewer)
	{
		if (this.isViewingMailbox(viewer))
		{
			this.openMailboxes.remove(viewer.getName());
			viewer.closeInventory(); // Force close in-case called from outside of an event.
		}
	}

	public void refreshMailbox(RunsafePlayer viewer, RunsafePlayer owner)
	{
		this.closeMailbox(viewer);
		this.openMailbox(viewer, owner);
	}

	public void handleMailboxClose(RunsafePlayer owner)
	{
		boolean hasRemoved = false;
		RunsafeInventory mailbox = this.openMailboxes.get(owner.getName()).getMailbox();
		for (RunsafeMeta itemStack : mailbox.getContents())
		{
			if (itemStack.is(Item.Decoration.Chest))
			{
				String displayName = itemStack.getDisplayName();

				if (displayName != null)
					if (displayName.startsWith("Mail Package #"))
						continue;
			}

			hasRemoved = true;
			mailbox.remove(itemStack);
			owner.getWorld().dropItem(owner.getLocation(), itemStack);
		}

		if (hasRemoved)
			owner.sendColouredMessage("&cThe mailbox growls and spits your items onto the floor.");

		this.mailboxRepository.updateMailbox(owner, mailbox);
		this.closeMailbox(owner);
	}

	public String sendOutstandingMail(RunsafePlayer sender)
	{
		if (this.isViewingSendAgent(sender))
		{
			MailSendAgent agent = this.openSendAgents.get(sender.getName());
			this.removeAgent(sender);

			// Check player can afford to send mail
			if (!this.hasMailCost(sender))
			{
				this.returnGoodsFromAgent(sender, agent);
				this.removeAgent(sender);
				return "&cYou do not have enough money to send mail. Sending mail costs " + this.getMailCostText() + ".";
			}

			RunsafePlayer recipient = agent.getRecipient();

			// Check the recipient has enough free space in their inbox.
			if (!this.mailSender.hasFreeMailboxSpace(recipient))
			{
				this.returnGoodsFromAgent(sender, agent);
				this.removeAgent(sender);
				return "&cThe recipient cannot accept mail at this point.";
			}

			this.removeMailCost(sender); // YOINK.

			this.mailSender.sendMail(recipient, sender.getName(), agent.getInventory());
			this.refreshMailboxViewers(recipient);

			return "&2Mail sent successfully.";
		}
		return null;
	}

	public int getInboxCount(RunsafePlayer player)
	{
		RunsafeInventory mailbox = this.mailboxRepository.getMailbox(player);
		return mailbox.getContents().size();
	}

	public void openPackage(RunsafePlayer player, int packageID)
	{
		RunsafeInventory mailPackage = this.mailPackageRepository.getMailPackage(packageID);
		RunsafeInventory playerInventory = player.getInventory();
		boolean sendWarning = false;
		HashMap<String, Integer> yield = new HashMap<String, Integer>();

		for (RunsafeMeta itemStack : mailPackage.getContents())
		{
			String displayName = itemStack.getDisplayName();
			if (displayName == null) displayName = itemStack.getNormalName();

			if (yield.containsKey(displayName))
				yield.put(displayName, yield.get(displayName) + itemStack.getAmount());
			else
				yield.put(displayName, itemStack.getAmount());

			if (playerInventory.getContents().size() < playerInventory.getSize())
			{
				playerInventory.addItems(itemStack);
			}
			else
			{
				sendWarning = true;
				player.getWorld().dropItem(player.getLocation(), itemStack);
			}
		}
		player.updateInventory();

		for (Map.Entry<String, Integer> result : yield.entrySet())
			player.sendColouredMessage(String.format("&3Gained %sx %s from package.", result.getValue(), result.getKey()));

		if (sendWarning)
			player.sendColouredMessage("&3Your inventory is full, some of the items from the package have been dropped at your feet.");

		this.mailPackageRepository.removePackage(packageID);
	}

	public String getMailCostText()
	{
		return this.mailSendCost + " " + Material.getMaterial(this.mailSendCurrency).name().replace("_", " ").toLowerCase();
	}

	private void returnGoodsFromAgent(RunsafePlayer player, MailSendAgent agent)
	{
		RunsafeInventory inventory = player.getInventory();
		boolean sendWarning = false;
		for (RunsafeMeta itemStack : agent.getInventory().getContents())
		{
			if (inventory.getContents().size() < inventory.getSize())
				inventory.addItems(itemStack);
			else
			{
				sendWarning = true;
				player.getWorld().dropItem(player.getLocation(), itemStack);
			}
		}

		if (sendWarning)
			player.sendColouredMessage("&3Your inventory is full, some returned items have been dropped at your feet.");

		player.updateInventory();
	}

	public void removeAgent(RunsafePlayer sender)
	{
		this.openSendAgents.remove(sender.getName());
	}

	public boolean hasMailCost(RunsafePlayer player)
	{
		return player.getInventory().contains(Item.get(this.mailSendCurrency), this.mailSendCost);
	}

	private void removeMailCost(RunsafePlayer player)
	{
		int currentTaken = 0;
		RunsafeInventory inventory = player.getInventory();
		for (RunsafeMeta itemStack : inventory.getContents())
		{
			if (itemStack.getItemId() == this.mailSendCurrency)
			{
				int need = this.mailSendCost - currentTaken;
				if (itemStack.getAmount() <= need)
				{
					currentTaken += itemStack.getAmount();
					inventory.remove(itemStack);
				}
				else
				{
					itemStack.remove(need);
					currentTaken += need;
				}
			}
		}
	}

	private void refreshMailboxViewers(RunsafePlayer owner)
	{
		for (Map.Entry<String, MailView> openMailbox : this.openMailboxes.entrySet())
			if (openMailbox.getKey().equals(owner.getName()))
				this.refreshMailbox(openMailbox.getValue().getViewer(), owner);
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		this.mailSendCurrency = configuration.getConfigValueAsInt("mailSendCurrency");
		this.mailSendCost = configuration.getConfigValueAsInt("mailSendAmount");
	}

	private final MailSender mailSender;
	public final HashMap<String, MailView> openMailboxes = new HashMap<String, MailView>();
	public final HashMap<String, MailSendAgent> openSendAgents = new HashMap<String, MailSendAgent>();
	private final MailboxRepository mailboxRepository;
	private final MailPackageRepository mailPackageRepository;
	private int mailSendCurrency;
	private int mailSendCost;
}
