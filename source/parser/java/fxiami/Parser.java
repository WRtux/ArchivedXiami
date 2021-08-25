package fxiami;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import fxiami.entry.AlbumEntry;
import fxiami.entry.ArtistEntry;
import fxiami.entry.CategoryEntry;
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
		
		static ArtistEntry processEntry(JSONObject cont, boolean ext) {
			Long id = cont.getLong("artistId");
			String sid = cont.getString("artistStringId");
			ArtistEntry en = null;
			if (ext) {
				en = ArtistEntry.getExactEntry(id, sid);
				if (en == null)
					en = new ArtistEntry(id, sid);
			} else {
				en = ArtistEntry.matchDummyEntry(id, sid);
			}
			return en;
		}
		
		static String processGender(JSONObject cont) {
			if (!cont.containsKey("gender"))
				return null;
			try {
				String str = cont.getString("gender");
				switch (str) {
				case "":
					str = Entry.NULL_STRING;
					break;
				case "M":
				case "F":
				case "B":
				case "N":
					break;
				default:
					throw new RuntimeException();
				}
				return str;
			} catch (RuntimeException ex) {
				System.out.println("Not a valid gender: " + String.valueOf(cont.get("gender")));
				return Entry.NULL_STRING;
			}
		}
		
		static Entry[] processSimilars(String typ, JSONObject cont) {
			String n;
			switch (typ) {
			case "artist":
				n = "similaryArtists";
				break;
			case "album":
				n = "albums";
				break;
			case "song":
				n = "songs";
				break;
			default:
				throw new IllegalArgumentException();
			}
			if (!cont.containsKey(n))
				return null;
			try {
				JSONArray arr = cont.getJSONArray(n);
				Entry[] ens = (Entry[])Array.newInstance(Entry.getEntryClass(typ), arr.size());
				int cnt = 0;
				for (int i = 0; i < ens.length; i++) {
					try {
						ens[i] = parseEntry(typ, arr.getJSONObject(i), false);
						if (ens[i] != null)
							cnt++;
					} catch (RuntimeException ex) {
						System.out.println("Not a valid entry: " + String.valueOf(arr.get(i)));
					}
				}
				System.out.printf("%d/%d %s entries added.%n", cnt, ens.length, typ);
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid entries: " + String.valueOf(cont.get(n)));
				return Entry.forNullEntry(ArtistEntry[].class);
			}
		}
		
		static void processCard(JSONObject cont) {
			if (!cont.containsKey("groupKey"))
				return;
			try {
				String n = cont.getString("groupKey");
				JSONArray arr = cont.getJSONArray("cards");
				if (arr.size() > 1)
					System.err.println("Multiple cards: " + arr.size());
				switch (n) {
				case "ARTIST_SIMILARIES":
					processSimilars("artist", arr.getJSONObject(0));
					break;
				case "ARTIST_ALBUMS":
					processSimilars("album", arr.getJSONObject(0));
					break;
				case "ARTIST_SONGS":
				case "ARTIST_DEMOS":
					processSimilars("song", arr.getJSONObject(0));
					break;
				case "ARTIST_MVS":
					break;
				default:
					System.out.println("Not a valid group name: " + n);
				}
			} catch (RuntimeException ex) {
				System.out.printf("Not a valid card: %s, %s%n",
					String.valueOf(cont.get("groupKey")), String.valueOf(cont.get("cards")));
			}
		}
		
		public static ArtistEntry parseArtistEntry(JSONObject o, boolean ext) {
			JSONObject cont = ext ? o.getJSONObject("artistDetail") : o;
			if (cont == null || cont.isEmpty())
				return null;
			ArtistEntry en = processEntry(cont, ext);
			if (en == null)
				return null;
			en.name = Helper.parseValidString(cont, "artistName");
			if (ext)
				System.out.println("Processing " + en.name + "...");
			en.subName = Helper.parseValidString(cont, "alias");
			if (en.subName != null && en.subName.isEmpty())
				en.subName = Entry.NULL_STRING;
			en.logoURL = Helper.parseValidString(cont, "artistLogo");
			en.gender = processGender(cont);
			en.birthday = Helper.parseValidInteger(cont, "birthday");
			en.area = Helper.parseValidString(cont, "area");
			if (en.area != null && en.area.isEmpty())
				en.area = null;
			en.category = AlbumParser.processCategory(cont);
			en.playCount = Helper.parseValidInteger(cont, "playCount");
			en.likeCount = Helper.parseValidInteger(cont, "countLikes");
			if (!ext)
				return en;
			en.update = Helper.parseValidInteger(o, "update");
			en.styles = SongParser.processStyles(cont);
			en.info = Helper.parseValidString(cont, "description");
			if (en.info != null && en.info.isEmpty())
				en.info = Entry.NULL_STRING;
			en.commentCount = Helper.parseValidInteger(cont, "comments");
			JSONArray arr = o.getJSONArray("artistExt");
			if (arr != null && !arr.isEmpty()) {
				for (int i = 0, len = arr.size(); i < len; i++) {
					cont = arr.getJSONObject(i);
					if (cont != null && !cont.isEmpty())
						processCard(cont);
				}
			}
			return en;
		}
		
		@Deprecated
		private ArtistParser() {
			throw new IllegalStateException();
		}
		
	}
	
	public static final class AlbumParser {
		
		static AlbumEntry processEntry(JSONObject cont, boolean ext) {
			Long id = cont.getLong("albumId");
			String sid = cont.getString("albumStringId");
			AlbumEntry en = null;
			if (ext) {
				en = AlbumEntry.getExactEntry(id, sid);
				if (en == null)
					en = new AlbumEntry(id, sid);
			} else {
				en = AlbumEntry.matchDummyEntry(id, sid);
			}
			return en;
		}
		
		static ReferenceEntry[] processArtists(JSONObject cont) {
			if (!cont.containsKey("artists"))
				return null;
			try {
				JSONArray arr = cont.getJSONArray("artists");
				ReferenceEntry[] ens = new ReferenceEntry[arr.size()];
				for (int i = 0; i < ens.length; i++) {
					try {
						ens[i] = Helper.parseValidEntry(arr.getJSONObject(i),
							"artistId", "artistStringId", "artistName");
					} catch (RuntimeException ex) {
						System.out.println("Not a valid artist: " + String.valueOf(arr.get(i)));
					}
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid artists: " + String.valueOf(cont.get("artists")));
				return Entry.forNullEntry(ReferenceEntry[].class);
			}
		}
		
		static ReferenceEntry[] processCompanies(JSONObject cont) {
			if (!cont.containsKey("companies"))
				return null;
			try {
				JSONArray arr = cont.getJSONArray("companies");
				ReferenceEntry[] ens = new ReferenceEntry[arr.size()];
				for (int i = 0; i < ens.length; i++) {
					try {
						ens[i] = Helper.parseValidEntry(arr.getJSONObject(i), "id", null, "name");
					} catch (RuntimeException ex) {
						System.out.println("Not a valid company: " + String.valueOf(arr.get(i)));
					}
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid companies: " + String.valueOf(cont.get("companies")));
				return Entry.forNullEntry(ReferenceEntry[].class);
			}
		}
		
		static CategoryEntry processCategory(JSONObject cont) {
			try {
				Long id = cont.getLong("categoryId");
				String n = Helper.parseValidString(cont, "albumCategory");
				CategoryEntry en = CategoryEntry.getCategory(id, n != Entry.NULL_STRING ? n : null);
				if (id != null && id != 0 && en == null)
					System.out.printf("No matching category for %d, %s.%n", id, n);
				return en;
			} catch (RuntimeException ex) {
				System.out.printf("Not a valid category: %s, %s%n",
					String.valueOf(cont.get("categoryId")), String.valueOf(cont.get("albumCategory")));
				return null;
			}
		}
		
		static ReferenceEntry[] processSongs(JSONObject cont) {
			if (!cont.containsKey("songs"))
				return null;
			try {
				JSONArray arr = cont.getJSONArray("songs");
				ReferenceEntry[] ens = new ReferenceEntry[arr.size()];
				int cnt = 0;
				for (int i = 0; i < ens.length; i++) {
					try {
						JSONObject o = arr.getJSONObject(i);
						ens[i] = Helper.parseValidEntry(o, "songId", "songStringId", "songName");
						if (parseEntry("song", o, false) != null)
							cnt++;
					} catch (RuntimeException ex) {
						System.out.println("Not a valid song: " + String.valueOf(arr.get(i)));
					}
				}
				System.out.printf("%d/%d songs listed.%n", cnt, ens.length);
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid songs: " + String.valueOf(cont.get("songs")));
				return Entry.forNullEntry(ReferenceEntry[].class);
			}
		}
		
		static AlbumEntry[] processSimilars(JSONObject cont) {
			if (!cont.containsKey("albums"))
				return null;
			try {
				JSONArray arr = cont.getJSONArray("albums");
				AlbumEntry[] ens = new AlbumEntry[arr.size()];
				int cnt = 0;
				for (int i = 0; i < ens.length; i++) {
					try {
						ens[i] = (AlbumEntry)parseEntry("album", arr.getJSONObject(i), false);
						if (ens[i] != null)
							cnt++;
					} catch (RuntimeException ex) {
						System.out.println("Not a valid album: " + String.valueOf(arr.get(i)));
					}
				}
				System.out.printf("%d/%d albums added.%n", cnt, ens.length);
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid albums: " + String.valueOf(cont.get("albums")));
				return Entry.forNullEntry(AlbumEntry[].class);
			}
		}
		
		public static AlbumEntry parseAlbumEntry(JSONObject o, boolean ext) {
			JSONObject cont = ext ? o.getJSONObject("albumDetail") : o;
			if (cont == null || cont.isEmpty())
				return null;
			AlbumEntry en = processEntry(cont, ext);
			if (en == null)
				return null;
			en.name = Helper.parseValidString(cont, "albumName");
			if (ext)
				System.out.println("Processing " + en.name + "...");
			en.subName = Helper.parseValidString(cont, "subName");
			if (en.subName != null && en.subName.isEmpty())
				en.subName = Entry.NULL_STRING;
			en.logoURL = Helper.parseValidString(cont, "albumLogo");
			en.artists = processArtists(cont);
			en.companies = processCompanies(cont);
			en.category = processCategory(cont);
			en.discCount = Helper.parseValidInteger(cont, "cdCount");
			if (en.discCount != null && en.discCount == 0)
				en.discCount = null;
			en.songCount = Helper.parseValidInteger(cont, "songCount");
			en.publishTime = Helper.parseValidInteger(cont, "gmtPublish");
			if (en.publishTime != null && en.publishTime == 0)
				en.publishTime = null;
			en.language = Helper.parseValidString(cont, "language");
			if (en.language != null && en.language.isEmpty())
				en.language = null;
			Double d = Helper.parseValidFloat(cont, "grade");
			if (d != null && d != Entry.NULL_FLOAT) {
				en.grade = Math.round(d.doubleValue() * 10);
			} else if (d == Entry.NULL_FLOAT) {
				en.grade = Entry.NULL_INTEGER;
			}
			en.gradeCount = Helper.parseValidInteger(cont, "gradeCount");
			if (en.grade != null && en.grade == 0 && (en.gradeCount == null || en.gradeCount < 10))
				en.grade = Entry.NULL_INTEGER;
			en.playCount = Helper.parseValidInteger(cont, "playCount");
			en.likeCount = Helper.parseValidInteger(cont, "collects");
			if (!ext)
				return en;
			en.update = Helper.parseValidInteger(o, "update");
			en.styles = SongParser.processStyles(cont);
			en.info = Helper.parseValidString(cont, "description");
			if (en.info != null && en.info.isEmpty())
				en.info = Entry.NULL_STRING;
			en.songs = processSongs(cont);
			en.commentCount = Helper.parseValidInteger(cont, "comments");
			cont = o.getJSONObject("artistAlbums");
			if (cont != null && !cont.isEmpty())
				processSimilars(cont);
			return en;
		}
		
		@Deprecated
		private AlbumParser() {
			throw new IllegalStateException();
		}
		
	}
	
	public static final class SongParser {
		
		static SongEntry processEntry(JSONObject cont, boolean ext) {
			Long id = cont.getLong("songId");
			String sid = cont.getString("songStringId");
			SongEntry en = null;
			if (ext) {
				en = SongEntry.getExactEntry(id, sid);
				if (en == null)
					en = new SongEntry(id, sid);
			} else {
				en = SongEntry.matchDummyEntry(id, sid);
			}
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
				JSONArray arr = cont.getJSONArray("singerVOs");
				ReferenceEntry[] ens = new ReferenceEntry[arr.size()];
				for (int i = 0; i < ens.length; i++) {
					try {
						ens[i] = Helper.parseValidEntry(arr.getJSONObject(i),
							"artistId", "artistStringId", "artistName");
					} catch (RuntimeException ex) {
						System.out.println("Not a valid singer: " + String.valueOf(arr.get(i)));
					}
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid singers: " + String.valueOf(cont.get("singerVOs")));
				return Entry.forNullEntry(ReferenceEntry[].class);
			}
		}
		
		private static StaffEntry processStaff(JSONObject cont) {
			StaffEntry en = new StaffEntry(cont.getString("type"));
			en.name = Helper.parseValidString(cont, "name");
			try {
				JSONArray arr = cont.getJSONArray("staffs");
				en.artists = new ReferenceEntry[arr.size()];
				for (int i = 0; i < en.artists.length; i++) {
					JSONObject o = arr.getJSONObject(i);
					en.artists[i] = Helper.parseValidEntry(o, "id", null, "name");
				}
			} catch (RuntimeException ex) {
				System.out.println("Not valid staff: " + String.valueOf(cont.get("staffs")));
				en.artists = Entry.forNullEntry(ReferenceEntry[].class);
			}
			return en;
		}
		
		static StaffEntry[] processStaffs(JSONObject cont) {
			if (!cont.containsKey("behindStaffs"))
				return null;
			try {
				JSONArray arr = cont.getJSONArray("behindStaffs");
				StaffEntry[] ens = new StaffEntry[arr.size()];
				for (int i = 0; i < ens.length; i++) {
					try {
						ens[i] = processStaff(arr.getJSONObject(i));
					} catch (RuntimeException ex) {
						System.out.println("Not valid staff: " + String.valueOf(arr.get(i)));
					}
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid staffs: " + String.valueOf(cont.get("behindStaffs")));
				return Entry.forNullEntry(StaffEntry[].class);
			}
		}
		
		static StyleEntry[] processStyles(JSONObject cont) {
			if (!cont.containsKey("styles"))
				return null;
			try {
				JSONArray arr = cont.getJSONArray("styles");
				StyleEntry[] ens = new StyleEntry[arr.size()];
				for (int i = 0; i < ens.length; i++) {
					try {
						JSONObject o = arr.getJSONObject(i);
						Long id = o.getLong("styleId");
						String n = Helper.parseValidString(o, "styleName");
						ens[i] = StyleEntry.getStyle(id, n != Entry.NULL_STRING ? n : null);
						if (id != null && id != 0 && ens[i] == null)
							System.out.printf("No matching style for %d, %s.%n", id, n);
					} catch (RuntimeException ex) {
						System.out.println("Not a valid style: " + String.valueOf(arr.get(i)));
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
				JSONArray arr = cont.getJSONArray("tags");
				String[][] ens = new String[arr.size()][];
				for (int i = 0; i < ens.length; i++) {
					try {
						JSONObject o = arr.getJSONObject(i);
						ens[i] = new String[] {o.getString("id"), o.getString("name")};
					} catch (RuntimeException ex) {
						System.out.println("Not a valid tag: " + String.valueOf(arr.get(i)));
					}
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid tags: " + String.valueOf(cont.get("songTag")));
				return Entry.forNullEntry(String[][].class);
			}
		}
		
		static InfoEntry[] processInfos(JSONObject cont) {
			if (!cont.containsKey("songDescs"))
				return null;
			try {
				JSONArray arr = cont.getJSONArray("songDescs");
				InfoEntry[] ens = new InfoEntry[arr.size()];
				for (int i = 0; i < ens.length; i++) {
					try {
						JSONObject o = arr.getJSONObject(i);
						ens[i] = new InfoEntry();
						ens[i].title = Helper.parseValidString(o, "title");
						ens[i].content = Helper.parseValidString(o, "desc");
					} catch (RuntimeException ex) {
						System.out.println("Not valid info: " + String.valueOf(arr.get(i)));
					}
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid infos: " + String.valueOf(cont.get("songDescs")));
				return Entry.forNullEntry(InfoEntry[].class);
			}
		}
		
		static SongEntry[] processSimilars(JSONObject cont) {
			String n = "similarSongsFullInfo";
			if (!cont.containsKey(n))
				return null;
			try {
				JSONArray arr = cont.getJSONArray(n);
				SongEntry[] ens = new SongEntry[arr.size()];
				int cnt = 0;
				for (int i = 0; i < ens.length; i++) {
					try {
						ens[i] = (SongEntry)parseEntry("song", arr.getJSONObject(i), false);
						if (ens[i] != null)
							cnt++;
					} catch (RuntimeException ex) {
						System.out.println("Not a valid song: " + String.valueOf(arr.get(i)));
					}
				}
				System.out.printf("%d/%d songs added.%n", cnt, ens.length);
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid songs: " + String.valueOf(cont.get(n)));
				return Entry.forNullEntry(SongEntry[].class);
			}
		}
		
		static LyricEntry[] processLyrics(JSONObject cont) {
			if (!cont.containsKey("songLyric"))
				return null;
			try {
				JSONArray arr = cont.getJSONArray("songLyric");
				LyricEntry[] ens = new LyricEntry[arr.size()];
				for (int i = 0; i < ens.length; i++) {
					try {
						JSONObject o = arr.getJSONObject(i);
						ens[i] = new LyricEntry(o.getLong("id"));
						ens[i].update = Helper.parseValidInteger(o, "gmtModified");
						ens[i].type = Helper.parseValidInteger(o, "type");
						ens[i].official = Helper.parseValidBoolean(o, "flagOfficial");
						ens[i].contentURL = Helper.parseValidString(o, "lyricUrl");
					} catch (RuntimeException ex) {
						System.out.println("Not a valid lyric: " + String.valueOf(arr.get(i)));
					}
				}
				return ens;
			} catch (RuntimeException ex) {
				System.out.println("Not valid lyrics: " + String.valueOf(cont.get("songLyric")));
				return Entry.forNullEntry(LyricEntry[].class);
			}
		}
		
		public static SongEntry parseSongEntry(JSONObject o, boolean ext) {
			JSONObject cont = ext ? o.getJSONObject("songDetail") : o;
			if (cont == null || cont.isEmpty())
				return null;
			SongEntry en = processEntry(cont, ext);
			if (en == null)
				return null;
			en.name = Helper.parseValidString(cont, "songName");
			if (ext)
				System.out.println("Processing " + en.name + "...");
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
			if (en.disc != null && en.disc == 0)
				en.disc = null;
			en.track = Helper.parseValidInteger(cont, "track");
			if (en.track != null && en.track == 0)
				en.track = Entry.NULL_INTEGER;
			en.length = Helper.parseValidInteger(cont, "length");
			if (en.length != null && en.length == 0)
				en.length = null;
			en.pace = Helper.parseValidInteger(cont, "pace");
			if (en.pace != null && en.pace == 0)
				en.pace = null;
			en.highlightOffset = Helper.parseValidInteger(cont, "hotPartStartTime");
			if (en.highlightOffset != null && en.highlightOffset != Entry.NULL_INTEGER) {
				Long t = Helper.parseValidInteger(cont, "hotPartEndTime");
				if (t != null && t != Entry.NULL_INTEGER)
					t = t > en.highlightOffset ? t - en.highlightOffset : Entry.NULL_INTEGER;
				if (en.highlightOffset == 0 && (t == null || t == Entry.NULL_INTEGER)) {
					en.highlightOffset = null;
				} else {
					en.highlightLength = t;
				}
			}
			en.playCount = Helper.parseValidInteger(cont, "playCount");
			en.likeCount = Helper.parseValidInteger(cont, "favCount");
			if (!ext)
				return en;
			en.update = Helper.parseValidInteger(o, "update");
			cont = o.getJSONObject("songExt");
			if (cont != null && !cont.isEmpty()) {
				en.singers = processSingers(cont);
				en.staffs = processStaffs(cont);
				try {
					if (parseEntry("album", cont.getJSONObject("album"), false) != null)
						System.out.println("Album extension added.");
				} catch (RuntimeException ex) {
					System.out.println("Not a valid album: " + String.valueOf(cont.get("album")));
				}
				en.styles = processStyles(cont);
				en.tags = processTags(cont);
				en.infos = processInfos(cont);
				if (Helper.isEmptyArray(en.infos))
					en.infos = null;
				en.commentCount = Helper.parseValidInteger(cont, "commentCount");
				processSimilars(cont);
			}
			en.lyrics = processLyrics(o);
			return en;
		}
		
		@Deprecated
		private SongParser() {
			throw new IllegalStateException();
		}
		
	}
	
	public static Entry parseEntry(String typ, JSONObject o, boolean ext) {
		switch (typ) {
		case "artist":
			return ArtistParser.parseArtistEntry(o, ext);
		case "album":
			return AlbumParser.parseAlbumEntry(o, ext);
		case "song":
			return SongParser.parseSongEntry(o, ext);
		default:
			throw new IllegalArgumentException();
		}
	}
	public static Entry parseEntry(String typ, String dat) {
		return parseEntry(typ, JSON.parseObject(dat), true);
	}
	
	public static List<Entry> parseJSONM(String typ, File f) throws IOException {
		InputStream in = new FileInputStream(f);
		BufferedReader rdr = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		List<Entry> li = new ArrayList<>();
		try {
			Entry.getEntryClass(typ);
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
					System.out.println("Accepted: " + en.name);
					li.add(en);
				} else {
					System.out.println("Rejected: " + ln);
				}
			}
			System.out.println("Parse completed.");
			return li;
		} catch (Exception ex) {
			System.err.println("Parse failed.");
			throw ex;
		} finally {
			rdr.close();
			System.gc();
		}
	}
	
	static void writeArray(Collection<? extends Entry> co, JSONWriter wtr) {
		wtr.startArray();
		for (Entry en : co) {
			try {
				wtr.writeValue(en.toJSON());
			} catch (RuntimeException ex) {
				System.err.println("Unexpected break:");
				ex.printStackTrace();
			}
		}
		wtr.endArray();
	}
	
	public static void exportJSON(String typ, File src, File dest) throws IOException {
		List<Entry> li = parseJSONM(typ, src);
		JSONWriter wtr = new JSONWriter(new OutputStreamWriter(new FileOutputStream(dest), "UTF-8"));
		wtr.config(SerializerFeature.WriteMapNullValue, true);
		try {
			System.out.println("Exporting...");
			writeArray(li, wtr);
			System.out.println("Export completed.");
		} catch (Exception ex) {
			System.err.println("Export failed.");
			throw ex;
		} finally {
			wtr.close();
			System.gc();
		}
	}
	
	public static void exportJSON(File dest) throws IOException {
		JSONWriter wtr = new JSONWriter(new OutputStreamWriter(new FileOutputStream(dest), "UTF-8"));
		wtr.config(SerializerFeature.WriteMapNullValue, true);
		try {
			System.out.println("Exporting...");
			wtr.startObject();
			wtr.writeKey("artists");
			writeArray(ArtistEntry.getAll(), wtr);
			wtr.writeKey("albums");
			writeArray(AlbumEntry.getAll(), wtr);
			wtr.writeKey("songs");
			writeArray(SongEntry.getAll(), wtr);
			wtr.endObject();
			System.out.println("Export completed.");
		} catch (Exception ex) {
			System.err.println("Export failed.");
			throw ex;
		} finally {
			wtr.close();
			System.gc();
		}
	}
	
	@Deprecated
	private Parser() {
		throw new IllegalStateException();
	}
	
}
