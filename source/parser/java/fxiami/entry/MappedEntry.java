package fxiami.entry;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public abstract class MappedEntry implements EntryPort.Entry {
	
	protected static final Map<Class<?>, Object> nullEntryMap = new HashMap<>();
	
	public static final Long NULL_INTEGER = new Long(0x80000000_00000000L);
	public static final Double NULL_FLOAT = Double.longBitsToDouble(0xFFF80000_00000000L);
	public static final Boolean NULL_BOOLEAN = new Boolean(false);
	public static final String NULL_STRING = new String(new char[] {0});
	public static final Object[] NULL_OBJECT_ARRAY = new Object[0];
	
	static {
		nullEntryMap.put(Number.class, NULL_INTEGER);
		nullEntryMap.put(Long.class, NULL_INTEGER);
		nullEntryMap.put(Double.class, NULL_FLOAT);
		nullEntryMap.put(Boolean.class, NULL_BOOLEAN);
		nullEntryMap.put(String.class, NULL_STRING);
		nullEntryMap.put(Object[].class, NULL_OBJECT_ARRAY);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T forNullEntry(Class<T> cls) {
		T o = (T)nullEntryMap.get(cls);
		if (o == null && cls.isArray()) {
			o = (T)Array.newInstance(cls.getComponentType(), 0);
			nullEntryMap.put(cls, o);
		}
		return o;
	}
	
	public static Class<? extends MappedEntry> getEntryClass(String typ) {
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
	
	public static Collection<? extends MappedEntry> getAll(String typ) {
		switch (typ) {
		case "artist":
			return ArtistEntry.getAll();
		case "album":
			return AlbumEntry.getAll();
		case "song":
			return SongEntry.getAll();
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public static void clearAll() {
		ArtistEntry.clearAll();
		AlbumEntry.clearAll();
		SongEntry.clearAll();
	}
	
	public final Long id;
	public final String sid;
	
	public final boolean dummy;
	
	public Long update;
	
	public String name;
	
	protected MappedEntry(Long id, String sid, boolean dummy) {
		this.id = (id != EntryPort.NULL_INTEGER ? id : null);
		this.sid = (sid != EntryPort.NULL_STRING ? sid : null);
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