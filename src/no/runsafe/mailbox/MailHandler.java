package no.runsafe.mailbox;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.inventory.RunsafeInventory;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.mailbox.repositories.MailPackageRepository;
import no.runsafe.mailbox.repositories.MailboxRepository;

import java.util.HashMap;
import java.util.Map;

public class MailHandler implements IConfigurationChanged
{
	public MailHandler(MailSender mailSender, MailboxRepository mailboxRepository, MailPackageRepository mailPackageRepository, IServer server)
	{
		this.mailSender = mailSender;
		this.mailboxRepository = mailboxRepository;
		this.mailPackageRepository = mailPackageRepository;
		this.server = server;
	}

	public void openMailbox(IPlayer viewer)
	{
		openMailbox(viewer, viewer);
	}

	public void openMailbox(IPlayer viewer, IPlayer mailboxOwner)
	{
		RunsafeInventory inventory = this.mailboxRepository.getMailbox(mailboxOwner);

		if (inventory.getContents().size() > 0)
		{
			this.openMailboxes.put(viewer, new MailView(mailboxOwner, inventory, viewer));
			viewer.openInventory(inventory);
		}
		else
		{
			if (viewer.equals(mailboxOwner))
				viewer.sendColouredMessage("&eYour mailbox is empty!");
			else
				viewer.sendColouredMessage(mailboxOwner.getPrettyName() + "&e's mailbox is empty!");
		}
	}

	public void openMailSender(IPlayer sender, IPlayer recipient)
	{
		sender.sendColouredMessage("&3Sending a magic parcel will cost " + this.getMailCostText() + ".");
		RunsafeInventory inventory = server.createInventory(null, 54, "Mail to " + recipient.getName());
		this.openSendAgents.put(sender, new MailSendAgent(recipient, inventory));
		sender.openInventory(inventory);
	}

	public boolean isViewingSendAgent(IPlayer sender)
	{
		return this.openSendAgents.containsKey(sender);
	}

	public boolean isViewingMailbox(IPlayer viewer)
	{
		return this.openMailboxes.containsKey(viewer);
	}

	public void closeMailbox(IPlayer viewer)
	{
		if (this.isViewingMailbox(viewer))
		{
			this.openMailboxes.remove(viewer);
			viewer.closeInventory(); // Force close in-case called from outside of an event.
		}
	}

	public void refreshMailbox(IPlayer viewer, IPlayer owner)
	{
		this.closeMailbox(viewer);
		this.openMailbox(viewer, owner);
	}

	public void handleMailboxClose(IPlayer viewer)
	{
		boolean hasRemoved = false;
		MailView mailbox = this.openMailboxes.get(viewer);
		RunsafeInventory mailboxInventory = mailbox.getMailbox();
		for (RunsafeMeta itemStack : mailboxInventory.getContents())
		{
			if (itemStack.is(Item.Decoration.Chest))
			{
				String displayName = itemStack.getDisplayName();

				if (displayName != null)
					if (displayName.startsWith("Magic Parcel #"))
						continue;
			}
			else if (itemStack.is(Item.Special.Crafted.WrittenBook))
			{
				// Allow written books inside the mailbox.
				continue;
			}

			hasRemoved = true;
			mailboxInventory.remove(itemStack);
			viewer.getWorld().dropItem(viewer.getLocation(), itemStack);
		}

		if (hasRemoved)
			viewer.sendColouredMessage("&cThe mailbox growls and spits your items onto the floor.");

		if (mailboxInventory.isEmpty())
			this.mailboxRepository.removeMailbox(mailbox.getOwner());
		else
			this.mailboxRepository.updateMailbox(mailbox.getOwner(), mailboxInventory);
		this.closeMailbox(viewer);
	}

