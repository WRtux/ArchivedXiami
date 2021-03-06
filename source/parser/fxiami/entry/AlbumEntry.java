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
	
	public ReferenceEntry artist;
	
	protected AlbumEntry(Long id, String sid, boolean dummy) {
		super(id, sid, dummy);
		if (!dummy) {
			if (this.id != null)
				idEntryMap.put(id, this);
			if (this.sid != null)
				sidEntryMap.put(sid, this);
		}
	}
	public AlbumEntry(Long id, String sid) {
		this(id, sid, false);
	}
	
}
