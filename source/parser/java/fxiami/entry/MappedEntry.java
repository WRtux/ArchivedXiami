package fxiami.entry;

import java.lang.reflect.Method;
import java.util.Collection;

import com.alibaba.fastjson.JSONObject;

public abstract class MappedEntry implements EntryPort.Entry {
	
	public static Class<? extends MappedEntry> getEntryClass(String typ) {
		Class<? extends EntryPort.Entry> cls = EntryPort.getEntryClass(typ);
		return (cls != null && MappedEntry.class.isAssignableFrom(cls)) ?
			cls.asSubclass(MappedEntry.class) : null;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<? extends MappedEntry> getAll(String typ) {
		Class<? extends MappedEntry> cls = getEntryClass(typ);
		if (cls == null)
			throw new IllegalArgumentException("Not a mapped entry type.");
		Method meth = EntryPort.getClassMethod(cls, "getAll", true, Collection.class);
		try {
			return meth != null ? (Collection<? extends MappedEntry>)meth.invoke(null) : null;
		} catch (ReflectiveOperationException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	public static void clearAll() {
		for (Class<? extends EntryPort.Entry> cls : EntryPort.entryClassMap.values()) {
			if (!MappedEntry.class.isAssignableFrom(cls))
				continue;
			Method meth = EntryPort.getClassMethod(cls, "clearAll", true, void.class);
			if (meth != null) {
				try {
					meth.invoke(null);
				} catch (ReflectiveOperationException ex) {
					throw new IllegalStateException(ex);
				}
			}
		}
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
