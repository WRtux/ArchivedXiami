package fxiami;

import java.io.File;
import java.io.IOException;

public final class Main {
	
	public static void main(String[] args) throws IOException {
		if (args.length != 3)
			throw new IllegalArgumentException();
		switch (args[0]) {
		case "export":
			File[] fs = new File(args[2]).listFiles();
			Exporter.exportJSONM(args[1], fs, new File(args[1] + ".jsonm"));
			break;
		case "parse":
			Parser.exportJSON(args[1], new File(args[2]), new File(args[1] + ".json"));
			break;
		case "index":
			throw new IllegalStateException();
		default:
			throw new IllegalArgumentException();
		}
	}
	
	@Deprecated
	private Main() {
		throw new IllegalStateException();
	}
	
}
