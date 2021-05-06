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
import fxiami.entry.ReferenceEntry;
import fxiami.entry.SongEntry;
import fxiami.entry.StaffEntry;

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
		en.update = Helper.parseValidInteger(o, "update");
		en.name = Helper.parseValidString(cont, "songName");
		en.subName = Helper.parseValidString(cont, "newSubName");
		if (en.subName == null || en.subName == Entry.NULL_STRING)
			en.subName = Helper.parseValidString(cont, "subName");
		if (en.subName != null && en.subName.length() == 0)
			en.subName = Entry.NULL_STRING;
		JSONArray artarr = cont.getJSONArray("artistVOs");
		if (artarr != null && artarr.size() > 0) {
			en.artist = Helper.parseValidEntry(artarr.getJSONObject(0), "artistId", "artistStringId");
			if (en.artist != null)
				en.artist.name = Helper.parseValidString(artarr.getJSONObject(0), "artistName");
			if (artarr.size() > 1)
				System.err.println("Multiple artists: " + artarr.size());
		}
		en.album = Helper.parseValidEntry(cont, "albumId", "albumStringId");
		if (en.album != null)
			en.album.name = Helper.parseValidString(cont, "albumName");
		en.disc = Helper.parseValidInteger(cont, "cdSerial");
		en.track = Helper.parseValidInteger(cont, "track");
		en.length = Helper.parseValidInteger(cont, "length");
		en.highlight = Helper.parseValidInteger(cont, "hotPartStartTime");
		en.pace = Helper.parseValidInteger(cont, "pace");
		en.playCount = Helper.parseValidInteger(cont, "playCount");
		en.likeCount = Helper.parseValidInteger(cont, "favCount");
		cont = o.getJSONObject("songExt");
		if (cont == null)
			return en;
		if (cont.containsKey("behindStaffs")) {
			JSONObject[] typs = Helper.parseValidArray(cont, "behindStaffs", new JSONObject[0]);
			en.staff = new StaffEntry[typs.length];
			for (int i = 0; i < typs.length; i++) {
				StaffEntry typen = new StaffEntry(typs[i].getString("type"));
				typen.name = typs[i].getString("name");
				JSONObject[] arts = typs[i].getJSONArray("staffs").toArray(new JSONObject[0]);
				typen.artists = new ReferenceEntry[arts.length];
				for (int j = 0; j < arts.length; j++) {
					typen.artists[j] = Helper.parseValidEntry(arts[j], "id", null);
					if (typen.artists[j] != null)
						typen.artists[j].name = Helper.parseValidString(arts[j], "name");
				}
				en.staff[i] = typen;
			}
		}
		if (cont.containsKey("songTag")) {
			JSONObject[] tags;
			tags = Helper.parseValidArray(cont.getJSONObject("songTag"), "tags", new JSONObject[0]);
			if (tags != null) {
				en.tags = new String[tags.length][];
				for (int i = 0; i < tags.length; i++) {
					en.tags[i] = new String[] {tags[i].getString("name"), tags[i].getString("id")};
				}
			} else {
				en.tags = new String[0][];
			}
		}
		//TODO
		return en;
	}
	
	public static Entry parseEntry(String typ, String dat) {
		switch (typ) {
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
