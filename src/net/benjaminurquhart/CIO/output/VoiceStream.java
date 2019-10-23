package net.benjaminurquhart.CIO.output;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.benjaminurquhart.CIO.input.SilenceSaturator;

public class VoiceStream extends ListenerAdapter implements ChannelStream, AudioSendHandler{

	public static final int BUFFSIZE = 3840;
	
	private VoiceChannel channel;
	private List<Byte> buff;
	private boolean isFull;
	private byte[] latest;
	private int index;
	
	protected VoiceStream(VoiceChannel channel) {
		Guild guild = channel.getGuild();
		if(guild.getAudioManager().isConnected() && guild.getAudioManager().getSendingHandler() != null && !(guild.getAudioManager().getSendingHandler() instanceof SilenceSaturator)) {
			throw new IllegalStateException("Already sending audio to this guild!");
		}
		this.channel = channel;
		this.latest = new byte[BUFFSIZE];
		this.buff = new ArrayList<>();
		this.index = 0;
		guild.getAudioManager().setSendingHandler(this);
		guild.getAudioManager().openAudioConnection(channel);
	}
	
	@Override
	public boolean canProvide() {
		return isFull;
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		if(isFull) {
			byte[] out = latest;
			latest = new byte[BUFFSIZE];
			isFull = false;
			index = 0;
			while(!isFull || !buff.isEmpty()) {
				try {
					this.write(buff.remove(0));
				}
				catch(Exception e) {}
			}
			return ByteBuffer.wrap(out);
		}
		else {
			return ByteBuffer.wrap(SilenceSaturator.SILENCE);
		}
	}

	@Override
	public void flush() throws IOException {
		isFull = true;
	}

	@Override
	public void close() throws IOException {
		channel.getGuild().getAudioManager().closeAudioConnection();
		this.buff = null;
		this.channel = null;
	}

	@Override
	public void write(byte b) throws IOException {
		if(isFull) {
			buff.add(b);
		}
		latest[index++] = b;
		if(index == BUFFSIZE) {
			isFull = true;
		}
	}

	@Override
	public int getBuffSize() {
		return index + buff.size();
	}
}
