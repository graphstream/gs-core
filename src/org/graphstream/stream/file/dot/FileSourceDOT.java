package org.graphstream.stream.file.dot;

import java.io.FileReader;

import org.graphstream.stream.SourceBase;

public class FileSourceDOT extends SourceBase {

	public static void main(String ... args) throws Exception {
		FileReader in = new FileReader("foo.dot");
		FileSourceDOT dot = new FileSourceDOT();
		DOTParser parser = new DOTParser(dot, in);
		
		parser.graph();
		
		while( parser.next() )
			;
	}
}
