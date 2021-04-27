package fxiami.entry;

import java.util.HashMap;
import java.util.Map;

public class ArtistEntry extends Entry {
	
	protected static final Map<Long, ArtistEntry> idEntryMap = new HashMap<>();
	protected static final Map<String, ArtistEntry> sidEntryMap = new HashMap<>();
	
	public static ArtistEntry getEntry(Long id) {
		return idEntryMap.get(id);
	}
	
	public static ArtistEntry getEntry(String sid) {
		return sidEntryMap.get(sid);
	}
	
	public static ArtistEntry matchEntry(Long id, String sid) {
		ArtistEntry[] ens = {getEntry(id), getEntry(sid)};
		if (ens[0] == ens[1]) {
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
	
	public ArtistEntry(Long id, String sid) {
		super(id, sid);
		if (id != null && id != Entry.NULL_INTEGER)
			idEntryMap.put(id, this);
		if (sid != null && sid != Entry.NULL_STRING)
			sidEntryMap.put(sid, this);
	}
	
}
