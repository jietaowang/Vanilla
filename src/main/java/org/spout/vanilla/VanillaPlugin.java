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
package org.spout.vanilla;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.spout.api.Client;
import org.spout.api.Platform;
import org.spout.api.Server;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.RootCommand;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.component.impl.DatatableComponent;
import org.spout.api.component.impl.NetworkComponent;
import org.spout.api.component.impl.ObserverComponent;
import org.spout.api.entity.Entity;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.input.Binding;
import org.spout.api.input.InputManager;
import org.spout.api.input.Keyboard;
import org.spout.api.input.Mouse;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.plugin.PluginLogger;
import org.spout.api.plugin.ServiceManager;
import org.spout.api.plugin.services.ProtectionService;
import org.spout.api.protocol.Protocol;
import org.spout.api.util.FlatIterator;

import org.spout.vanilla.command.AdministrationCommands;
import org.spout.vanilla.command.AdministrationCommands.TPSMonitor;
import org.spout.vanilla.command.InputCommands;
import org.spout.vanilla.command.QuickbarCommandExecutor;
import org.spout.vanilla.command.TestCommands;
import org.spout.vanilla.component.world.sky.NetherSky;
import org.spout.vanilla.component.world.sky.NormalSky;
import org.spout.vanilla.component.world.sky.TheEndSky;
import org.spout.vanilla.data.Difficulty;
import org.spout.vanilla.data.Dimension;
import org.spout.vanilla.data.GameMode;
import org.spout.vanilla.data.VanillaData;
import org.spout.vanilla.data.configuration.VanillaConfiguration;
import org.spout.vanilla.data.configuration.WorldConfigurationNode;
import org.spout.vanilla.data.resources.RecipeYaml;
import org.spout.vanilla.data.resources.loader.RecipeLoader;
import org.spout.vanilla.inventory.recipe.VanillaRecipes;
import org.spout.vanilla.material.VanillaBlockMaterial;
import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.material.enchantment.VanillaEnchantments;
import org.spout.vanilla.protocol.LANThread;
import org.spout.vanilla.protocol.VanillaProtocol;
import org.spout.vanilla.protocol.rcon.RemoteConnectionCore;
import org.spout.vanilla.protocol.rcon.RemoteConnectionServer;
import org.spout.vanilla.service.VanillaProtectionService;
import org.spout.vanilla.service.protection.SpawnProtection;
import org.spout.vanilla.util.thread.SpawnLoader;
import org.spout.vanilla.world.generator.VanillaGenerator;
import org.spout.vanilla.world.generator.VanillaGenerators;
import org.spout.vanilla.world.generator.nether.NetherGenerator;
import org.spout.vanilla.world.generator.skylands.SkylandsGenerator;
import org.spout.vanilla.world.generator.theend.TheEndGenerator;
import org.spout.vanilla.world.lighting.VanillaLighting;

public class VanillaPlugin extends CommonPlugin {
	public static final int MINECRAFT_PROTOCOL_ID = 60;
	public static final int VANILLA_PROTOCOL_ID = NetworkComponent.getProtocolId("org.spout.vanilla.plugin.protocol");
	protected static VanillaPlugin instance;
	private VanillaConfiguration config;
	private RemoteConnectionCore rcon;
	//Client only
	//TODO Remove, seriously not secure.
	private String username = "";
	private String sessionId = "";

	@Override
	public void onDisable() {
		instance = null;
		getLogger().info("disabled");
	}

