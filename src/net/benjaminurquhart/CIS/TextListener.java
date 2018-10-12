package net.benjaminurquhart.CIS;

import java.io.IOException;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class TextListener extends ListenerAdapter implements Listener{
	
	private JDA jda;
	private String channelId;
	private String guildId;
	private String buff = "";
	private boolean deleted;
	private boolean isPrivate;
	private boolean ignoreBots;
	
	protected TextListener(TextChannel channel, boolean ignoreBots) {
		this.channelId = channel.getId();
		this.guildId = channel.getGuild().getId();
		this.jda = channel.getJDA();
		this.isPrivate = false;
		this.ignoreBots = ignoreBots;
		this.jda.addEventListener(this);
	}
	
	protected TextListener(MessageChannel channel, Guild guild, boolean ignoreBots) {
		this.channelId = channel.getId();
		this.guildId = (guild == null ? null : guild.getId());
		this.jda = channel.getJDA();
		this.isPrivate = (guild == null);
		this.ignoreBots = ignoreBots;
		this.jda.addEventListener(this);
	}
	
	protected TextListener(PrivateChannel channel, boolean ignoreBots) {
		this.channelId = channel.getId();
		this.guildId = null;
		this.jda = channel.getJDA();
		this.isPrivate = true;
		this.ignoreBots = ignoreBots;
		this.jda.addEventListener(this);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(ignoreBots && event.getAuthor().isBot()){
			return;
		}
		if((!isPrivate) && !event.getGuild().getId().equals(guildId)){
			return;
		}
		if(!event.getChannel().getId().equals(channelId)){
			return;
		}
		buff += event.getMessage().getContentRaw() + "\n";
	}
	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent event) {
		if(event.getChannel().getId().equals(channelId) && event.getGuild().getId().equals(guildId)) {
			deleted = true;
		}
	}
	@Override
	public int getNext() throws IOException {
		while(buff.isEmpty()){
			if(deleted) {
				throw new IOException("Channel deleted");
			}
			continue;
		}
		int next = (int)buff.charAt(0);
		buff = buff.substring(1);
		return next;
	}
	@Override
	public int available() {
		return buff.length();
	}
	@Override
	public void close() {
		jda.removeEventListener(this);
	}
}
