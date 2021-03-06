package fxiami.entry;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public abstract class Entry {
	
	public static final Map<Class<?>, Object> nullEntryMap = new HashMap<>();
	
	public static final Long NULL_INTEGER = new Long(0x80000000_00000000L);
	public static final Double NULL_FLOAT = Double.longBitsToDouble(0xFFF80000_00000000L);
	public static final String NULL_STRING = new String(new char[] {0});
	public static final Object[] NULL_OBJECT_ARRAY = new Object[0];
	
	static {
		nullEntryMap.put(Number.class, NULL_INTEGER);
		nullEntryMap.put(Long.class, NULL_INTEGER);
		nullEntryMap.put(Double.class, NULL_FLOAT);
		nullEntryMap.put(String.class, NULL_STRING);
		nullEntryMap.put(Object[].class, NULL_OBJECT_ARRAY);
		nullEntryMap.put(String[].class, new String[0]);
	}
	
	public static Class<? extends Entry> getEntryClass(String typ) {
		switch (typ) {
		case "artist":
			return ArtistEntry.class;
		case "album":
			return AlbumEntry.class;
		case "song":
			return SongEntry.class;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public final Long id;
	public final String sid;
	
	protected final boolean dummy;
	
	public Long update;
	
	public String name;
	
	protected Entry(Long id, String sid, boolean dummy) {
		this.id = (id != Entry.NULL_INTEGER ? id : null);
		this.sid = (sid != Entry.NULL_STRING ? sid : null);
		this.dummy = dummy;
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject(true);
		Helper.putValidEntry(o, this);
		if (!this.dummy)
			Helper.putValidInteger(o, "update", this.update);
		Helper.putValidString(o, "name", this.name);
		return o;
	}
	
}
