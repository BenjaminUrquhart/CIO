package net.benjaminurquhart.CIS;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class AudioListener implements AudioReceiveHandler, Listener{
	
	
	public AudioListener(VoiceChannel channel) {
		
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
		
	}

	@Override
	public void handleUserAudio(UserAudio arg0) {
		
	}

	@Override
	public int getNext() {
		return 0;
	}

	@Override
	public int available() {
		return 0;
	}

	@Override
	public void close() {
		
	}

}
