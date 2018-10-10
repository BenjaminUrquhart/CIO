package net.benjaminurquhart.CIS;

import java.time.Instant;

import net.dv8tion.jda.core.audio.AudioSendHandler;

public class SilenceSaturator implements AudioSendHandler{
	
	public static final byte[] silence = new byte[3840];
	public boolean send;
	public long startTime;
	
	public SilenceSaturator() {
		this.startTime = Instant.now().getEpochSecond();
		this.send = true;
	}
	
	@Override
	public boolean canProvide() {
		if(send) {
			if(Instant.now().getEpochSecond() - startTime > 5) {
				send = false;
			}
		}
		return send;
	}

	@Override
	public byte[] provide20MsAudio() {
		return silence;
	}

}
