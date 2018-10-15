package net.benjaminurquhart.CIO.input;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;

import net.benjaminurquhart.CIO.common.ChannelDeletionHandler;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class ChannelInputStream extends InputStream {
	
	private Channel channel;
	private Listener listener;
	private ChannelDeletionHandler deletionHandler;
	private boolean closed;
	
	public ChannelInputStream(TextChannel channel, boolean ignoreBots) {
		this.channel = channel;
		this.listener = new TextListener(channel, ignoreBots);
		this.deletionHandler = new ChannelDeletionHandler(channel);
		channel.getJDA().addEventListener(this.deletionHandler);
	}
	
	public ChannelInputStream(MessageChannel channel, Guild guild, boolean ignoreBots) {
		this.channel = (Channel) channel;
		this.listener = new TextListener(channel, guild, ignoreBots);
		this.deletionHandler = new ChannelDeletionHandler(channel);
		channel.getJDA().addEventListener(this.deletionHandler);
	}
	
	public ChannelInputStream(PrivateChannel channel, boolean ignoreBots) {
		this.channel = (Channel) channel;
		this.listener = new TextListener(channel, ignoreBots);
		this.deletionHandler = new ChannelDeletionHandler(channel);
		channel.getJDA().addEventListener(this.deletionHandler);
	}
	
	public ChannelInputStream(VoiceChannel channel) {
		this.channel = (Channel) channel;
		this.listener = new AudioListener(channel);
		this.deletionHandler = new ChannelDeletionHandler(channel);
		channel.getJDA().addEventListener(this.deletionHandler);
		while(!((AudioListener)listener).isLoaded()) {
			continue;
		}
	}
	public Channel getChannel(){
		return this.channel;
	}
	public boolean hasNext() throws InterruptedException{
		if(deletionHandler.isDeleted() && available() == 0) {
			listener.close();
			throw new InterruptedException("Channel deleted");
		}
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
		channel.getJDA().removeEventListener(this.deletionHandler);
		listener.close();
		super.close();
		closed = true;
	}
	public User getLatestUser() {
		return listener.getLatestUser();
	}
	public AudioInputStream getAudioInputStream(){
		if(!channel.getType().equals(ChannelType.VOICE)){
			throw new UnsupportedOperationException("Cannot create an audio stream of text!");
		}
		return new AudioInputStream(this, AudioReceiveHandler.OUTPUT_FORMAT, SilenceSaturator.silence.length);
	}
}
