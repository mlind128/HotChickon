package org.mineacademy.template;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.mineacademy.fo.plugin.SimplePlugin;

/**
 * PluginTemplate is a simple template you can use every time you make
 * a new plugin. This will save you time because you no longer have to
 * recreate the same skeleton and features each time.
 * <p>
 * It uses Foundation for fast and efficient development process.
 */
public final class PluginTemplate extends SimplePlugin {


	/**
	 * why an apple
	 */
	int apple = 5;

	/**
	 * Automatically perform login ONCE when the plugin starts.
	 */
	@Override
	protected void onPluginStart() {


		String hello = "hello welcome";

		System.out.println("Hello Wellcome");


	}


	/* ------------------------------------------------------------------------------- */
	/* Events */
	/* ------------------------------------------------------------------------------- */

	/**
	 * An example event that checks if the right clicked entity is a cow, and makes an explosion.
	 * You can write your events to your main class without having to register a listener.
	 *
	 * @param event
	 */
	@EventHandler
	public void onRightClick(PlayerInteractEntityEvent event) {


		if (event.getRightClicked().getType() == EntityType.CHICKEN)
			event.getRightClicked().setFireTicks(80);
		//	event.getRightClicked().getWorld().createExplosion(event.getRightClicked().getLocation(), 5);


	}


}
