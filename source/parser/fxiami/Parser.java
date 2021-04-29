package fxiami;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import fxiami.entry.Entry;
import fxiami.entry.Helper;
import fxiami.entry.SongEntry;

public final class Parser {
	
	public static SongEntry parseSongEntry(String dat) {
		JSONObject o = JSON.parseObject(dat), cont = o.getJSONObject("songDetail");
		if (cont == null)
			return null;
		SongEntry en = new SongEntry(
			Helper.parseValidInteger(cont, "songId"), Helper.parseValidString(cont, "songStringId"));
		en.update = o.getLong("update");
		en.name = Helper.parseValidString(cont, "songName");
		//TODO
		return en;
	}
	
	public static Entry parseEntry(String typ, String dat) {
		switch (typ) {//TODO
		case "artist":
			return null;
		case "album":
			return null;
		case "song":
			return parseSongEntry(dat);
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public static List<Entry> parseJSONM(String typ, File f) throws IOException {
		InputStream in = new FileInputStream(f);
		BufferedReader rdr = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		List<Entry> li = new ArrayList<>();
		try {
			System.out.println("Parsing " + f.getName() + "...");
			String ln = null;
			while ((ln = rdr.readLine()) != null) {
				Entry en = parseEntry(typ, ln);
				if (en != null) {
					System.out.println("Process " + en.name);
					li.add(en);
				}
			}
			System.out.println("Parse completed.");
			return li;
		} catch (Exception ex) {
			System.err.println("Parse failed.");
			throw ex;
		} finally {
			rdr.close();
		}
	}
	
	@Deprecated
	private Parser() {
		throw new IllegalStateException();
	}
	
}
