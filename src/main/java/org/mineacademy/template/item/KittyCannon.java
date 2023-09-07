package org.mineacademy.template.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.EntityUtil;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.CompSound;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KittyCannon extends Tool {

	@Getter
	private static final KittyCannon instance = new KittyCannon();

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
						CompMaterial.STICK,
						"&dKitty Cannon",
						"",
						"Right click air to",
						"launch flying kittens..",
						"Warning: They explode",
						"upon hitting the ground!")
				.glow(true)
				.make();
	}

	@Override
	protected void onBlockClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR)
			return;

		Player player = event.getPlayer();
		Ocelot cat = player.getWorld().spawn(player.getEyeLocation(), Ocelot.class);

		cat.setVelocity(player.getEyeLocation().getDirection().multiply(2.0));
		CompSound.CAT_MEOW.play(player);

		EntityUtil.trackFlying(cat, () -> CompParticle.END_ROD.spawn(cat.getLocation()));

		EntityUtil.trackFalling(cat, new Runnable() {
			@Override
			public void run() {
				cat.getWorld().createExplosion(cat.getLocation(), 4F);
				cat.remove();

				CompSound.CAT_MEOW.play(player, 1F, 2F);
			}
		});
	}

	@Override
	protected boolean ignoreCancelled() {
		return false;
	}
}
