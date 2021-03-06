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
package org.spout.vanilla.world.generator.nether.structure.fortress;

import java.util.List;

import org.spout.api.math.Vector3;

import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.world.generator.structure.PieceCuboidBuilder;
import org.spout.vanilla.world.generator.structure.SimpleBlockMaterialPicker;
import org.spout.vanilla.world.generator.structure.Structure;
import org.spout.vanilla.world.generator.structure.StructurePiece;

public class FortressBridge extends StructurePiece {
	public FortressBridge(Structure parent) {
		super(parent);
	}

	@Override
	public boolean canPlace() {
		return true;
	}

	@Override
	public void place() {
		// Building objects
		final PieceCuboidBuilder box = new PieceCuboidBuilder(this);
		final SimpleBlockMaterialPicker picker = new SimpleBlockMaterialPicker();
		box.setPicker(picker);
		// Floor
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK, VanillaMaterials.NETHER_BRICK);
		box.setMinMax(0, 3, 0, 4, 4, 18).fill();
		// Clear some space
		picker.setOuterInnerMaterials(VanillaMaterials.AIR, VanillaMaterials.AIR);
		box.setMinMax(1, 5, 0, 3, 7, 18).fill();
		// Side walls
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK, VanillaMaterials.NETHER_BRICK);
		box.setMinMax(0, 5, 0, 0, 5, 18).fill();
		box.offsetMinMax(4, 0, 0, 4, 0, 0).fill();
		// Add material under the bridge to make an arch
		box.setMinMax(0, 2, 0, 4, 2, 5).fill();
		box.offsetMinMax(0, 0, 13, 0, 0, 13).fill();
		box.setMinMax(0, 0, 0, 4, 1, 3).fill();
		box.offsetMinMax(0, 0, 15, 0, 0, 15).fill();
		// Build the pillars on the both ends downwards
		for (int xx = 0; xx <= 4; xx++) {
			for (int zz = 0; zz <= 2; zz++) {
				fillDownwards(xx, -1, zz, 50, VanillaMaterials.NETHER_BRICK);
				fillDownwards(xx, -1, 18 - zz, 50, VanillaMaterials.NETHER_BRICK);
			}
		}
		// Decoration fences on the side of the bridge
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK_FENCE, VanillaMaterials.NETHER_BRICK_FENCE);
		box.setMinMax(0, 3, 4, 0, 4, 4).fill();
		box.offsetMinMax(0, 0, 10, 0, 0, 10).fill();
		box.setMinMax(4, 3, 4, 4, 4, 4).fill();
		box.offsetMinMax(0, 0, 10, 0, 0, 10).fill();
		box.setMinMax(0, 1, 1, 0, 4, 1).fill();
		box.offsetMinMax(0, 0, 16, 0, 0, 16).fill();
		box.setMinMax(4, 1, 1, 4, 4, 1).fill();
		box.offsetMinMax(0, 0, 16, 0, 0, 16).fill();
	}

	@Override
	public void randomize() {
	}

	@Override
	public List<StructurePiece> getNextComponents() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public BoundingBox getBoundingBox() {
		final Vector3 rotatedMin = transform(0, 0, 0);
		final Vector3 rotatedMax = transform(4, 7, 18);
		return new BoundingBox(Vector3.min(rotatedMin, rotatedMax), Vector3.max(rotatedMin, rotatedMax));
	}
}
