/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Vanilla is licensed under the Spout License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.vanilla.material.block.redstone;

import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockSnapshot;
import org.spout.api.material.range.EffectRange;

import org.spout.vanilla.data.RedstonePowerMode;
import org.spout.vanilla.data.resources.VanillaMaterialModels;
import org.spout.vanilla.data.tool.ToolLevel;
import org.spout.vanilla.data.tool.ToolType;
import org.spout.vanilla.material.block.Solid;

public class RedstoneBlock extends Solid implements RedstoneSource {
	private static final EffectRange physicsRange = EffectRange.THIS_AND_NEIGHBORS;

	public RedstoneBlock(String name, int id) {
		super(name, id, VanillaMaterialModels.REDSTONE_BLOCK);
		this.setHardness(3.0F).setResistance(5.0F).addMiningType(ToolType.PICKAXE).setMiningLevel(ToolLevel.WOOD);
	}

	@Override
	public short getDirectRedstonePower(Block block, BlockFace direction, RedstonePowerMode powerMode) {
		return 0;
	}

	@Override
	public boolean hasDirectRedstonePower(Block block, BlockFace direction, RedstonePowerMode powerMode) {
		return false;
	}

	@Override
	public short getRedstonePowerStrength(BlockSnapshot block) {
		return REDSTONE_POWER_MAX;
	}

	@Override
	public EffectRange getPhysicsRange(short data) {
		return physicsRange;
	}

	@Override
	public boolean hasPhysics() {
		return true;
	}
}
