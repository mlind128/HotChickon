package org.mineacademy.template.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.visual.VisualTool;
import org.mineacademy.fo.visual.VisualizedRegion;
import org.mineacademy.template.settings.PlayerData;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegionTool extends VisualTool {

	@Getter
	private static final RegionTool instance = new RegionTool();

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(CompMaterial.GOLDEN_AXE,
						"&6Region Tool")
				.lore(this.getItemLore())
				.glow(true)
				.make();
	}

	@Override
	protected CompMaterial getBlockMask(Block block, Player player) {
		return CompMaterial.GOLD_BLOCK;
	}

	@Override
	protected VisualizedRegion getVisualizedRegion(Player player) {
		return PlayerData.from(player).getRegion();
	}
}
