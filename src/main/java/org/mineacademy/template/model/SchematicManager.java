package org.mineacademy.template.model;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.world.block.BaseBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.model.ChunkedTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SchematicManager {

	public static void save(org.mineacademy.fo.region.Region region, File schematicFile) {
		Valid.checkBoolean(region.isWhole(), "Cannot save region that lacks primary or secondary point: " + region);

		try (Closer closer = Closer.create()) {
			final Region weRegion = toWorldEditRegion(region);
			final EditSession session = createEditSession(region.getWorld());

			final Clipboard clipboard = new BlockArrayClipboard(weRegion);
			final ForwardExtentCopy copy = new ForwardExtentCopy(session, weRegion, clipboard, weRegion.getMinimumPoint());

			Operations.complete(copy);

			final FileOutputStream fos = new FileOutputStream(schematicFile);
			final FileOutputStream out = closer.register(fos);
			final ClipboardWriter writer = closer.register(BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(out));

			writer.write(clipboard);

		} catch (final Throwable t) {
			t.printStackTrace();
		}
	}

	private static Region toWorldEditRegion(org.mineacademy.fo.region.Region region) {
		final BlockVector3 min = toWorldEditVector(region.getPrimary());
		final BlockVector3 max = toWorldEditVector(region.getSecondary());

		return new CuboidRegion(min, max);
	}

	public static void paste(Location to, File schematicFile) {

		try (EditSession session = createEditSession(to.getWorld())) {
			final Clipboard clipboard = loadSchematic(schematicFile);
			final Operation operation = new ClipboardHolder(clipboard)
					.createPaste(session)
					.to(toWorldEditVector(to))
					.build();

			Operations.complete(operation);

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void paste(org.mineacademy.fo.region.Region region, File schematicFile) {

		Common.runAsync(() -> {
			final Clipboard clipboard = loadSchematic(schematicFile);
			final List<BlockVector3> worldEditBlocks = new ArrayList<>();

			for (final Block block : region.getBlocks())
				worldEditBlocks.add(toWorldEditVector(block.getLocation()));

			final EditSession session = createEditSession(region.getWorld());

			final ChunkedTask task = new ChunkedTask(50_000) {

				@Override
				protected void onProcess(int index) {
					final BlockVector3 vector = worldEditBlocks.get(index);
					final BaseBlock copy = clipboard.getFullBlock(vector);

					try {
						if (copy != null) {
							session.setBlock(vector, copy);

							Operations.completeBlindly(session.commit());
						}

					} catch (final MaxChangedBlocksException ex) {
						ex.printStackTrace();
					}
				}

				@Override
				protected boolean canContinue(int index) {
					return index < worldEditBlocks.size();
				}
			};

			Common.runLater(task::startChain);
		});
	}

	private static Clipboard loadSchematic(File file) {

		try {
			final ClipboardFormat format = ClipboardFormats.findByFile(file);
			Valid.checkNotNull(format, "Null schematic file format " + file + " (file corrupted or WorldEdit outdated - or too new?)!");

			final ClipboardReader reader = format.getReader(new FileInputStream(file));
			final Clipboard schematic = reader.read();
			Valid.checkNotNull(schematic, "Failed to read schematic from " + file);

			return schematic;

		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private static EditSession createEditSession(org.bukkit.World bukkitWorld) {
		final BukkitWorld world = new BukkitWorld(bukkitWorld);
		final EditSession session = WorldEdit.getInstance().newEditSession(world);

		session.setSideEffectApplier(SideEffectSet.defaults());

		return session;
	}

	private static BlockVector3 toWorldEditVector(Location location) {
		return BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
}
