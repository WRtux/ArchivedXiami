package fxiami;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import com.alibaba.fastjson.JSONArray;

import fxiami.entry.Entry;

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
			List<Entry> li = Parser.parseJSONM(args[1], new File(args[2]));
			JSONArray arr = new JSONArray();
			for (Entry en : li) {
				arr.add(en.toJSON());
			}
			System.gc();
			Writer wtr = new OutputStreamWriter(new FileOutputStream("parse.json"), "UTF-8");
			wtr.write(arr.toJSONString());
			wtr.close();
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	@Deprecated
	private Main() {
		throw new IllegalStateException();
	}
	
}
