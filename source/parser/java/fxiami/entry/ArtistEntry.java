package fxiami.entry;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;

public class ArtistEntry extends MappedEntry {
	
	public static final String entryName = "artist";
	
	protected static final Map<Long, ArtistEntry> idEntryMap = new HashMap<>();
	protected static final Map<String, ArtistEntry> sidEntryMap = new HashMap<>();
	
	public static ArtistEntry getEntry(Long id) {
		return idEntryMap.get(id);
	}
	
	public static ArtistEntry getEntry(String sid) {
		return sidEntryMap.get(sid);
	}
	
	public static ArtistEntry getEntry(Long id, String sid) {
		ArtistEntry en = idEntryMap.get(id);
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
	
	public static ArtistEntry getExactEntry(Long id, String sid) {
		ArtistEntry en = idEntryMap.get(id);
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
	
	public static Collection<ArtistEntry> getAll() {
		Set<ArtistEntry> set = new HashSet<>(idEntryMap.values());
		set.addAll(sidEntryMap.values());
		return set;
	}
	
	public static void clearAll() {
		idEntryMap.clear();
		sidEntryMap.clear();
	}
	
	public static ArtistEntry matchEntry(Long id, String sid) {
		ArtistEntry en = idEntryMap.get(id);
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
		return new ArtistEntry(id, sid, true);
	}
	
	public static ArtistEntry matchDummyEntry(Long id, String sid) {
		ArtistEntry en = idEntryMap.get(id);
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
		return new ArtistEntry(id, sid, true);
	}
	
	public String subName;
	
	public String logoURL;
	
	public String gender;
	
	public Long birthday;
	
	public String area;
	
	public CategoryEntry category;
	
	public StyleEntry[] styles;
	
	public String info;
	
	public Long playCount;
	public Long likeCount;
	public Long commentCount;
	
	protected ArtistEntry(Long id, String sid, boolean dummy) {
		super(id, sid, dummy);
		if (this.id != null)
			idEntryMap.put(id, this);
		if (this.sid != null)
			sidEntryMap.put(sid, this);
	}
	public ArtistEntry(Long id, String sid) {
		this(id, sid, false);
	}
	
	public static ArtistEntry parseJSON(JSONObject cont) {
		ArtistEntry en = new ArtistEntry(cont.getLong("id"), cont.getString("sid"));
		en.update = Helper.parseValidInteger(cont, "update");
		en.name = Helper.parseValidString(cont, "name");
		en.subName = Helper.parseValidString(cont, "subName");
		en.logoURL = Helper.parseValidString(cont, "logoURL");
		en.gender = Helper.parseValidString(cont, "gender");
		en.birthday = Helper.parseValidInteger(cont, "birthday");
		en.area = Helper.parseValidString(cont, "area");
		en.category = EntryPort.parseJSON(CategoryEntry.class, cont.getJSONObject("category"));
		if (cont.containsKey("styles"))
			en.styles = EntryPort.parseJSONArray(StyleEntry.class, cont.getJSONArray("styles"));
		en.info = Helper.parseValidString(cont, "info");
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
		Helper.putValidString(o, "gender", this.gender);
		Helper.putValidInteger(o, "birthday", this.birthday);
		Helper.putValidString(o, "area", this.area);
		o.put("category", EntryPort.toJSON(this.category));
		if (this.styles != null)
			o.put("styles", EntryPort.toJSONArray(this.styles));
		Helper.putValidString(o, "info", this.info);
		Helper.putValidInteger(o, "playCount", this.playCount);
		Helper.putValidInteger(o, "likeCount", this.likeCount);
		Helper.putValidInteger(o, "commentCount", this.commentCount);
		return o;
	}
	
}
