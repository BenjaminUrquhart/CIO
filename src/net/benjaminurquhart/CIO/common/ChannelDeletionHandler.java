package net.benjaminurquhart.CIO.common;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
//import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChannelDeletionHandler extends ListenerAdapter {

	private String guildId;
	private String channelId;
	private boolean deleted;
	
	public ChannelDeletionHandler(TextChannel channel){
		this.channelId = channel.getId();
		this.guildId = channel.getGuild().getId();
	}
	public ChannelDeletionHandler(MessageChannel channel){
		this.channelId = channel.getId();
		if(channel.getType().equals(ChannelType.TEXT)){
			this.guildId = ((TextChannel)channel).getGuild().getId();
		}
	}
	/*
	public ChannelDeletionHandler(PrivateChannel channel){
		this.channelId = channel.getId();
		this.guildId = null;
	}*/
	public ChannelDeletionHandler(VoiceChannel channel){
		this.channelId = channel.getId();
		this.guildId = channel.getGuild().getId();
	}
	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent event) {
		if(event.getChannel().getId().equals(channelId) && (guildId != null && event.getGuild().getId().equals(guildId))) {
			deleted = true;
		}
	}
	@Override
	public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
		if(event.getChannel().getId().equals(channelId) && (guildId != null && event.getGuild().getId().equals(guildId))) {
			deleted = true;
		}
	}
	public boolean isDeleted(){
		return deleted;
	}
}
