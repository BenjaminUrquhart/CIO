# CIO
Input/Output Stream wrapper for JDA Channel objects

Fair warning: for some reason this does not work with the `java.util.Scanner` class.

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
