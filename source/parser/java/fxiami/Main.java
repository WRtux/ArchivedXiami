package fxiami;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import fxiami.entry.CategoryEntry;
import fxiami.entry.Entry;
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
	
	static void dispatchAction(String[] args, boolean ext) throws InterruptedException, IOException {
		switch (args[0]) {
		case "clear":
			Entry.clearAll();
			break;
		case "category":
			if (args.length != 1)
				throw new InterruptedException("Illegal argument count.");
			System.out.println("Listing categories...");
			for (CategoryEntry en : CategoryEntry.getAll()) {
				System.out.println(en.toJSON());
			}
			System.out.println("Listing styles...");
			for (StyleEntry en : StyleEntry.getAll()) {
				System.out.println(en.toJSON());
			}
			break;
		case "load":
			if (args.length != 3)
				throw new InterruptedException("Illegal argument count.");
			if (ext)
				throw new InterruptedException("Not supported in command mode.");
			Loader.loadJSON(args[1], new File(args[2]));
			break;
		case "load-hybrid":
			if (args.length != 2)
				throw new InterruptedException("Illegal argument count.");
			if (ext)
				throw new InterruptedException("Not supported in command mode.");
			//TODO
			break;
		case "export":
			//TODO
		case "extract":
			if (args.length == 1) {
				if (ext)
					throw new InterruptedException("Not supported in command mode.");
				Loader.exportJSON(new File("hybrid.json"));
				break;
			}
			if (args.length != 3)
				throw new InterruptedException("Illegal argument count.");
			File[] fs = new File(args[2]).listFiles();
			Exporter.exportJSONM(args[1], fs, new File(args[1] + ".jsonm"));
			break;
		case "parse":
			if (args.length != 3)
				throw new InterruptedException("Illegal argument count.");
			List<Entry> li = Parser.parseJSONM(args[1], new File(args[2]));
			if (ext)
				Loader.exportJSON(li, new File(args[1] + ".json"));
			break;
		case "parse-hybrid":
			if (args.length != 4)
				throw new InterruptedException("Illegal argument count.");
			Parser.parseJSONM("artist", new File(args[1]));
			Parser.parseJSONM("album", new File(args[2]));
			Parser.parseJSONM("song", new File(args[3]));
			if (ext)
				Loader.exportJSON(new File("hybrid.json"));
			break;
		case "index":
			if (ext ? args.length != 4 : args.length != 1)
				throw new InterruptedException("Illegal argument count.");
			if (ext) {
				Parser.parseJSONM("artist", new File(args[1]));
				Parser.parseJSONM("album", new File(args[2]));
				Parser.parseJSONM("song", new File(args[3]));
			}
			Indexer.exportIndex(new File("hybrid.ijsom"));
			break;
		default:
			throw new InterruptedException("Unknown action.");
		}
	}
	
	static void interact() {
		System.out.println("Enter interactive mode.");
		while (true) {
			Scanner sc = new Scanner(System.in);
			System.out.print("> ");
			String[] args = sc.nextLine().split(" ");
			if (args.length == 1 && args[0].equals("exit"))
				break;
			try {
				dispatchAction(args, false);
			} catch (InterruptedException ex) {
				System.out.println(ex.getMessage());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		loadData();
		if (args.length == 0 || args.length == 1 && args[0].equals("interact")) {
			interact();
			return;
		}
		try {
			dispatchAction(args, true);
		} catch (InterruptedException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	@Deprecated
	private Main() {
		throw new IllegalStateException();
	}
	
}
