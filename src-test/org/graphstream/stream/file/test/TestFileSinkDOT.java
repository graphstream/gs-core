package org.graphstream.stream.file.test;

import org.graphstream.stream.file.FileSinkDOT;
import org.graphstream.stream.file.FileSourceDOT;

public class TestFileSinkDOT extends TestFileSinkBase {

	public void setup() {
		input = new FileSourceDOT();
		output = new FileSinkDOT();
		formatHandleDynamics = false;
		formatHandlesEdgesIDs = false;
	}

	protected String aTemporaryGraphFileName() {
		return "foo.dot";
	}

}
