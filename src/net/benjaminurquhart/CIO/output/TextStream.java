package net.benjaminurquhart.CIO.output;

import java.io.IOException;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;

public class TextStream implements ChannelStream{

	private MessageChannel channel;
	private String buff = "";
	
	public TextStream(MessageChannel channel) {
		this.channel = channel;
	}
	public TextStream(PrivateChannel channel) {
		this.channel = (MessageChannel) channel;
	}
	public TextStream(TextChannel channel) {
		this.channel = (MessageChannel) channel;
	}
	@Override
	public void flush() throws IOException {
		if(buff == null) {
			throw new IOException("Stream closed");
		}
		while(buff.length() > 2000) {
			channel.sendMessage(buff.substring(0, 1999)).queue();
			buff = buff.substring(2000);
		}
		channel.sendMessage(buff).queue();
		buff = "";
	}
	@Override
	public void write(byte b) throws IOException {
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
}
