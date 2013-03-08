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
package org.spout.vanilla.protocol.codec.server;

import java.io.IOException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import org.spout.api.protocol.MessageCodec;

import org.spout.vanilla.protocol.ChannelBufferUtils;
import org.spout.vanilla.protocol.msg.scoreboard.TeamMessage;

public class TeamCodec extends MessageCodec<TeamMessage> {

	public TeamCodec() {
		super(TeamMessage.class, 0xD1);
	}

	@Override
	public TeamMessage decode(ChannelBuffer buffer) throws IOException {
		String name = ChannelBufferUtils.readString(buffer);
		String displayName, prefix, suffix;
		boolean friendlyFire = false;
		String[] players = null;

		byte mode = buffer.readByte();
		switch (mode) {
			case 0:
			case 2:
				displayName = ChannelBufferUtils.readString(buffer);
				prefix = ChannelBufferUtils.readString(buffer);
				suffix = ChannelBufferUtils.readString(buffer);
				friendlyFire = buffer.readByte() == 1;
				players = null;
				break;
			case 3:
			case 4:
				short count = buffer.readShort();
				players = new String[count];
				for (int i = 0; i < count; i++) {
					players[i] = ChannelBufferUtils.readString(buffer);
				}
			default:
				displayName = prefix = suffix = null;
				break;
		}

		return new TeamMessage(name, mode, displayName, prefix, suffix, friendlyFire, players);
	}

	@Override
	public ChannelBuffer encode(TeamMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		ChannelBufferUtils.writeString(buffer, message.getName());
		buffer.writeByte(message.getMode());
		switch (message.getMode()) {
			case 0:
			case 2:
				ChannelBufferUtils.writeString(buffer, message.getDisplayName());
				ChannelBufferUtils.writeString(buffer, message.getPrefix());
				ChannelBufferUtils.writeString(buffer, message.getSuffix());
				buffer.writeByte(message.isFriendlyFire() ? 1 : 0);
				break;
			case 3:
			case 4:
				buffer.writeShort(message.getPlayers().length);
				for (String name : message.getPlayers()) {
					ChannelBufferUtils.writeString(buffer, name);
				}
				break;
			default:
		}
		return buffer;
	}
}
