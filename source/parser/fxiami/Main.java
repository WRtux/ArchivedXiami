package fxiami;

import java.io.File;
import java.io.IOException;

public final class Main {
	
	public static void main(String[] args) throws IOException {
		if (args.length != 2)
			throw new IllegalArgumentException();
//		File[] fs = new File(args[1]).listFiles();
//		Exporter.exportJSONM(args[0], fs, new File(args[0] + ".jsonm"));
		Parser.parseJSONM(args[0], new File(args[1]));
	}
	
	@Deprecated
	private Main() {
		throw new IllegalStateException();
	}
	
}
