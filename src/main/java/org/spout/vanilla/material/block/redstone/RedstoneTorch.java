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

import org.spout.api.event.Cause;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.material.block.BlockSnapshot;
import org.spout.api.material.range.EffectRange;
import org.spout.api.material.range.ListEffectRange;

import org.spout.vanilla.data.RedstonePowerMode;
import org.spout.vanilla.material.InitializableMaterial;
import org.spout.vanilla.material.VanillaBlockMaterial;
import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.material.block.misc.Torch;
import org.spout.vanilla.util.RedstoneUtil;

public class RedstoneTorch extends Torch implements RedstoneSource, RedstoneTarget, DynamicMaterial, InitializableMaterial {
	public static final int TICK_DELAY = 100;
	private static final EffectRange physicsRange = new ListEffectRange(
			new ListEffectRange(BlockFaces.NESWT).translate(BlockFace.TOP),
			new ListEffectRange(BlockFaces.NESWB));
	private final boolean powered;

	public RedstoneTorch(String name, int id, boolean powered) {
		super((short) 0, name, id, null);
		this.powered = powered;
	}

	@Override
	public void initialize() {
		this.getDrops().DEFAULT.clear().add(VanillaMaterials.REDSTONE_TORCH_ON);
	}

	@Override
	public byte getLightLevel(short data) {
		return powered ? (byte) 7 : (byte) 0;
	}

	@Override
	public void onCreate(Block block, short data, Cause<?> cause) {
		block.setMaterial(VanillaMaterials.REDSTONE_TORCH_ON, cause);
	}

	public boolean isPowered() {
		return this.powered;
	}

	public void setPowered(Block block, boolean powered) {
		block.setMaterial(powered ? VanillaMaterials.REDSTONE_TORCH_ON : VanillaMaterials.REDSTONE_TORCH_OFF, block.getData());
	}

	@Override
	public void onUpdate(BlockMaterial oldMaterial, Block block) {
		super.onUpdate(oldMaterial, block);
		boolean receiving = this.isReceivingPower(block);
		if (this.isPowered() == receiving) {
			block.dynamicUpdate(block.getWorld().getAge() + TICK_DELAY, false);
		}
	}

	@Override
	public boolean isReceivingPower(Block block) {
		BlockFace attached = this.getAttachedFace(block);
		return RedstoneUtil.isEmittingPower(block.translate(attached), attached.getOpposite());
	}

	@Override
	public short getRedstonePower(Block block, RedstonePowerMode powerMode) {
		return this.isPowered() ? VanillaBlockMaterial.REDSTONE_POWER_MAX : VanillaBlockMaterial.REDSTONE_POWER_MIN;
	}

	@Override
	public short getIndirectRedstonePower(Block block, BlockFace direction, RedstonePowerMode powerMode) {
		if (this.getAttachedFace(block) == direction) {
			return VanillaBlockMaterial.REDSTONE_POWER_MIN;
		}
		return super.getIndirectRedstonePower(block, direction, powerMode);
	}

	@Override
	public short getDirectRedstonePower(Block block, BlockFace direction, RedstonePowerMode powerMode) {
		return (this.isPowered() && direction == BlockFace.TOP) ? VanillaBlockMaterial.REDSTONE_POWER_MAX : VanillaBlockMaterial.REDSTONE_POWER_MIN;
	}

	@Override
	public boolean hasDirectRedstonePower(Block block, BlockFace direction, RedstonePowerMode powerMode) {
		return this.getDirectRedstonePower(block, direction, powerMode) > 0;
	}

	@Override
	public void onFirstUpdate(Block b, long currentTime) {
		b.dynamicUpdate(currentTime + TICK_DELAY, false);
	}

	@Override
	public void onDynamicUpdate(Block block, long updateTime, int data) {
		boolean receiving = this.isReceivingPower(block);
		if (this.isPowered() == receiving) {
			this.setPowered(block, !receiving);
		}
	}

	@Override
	public EffectRange getDynamicRange() {
		return EffectRange.THIS;
	}

	@Override
	public EffectRange getPhysicsRange(short data) {
		return physicsRange;
	}

	@Override
	public short getRedstonePowerStrength(BlockSnapshot state) {
		return isPowered() ? VanillaBlockMaterial.REDSTONE_POWER_MAX : VanillaBlockMaterial.REDSTONE_POWER_MIN;
	}
}
