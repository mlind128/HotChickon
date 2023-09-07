package org.mineacademy.template.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WolffangBlade extends Tool implements Listener {

	@Getter
	private static final WolffangBlade instance = new WolffangBlade();

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
						CompMaterial.DIAMOND_SWORD,
						"&dWolffang Blade",
						"",
						"This sharp sword can",
						"cut thrue allmost enything")
				.enchant(Enchantment.DAMAGE_ALL, 10)
				.enchant(Enchantment.DURABILITY, 10)
				.enchant(Enchantment.LOOT_BONUS_MOBS, 10)
				.enchant(Enchantment.MENDING)
				.enchant(Enchantment.FIRE_ASPECT, 10)
				.enchant(Enchantment.DAMAGE_UNDEAD, 10)
				.enchant(Enchantment.DAMAGE_ARTHROPODS, 10)
				.glow(true)
				.make();
	}

}
