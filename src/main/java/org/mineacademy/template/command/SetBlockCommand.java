package org.mineacademy.template.command;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.model.ChunkedTask;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.template.model.SchematicManager;
import org.mineacademy.template.settings.PlayerData;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AutoRegister
public final class SetBlockCommand extends SimpleCommand {

	private final Map<UUID, ChunkedTask> activeTasks = new HashMap<>();

	public SetBlockCommand() {
		super("setblock");

		setUsage("<material>");
		setMinArguments(1);
	}

	@Override
	protected void onCommand() {
		checkConsole();

		Player player = getPlayer();
		UUID uniqueId = player.getUniqueId();
		String param = args[0];

		if ("stop".equals(param)) {
			checkBoolean(this.activeTasks.containsKey(uniqueId), "You Do not have a pending task.");
			ChunkedTask task = this.activeTasks.get(uniqueId);

			if (task.isProcessing())
				task.cancel();

			return;
		}

		PlayerData data = PlayerData.from(player);
		Region region = data.getRegion();

		checkBoolean(region.isWhole(), "Use /tools to set both primary/secondary region points.");

		if ("save".equals(param) || "paste".equals(param) || "pastehere".equals(param)) {
			checkBoolean(HookManager.isWorldEditLoaded(), "Loading or saving schematic requires WorldEdit.");
			checkBoolean(MinecraftVersion.atLeast(MinecraftVersion.V.v1_13), "Loading or saving schematic requires Minecraft 1.13 or greater.");

			File demoFile = FileUtil.getFile("demo_schematic.schematic");

			if ("save".equals(param)) {
				tellInfo("Saving schematic...");

				SchematicManager.save(region, demoFile);

			} else if ("paste".equals(param)) {
				checkBoolean(demoFile.exists(), "File" + demoFile + "dose not exist. Please type ?{label} save first.");

				tellInfo("Pasting schematic to its original region...");
				SchematicManager.paste(region, demoFile);

			} else {
				checkBoolean(demoFile.exists(), "File" + demoFile + "dose not exist. Please type ?{label} save first.");

				tellInfo("Pasting schematic to your location...");
				SchematicManager.paste(player.getLocation(), demoFile);
			}

			return;
		}

		CompMaterial material = findMaterial(param, "No such material: '{0}'. Type /{label} and use tab completion to list materials.");

		checkBoolean(!this.activeTasks.containsKey(uniqueId), "You already have a pending task, type '/{label} stop' to cancel it.");

		List<Block> blocks = data.getRegion().getBlocks();
		ChunkedTask task = new ChunkedTask(25_000) {

			@Override
			protected void onProcess(int index) {
				Block block = blocks.get(index);

				Remain.setTypeAndData(block, material);
			}

			@Override
			protected boolean canContinue(int index) {
				return index < blocks.size();
			}

			@Override
			protected void onFinish(boolean gracefully) {
				tellSuccess("Operation " + (gracefully ? "&afinished" : "&ccancelled") + "&7.");

				activeTasks.remove(uniqueId);
			}
		};

		this.activeTasks.put(uniqueId, task);
		tellInfo("Operation started... (" + Common.plural(blocks.size(), "block") + ")");

		task.startChain();
	}

	@Override
	protected List<String> tabComplete() {
		return args.length == 1 ? completeLastWord(CompMaterial.values(), "stop") : NO_COMPLETE;
	}
}
