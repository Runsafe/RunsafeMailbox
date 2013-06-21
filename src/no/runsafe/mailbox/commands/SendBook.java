package no.runsafe.mailbox.commands;

import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafeAmbiguousPlayer;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mailbox.MailHandler;
import no.runsafe.mailbox.MailSender;

import java.util.HashMap;

public class SendBook extends PlayerCommand
{
	public SendBook(MailHandler mailHandler, MailSender mailSender)
	{
		super("sendbook", "Sends a book that you are holding", "runsafe.mailbox.send.book", "player");
		this.mailHandler = mailHandler;
		this.mailSender = mailSender;
	}
	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		RunsafePlayer player = RunsafeServer.Instance.getPlayer(parameters.get("player"));

		if (player == null)
			return "&cThat player does not exist.";

		if (player instanceof RunsafeAmbiguousPlayer)
			return player.toString();

		String executorName = executor.getName();
		if (player.getName().equalsIgnoreCase(executorName))
			return "&cYou cannot mail things to yourself.";

		if (!this.mailHandler.hasMailBookCost(executor))
			return "&cYou do not have enough money to send a book. Sending books costs " + this.mailHandler.getMailBookCostText() + ".";

		if (!this.mailSender.hasFreeMailboxSpace(player))
			return "&cThat player cannot receive mail right now.";

		RunsafeMeta itemInHand = player.getItemInHand();
		if (itemInHand == null || !itemInHand.is(Item.Special.Crafted.WrittenBook))
			return "&cYou must be holding a written book.";

		this.mailSender.sendItemInHand(player, executorName);
		return null;
	}

	private MailHandler mailHandler;
	private MailSender mailSender;
}
