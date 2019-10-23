package net.benjaminurquhart.CIO.input;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.User;

public class Main {

	private static final int BUFFSIZE = AudioReceiveHandler.OUTPUT_FORMAT.getFrameSize();
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
		JDA jda = new JDABuilder(token).build().awaitReady();
		ChannelInputStream cis;
		boolean loop = true;
		if(isVoice){
			cis = new ChannelInputStream(jda.getGuildById(guildId).getVoiceChannelById(channelId));
		}
		else{
			cis = new ChannelInputStream(jda.getGuildById(guildId).getTextChannelById(channelId), true);
		}
		GuildChannel channel = cis.getChannel();
		System.out.println("Guild: " + channel.getGuild().getName());
		System.out.println("Channel: " + channel.getName());
		System.out.println("Channel type: " + channel.getType());
		System.out.println("Ready.");
		
		int next;
		String latest = "";
		User user;
		byte[] voiceData = new byte[BUFFSIZE];
		int numRead = 0;
		DataLine.Info info = null;
		SourceDataLine line = null;
		if(isVoice){
			info = new DataLine.Info(SourceDataLine.class, AudioReceiveHandler.OUTPUT_FORMAT);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(AudioReceiveHandler.OUTPUT_FORMAT, BUFFSIZE);
			line.start();
		}
		while(loop){
			try {
				if(cis.hasNext()){
					next = cis.read();
					user = cis.getLatestUser();
					if(!latest.equals(user.getId())) {
						latest = user.getId();
						System.out.print(channel.getGuild().getMember(user).getEffectiveName() + ": ");
					}
					if(isVoice){
						numRead = cis.read(voiceData, 0, BUFFSIZE);
						while(numRead < BUFFSIZE){
							if(numRead < 0){
								break;
							}
							numRead = cis.read(voiceData, numRead, BUFFSIZE - numRead);
						}
						line.write(voiceData, 0, BUFFSIZE);
					}
					else{
						System.out.print((char)next);
					}
				}
				//Thread.sleep(10);
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
