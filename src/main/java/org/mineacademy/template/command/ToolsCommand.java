package org.mineacademy.template.command;

import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.menu.MenuTools;
import org.mineacademy.template.item.HotStick;
import org.mineacademy.template.item.KittyCannon;
import org.mineacademy.template.item.RegionTool;
import org.mineacademy.template.item.WolffangBlade;

@AutoRegister
public final class ToolsCommand extends SimpleCommand {

	public ToolsCommand() {
		super("hctools");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		new MenuTools() {

			@Override
			protected Object[] compileTools() {
				return new Object[]{
						KittyCannon.class, RegionTool.class, HotStick.class,
						WolffangBlade.class
				};
			}
		}.displayTo(getPlayer());
	}
}
