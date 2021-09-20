package fxiami.entry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class StyleEntry implements EntryPort.Entry {
	
	public static final String entryName = "style";
	
	protected static final Map<Long, StyleEntry> entryMap = new LinkedHashMap<>();
	
	public static StyleEntry getStyle(Long id, String n) {
		StyleEntry en =  entryMap.get(id);
		if (en != null && n != null && !n.equals(en.name))
			System.err.printf("Style name mismatch for %d-%d, %s, expected %s.%n",
				en.genre, en.id, n, en.name);
		return en;
	}
	public static StyleEntry getStyle(Long id) {
		return getStyle(id, null);
	}
	
	public static Collection<StyleEntry> getAll() {
		return new ArrayList<>(entryMap.values());
	}
	
	public final Long genre;
	public final Long id;
	
	public String name;
	
	public StyleEntry(Long gen, Long id, String n) {
		if (id == null || id == EntryPort.NULL_INTEGER)
			throw new NullPointerException();
		this.genre = (gen != EntryPort.NULL_INTEGER ? gen : null);
		this.id = id;
		this.name = n;
		if (entryMap.containsKey(id))
			System.out.printf("Duplicate style: %d-%d, %s%n", gen, id, entryMap.get(id).name);
		entryMap.put(id, this);
	}
	public StyleEntry(Long gen, Long id) {
		this(gen, id, null);
	}
	
	public static StyleEntry parseJSON(JSONObject cont) {
		Long gen = cont.getLong("genre"), id = cont.getLong("id");
		String n = Helper.parseValidString(cont, "name");
		StyleEntry en = getStyle(id, n);
		return en != null ? en : new StyleEntry(gen, id, n);
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject(true);
		o.put("genre", this.genre);
		o.put("id", this.id);
		Helper.putValidString(o, "name", this.name);
		return o;
	}
	
}
