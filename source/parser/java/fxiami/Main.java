package fxiami;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	static void dispatchAction(String[] args) throws InterruptedException, IOException {
		int len = args.length;
		if (len == 0)
			return;
		switch (args[0]) {
		case "load":
			if (len == 3) {
				Loader.loadJSON(args[1], new File(args[2]));
			} else if (len == 4) {
				Loader.loadJSON("artist", new File(args[1]));
				Loader.loadJSON("album", new File(args[2]));
				Loader.loadJSON("song", new File(args[3]));
			} else {
				throw new InterruptedException("Illegal argument count.");
			}
			break;
		case "load-hybrid":
			if (len != 2)
				throw new InterruptedException("Illegal argument count.");
			Loader.loadJSON(new File(args[1]));
			break;
		case "list":
			if (len != 2)
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
			if (len == 2 || len == 3) {
				Loader.exportJSON(args[1], new File(len == 3 ? args[2] : args[1] + ".json"));
			} else if (len == 1 || len == 4) {
				Loader.exportJSON("artist", new File(len == 4 ? args[1] : "artist.json"));
				Loader.exportJSON("album", new File(len == 4 ? args[2] : "album.json"));
				Loader.exportJSON("song", new File(len == 4 ? args[3] : "song.json"));
			} else {
				throw new InterruptedException("Illegal argument count.");
			}
			break;
		case "export-hybrid":
			if (len != 1 && len != 2)
				throw new InterruptedException("Illegal argument count.");
			Loader.exportJSON(new File(len == 2 ? args[1] : "hybrid.json"));
			break;
		case "clear": {
			if (len != 1)
				throw new InterruptedException("Illegal argument count.");
			MappedEntry.clearAll();
			System.gc();
			Runtime rt = Runtime.getRuntime();
			long mem = Math.round((rt.totalMemory() - rt.freeMemory()) / 1024.0);
			System.out.println("Memory: " + mem + "KiB");
			break;
		}
		case "free": {
			if (len != 1)
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
			if (len != 3 && len != 4)
				throw new InterruptedException("Illegal argument count.");
			File[] fs = new File(args[2]).listFiles();
			Extractor.extractRaw(args[1], fs, new File(len == 4 ? args[3] : args[1] + ".jsonm"));
			break;
		}
		case "convert":
			if (len == 3) {
				Converter.convertJSONM(args[1], new File(args[2]));
			} else if (len == 4) {
				Converter.convertJSONM("artist", new File(args[1]));
				Converter.convertJSONM("album", new File(args[2]));
				Converter.convertJSONM("song", new File(args[3]));
			} else {
				throw new InterruptedException("Illegal argument count.");
			}
			break;
		case "index":
			if (len != 1 && len != 2)
				throw new InterruptedException("Illegal argument count.");
			Indexer.exportIndex(new File(len == 2 ? args[1] : "hybrid.ijsom"));
			break;
		case "exit":
			throw new InterruptedException(len == 1 ? null : "Illegal argument count.");
		default:
			throw new InterruptedException("Unknown action.");
		}
	}
	
	static void interact() {
		System.out.println("Enter interactive mode.");
		Scanner sc = new Scanner(System.in);
		// \s*(?:"([^"]*)"(?=\s|$)|(\S+))
		Pattern regex = Pattern.compile("\\s*(?:\"([^\"]*)\"(?=\\s|$)|(\\S+))");
		while (true) {
			System.out.print("> ");
			try {
				Matcher mat = regex.matcher(sc.nextLine());
				List<String> argli = new ArrayList<>();
				while (mat.find()) {
					String arg = mat.group(2);
					argli.add(arg != null ? arg : mat.group(1));
				}
				dispatchAction(argli.toArray(new String[0]));
			} catch (InterruptedException ex) {
				if (ex.getMessage() == null)
					break;
				System.out.println(ex.getMessage());
			} catch (Exception ex) {
				System.err.println("Unexpected break:");
				ex.printStackTrace();
			}
		}
		sc.close();
	}
	
	public static void main(String[] args) {
		try {
			loadData();
			List<String> argli = new ArrayList<>();
			for (int i = 0; i <= args.length; i++) {
				if (i != args.length && !args[i].equals(";")) {
					argli.add(args[i]);
				} else {
					if (argli.size() > 0) {
						dispatchAction(argli.toArray(new String[0]));
						argli.clear();
					}
				}
			}
			interact();
		} catch (Exception ex) {
			if (ex instanceof InterruptedException && ex.getMessage() == null)
				return;
			System.err.println("Unexpected break:");
			ex.printStackTrace();
		}
	}
	
	@Deprecated
	private Main() {
		throw new IllegalStateException();
	}
	
}
