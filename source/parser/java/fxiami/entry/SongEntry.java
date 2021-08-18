package fxiami.entry;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class SongEntry extends Entry {
	
	protected static final Map<Long, SongEntry> idEntryMap = new HashMap<>();
	protected static final Map<String, SongEntry> sidEntryMap = new HashMap<>();
	
	public static SongEntry getEntry(Long id) {
		return idEntryMap.get(id);
	}
	
	public static SongEntry getEntry(String sid) {
		return sidEntryMap.get(sid);
	}
	
	public static SongEntry getEntry(Long id, String sid) {
		SongEntry en = idEntryMap.get(id);
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
	
	public static SongEntry getExactEntry(Long id, String sid) {
		SongEntry en = idEntryMap.get(id);
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
	
	public static Collection<SongEntry> getAll() {
		Set<SongEntry> set = new HashSet<>(idEntryMap.values());
		set.addAll(sidEntryMap.values());
		return set;
	}
	
	public static SongEntry matchEntry(Long id, String sid) {
		SongEntry en = idEntryMap.get(id);
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
		return new SongEntry(id, sid, true);
	}
	
	public static SongEntry matchDummyEntry(Long id, String sid) {
		SongEntry en = idEntryMap.get(id);
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
		return new SongEntry(id, sid, true);
	}
	
	public String subName;
	public String translation;
	
	public ReferenceEntry artist;
	public ReferenceEntry[] singers;
	public StaffEntry[] staffs;
	
	public ReferenceEntry album;
	
	public Long disc;
	public Long track;
	
	public Long length;
	
	public Long pace;
	
	public Long highlightOffset;
	public Long highlightLength;
	
	public StyleEntry[] styles;
	
	public String[][] tags;
	
	public InfoEntry[] infos;
	
	public LyricEntry[] lyrics;
	
	public Long playCount;
	public Long likeCount;
	public Long commentCount;
	
	protected SongEntry(Long id, String sid, boolean dummy) {
		super(id, sid, dummy);
		if (this.id != null)
			idEntryMap.put(id, this);
		if (this.sid != null)
			sidEntryMap.put(sid, this);
	}
	public SongEntry(Long id, String sid) {
		this(id, sid, false);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject o = super.toJSON();
		Helper.putValidString(o, "subName", this.subName);
		Helper.putValidString(o, "translation", this.translation);
		o.put("artist", this.artist != null ? this.artist.toJSON() : null);
		if (this.singers != null) {
			JSONArray arr = null;
			if (this.singers != Entry.forNullEntry(ReferenceEntry[].class)) {
				arr = new JSONArray(this.singers.length);
				for (ReferenceEntry en : this.singers) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("singers", arr);
		}
		if (this.staffs != null) {
			JSONArray arr = null;
			if (this.staffs != Entry.forNullEntry(StaffEntry[].class)) {
				arr = new JSONArray(this.staffs.length);
				for (StaffEntry en : this.staffs) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("staffs", arr);
		}
		o.put("album", this.album != null ? this.album.toJSON() : null);
		Helper.putValidInteger(o, "disc", this.disc);
		Helper.putValidInteger(o, "track", this.track);
		Helper.putValidInteger(o, "length", this.length);
		Helper.putValidInteger(o, "pace", this.pace);
		Helper.putValidInteger(o, "highlightOffset", this.highlightOffset);
		Helper.putValidInteger(o, "highlightLength", this.highlightLength);
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
		Helper.putValidArray(o, "tags", this.tags);
		if (this.infos != null) {
			JSONArray arr = null;
			if (this.infos != Entry.forNullEntry(InfoEntry[].class)) {
				arr = new JSONArray(this.infos.length);
				for (InfoEntry en : this.infos) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("infos", arr);
		}
		if (this.lyrics != null) {
			JSONArray arr = null;
			if (this.lyrics != Entry.forNullEntry(LyricEntry[].class)) {
				arr = new JSONArray(this.lyrics.length);
				for (LyricEntry en : this.lyrics) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("lyrics", arr);
		}
		Helper.putValidInteger(o, "playCount", this.playCount);
		Helper.putValidInteger(o, "likeCount", this.likeCount);
		Helper.putValidInteger(o, "commentCount", this.commentCount);
		return o;
	}
	
}
