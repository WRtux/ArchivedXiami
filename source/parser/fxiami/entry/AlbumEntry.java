package fxiami.entry;

import java.util.HashMap;
import java.util.Map;

public class AlbumEntry extends Entry {
	
	protected static final Map<Long, AlbumEntry> idEntryMap = new HashMap<>();
	protected static final Map<String, AlbumEntry> sidEntryMap = new HashMap<>();
	
	public static AlbumEntry getEntry(Long id) {
		return idEntryMap.get(id);
	}
	
	public static AlbumEntry getEntry(String sid) {
		return sidEntryMap.get(sid);
	}
	
	public static AlbumEntry matchEntry(Long id, String sid) {
		AlbumEntry[] ens = {getEntry(id), getEntry(sid)};
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
	
	public ArtistEntry artist;
	
	public AlbumEntry(Long id, String sid) {
		super(id, sid);
		if (id != null && id != Entry.NULL_INTEGER)
			idEntryMap.put(id, this);
		if (sid != null && sid != Entry.NULL_STRING)
			sidEntryMap.put(sid, this);
	}
	
}
