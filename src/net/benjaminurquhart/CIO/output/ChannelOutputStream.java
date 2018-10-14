package net.benjaminurquhart.CIO.output;

import java.io.IOException;
import java.io.OutputStream;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class ChannelOutputStream extends OutputStream{

	private boolean closed;
	private Channel channel;
	private ChannelStream stream;
	
	public ChannelOutputStream(TextChannel channel) {
		this.channel = (Channel) channel;
		this.stream = new TextStream(channel);
	}
	public ChannelOutputStream(MessageChannel channel) {
		this.channel = (Channel) channel;
		this.stream = new TextStream(channel);
	}
	public ChannelOutputStream(PrivateChannel channel) {
		this.channel = (Channel) channel;
		this.stream = new TextStream(channel);
	}
	public ChannelOutputStream(VoiceChannel channel) {
		this.channel = (Channel) channel;
	}
	private boolean hasPerms() {
		Guild guild = channel.getGuild();
		if(channel.getType().equals(ChannelType.PRIVATE)) {
			return true;
		}
		if(channel.getType().equals(ChannelType.VOICE)) {
			return guild.getSelfMember().hasPermission(channel, Permission.VOICE_SPEAK, Permission.VOICE_CONNECT);
		}
		//There's a canTalk() method in the MessageChannel class that does this. I don't feel like casting, however.
		return guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ);
	}
	public Channel getChannel() {
		return this.channel;
	}
	public int getBuffSize(){
		return stream.getBuffSize();
	}
	@Override
	public void write(int data) throws IOException {
		if(closed) {
			throw new IOException("Stream closed");
		}
		if(!hasPerms()) {
			throw new IOException("Failed to write to stream: Missing Permissions");
		}
		stream.write((byte)data);
	}
	@Override
	public void flush() throws IOException {
		stream.flush();
	}
	@Override
	public void close() throws IOException{
		if(closed) {
			throw new IOException("Stream closed");
		}
		stream.close();
		closed = true;
	}
	
}
