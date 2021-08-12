package fxiami.entry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AlbumEntry extends Entry {
	
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
	
	public String info;
	
	public StyleEntry[] styles;
	
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
	
	@Override
	public JSONObject toJSON() {
		JSONObject o = super.toJSON();
		Helper.putValidString(o, "subName", this.subName);
		Helper.putValidString(o, "logoURL", this.logoURL);
		if (this.artists != null) {
			JSONArray arr = null;
			if (this.artists != Entry.forNullEntry(ReferenceEntry[].class)) {
				arr = new JSONArray(this.artists.length);
				for (ReferenceEntry en : this.artists) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("artists", arr);
		}
		if (this.companies != null) {
			JSONArray arr = null;
			if (this.artists != Entry.forNullEntry(ReferenceEntry[].class)) {
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
		Helper.putValidString(o, "info", this.info);
		if (this.styles != null) {
			JSONArray arr = null;
			if (this.styles != Entry.forNullEntry(StyleEntry[].class)) {
				arr = new JSONArray(this.styles.length);
				for (StyleEntry en : this.styles) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("styles", arr);
		}
		if (this.songs != null) {
			JSONArray arr = null;
			if (this.songs != Entry.forNullEntry(ReferenceEntry[].class)) {
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
