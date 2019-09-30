# CIO
Input/Output Stream wrapper for JDA Channel objects

Fair warning: for some reason this does not work with the `java.util.Scanner` class.

NOTE: This was designed for JDA 3. I may update it to JDA 4 in the future.

InputStream:
- TextChannel: Working
- MessageChannel: Working
- PrivateChannel: Untested
- VoiceChannel: NOT Working. Not sure if it's Discord audio being funky again.

OutputStream:
- TextChannel: Working
- MessageChannel: Working
- PrivateChannel: Untested
- VoiceChannel: Untested

I should probably have read the [InputStream docs](https://docs.oracle.com/javase/8/docs/api/java/io/InputStream.html) first before making this :^)

# Example usage:
```java
//Let's say you have a MessageChannel from an onMessageReceivedEvent.
ChannelInputStream input = new ChannelInputStream(event.getChannel(), true); //The boolean toggles ignoring bots.
ChannelOutputStream output = new ChannelOutputStream(event.getChannel());
//Let's write something to the stream
output.write("Hello there!".getBytes());
output.flush(); //This actually sends the message. It is automatically called when the buffer length reaches 2000 characters.
//Let's read the next message
String msg = (char)input.read() + "";
while(input.hasNext()){
  msg += (char)input.read();
}
System.out.println(msg);
```

