package net.benjaminurquhart.CIO.input;

import java.io.IOException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
//import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TextListener extends ListenerAdapter implements Listener{
	
	private JDA jda;
	private User latestUser;
	private String channelId;
	private String guildId;
	private StringBuffer buffer;
	private volatile boolean deleted;
	private boolean isPrivate;
	private boolean ignoreBots;
	
	protected TextListener(TextChannel channel, boolean ignoreBots) {
		this.channelId = channel.getId();
		this.guildId = channel.getGuild().getId();
		this.jda = channel.getJDA();
		this.isPrivate = false;
		this.ignoreBots = ignoreBots;
		this.jda.addEventListener(this);
		this.buffer = new StringBuffer();
	}
	
	protected TextListener(MessageChannel channel, Guild guild, boolean ignoreBots) {
		this.channelId = channel.getId();
		this.guildId = (guild == null ? null : guild.getId());
		this.jda = channel.getJDA();
		this.isPrivate = (guild == null);
		this.ignoreBots = ignoreBots;
		this.jda.addEventListener(this);
		this.buffer = new StringBuffer();
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
		latestUser = event.getAuthor();
		buffer.append(event.getMessage().getContentRaw());
		buffer.append("\n");
	}
	@Override
	public int getNext() throws IOException {
		while(buffer.length() == 0){
			if(deleted) {
				throw new IOException("Channel deleted");
			}
			continue;
		}
		int next = buffer.charAt(0);
		buffer.deleteCharAt(0);
		return next;
	}
	@Override
	public int available() {
		return buffer.length();
	}
	@Override
	public void close() {
		jda.removeEventListener(this);
	}
	@Override
	public User getLatestUser() {
		return latestUser;
	}
}