	@Override
	public void onEnable() {
		//Commands
		final CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(getEngine(), new SimpleInjector(this), new SimpleAnnotatedCommandExecutorFactory());
		final RootCommand root = getEngine().getRootCommand();
		root.addSubCommands(this, AdministrationCommands.class, commandRegFactory);

		if (getEngine().debugMode()) {
			getEngine().getRootCommand().addSubCommands(this, TestCommands.class, commandRegFactory);
		}
		getEngine().getEventManager().registerEvents(new VanillaListener(this), this);

		switch (getEngine().getPlatform()) {
			case CLIENT:

				final Client client = (Client) getEngine();
				//Setup client input
				final InputManager input = client.getInputManager();
				input.bind(new Binding("quickbar_left", Mouse.MOUSE_SCROLLUP));
				input.bind(new Binding("quickbar_right", Mouse.MOUSE_SCROLLDOWN));
				for (int i = 1; i < 10; i++) {
					input.bind(new Binding("quickbar_" + i, Keyboard.valueOf("KEY_" + i)));
				}

				final QuickbarCommandExecutor exe = new QuickbarCommandExecutor();
				root.addSubCommand(this, "quickbar_left").setHelp("Changes quickbar slot!").setExecutor(exe).setArgBounds(1, 1);
				root.addSubCommand(this, "quickbar_right").setHelp("Changes quickbar slot!").setExecutor(exe).setArgBounds(1, 1);
				for (int i = 1; i < 10; i++) {
					root.addSubCommand(this, "quickbar_" + i).setHelp("Changes quickbar slot!").setExecutor(exe).setArgBounds(1, 1);
				}

				root.addSubCommands(this, InputCommands.class, commandRegFactory);

				if (getEngine().debugMode()) {
					setupWorlds();
				}
				break;
			case SERVER:
				setupWorlds();
				if (VanillaConfiguration.LAN_DISCOVERY.getBoolean()) {
					final LANThread lanThread = new LANThread();
					lanThread.start();
				}
				break;
			default:
				break;
		}

		//Configuration
		VanillaBlockMaterial.REDSTONE_POWER_MAX = (short) VanillaConfiguration.REDSTONE_MAX_RANGE.getInt();
		VanillaBlockMaterial.REDSTONE_POWER_MIN = (short) VanillaConfiguration.REDSTONE_MIN_RANGE.getInt();

		//TODO: Remove this check when the null world bug is fixed
		for (World world : getEngine().getWorlds()) {
			if (world == null) {
				getLogger().log(Level.SEVERE, "A World element in Engine.getWorlds() is null!");
			}
		}

		getLogger().info("v" + getDescription().getVersion() + " enabled. Protocol: " + getDescription().getData("protocol"));
	}

	@Override
	public void onLoad() {
		instance = this;
		//Config
		config = new VanillaConfiguration(getDataFolder());
		config.load();
		//Logger
		((PluginLogger) getLogger()).setTag(new ChatArguments(ChatStyle.RESET, "[", ChatStyle.GOLD, "Vanilla", ChatStyle.RESET, "] "));
		//Spout.getFilesystem().registerLoader(new MapPaletteLoader());
		getEngine().getFilesystem().registerLoader(new RecipeLoader());
		Protocol.registerProtocol(new VanillaProtocol());

		VanillaMaterials.initialize();
		VanillaLighting.initialize();
		VanillaEnchantments.initialize();
		//MapPalette.DEFAULT = (MapPalette) Spout.getFilesystem().getResource("mappalette://Vanilla/map/mapColorPalette.dat");
		RecipeYaml.DEFAULT = (RecipeYaml) getEngine().getFilesystem().getResource("recipe://Vanilla/recipes.yml");
		VanillaRecipes.initialize();

		getLogger().info("loaded");
	}

	@SuppressWarnings("unused")
	private void setupRcon() {
		if (getEngine().getPlatform() == Platform.SERVER) {
			RemoteConnectionServer server = new RemoteConnectionServer(getLogger(), getDataFolder());
			server.bindDefaultPorts((Server) getEngine());
			rcon = server;
		}
	}

