package fxiami;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import fxiami.entry.CategoryEntry;
import fxiami.entry.StyleEntry;

public final class Main {
	
	static void loadData() throws IOException {
		InputStream in = Main.class.getResourceAsStream("category.json");
		BufferedReader rdr = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		JSONArray arr = JSON.parseArray(rdr.readLine());
		for (int i = 0, len = arr.size(); i < len; i++) {
			JSONObject o = arr.getJSONObject(i);
			new CategoryEntry(o.getLong("id"), o.getString("name"));
		}
		rdr.close();
		in = Main.class.getResourceAsStream("style.json");
		rdr = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		arr = JSON.parseArray(rdr.readLine());
		for (int i = 0, len = arr.size(); i < len; i++) {
			JSONObject o = arr.getJSONObject(i);
			new StyleEntry(o.getLong("genre"), o.getLong("id"), o.getString("name"));
		}
		rdr.close();
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.err.println("Missing action argument.");
			return;
		}
		loadData();
		if (args.length == 1) {
			switch (args[0]) {
			case "category":
				throw new IllegalStateException();
			default:
				System.err.println("Unknown action.");
				break;
			}
		} else if (args.length == 3) {
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
				System.err.println("Unknown action.");
				break;
			}
		} else if (args.length == 4) {
			switch (args[0]) {
			case "parse-hybrid":
				Parser.parseJSONM("artist", new File(args[1]));
				Parser.parseJSONM("album", new File(args[2]));
				Parser.parseJSONM("song", new File(args[3]));
				Parser.exportJSON(new File("hybrid.json"));
				break;
			default:
				System.err.println("Unknown action.");
				break;
			}
		} else {
			System.err.println("Illegal argument count.");
		}
	}
	
	@Deprecated
	private Main() {
		throw new IllegalStateException();
	}
	
}
