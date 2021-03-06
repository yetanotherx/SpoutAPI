/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.generator.biome;

import java.util.ArrayList;
import java.util.Set;

import org.spout.api.generator.Populator;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.util.cuboid.CuboidShortBuffer;

/**
 * Abstract Biome Generator.
 */
public abstract class BiomeGenerator implements WorldGenerator {
	private World world = null;
	private final BiomeMap biomes = new BiomeMap();
	private final ArrayList<Populator> populators = new ArrayList<Populator>();

	public BiomeGenerator() {
		populators.add(new BiomePopulator(biomes));
		registerBiomes();
	}

	public World getWorld() {
		return world;
	}

	/**
	 * Called during biome generator's construction phase
	 */
	public abstract void registerBiomes();

	protected void setSelector(BiomeSelector selector) {
		biomes.setSelector(selector);
	}

	/**
	 * Register a new Biome Type to be generated by this generator
	 * @param biome
	 */
	public void register(Biome biome) {
		biomes.addBiome(biome);
	}

	@Override
	public BiomeManager generate(CuboidShortBuffer blockData, int chunkX, int chunkY, int chunkZ) {
		final int x = chunkX << Chunk.BLOCKS.BITS;
		final int z = chunkZ << Chunk.BLOCKS.BITS;
		final long seed = blockData.getWorld().getSeed();
		Simple2DBiomeManager biomeManager = new Simple2DBiomeManager(chunkX, chunkY, chunkZ);
		byte[] biomeData = new byte[Chunk.BLOCKS.AREA];
		for (int dx = x; dx < x + Chunk.BLOCKS.SIZE; ++dx) {
			for (int dz = z; dz < z + Chunk.BLOCKS.SIZE; ++dz) {
				Biome biome = biomes.getBiome(dx, dz, seed);
				biome.generateColumn(blockData, dx, chunkY, dz);
				biomeData[(dz & Chunk.BLOCKS.MASK) << 4 | (dx & Chunk.BLOCKS.MASK)] = (byte) biome.getId();
			}
		}
		biomeManager.deserialize(biomeData);
		return biomeManager;
	}

	@Override
	public final Populator[] getPopulators() {
		return populators.toArray(new Populator[populators.size()]);
	}

	public void addPopulator(Populator populator) {
		populators.add(populator);
	}

	public Biome getBiome(int x, int y, int z, long seed) {
		return biomes.getBiome(x, y, z, seed);
	}

	public Biome getBiome(int x, int z, long seed) {
		return biomes.getBiome(x, z, seed);
	}

	public Set<Biome> getBiomes() {
		return biomes.getBiomes();
	}

	public int indexOf(Biome biome) {
		return biomes.indexOf(biome);
	}
}
