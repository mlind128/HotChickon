package org.mineacademy.template.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HotStick extends Tool implements Listener {

	@Getter
	private static final HotStick instance = new HotStick();

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
						CompMaterial.BLAZE_ROD,
						"&dHot Stick",
						"",
						"Right click chicken,",
						"cow, sheep or pig to",
						"set them on fire.",
						"They may drop cooked meat!")
				.glow(true)
				.make();
	}

	int sec = 20;

	@EventHandler
	private void onRightClick(PlayerInteractEntityEvent event) {

		if (event.getRightClicked().getType() == EntityType.CHICKEN)
			event.getRightClicked().setFireTicks((sec * 4));

		else if (event.getRightClicked().getType() == EntityType.PIG)
			event.getRightClicked().setFireTicks((sec * 10));

		else if (event.getRightClicked().getType() == EntityType.COW)
			event.getRightClicked().setFireTicks((sec * 10));

		else if (event.getRightClicked().getType() == EntityType.SHEEP)
			event.getRightClicked().setFireTicks((sec * 10));
		

	}

}
