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

import fxiami.entry.AlbumEntry;
import fxiami.entry.ArtistEntry;
import fxiami.entry.Entry;
import fxiami.entry.Helper;
import fxiami.entry.SongEntry;

public final class Parser {
	
	public static ArtistEntry parseArtistEntry(String dat) {
		JSONObject o = JSON.parseObject(dat), cont = o.getJSONObject("artistDetail");
		if (cont == null || cont.size() == 0)
			return null;
		ArtistEntry en = new ArtistEntry(cont.getLong("artistId"), cont.getString("artistStringId"));
		//TODO
		return en;
	}
	
	public static AlbumEntry parseAlbumEntry(String dat) {
		JSONObject o = JSON.parseObject(dat), cont = o.getJSONObject("albumDetail");
		if (cont == null || cont.size() == 0)
			return null;
		AlbumEntry en = new AlbumEntry(cont.getLong("albumId"), cont.getString("albumStringId"));
		//TODO
		return en;
	}
	
	public static SongEntry parseSongEntry(String dat) {
		JSONObject o = JSON.parseObject(dat), cont = o.getJSONObject("songDetail");
		if (cont == null || cont.size() == 0)
			return null;
		SongEntry en = new SongEntry(cont.getLong("songId"), cont.getString("songStringId"));
		en.update = o.getLong("update");
		en.name = Helper.parseValidString(cont, "songName");
		en.subName = Helper.parseValidString(cont, "newSubName");
		if (en.subName == null || en.subName == Entry.NULL_STRING)
			en.subName = Helper.parseValidString(cont, "subName");
		JSONArray arr = cont.getJSONArray("artistVOs");
		if (arr != null && arr.size() > 0) {
			en.artist = Helper.parseValidEntry(arr.getJSONObject(0), "artistId", "artistStringId");
			en.artist.name = Helper.parseValidString(arr.getJSONObject(0), "artistName");
			if (arr.size() > 1)
				System.err.println("Multiple artists: " + arr.size());
		}
		en.album = Helper.parseValidEntry(cont, "albumId", "albumStringId");
		en.album.name = Helper.parseValidString(cont, "albumName");
		en.disc = Helper.parseValidInteger(cont, "cdSerial");
		en.track = Helper.parseValidInteger(cont, "track");
		en.length = Helper.parseValidInteger(cont, "length");
		en.playCount = Helper.parseValidInteger(cont, "playCount");
		en.likeCount = Helper.parseValidInteger(cont, "favCount");
		cont = o.getJSONObject("songExt");
		if (cont == null)
			return en;
		//TODO
		return en;
	}
	
	public static Entry parseEntry(String typ, String dat) {
		switch (typ) {//TODO
		case "artist":
			return parseArtistEntry(dat);
		case "album":
			return parseAlbumEntry(dat);
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
