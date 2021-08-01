package fxiami;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import fxiami.entry.AlbumEntry;
import fxiami.entry.ArtistEntry;
import fxiami.entry.Entry;
import fxiami.entry.Helper;
import fxiami.entry.InfoEntry;
import fxiami.entry.LyricEntry;
import fxiami.entry.ReferenceEntry;
import fxiami.entry.SongEntry;
import fxiami.entry.StaffEntry;
import fxiami.entry.StyleEntry;

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
			en.update = Helper.parseValidInteger(o, "update");
			en.name = Helper.parseValidString(cont, "artistName");
			System.out.println("Process " + en.name);
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
			en.update = Helper.parseValidInteger(o, "update");
			en.name = Helper.parseValidString(cont, "albumName");
			System.out.println("Process " + en.name);
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
		
		static InfoEntry[] processInfos(JSONObject cont) {
			if (!cont.containsKey("songDescs"))
				return null;
			try {
				JSONObject[] infs = cont.getJSONArray("songDescs").toArray(new JSONObject[0]);
				InfoEntry[] ens = new InfoEntry[infs.length];
				for (int i = 0; i < infs.length; i++) {
					ens[i] = new InfoEntry();
					ens[i].title = Helper.parseValidString(infs[i], "title");
					ens[i].content = Helper.parseValidString(infs[i], "desc");
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid infos: " + String.valueOf(cont.get("songDescs")));
				return Entry.forNullEntry(InfoEntry[].class);
			}
		}
		
		static StyleEntry[] processStyles(JSONObject cont) {
			if (!cont.containsKey("styles"))
				return null;
			try {
				JSONObject[] stys = cont.getJSONArray("styles").toArray(new JSONObject[0]);
				StyleEntry[] ens = new StyleEntry[stys.length];
				for (int i = 0; i < stys.length; i++) {
					try {
						ens[i] = new StyleEntry(stys[i].getLong("styleId"));
						ens[i].name = Helper.parseValidString(stys[i], "styleName");
					} catch (RuntimeException ex) {
						System.out.println("Not a valid style: " + String.valueOf(stys[i]));
					}
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid styles: " + String.valueOf(cont.get("styles")));
				return Entry.forNullEntry(StyleEntry[].class);
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
		
		static LyricEntry[] processLyrics(JSONArray arr) {
			if (arr == null)
				return null;
			try {
				LyricEntry[] ens = new LyricEntry[arr.size()];
				for (int i = 0, len = arr.size(); i < len; i++) {
					try {
						JSONObject o = arr.getJSONObject(i);
						ens[i] = new LyricEntry(o.getLong("id"));
						ens[i].type = Helper.parseValidInteger(o, "type");
						ens[i].official = Helper.parseValidBoolean(o, "flagOfficial");
						ens[i].content = Helper.parseValidString(o, "content");
					} catch (RuntimeException ex) {
						System.out.println("Not a valid style: " + String.valueOf(arr.get(i)));
					}
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid lyrics: " + String.valueOf(arr));
				return Entry.forNullEntry(LyricEntry[].class);
			}
		}
		
		public static SongEntry parseSongEntry(String dat) {
			JSONObject o = JSON.parseObject(dat), cont = o.getJSONObject("songDetail");
			if (cont == null || cont.isEmpty())
				return null;
			SongEntry en = processEntry(cont);
			en.update = Helper.parseValidInteger(o, "update");
			en.name = Helper.parseValidString(cont, "songName");
			System.out.println("Process " + en.name);
			en.subName = Helper.parseValidString(cont, "newSubName");
			if (en.subName == null || en.subName == Entry.NULL_STRING || en.subName.isEmpty())
				en.subName = Helper.parseValidString(cont, "subName");
			if (en.subName != null && en.subName.isEmpty())
				en.subName = Entry.NULL_STRING;
			en.translation = Helper.parseValidString(cont, "translation");
			if (en.translation != null && en.translation.isEmpty())
				en.translation = Entry.NULL_STRING;
			en.artist = processArtist(cont);
			en.album = Helper.parseValidEntry(cont, "albumId", "albumStringId", "albumName");
			en.disc = Helper.parseValidInteger(cont, "cdSerial");
			en.track = Helper.parseValidInteger(cont, "track");
			en.length = Helper.parseValidInteger(cont, "length");
			if (en.length != null && en.length == 0)
				en.length = Entry.NULL_INTEGER;
			en.highlight = Helper.parseValidInteger(cont, "hotPartStartTime");
			en.pace = Helper.parseValidInteger(cont, "pace");
			if (en.pace != null && en.pace == 0)
				en.pace = Entry.NULL_INTEGER;
			en.playCount = Helper.parseValidInteger(cont, "playCount");
			en.likeCount = Helper.parseValidInteger(cont, "favCount");
			cont = o.getJSONObject("songExt");
			if (cont == null || cont.isEmpty())
				return en;
			en.singers = processSingers(cont);
			en.staffs = processStaffs(cont);
			en.infos = processInfos(cont); 
			en.styles = processStyles(cont);
			en.tags = processTags(cont);
			en.commentCount = Helper.parseValidInteger(cont, "commentCount");
			if ((en.playCount != null && en.playCount >= 20000)
					|| (en.likeCount != null && en.likeCount >= 100)) {
				en.lyrics = processLyrics(o.getJSONArray("songLyric"));
				if (en.lyrics != null && en.lyrics.length > 0)
					System.out.println(en.lyrics.length + " lyrics added.");
			}
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
				Entry en = null;
				try {
					en = parseEntry(typ, ln);
				} catch (RuntimeException ex) {
					System.err.println("Unexpected break:");
					ex.printStackTrace();
				}
				if (en != null) {
					System.out.println("Accept " + en.name);
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
	
	public static void exportJSON(String typ, File src, File dest) throws IOException {
		List<Entry> li = parseJSONM(typ, src);
		System.gc();
		System.out.println("JSON ready.");
		System.out.println("Exporting...");
		JSONWriter wtr = new JSONWriter(new OutputStreamWriter(new FileOutputStream(dest), "UTF-8"));
		wtr.config(SerializerFeature.WriteMapNullValue, true);
		try {
			wtr.startArray();
			for (Entry en : li) {
				try {
					wtr.writeValue(en.toJSON());
				} catch (RuntimeException ex) {
					System.err.println("Unexpected break:");
					ex.printStackTrace();
				}
			}
			wtr.endArray();
			System.out.println("Export completed.");
		} catch (Exception ex) {
			System.err.println("Export failed.");
			throw ex;
		} finally {
			wtr.close();
		}
	}
	
	@Deprecated
	private Parser() {
		throw new IllegalStateException();
	}
	
}
