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

public class FortressGateIntersection extends StructurePiece {
	public FortressGateIntersection(Structure parent) {
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
		box.setMinMax(0, 0, 0, 6, 1, 6).fill();
		// Interior space
		picker.setOuterInnerMaterials(VanillaMaterials.AIR, VanillaMaterials.AIR);
		box.offsetMinMax(0, 2, 0, 0, 6, 0).fill();
		// The four corner columns
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK, VanillaMaterials.NETHER_BRICK);
		box.setMinMax(0, 2, 0, 1, 6, 0).fill();
		box.offsetMinMax(0, 0, 6, 0, 0, 6).fill();
		box.offsetMinMax(5, 0, -6, 5, 0, -6).fill();
		box.offsetMinMax(0, 0, 6, 0, 0, 6).fill();
		box.setMinMax(0, 2, 0, 0, 6, 1).fill();
		box.offsetMinMax(0, 0, 5, 0, 0, 5).fill();
		box.offsetMinMax(6, 0, -5, 6, 0, -5).fill();
		box.offsetMinMax(0, 0, 5, 0, 0, 5).fill();
		// Connect the corner columns horizontally at the ceiling to create gateways
		box.setMinMax(2, 6, 0, 4, 6, 0).fill();
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK_FENCE, VanillaMaterials.NETHER_BRICK_FENCE);
		box.offsetMinMax(0, -1, 0, 0, -1, 0).fill();
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK, VanillaMaterials.NETHER_BRICK);
		box.offsetMinMax(0, 1, 6, 0, 1, 6).fill();
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK_FENCE, VanillaMaterials.NETHER_BRICK_FENCE);
		box.offsetMinMax(0, -1, 0, 0, -1, 0).fill();
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK, VanillaMaterials.NETHER_BRICK);
		box.setMinMax(0, 6, 2, 0, 6, 4).fill();
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK_FENCE, VanillaMaterials.NETHER_BRICK_FENCE);
		box.offsetMinMax(0, -1, 0, 0, -1, 0).fill();
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK, VanillaMaterials.NETHER_BRICK);
		box.offsetMinMax(6, 1, 0, 6, 1, 0).fill();
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK_FENCE, VanillaMaterials.NETHER_BRICK_FENCE);
		box.offsetMinMax(0, -1, 0, 0, -1, 0).fill();
		// Fill down to the ground
		for (int xx = 0; xx <= 6; xx++) {
			for (int zz = 0; zz <= 6; zz++) {
				fillDownwards(xx, -1, zz, 50, VanillaMaterials.NETHER_BRICK);
			}
		}
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
		final Vector3 rotatedMax = transform(6, 8, 6);
		return new BoundingBox(Vector3.min(rotatedMin, rotatedMax), Vector3.max(rotatedMin, rotatedMax));
	}
}
