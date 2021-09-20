package fxiami.entry;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AlbumEntry extends MappedEntry {
	
	public static final String entryName = "album";
	
	protected static final Map<Long, AlbumEntry> idEntryMap = new HashMap<>();
	protected static final Map<String, AlbumEntry> sidEntryMap = new HashMap<>();
	
	public static AlbumEntry getEntry(Long id) {
		return idEntryMap.get(id);
	}
	
	public static AlbumEntry getEntry(String sid) {
		return sidEntryMap.get(sid);
	}
	
	public static AlbumEntry getEntry(Long id, String sid) {
		AlbumEntry en = idEntryMap.get(id);
		if (en != null) {
			if (sid == null || sid.equals(en.sid))
				return en;
			System.err.printf("SID mismatch for ID: %d, SID: %s, expected %s.%n", id, sid, en.sid);
		}
		en = sidEntryMap.get(sid);
		if (en != null) {
			if (id == null || id.equals(en.id))
				return en;
			System.err.printf("ID mismatch for ID: %d, SID: %s, expected %d.%n", id, sid, en.id);
		}
		return null;
	}
	
	public static AlbumEntry getExactEntry(Long id, String sid) {
		AlbumEntry en = idEntryMap.get(id);
		if (en != null && !en.dummy) {
			if (Objects.equals(en.sid, sid))
				return en;
			System.err.printf("SID mismatch for ID: %d, SID: %s, expected %s.%n", id, sid, en.sid);
		}
		en = sidEntryMap.get(sid);
		if (en != null && !en.dummy) {
			if (Objects.equals(en.id, id))
				return en;
			System.err.printf("ID mismatch for ID: %d, SID: %s, expected %d.%n", id, sid, en.id);
		}
		return null;
	}
	
	public static Collection<AlbumEntry> getAll() {
		Set<AlbumEntry> set = new HashSet<>(idEntryMap.values());
		set.addAll(sidEntryMap.values());
		return set;
	}
	
	public static void clearAll() {
		idEntryMap.clear();
		sidEntryMap.clear();
	}
	
	public static AlbumEntry matchEntry(Long id, String sid) {
		AlbumEntry en = idEntryMap.get(id);
		if (en != null) {
			if (sid == null || sid.equals(en.sid))
				return en;
			System.err.printf("SID mismatch for ID: %d, SID: %s, expected %s.%n", id, sid, en.sid);
			if (!en.dummy)
				return null;
		}
		en = sidEntryMap.get(sid);
		if (en != null) {
			if (id == null || id.equals(en.id))
				return en;
			System.err.printf("ID mismatch for ID: %d, SID: %s, expected %d.%n", id, sid, en.id);
			if (!en.dummy)
				return null;
		}
		return new AlbumEntry(id, sid, true);
	}
	
	public static AlbumEntry matchDummyEntry(Long id, String sid) {
		AlbumEntry en = idEntryMap.get(id);
		if (en != null) {
			if (!en.dummy)
				return null;
			if (sid == null || sid.equals(en.sid))
				return en;
			System.err.printf("SID mismatch for ID: %d, SID: %s, expected %s.%n", id, sid, en.sid);
		}
		en = sidEntryMap.get(sid);
		if (en != null) {
			if (!en.dummy)
				return null;
			if (id == null || id.equals(en.id))
				return en;
			System.err.printf("ID mismatch for ID: %d, SID: %s, expected %d.%n", id, sid, en.id);
		}
		return new AlbumEntry(id, sid, true);
	}
	
	public String subName;
	
	public String logoURL;
	
	public ReferenceEntry[] artists;
	public ReferenceEntry[] companies;
	
	public CategoryEntry category;
	
	public Long discCount;
	public Long songCount;
	
	public Long publishTime;
	
	public String language;
	
	public StyleEntry[] styles;
	
	public String info;
	
	public ReferenceEntry[] songs;
	
	public Long grade;
	public Long gradeCount;
	
	public Long playCount;
	public Long likeCount;
	public Long commentCount;
	
	protected AlbumEntry(Long id, String sid, boolean dummy) {
		super(id, sid, dummy);
		if (this.id != null)
			idEntryMap.put(id, this);
		if (this.sid != null)
			sidEntryMap.put(sid, this);
	}
	public AlbumEntry(Long id, String sid) {
		this(id, sid, false);
	}
	
	public static AlbumEntry parseJSON(JSONObject cont) {
		AlbumEntry en = new AlbumEntry(cont.getLong("id"), cont.getString("sid"));
		en.update = Helper.parseValidInteger(cont, "update");
		en.name = Helper.parseValidString(cont, "name");
		en.subName = Helper.parseValidString(cont, "subName");
		en.logoURL = Helper.parseValidString(cont, "logoURL");
		if (cont.containsKey("artists"))
			en.artists = EntryPort.parseJSONArray(ReferenceEntry.class, cont.getJSONArray("artists"));
		if (cont.containsKey("companies"))
			en.companies = EntryPort.parseJSONArray(ReferenceEntry.class, cont.getJSONArray("companies"));
		en.category = EntryPort.parseJSON(CategoryEntry.class, cont.getJSONObject("category"));
		en.discCount = Helper.parseValidInteger(cont, "discCount");
		en.songCount = Helper.parseValidInteger(cont, "songCount");
		en.publishTime = Helper.parseValidInteger(cont, "publishTime");
		en.language = Helper.parseValidString(cont, "language");
		if (cont.containsKey("styles"))
			en.styles = EntryPort.parseJSONArray(StyleEntry.class, cont.getJSONArray("styles"));
		en.info = Helper.parseValidString(cont, "info");
		if (cont.containsKey("songs"))
			en.songs = EntryPort.parseJSONArray(ReferenceEntry.class, cont.getJSONArray("songs"));
		en.grade = Helper.parseValidInteger(cont, "grade");
		en.gradeCount = Helper.parseValidInteger(cont, "gradeCount");
		en.playCount = Helper.parseValidInteger(cont, "playCount");
		en.likeCount = Helper.parseValidInteger(cont, "likeCount");
		en.commentCount = Helper.parseValidInteger(cont, "commentCount");
		return en;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject o = super.toJSON();
		Helper.putValidString(o, "subName", this.subName);
		Helper.putValidString(o, "logoURL", this.logoURL);
		if (this.artists != null) {
			JSONArray arr = null;
			if (this.artists != EntryPort.forNullEntry(ReferenceEntry[].class)) {
				arr = new JSONArray(this.artists.length);
				for (ReferenceEntry en : this.artists) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("artists", arr);
		}
		if (this.companies != null) {
			JSONArray arr = null;
			if (this.artists != EntryPort.forNullEntry(ReferenceEntry[].class)) {
				arr = new JSONArray(this.companies.length);
				for (ReferenceEntry en : this.companies) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("companies", arr);
		}
		o.put("category", this.category != null ? this.category.toJSON() : null);
		Helper.putValidInteger(o, "discCount", this.discCount);
		Helper.putValidInteger(o, "songCount", this.songCount);
		Helper.putValidInteger(o, "publishTime", this.publishTime);
		Helper.putValidString(o, "language", this.language);
		if (this.styles != null) {
			JSONArray arr = null;
			if (this.styles != EntryPort.forNullEntry(StyleEntry[].class)) {
				arr = new JSONArray(this.styles.length);
				for (StyleEntry en : this.styles) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("styles", arr);
		}
		Helper.putValidString(o, "info", this.info);
		if (this.songs != null) {
			JSONArray arr = null;
			if (this.songs != EntryPort.forNullEntry(ReferenceEntry[].class)) {
				arr = new JSONArray(this.songs.length);
				for (ReferenceEntry en : this.songs) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("songs", arr);
		}
		Helper.putValidInteger(o, "grade", this.grade);
		Helper.putValidInteger(o, "gradeCount", this.gradeCount);
		Helper.putValidInteger(o, "playCount", this.playCount);
		Helper.putValidInteger(o, "likeCount", this.likeCount);
		Helper.putValidInteger(o, "commentCount", this.commentCount);
		return o;
	}
	
}