	@SuppressWarnings("unused")
	private void closeRcon() {
		getEngine().getLogger().info("Shutting down rcon connections");
		if (rcon != null) {
			try {
				rcon.close();
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "Error closing RCON channels: ", e);
			}
		}
	}

	private void setupWorlds() {
		ArrayList<World> worlds = new ArrayList<World>();

		for (WorldConfigurationNode worldNode : VanillaConfiguration.WORLDS.getAll()) {
			if (worldNode.LOAD.getBoolean()) {
				// Obtain generator and start generating world
				String generatorName = worldNode.GENERATOR.getString();
				VanillaGenerator generator = VanillaGenerators.byName(generatorName);
				if (generator == null) {
					throw new IllegalArgumentException("Invalid generator name for world '" + worldNode.getWorldName() + "': " + generatorName);
				}
				World world = getEngine().loadWorld(worldNode.getWorldName(), generator);

				// Apply general settings
				final DatatableComponent data = world.getData();
				data.put(VanillaData.GAMEMODE, GameMode.get(worldNode.GAMEMODE.getString()));
				data.put(VanillaData.DIFFICULTY, Difficulty.get(worldNode.DIFFICULTY.getString()));
				data.put(VanillaData.DIMENSION, Dimension.get(worldNode.SKY_TYPE.getString()));

				world.addLightingManager(VanillaLighting.BLOCK_LIGHT);
				world.addLightingManager(VanillaLighting.SKY_LIGHT);

				// Add to worlds
				worlds.add(world);
			}
		}

		final int radius = VanillaConfiguration.SPAWN_RADIUS.getInt();
		final int protectionRadius = VanillaConfiguration.SPAWN_PROTECTION_RADIUS.getInt();
		SpawnLoader loader = new SpawnLoader(1);

		if (worlds.isEmpty()) {
			return;
		}
		//Register protection service used for spawn protection.
		getEngine().getServiceManager().register(ProtectionService.class, new VanillaProtectionService(), this, ServiceManager.ServicePriority.Highest);

		for (World world : worlds) {
			// Keep spawn loaded
			WorldConfigurationNode worldConfig = VanillaConfiguration.WORLDS.get(world);
			boolean newWorld = world.getAge() <= 0;
			if (worldConfig.LOADED_SPAWN.getBoolean() || newWorld) {
				// Initialize the first chunks
				Point point = world.getSpawnPoint().getPosition();
				int cx = point.getBlockX() >> Chunk.BLOCKS.BITS;
				int cz = point.getBlockZ() >> Chunk.BLOCKS.BITS;

				((VanillaProtectionService) getEngine().getServiceManager().getRegistration(ProtectionService.class).getProvider()).addProtection(new SpawnProtection(world.getName() + " Spawn Protection", world, point, protectionRadius));

				// Load or generate spawn area
				int effectiveRadius = newWorld ? (2 * radius) : radius;
				loader.load(world, cx, cz, effectiveRadius, newWorld);

				if (worldConfig.LOADED_SPAWN.getBoolean()) {
					@SuppressWarnings("unchecked")
					Entity e = world.createAndSpawnEntity(point, LoadOption.LOAD_GEN, ObserverComponent.class);
					e.setObserver(new FlatIterator(cx, 0, cz, 16, effectiveRadius));
				}

				// Grab safe spawn if newly created world.
				if (newWorld && world.getGenerator() instanceof VanillaGenerator) {
					Point spawn = ((VanillaGenerator) world.getGenerator()).getSafeSpawn(world);
					world.setSpawnPoint(new Transform(spawn, Quaternion.IDENTITY, Vector3.ONE));
				}

				// Grab safe spawn if newly created world.
				if (newWorld && world.getGenerator() instanceof VanillaGenerator) {
					Point spawn = ((VanillaGenerator) world.getGenerator()).getSafeSpawn(world);
					world.setSpawnPoint(new Transform(spawn, Quaternion.IDENTITY, Vector3.ONE));
				}
			}

			if (world.getGenerator() instanceof NetherGenerator) {
				world.add(NetherSky.class).setHasWeather(false);
			} else if (world.getGenerator() instanceof TheEndGenerator) {
				world.add(TheEndSky.class).setHasWeather(false);
			} else if (world.getGenerator() instanceof SkylandsGenerator) {
				world.add(NormalSky.class).setHasWeather(false);
			} else {
				world.add(NormalSky.class);
			}
		}
	}

	public void setClientAuthInfos(String username, String sessionId) {
		this.username = username;
		this.sessionId = sessionId;
	}

	public String getUsername() {
		return username;
	}

	public String getSessionId() {
		return sessionId;
	}

	/**
	 * Gets the running instance of VanillaPlugin
	 * @return the running instance of VanillaPlugin
	 */
	public static VanillaPlugin getInstance() {
		return instance;
	}

	/**
	 * Gets the configuration instance for Vanilla
	 * @return the configuration instance
	 */
	public VanillaConfiguration getConfig() {
		return config;
	}

	public ChatArguments getPrefix() {
		return ((PluginLogger) getLogger()).getTag();
	}

	private TPSMonitor monitor;

	public TPSMonitor getTPSMonitor() {
		return monitor;
	}

	public void setTPSMonitor(TPSMonitor monitor) {
		this.monitor = monitor;
	}
}
