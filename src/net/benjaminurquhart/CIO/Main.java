package net.benjaminurquhart.CIO;

import java.time.Instant;

import net.benjaminurquhart.CIO.input.ChannelInputStream;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Channel;

public class Main {

	public static void main(String[] args) throws Exception{
		if(args.length < 3){
			System.out.println("Usage: java -jar path/to/file.jar <token> <guild id> <channel id> [voice/text]");
			return;
		}
		String token = args[0];
		String guildId = args[1];
		String channelId = args[2];
		boolean isVoice = false;
		if(args.length == 4){
			isVoice = args[3].toLowerCase().equals("voice");
		}
		JDA jda = new JDABuilder(token).setAudioEnabled(isVoice).build().awaitReady();
		long startTime = Instant.now().getEpochSecond();
		ChannelInputStream cis;
		boolean loop = true;
		if(isVoice){
			cis = new ChannelInputStream(jda.getGuildById(guildId).getVoiceChannelById(channelId));
		}
		else{
			cis = new ChannelInputStream(jda.getGuildById(guildId).getTextChannelById(channelId), true);
		}
		Channel channel = cis.getChannel();
		System.out.println("Guild: " + channel.getGuild().getName());
		System.out.println("Channel: " + channel.getName());
		System.out.println("Channel type: " + channel.getType());
		System.out.println("Ready.");
		int next;
		while(loop){
			try {
				if(cis.hasNext()){
					next = cis.read();
					if(isVoice){
						System.out.println(next);
						if(Instant.now().getEpochSecond() - startTime > 5){
							cis.close();
							loop = false;
						}
					}
					else{
						System.out.print((char)next);
					}
				}
				Thread.sleep(10);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		cis.close();
		jda.shutdown();
		System.exit(0);
	}
}
