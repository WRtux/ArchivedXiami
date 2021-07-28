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
	
	public static final class ArtistParser {
		
		static ArtistEntry processEntry(JSONObject cont) {
			Long id = cont.getLong("artistId");
			String sid = cont.getString("artistStringId");
			ArtistEntry en = ArtistEntry.getExactEntry(id, sid);
			if (en == null)
				en = new ArtistEntry(id, sid);
			return en;
		}
		
		public static ArtistEntry parseArtistEntry(String dat) {
			JSONObject o = JSON.parseObject(dat), cont = o.getJSONObject("artistDetail");
			if (cont == null || cont.isEmpty())
				return null;
			ArtistEntry en = processEntry(cont);
			//TODO
			return en;
		}
		
		@Deprecated
		private ArtistParser() {
			throw new IllegalStateException();
		}
		
	}
	
	public static final class AlbumParser {
		
		static AlbumEntry processEntry(JSONObject cont) {
			Long id = cont.getLong("albumId");
			String sid = cont.getString("albumStringId");
			AlbumEntry en = AlbumEntry.getExactEntry(id, sid);
			if (en == null)
				en = new AlbumEntry(id, sid);
			return en;
		}
		
		public static AlbumEntry parseAlbumEntry(String dat) {
			JSONObject o = JSON.parseObject(dat), cont = o.getJSONObject("albumDetail");
			if (cont == null || cont.isEmpty())
				return null;
			AlbumEntry en = processEntry(cont);
			//TODO
			return en;
		}
		
		@Deprecated
		private AlbumParser() {
			throw new IllegalStateException();
		}
		
	}
	
	public static final class SongParser {
		
		static SongEntry processEntry(JSONObject cont) {
			Long id = cont.getLong("songId");
			String sid = cont.getString("songStringId");
			SongEntry en = SongEntry.getExactEntry(id, sid);
			if (en == null)
				en = new SongEntry(id, sid);
			return en;
		}
		
		static ReferenceEntry processArtist(JSONObject cont) {
			JSONArray arr = cont.getJSONArray("artistVOs");
			if (arr == null || arr.isEmpty())
				return null;
			if (arr.size() > 1)
				System.err.println("Multiple artists: " + arr.size());
			return Helper.parseValidEntry(arr.getJSONObject(0),
				"artistId", "artistStringId", "artistName");
		}
		
		static ReferenceEntry[] processSingers(JSONObject cont) {
			if (!cont.containsKey("singerVOs"))
				return null;
			try {
				JSONObject[] arts = cont.getJSONArray("singerVOs").toArray(new JSONObject[0]);
				ReferenceEntry[] ens = new ReferenceEntry[arts.length];
				for (int i = 0; i < arts.length; i++) {
					ens[i] = Helper.parseValidEntry(arts[i],
						"artistId", "artistStringId", "artistName");
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid singers: " + String.valueOf(cont.get("singerVOs")));
				return Entry.forNullEntry(ReferenceEntry[].class);
			}
		}
		
		static StaffEntry[] processStaffs(JSONObject cont) {
			if (!cont.containsKey("behindStaffs"))
				return null;
			try {
				JSONObject[] typs = cont.getJSONArray("behindStaffs").toArray(new JSONObject[0]);
				StaffEntry[] ens = new StaffEntry[typs.length];
				for (int i = 0; i < typs.length; i++) {
					try {
						ens[i] = new StaffEntry(typs[i].getString("type"));
						ens[i].name = Helper.parseValidString(typs[i], "name");
						JSONObject[] arts = typs[i].getJSONArray("staffs").toArray(new JSONObject[0]);
						ens[i].artists = new ReferenceEntry[arts.length];
						for (int j = 0; j < arts.length; j++) {
							ens[i].artists[j] = Helper.parseValidEntry(arts[j], "id", null, "name");
						}
					} catch (RuntimeException ex) {
						System.out.println("Not valid staff: " + String.valueOf(typs[i]));
					}
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid staffs: " + String.valueOf(cont.get("behindStaffs")));
				return Entry.forNullEntry(StaffEntry[].class);
			}
		}
		
		static String[][] processTags(JSONObject cont) {
			if (!cont.containsKey("songTag"))
				return null;
			try {
				cont = cont.getJSONObject("songTag");
				if (!cont.containsKey("tags"))
					return new String[0][];
				JSONObject[] tags = cont.getJSONArray("tags").toArray(new JSONObject[0]);
				String[][] ens = new String[tags.length][];
				for (int i = 0; i < tags.length; i++) {
					try {
						ens[i] = new String[] {tags[i].getString("name"), tags[i].getString("id")};
					} catch (RuntimeException ex) {
						System.out.println("Not a valid tag: " + String.valueOf(tags[i]));
					}
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid tags: " + String.valueOf(cont.get("songTag")));
				return Entry.forNullEntry(String[][].class);
			}
		}
		
		public static SongEntry parseSongEntry(String dat) {
			JSONObject o = JSON.parseObject(dat), cont = o.getJSONObject("songDetail");
			if (cont == null || cont.isEmpty())
				return null;
			SongEntry en = processEntry(cont);
			en.update = Helper.parseValidInteger(o, "update");
			en.name = Helper.parseValidString(cont, "songName");
			en.subName = Helper.parseValidString(cont, "newSubName");
			if (en.subName == null || en.subName == Entry.NULL_STRING)
				en.subName = Helper.parseValidString(cont, "subName");
			if (en.subName != null && en.subName.isEmpty())
				en.subName = Entry.NULL_STRING;
			en.artist = processArtist(cont);
			en.album = Helper.parseValidEntry(cont, "albumId", "albumStringId", "albumName");
			en.disc = Helper.parseValidInteger(cont, "cdSerial");
			en.track = Helper.parseValidInteger(cont, "track");
			en.length = Helper.parseValidInteger(cont, "length");
			en.highlight = Helper.parseValidInteger(cont, "hotPartStartTime");
			en.pace = Helper.parseValidInteger(cont, "pace");
			en.playCount = Helper.parseValidInteger(cont, "playCount");
			en.likeCount = Helper.parseValidInteger(cont, "favCount");
			cont = o.getJSONObject("songExt");
			if (cont == null || cont.isEmpty())
				return en;
			en.singers = processSingers(cont);
			en.staffs = processStaffs(cont);
			en.tags = processTags(cont);
			en.commentCount = Helper.parseValidInteger(cont, "commentCount");
			//TODO
			return en;
		}
		
		@Deprecated
		private SongParser() {
			throw new IllegalStateException();
		}
		
	}
	
	public static Entry parseEntry(String typ, String dat) {
		switch (typ) {
		case "artist":
			return ArtistParser.parseArtistEntry(dat);
		case "album":
			return AlbumParser.parseAlbumEntry(dat);
		case "song":
			return SongParser.parseSongEntry(dat);
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
