package net.benjaminurquhart.CIS;

import java.io.IOException;
import java.io.InputStream;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class ChannelInputStream extends InputStream {
	
	private Channel channel;
	private Listener listener;
	private boolean closed;
	
	public ChannelInputStream(TextChannel channel, boolean ignoreBots) {
		this.channel = channel;
		this.listener = new TextListener(channel, ignoreBots);
	}
	
	public ChannelInputStream(MessageChannel channel, Guild guild, boolean ignoreBots) {
		this.channel = (Channel) channel;
		this.listener = new TextListener(channel, guild, ignoreBots);
	}
	
	public ChannelInputStream(PrivateChannel channel, boolean ignoreBots) {
		this.channel = (Channel) channel;
		this.listener = new TextListener(channel, ignoreBots);
	}
	
	public ChannelInputStream(VoiceChannel channel) {
		this.channel = (Channel) channel;
		this.listener = new AudioListener(channel);
		while(!((AudioListener)listener).isLoaded()) {
			continue;
		}
	}
	public Channel getChannel(){
		return this.channel;
	}
	public boolean hasNext(){
		return available() > 0;
	}
	@Override
	public int available() {
		return listener.available();
	}
	@Override
	public int read() throws IOException {
		if(closed) {
			throw new IOException("Stream closed");
		}
		try {
			return listener.getNext();
		}
		catch(Exception e) {
			return -1;
		}
	}
	@Override
	public void close() throws IOException {
		if(closed) {
			throw new IOException("Stream already closed!");
		}
		listener.close();
		super.close();
		closed = true;
	}
}
