package net.benjaminurquhart.CIO.output;

import java.io.IOException;
import java.io.OutputStream;

import net.benjaminurquhart.CIO.common.ChannelDeletionHandler;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.Permission;

public class ChannelOutputStream extends OutputStream{

	private boolean closed;
	private GuildChannel channel;
	private ChannelStream stream;
	private ChannelDeletionHandler deletionHandler;
	
	public ChannelOutputStream(TextChannel channel) {
		this.channel = channel;
		this.stream = new TextStream(channel);
		this.deletionHandler = new ChannelDeletionHandler(channel);
	}
	public ChannelOutputStream(VoiceChannel channel) {
		this.channel = channel;
		this.stream = new VoiceStream(channel);
		this.deletionHandler = new ChannelDeletionHandler(channel);
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
	public GuildChannel getChannel() {
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
		if(deletionHandler.isDeleted()){
			throw new IOException("Channel deleted");
		}
		if(!hasPerms()) {
			throw new IOException("Failed to write to stream: Missing Permissions");
		}
		stream.write((byte)data);
	}
	@Override
	public void flush() throws IOException {
		if(closed) {
			throw new IOException("Stream closed");
		}
		if(deletionHandler.isDeleted()){
			throw new IOException("Channel deleted");
		}
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
