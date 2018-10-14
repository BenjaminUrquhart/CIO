package net.benjaminurquhart.CIO.output;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Channel;

public class Main {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception{
		String token = args[0];
		String guildId = args[1];
		String channelId = args[2];
		boolean isVoice = false;
		if(args.length == 4){
			isVoice = args[3].toLowerCase().equals("voice");
		}
		if(isVoice){
			throw new UnsupportedOperationException("Cannot create a voice output stream in demo mode");
		}
		JDA jda = new JDABuilder(token).setAudioEnabled(isVoice).build().awaitReady();
		ChannelOutputStream cos = new ChannelOutputStream(jda.getGuildById(guildId).getTextChannelById(channelId));
		Channel channel = cos.getChannel();
		System.out.println("Guild: " + channel.getGuild().getName());
		System.out.println("Channel: " + channel.getName());
		System.out.println("Channel type: " + channel.getType());
		System.out.println("Ready.");
		while(true){
			while(System.in.available() > 0){
				cos.write(System.in.read());
			}
			if(cos.getBuffSize() > 0){
				cos.flush();
			}
		}
	}
}
