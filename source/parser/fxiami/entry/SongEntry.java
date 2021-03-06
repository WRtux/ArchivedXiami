package fxiami.entry;

import java.util.HashMap;
import java.util.Map;

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
	
	public static SongEntry getExactEntry(Long id, String sid) {
		SongEntry[] ens = {idEntryMap.get(id), sidEntryMap.get(sid)};
		SongEntry en;
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
	
	public static SongEntry matchEntry(Long id, String sid) {
		SongEntry[] ens = {idEntryMap.get(id), sidEntryMap.get(sid)};
		if (ens[0] == ens[1]) {
			if (ens[1] == null)
				ens[1] = new SongEntry(id, sid, true);
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
	
	public ReferenceEntry artist;
	public StaffEntry[] staff;
	
	public ReferenceEntry album;
	
	public Long disc;
	public Long track;
	
	public Long length;
	public Long highlight;
	
	public Long pace;
	
	public String[][] tags;
	
	public Long playCount;
	public Long likeCount;
	
	protected SongEntry(Long id, String sid, boolean dummy) {
		super(id, sid, dummy);
		if (!dummy) {
			if (this.id != null)
				idEntryMap.put(id, this);
			if (this.sid != null)
				sidEntryMap.put(sid, this);
		}
	}
	public SongEntry(Long id, String sid) {
		this(id, sid, false);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject o = super.toJSON();
		Helper.putValidString(o, "subName", this.subName);
		o.put("artist", this.artist != null ? this.artist.toJSON() : null);
		if (this.staff != null) {
			JSONObject[] typs = new JSONObject[this.staff.length];
			for (int i = 0; i < typs.length; i++) {
				typs[i] = this.staff[i].toJSON();
			}
			o.put("staff", this.staff != Entry.nullEntryMap.get(StaffEntry[].class) ? typs : null);
		}
		o.put("album", this.album != null ? this.album.toJSON() : null);
		Helper.putValidInteger(o, "disc", this.disc);
		Helper.putValidInteger(o, "track", this.track);
		Helper.putValidInteger(o, "length", this.length);
		Helper.putValidInteger(o, "highlight", this.highlight);
		Helper.putValidInteger(o, "pace", this.pace);
		Helper.putValidArray(o, "tags", this.tags);
		Helper.putValidInteger(o, "playCount", this.playCount);
		Helper.putValidInteger(o, "likeCount", this.likeCount);
		//TODO
		return o;
	}
	
}
