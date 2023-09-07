package org.mineacademy.template.settings;

import lombok.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.model.Tuple;
import org.mineacademy.fo.settings.YamlConfig;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public final class PlayerData extends YamlConfig {

	private static Map<UUID, PlayerData> playerData = new HashMap<>();

	private final String playerName;
	private final UUID uuid;

	private double health;
	private String tabListName;

	private Kit kit;
	private ChatColor color;
	private List<ItemStack> eggs;
	private List<Tuple<ItemStack, Double>> eggChances;

	// NOT SAVED TO DISK
	private VisualizedRegion region = new VisualizedRegion();

	@Getter
	@ToString
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	private static class Kit implements ConfigSerializable {

		private String name;
		private List<ItemStack> items;

		@Override
		public SerializedMap serialize() {
			SerializedMap map = new SerializedMap();

			map.put("Name", this.name);
			map.put("Items", this.items);

			return map;
		}

		public static Kit deserialize(SerializedMap map) {
			Kit kit = new Kit();

			kit.name = map.getString("Name");
			kit.items = map.getList("Items", ItemStack.class);

			return kit;
		}
	}

	private PlayerData(String playerName, UUID uuid) {
		this.playerName = playerName;
		this.uuid = uuid;

		this.setHeader("My\nHeader");
		this.loadConfiguration(NO_DEFAULT, "players/" + uuid + ".yml");
		this.save();
	}

	@Override
	protected void onLoad() {
		this.health = this.getDouble("Health", 20D);
		this.tabListName = this.getString("Tablist_Name", this.playerName);
		this.kit = this.get("Kit", Kit.class);
		this.color = this.get("Color", ChatColor.class);
		this.eggs = this.getList("Eggs", ItemStack.class);
		this.eggChances = this.getTupleList("Egg_Chances", ItemStack.class, Double.class);
	}

	@Override
	protected void onSave() {
		this.set("Health", this.health);
		this.set("Tablist_Name", this.tabListName);
		this.set("Kit", this.kit);
		this.set("Color", this.color);
		this.set("Eggs", this.eggs);
		this.set("Egg_Chances", this.eggChances);
	}

	public void setHealth(double health) {
		this.health = health;

		this.save();
	}

	public void setTabListName(String tabListName) {
		this.tabListName = tabListName;

		this.save();
	}

	public void setKit(String kitName, List<ItemStack> items) {
		this.kit = new Kit(kitName, items);

		this.save();
	}

	public void setColor(ChatColor color) {
		this.color = color;

		this.save();
	}

	public void setEggs(List<ItemStack> eggs) {
		this.eggs = eggs;

		this.save();
	}

	public void setEggChances(List<Tuple<ItemStack, Double>> eggChances) {
		this.eggChances = eggChances;

		this.save();
	}

	public static PlayerData from(Player player) {
		UUID uuid = player.getUniqueId();
		PlayerData data = playerData.get(uuid);

		if (data == null) {
			data = new PlayerData(player.getName(), uuid);

			playerData.put(uuid, data);
		}

		return data;
	}

	public static void remove(Player player) {
		playerData.remove(player.getUniqueId());
	}
}
