package net.benjaminurquhart.CIO.output;

import java.io.IOException;
import java.util.ArrayList;

import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class VoiceStream implements ChannelStream, AudioSendHandler{

	private ArrayList<Byte> buff;
	private VoiceChannel channel;
	
	public VoiceStream(VoiceChannel channel) {
		
	}
	
	@Override
	public boolean canProvide() {
		return false;
	}

	@Override
	public byte[] provide20MsAudio() {
		return null;
	}

	@Override
	public void flush() throws IOException {
		throw new UnsupportedOperationException("Cannot flush a voice stream!");
	}

	@Override
	public void close() throws IOException {
		
	}

	@Override
	public void write(byte b) throws IOException {
		buff.add(b);
	}

}
