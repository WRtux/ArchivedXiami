package fxiami.entry;

import java.util.HashMap;
import java.util.Map;

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
	
	public static AlbumEntry getExactEntry(Long id, String sid) {
		AlbumEntry[] ens = {idEntryMap.get(id), sidEntryMap.get(sid)};
		AlbumEntry en;
		if (ens[0] == ens[1]) {
			en = ens[1];
		} else {
			if (ens[0] == null) {
				en = ens[1];
			} else if (ens[1] == null) {
				en = ens[0];
			} else {
				System.err.printf("Mismatch for id: %d, sid: %s.%n", id, sid);
				en = null;
			}
		}
		return (en != null && !en.dummy) ? en : null;
	}
	
	public static AlbumEntry matchEntry(Long id, String sid) {
		AlbumEntry[] ens = {idEntryMap.get(id), sidEntryMap.get(sid)};
		if (ens[0] == ens[1]) {
			if (ens[1] == null)
				ens[1] = new AlbumEntry(id, sid, true);
			return ens[1];
		} else {
			if (ens[0] == null) {
				return ens[1];
			} else if (ens[1] == null) {
				return ens[0];
			} else {
				System.err.printf("Mismatch for id: %d, sid: %s.%n", id, sid);
				return ens[1];
			}
		}
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
			if (this.artists != Entry.nullEntryMap.get(ReferenceEntry[].class)) {
				arr = new JSONArray(this.artists.length);
				for (ReferenceEntry en : this.artists) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("artists", arr);
		}
		if (this.companies != null) {
			JSONArray arr = null;
			if (this.artists != Entry.nullEntryMap.get(ReferenceEntry[].class)) {
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
			if (this.styles != Entry.nullEntryMap.get(StyleEntry[].class)) {
				arr = new JSONArray(this.styles.length);
				for (StyleEntry en : this.styles) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("styles", arr);
		}
		if (this.songs != null) {
			JSONArray arr = null;
			if (this.songs != Entry.nullEntryMap.get(ReferenceEntry[].class)) {
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
