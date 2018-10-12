package net.benjaminurquhart.CIS;

import java.io.IOException;
import java.util.ArrayList;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AudioListener extends ListenerAdapter implements AudioReceiveHandler, Listener{
	
	private Guild guild;
	private ArrayList<Byte> buff;
	private String channelId;
	private String guildId;
	private boolean ready;
	private boolean loaded;
	private boolean deleted;
	private long cached;
	
	public AudioListener(VoiceChannel channel) {
		this.guild = channel.getGuild();
		this.channelId = channel.getId();
		this.guildId = channel.getGuild().getId();
		if(guild.getAudioManager().isConnected()) {
			throw new IllegalStateException("Already receiving audio from this guild!");
		}
		this.buff = new ArrayList<>();
		this.cached = 0;
		guild.getJDA().addEventListener(this);
		guild.getAudioManager().setReceivingHandler(this);
		guild.getAudioManager().setSendingHandler(new SilenceSaturator());
		guild.getAudioManager().openAudioConnection(channel);
	}
	@Override
	public boolean canReceiveCombined() {
		loaded = true;
		return true;
	}

	@Override
	public boolean canReceiveUser() {
		return false;
	}

	@Override
	public void handleCombinedAudio(CombinedAudio arg0) {
		byte[] arr = arg0.getAudioData(1.0);
		for(byte b : arr) {
			buff.add(b);
		}
		cached += arr.length;
		ready = true;
	}

	@Override
	public void handleUserAudio(UserAudio arg0) {
		return;
	}

	@Override
	public int getNext() throws IOException{
		while(!ready || available() == 0){
			if(deleted) {
				throw new IOException("Channel deleted");
			}
			continue;
		}
		cached--;
		return buff.remove(0);
	}

	@Override
	public int available() {
		return (int)cached;
	}
	@Override
	public boolean isDeleted() {
		return deleted;
	}
	@Override
	public void close() {
		guild.getAudioManager().closeAudioConnection();
		guild.getJDA().removeEventListener(this);
	}
	
	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent event) {
		if(event.getChannel().getId().equals(channelId) && event.getGuild().getId().equals(guildId)) {
			deleted = true;
		}
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
}
