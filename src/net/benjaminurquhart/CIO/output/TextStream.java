package net.benjaminurquhart.CIO.output;

import java.io.IOException;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TextStream extends ListenerAdapter implements ChannelStream{

	private MessageChannel channel;
	private String buff = "";
	private boolean deleted;
	
	protected TextStream(MessageChannel channel) {
		this.channel = channel;
		channel.getJDA().addEventListener(this);
	}
	protected TextStream(PrivateChannel channel) {
		this.channel = (MessageChannel) channel;
		channel.getJDA().addEventListener(this);
	}
	protected TextStream(TextChannel channel) {
		this.channel = (MessageChannel) channel;
		channel.getJDA().addEventListener(this);
	}
	@Override
	public void flush() throws IOException {
		if(deleted) {
			throw new IOException("Channel deleted");
		}
		if(buff == null) {
			throw new IOException("Stream closed");
		}
		while(buff.length() > 2000) {
			channel.sendMessage(buff.substring(0, 2000)).queue();
			buff = buff.substring(2000);
		}
		channel.sendMessage(buff).queue();
		buff = "";
	}
	@Override
	public void write(byte b) throws IOException {
		if(deleted) {
			buff = null;
			throw new IOException("Channel deleted");
		}
		if(buff == null) {
			throw new IOException("Stream closed");
		}
		buff += (char)b;
	}
	@Override
	public void close() throws IOException {
		this.flush();
		buff = null;
	}
	@Override
	public int getBuffSize() {
		return buff.length();
	}
}
