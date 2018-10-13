package net.benjaminurquhart.CIO.input;

import java.io.IOException;

interface Listener {

	int getNext() throws IOException;
	boolean isDeleted();
	int available();
	void close();
}