	public String sendOutstandingMail(IPlayer sender)
	{
		if (this.isViewingSendAgent(sender))
		{
			MailSendAgent agent = this.openSendAgents.get(sender);
			this.removeAgent(sender);

			// Check player can afford to send mail
			if (!this.hasMailCost(sender))
			{
				this.returnGoodsFromAgent(sender, agent);
				this.removeAgent(sender);
				return "&cYou do not have enough money to send a magic parcel. Sending parcels costs " + this.getMailCostText() + ".";
			}

			IPlayer recipient = agent.getRecipient();

			// Check the recipient has enough free space in their inbox.
			if (!this.mailSender.hasFreeMailboxSpace(recipient))
			{
				this.returnGoodsFromAgent(sender, agent);
				this.removeAgent(sender);
				return "&cThat recipient cannot receive magic parcels at this point.";
			}

			this.removeMailCost(sender); // YOINK.

			this.mailSender.sendMail(recipient, sender.getName(), agent.getInventory());
			this.refreshMailboxViewers(recipient);

			return "&2Magic Parcel sent successfully.";
		}
		return null;
	}

	public int getInboxCount(IPlayer player)
	{
		RunsafeInventory mailbox = this.mailboxRepository.getMailbox(player);
		return mailbox.getContents().size();
	}

	public void openPackage(IPlayer player, int packageID)
	{
		RunsafeInventory mailPackage = this.mailPackageRepository.getMailPackage(packageID);
		RunsafeInventory playerInventory = player.getInventory();
		boolean sendWarning = false;
		HashMap<String, Integer> yield = new HashMap<>();

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
			player.sendColouredMessage(String.format("&3Gained %sx %s from parcel.", result.getValue(), result.getKey()));

		if (sendWarning)
			player.sendColouredMessage("&3Your inventory is full, some of the items from the package have been dropped at your feet.");

		this.mailPackageRepository.removePackage(packageID);
	}

	public String getMailCostText()
	{
		return String.format("%d %s", this.mailSendCost, currencyName(mailSendCurrency));
	}

	public String getMailBookCostText()
	{
		return String.format("%d %s", this.mailSendBookAmount, currencyName(mailSendBookCurrency));
	}

	private String currencyName(Item item)
	{
		if (item == null)
			return null;
		return item.getType().name().replace("_", " ").toLowerCase();
	}

	private void returnGoodsFromAgent(IPlayer player, MailSendAgent agent)
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

	public void removeAgent(IPlayer sender)
	{
		this.openSendAgents.remove(sender);
	}

	public boolean hasMailCost(IPlayer player)
	{
		return player.hasItem(this.mailSendCurrency, this.mailSendCost);
	}

	public boolean hasMailBookCost(IPlayer player)
	{
		return player.hasItem(this.mailSendBookCurrency, this.mailSendBookAmount);
	}

	public void removeMailBookCost(IPlayer player)
	{
		player.removeItem(this.mailSendBookCurrency, this.mailSendBookAmount);
		player.updateInventory();
	}

	private void removeMailCost(IPlayer player)
	{
		player.removeItem(mailSendCurrency, mailSendCost);
		player.updateInventory();
	}

	private void refreshMailboxViewers(IPlayer owner)
	{
		for (Map.Entry<IPlayer, MailView> openMailbox : this.openMailboxes.entrySet())
			if (openMailbox.getKey().equals(owner))
				this.refreshMailbox(openMailbox.getValue().getViewer(), owner);
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		this.mailSendCurrency = configuration.getConfigValueAsItem("mail.items.currency");
		this.mailSendCost = configuration.getConfigValueAsInt("mail.items.amount");
		this.mailSendBookCurrency = configuration.getConfigValueAsItem("mail.book.currency");
		this.mailSendBookAmount = configuration.getConfigValueAsInt("mail.book.amount");
	}

	private final MailSender mailSender;
	private final HashMap<IPlayer, MailView> openMailboxes = new HashMap<>();
	private final HashMap<IPlayer, MailSendAgent> openSendAgents = new HashMap<>();
	private final MailboxRepository mailboxRepository;
	private final MailPackageRepository mailPackageRepository;
	private final IServer server;
	private Item mailSendCurrency;
	private int mailSendCost;
	private Item mailSendBookCurrency;
	private int mailSendBookAmount;
}
