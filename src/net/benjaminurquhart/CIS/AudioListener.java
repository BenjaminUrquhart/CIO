package net.benjaminurquhart.CIS;

import java.util.ArrayList;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class AudioListener implements AudioReceiveHandler, Listener{
	
	private Guild guild;
	private ArrayList<Byte> buff;
	private boolean ready;
	
	public AudioListener(VoiceChannel channel) {
		this.guild = channel.getGuild();
		if(guild.getAudioManager().isConnected()) {
			throw new IllegalStateException("Already receiving audio from this guild!");
		}
		buff = new ArrayList<>();
		guild.getAudioManager().setReceivingHandler(this);
		guild.getAudioManager().setSendingHandler(new SilenceSaturator());
		guild.getAudioManager().openAudioConnection(channel);
	}
	@Override
	public boolean canReceiveCombined() {
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
		ready = true;
	}

	@Override
	public void handleUserAudio(UserAudio arg0) {
		return;
	}

	@Override
	public int getNext() {
		while(!ready){
			continue;
		}
		return buff.remove(0);
	}

	@Override
	public int available() {
		return buff.size();
	}

	@Override
	public void close() {
		guild.getAudioManager().closeAudioConnection();
	}

}
