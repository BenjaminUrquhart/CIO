package net.benjaminurquhart.CIO.output;

import java.io.IOException;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TextStream extends ListenerAdapter implements ChannelStream{

	private TextChannel channel;
	private StringBuffer buffer;
	private volatile boolean deleted;
	
	protected TextStream(TextChannel channel) {
		this.channel = channel;
		this.buffer = new StringBuffer();
		channel.getJDA().addEventListener(this);
	}
	@Override
	public void flush() throws IOException {
		if(deleted) {
			throw new IOException("Channel deleted");
		}
		if(buffer == null) {
			throw new IOException("Stream closed");
		}
		while(buffer.length() > 2000) {
			channel.sendMessage(buffer.substring(0, 2000)).queue();
			buffer.delete(0, 2000);
		}
		channel.sendMessage(buffer).queue();
		buffer.delete(0, buffer.length());
	}
	@Override
	public void write(byte b) throws IOException {
		if(deleted) {
			buffer = null;
			throw new IOException("Channel deleted");
		}
		if(buffer == null) {
			throw new IOException("Stream closed");
		}
		buffer.append((char)b);
	}
	@Override
	public void close() throws IOException {
		this.flush();
		buffer = null;
	}
	@Override
	public int getBuffSize() {
		return buffer.length();
	}
}
