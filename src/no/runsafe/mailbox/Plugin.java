package no.runsafe.mailbox;

import no.runsafe.framework.RunsafePlugin;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		addComponent(SomeComponent.class); // Replace this with your own components, this is just an example.
	}
}
