package net.benjaminurquhart.CIO.output;

import java.io.IOException;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TextStream extends ListenerAdapter implements ChannelStream{

	private MessageChannel channel;
	private String buff = "";
	private String channelId;
	private String guildId;
	private boolean deleted;
	
	protected TextStream(MessageChannel channel) {
		this.channel = channel;
		this.channelId = channel.getId();
		this.guildId = (channel.getType().equals(ChannelType.TEXT) ? ((TextChannel)channel).getGuild().getId() : null);
		channel.getJDA().addEventListener(this);
	}
	protected TextStream(PrivateChannel channel) {
		this.channel = (MessageChannel) channel;
		this.channelId = channel.getId();
		this.guildId = null;
		channel.getJDA().addEventListener(this);
	}
	protected TextStream(TextChannel channel) {
		this.channel = (MessageChannel) channel;
		this.channelId = channel.getId();
		this.guildId = channel.getId();
		channel.getJDA().addEventListener(this);
	}
	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent event) {
		if(event.getChannel().getId().equals(channelId) && (guildId != null && event.getGuild().getId().equals(guildId))) {
			deleted = true;
		}
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
	public boolean isDeleted() {
		return deleted;
	}
	@Override
	public int getBuffSize() {
		return buff.length();
	}
}
