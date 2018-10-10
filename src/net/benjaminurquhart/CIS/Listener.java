package net.benjaminurquhart.CIS;

interface Listener {

	default boolean hasNext() {
		return available() > 0;
	}
	int getNext();
	int available();
	void close();
}
