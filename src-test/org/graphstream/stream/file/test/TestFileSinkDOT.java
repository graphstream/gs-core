package org.graphstream.stream.file.test;

import org.graphstream.stream.file.FileSinkDOT;
import org.graphstream.stream.file.FileSourceDOT;
import org.junit.Test;

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

	@Override
	@Test
	public void test_DirectedTriangle() {
		((FileSinkDOT) output).setDirected(true);
		super.test_DirectedTriangle();
		((FileSinkDOT) output).setDirected(false);
	}

}
