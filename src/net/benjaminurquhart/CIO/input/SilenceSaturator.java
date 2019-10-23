package net.benjaminurquhart.CIO.input;

import java.nio.ByteBuffer;
import java.time.Instant;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class SilenceSaturator implements AudioSendHandler{
	
	public static final byte[] SILENCE = new byte[3840];
	public boolean send;
	public long startTime;
	
	public SilenceSaturator() {
		this.startTime = Instant.now().getEpochSecond();
		this.send = true;
	}
	
	@Override
	public boolean canProvide() {
		if(send) {
			if(Instant.now().getEpochSecond() - startTime > 2) {
				send = false;
			}
		}
		return send;
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		return ByteBuffer.wrap(SILENCE);
	}

}
