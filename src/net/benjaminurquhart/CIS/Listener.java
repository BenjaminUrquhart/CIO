package net.benjaminurquhart.CIS;

import java.io.IOException;

interface Listener {

	int getNext() throws IOException;
	int available();
	void close();
}
