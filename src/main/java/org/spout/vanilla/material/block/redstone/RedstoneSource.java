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

import org.spout.vanilla.data.RedstonePowerMode;

/**
 * Defines a material that can supply redstone power to targets and solid blocks<br>
 * Redstone wire will automatically attach to this material
 */
public interface RedstoneSource extends IndirectRedstoneSource {
	/**
	 * Gets how much redstone power this redstone source block provides to the direction given.<br>
	 * This is direct power, which can power other solid blocks
	 * @param block of this redstone source
	 * @param direction it provides power to
	 * @param powerMode to use to get the power
	 * @return how much power this block provides to the given direction
	 */
	short getDirectRedstonePower(Block block, BlockFace direction, RedstonePowerMode powerMode);

	/**
	 * Gets if this redstone source block provides power to the direction given.<br>
	 * This is direct power, which can power other solid blocks
	 * @param block of this redstone source
	 * @param direction it provides power to
	 * @param powerMode to use to get the power
	 * @return True if this redstone source block provides power
	 */
	boolean hasDirectRedstonePower(Block block, BlockFace direction, RedstonePowerMode powerMode);

	/**
	 * Gets the amount of redstone power this material generates for itself
	 * @param state of the block
	 * @return power strength
	 */
	short getRedstonePowerStrength(BlockSnapshot state);
}
