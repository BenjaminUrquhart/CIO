package net.benjaminurquhart.CIO.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class AudioListener implements AudioReceiveHandler, Listener{
	
	private JDA jda;
	
	private String guildID;
	private User latestUser;
	private List<Byte> buff;
	private boolean ready;
	private boolean loaded;
	private long cached;
	
	public AudioListener(VoiceChannel channel) {
		Guild guild = channel.getGuild();
		this.guildID = guild.getId();
		this.jda = channel.getJDA();
		if(guild.getAudioManager().isConnected() && guild.getAudioManager().getReceivingHandler() != null) {
			throw new IllegalStateException("Already receiving audio from this guild!");
		}
		this.buff = new ArrayList<>();
		this.cached = 0;
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
		try {
			latestUser = arg0.getUsers().get(0);
		}
		catch(Exception e) {}
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
	public void close() {
		jda.getGuildById(guildID).getAudioManager().closeAudioConnection();
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	@Override
	public User getLatestUser() {
		return latestUser;
	}
	
}
