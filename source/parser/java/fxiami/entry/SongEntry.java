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
	
	public static final String entryName = "song";
	
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
	
	public static void clearAll() {
		idEntryMap.clear();
		sidEntryMap.clear();
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
	
	public static SongEntry parseJSON(JSONObject cont) {
		SongEntry en = new SongEntry(cont.getLong("id"), cont.getString("sid"));
		en.update = Helper.parseValidInteger(cont, "update");
		en.name = Helper.parseValidString(cont, "name");
		en.subName = Helper.parseValidString(cont, "subName");
		en.translation = Helper.parseValidString(cont, "translation");
		en.artist = EntryPort.parseJSON(ReferenceEntry.class, cont.getJSONObject("artist"));
		if (cont.containsKey("singers"))
			en.singers = EntryPort.parseJSONArray(ReferenceEntry.class, cont.getJSONArray("singers"));
		if (cont.containsKey("staffs"))
			en.staffs = EntryPort.parseJSONArray(StaffEntry.class, cont.getJSONArray("staffs"));
		en.album = EntryPort.parseJSON(ReferenceEntry.class, cont.getJSONObject("album"));
		en.disc = Helper.parseValidInteger(cont, "disc");
		en.track = Helper.parseValidInteger(cont, "track");
		en.length = Helper.parseValidInteger(cont, "length");
		en.pace = Helper.parseValidInteger(cont, "pace");
		en.highlightOffset = Helper.parseValidInteger(cont, "highlightOffset");
		en.highlightLength = Helper.parseValidInteger(cont, "highlightLength");
		if (cont.containsKey("styles"))
			en.styles = EntryPort.parseJSONArray(StyleEntry.class, cont.getJSONArray("styles"));
		if (cont.containsKey("tags")) {
			String[][] tags = cont.getObject("tags", String[][].class);
			en.tags = (tags != null) ? tags : forNullEntry(String[][].class);
		}
		if (cont.containsKey("infos"))
			en.infos = EntryPort.parseJSONArray(InfoEntry.class, cont.getJSONArray("infos"));
		if (cont.containsKey("lyrics"))
			en.lyrics = EntryPort.parseJSONArray(LyricEntry.class, cont.getJSONArray("lyrics"));
		en.playCount = Helper.parseValidInteger(cont, "playCount");
		en.likeCount = Helper.parseValidInteger(cont, "likeCount");
		en.commentCount = Helper.parseValidInteger(cont, "commentCount");
		return en;
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
