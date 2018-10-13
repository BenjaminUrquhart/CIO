package net.benjaminurquhart.CIO.output;

import java.io.IOException;
import java.util.ArrayList;

import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.benjaminurquhart.CIO.input.SilenceSaturator;

public class VoiceStream extends ListenerAdapter implements ChannelStream, AudioSendHandler{

	public static final int BUFFSIZE = 3840;
	
	private ArrayList<Byte> buff;
	private VoiceChannel channel;
	private String channelId;
	private String guildId;
	private boolean deleted;
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
		this.channelId = channel.getId();
		this.guildId = guild.getId();
		this.index = 0;
		guild.getAudioManager().setSendingHandler(this);
		guild.getAudioManager().openAudioConnection(channel);
	}
	
	@Override
	public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
		if(event.getChannel().getId().equals(channelId) && event.getGuild().getId().equals(guildId)) {
			deleted = true;
		}
	}
	@Override
	public boolean canProvide() {
		return isFull;
	}

	@Override
	public byte[] provide20MsAudio() {
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
			return out;
		}
		else {
			return SilenceSaturator.silence;
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
		this.guildId = null;
		this.channelId = null;
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
	public boolean isDeleted() {
		return deleted;
	}
}
