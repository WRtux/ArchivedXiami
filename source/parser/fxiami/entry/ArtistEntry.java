package fxiami.entry;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class ArtistEntry extends Entry {
	
	protected static final Map<Long, ArtistEntry> idEntryMap = new HashMap<>();
	protected static final Map<String, ArtistEntry> sidEntryMap = new HashMap<>();
	
	public static ArtistEntry getEntry(Long id) {
		return idEntryMap.get(id);
	}
	
	public static ArtistEntry getEntry(String sid) {
		return sidEntryMap.get(sid);
	}
	
	public static ArtistEntry getExactEntry(Long id, String sid) {
		ArtistEntry[] ens = {idEntryMap.get(id), sidEntryMap.get(sid)};
		ArtistEntry en;
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
	
	public static ArtistEntry matchEntry(Long id, String sid) {
		ArtistEntry[] ens = {idEntryMap.get(id), sidEntryMap.get(sid)};
		if (ens[0] == ens[1]) {
			if (ens[1] == null)
				ens[1] = new ArtistEntry(id, sid, true);
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
	
	@Override
	public JSONObject toJSON() {
		JSONObject o = super.toJSON();
		Helper.putValidString(o, "subName", this.subName);
		Helper.putValidString(o, "logoURL", this.logoURL);
		//TODO
		Helper.putValidInteger(o, "playCount", this.playCount);
		Helper.putValidInteger(o, "likeCount", this.likeCount);
		Helper.putValidInteger(o, "commentCount", this.commentCount);
		return o;
	}
	
}
