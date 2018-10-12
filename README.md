# CIS
InputStream wrapper for JDA Channel objects

Fair warning: for some reason this does not work with the `java.util.Scanner` class. Also this does not work on OSX (Windows only)

Progress:
- TextChannel: Working
- MessageChannel: Working
- PrivateChannel: Untested
- VoiceChannel: NOT Working. No sure if it's Discord audio being funky again.

I should probably have read the [InputStream docs](https://docs.oracle.com/javase/8/docs/api/java/io/InputStream.html) first before making this :^)
