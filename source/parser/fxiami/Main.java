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
