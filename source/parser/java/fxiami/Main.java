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
import fxiami.entry.MappedEntry;
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
		case "load":
			if (ext)
				throw new InterruptedException("Not supported in command mode.");
			if (args.length == 3) {
				Loader.loadJSON(args[1], new File(args[2]));
			} else if (args.length == 4) {
				Loader.loadJSON("artist", new File(args[1]));
				Loader.loadJSON("album", new File(args[2]));
				Loader.loadJSON("song", new File(args[3]));
			} else {
				throw new InterruptedException("Illegal argument count.");
			}
			break;
		case "load-hybrid":
			if (ext)
				throw new InterruptedException("Not supported in command mode.");
			if (args.length != 2)
				throw new InterruptedException("Illegal argument count.");
			//TODO
			break;
		case "list":
			if (args.length != 2)
				throw new InterruptedException("Illegal argument count.");
			switch (args[1]) {
			case "category":
				System.out.println("Listing categories...");
				for (CategoryEntry en : CategoryEntry.getAll()) {
					System.out.println(en.toJSON());
				}
				System.out.println("Listing styles...");
				for (StyleEntry en : StyleEntry.getAll()) {
					System.out.println(en.toJSON());
				}
				break;
			default:
				throw new InterruptedException("Unknown target.");
			}
			break;
		case "export":
			if (ext)
				throw new InterruptedException("Not supported in command mode.");
			if (args.length != 2)
				throw new InterruptedException("Illegal argument count.");
			Loader.exportJSON(args[1], new File(args[1] + ".json"));
			break;
		case "export-hybrid":
			if (ext)
				throw new InterruptedException("Not supported in command mode.");
			if (args.length != 1)
				throw new InterruptedException("Illegal argument count.");
			Loader.exportJSON(new File("hybrid.json"));
			break;
		case "clear": {
			if (ext)
				throw new InterruptedException("Not supported in command mode.");
			if (args.length != 1)
				throw new InterruptedException("Illegal argument count.");
			MappedEntry.clearAll();
			System.gc();
			Runtime rt = Runtime.getRuntime();
			long mem = Math.round((rt.totalMemory() - rt.freeMemory()) / 1024.0);
			System.out.println("Memory: " + mem + "KiB");
			break;
		}
		case "free": {
			if (ext)
				throw new InterruptedException("Not supported in command mode.");
			if (args.length != 1)
				throw new InterruptedException("Illegal argument count.");
			Runtime rt = Runtime.getRuntime();
			long mem = Math.round((rt.totalMemory() - rt.freeMemory()) / 1024.0);
			System.out.print("Memory: " + mem + "KiB -> ");
			System.gc();
			mem = Math.round((rt.totalMemory() - rt.freeMemory()) / 1024.0);
			System.out.println(mem + "KiB");
			break;
		}
		case "extract": {
			if (args.length != 3)
				throw new InterruptedException("Illegal argument count.");
			File[] fs = new File(args[2]).listFiles();
			Extractor.extractRaw(args[1], fs, new File(args[1] + ".jsonm"));
			break;
		}
		case "convert":
			if (args.length == 3) {
				List<MappedEntry> li = Converter.convertJSONM(args[1], new File(args[2]));
				if (ext)
					Loader.exportJSON(li, new File(args[1] + ".json"));
			} else if (args.length == 4) {
				Converter.convertJSONM("artist", new File(args[1]));
				Converter.convertJSONM("album", new File(args[2]));
				Converter.convertJSONM("song", new File(args[3]));
				if (ext)
					Loader.exportJSON(new File("hybrid.json"));
			} else {
				throw new InterruptedException("Illegal argument count.");
			}
			break;
		case "index":
			if (ext ? args.length != 4 : args.length != 1)
				throw new InterruptedException("Illegal argument count.");
			if (ext) {
				Converter.convertJSONM("artist", new File(args[1]));
				Converter.convertJSONM("album", new File(args[2]));
				Converter.convertJSONM("song", new File(args[3]));
			}
			Indexer.exportIndex(new File("hybrid.ijsom"));
			break;
		default:
			throw new InterruptedException("Unknown action.");
		}
	}
	
	static void interact() {
		System.out.println("Enter interactive mode.");
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.print("> ");
			String[] args = sc.nextLine().split(" ");
			if (args.length == 1 && args[0].equals("exit"))
				break;
			try {
				dispatchAction(args, false);
			} catch (InterruptedException ex) {
				System.out.println(ex.getMessage());
			} catch (Exception ex) {
				System.err.println("Unexpected break:");
				ex.printStackTrace();
			}
		}
		sc.close();
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
