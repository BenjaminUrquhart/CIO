package net.benjaminurquhart.CIO.output;

import java.io.IOException;

interface ChannelStream{

	int getBuffSize();
	void flush() throws IOException;
	void close() throws IOException;
	void write(byte b) throws IOException;
	
}
