package fxiami.entry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class CategoryEntry implements EntryPort.Entry {
	
	public static final String entryName = "category";
	
	protected static final Map<Long, CategoryEntry> entryMap = new LinkedHashMap<>();
	
	public static CategoryEntry getCategory(Long id, String n) {
		CategoryEntry en = entryMap.get(id);
		if (en != null && n != null && !n.equals(en.name))
			System.err.printf("Category name mismatch for %d, %s, expected %s.%n",
				en.id, n, en.name);
		return en;
	}
	public static CategoryEntry getCategory(Long id) {
		return getCategory(id, null);
	}
	
	public static Collection<CategoryEntry> getAll() {
		return new ArrayList<>(entryMap.values());
	}
	
	public final Long id;
	
	public String name;
	
	public CategoryEntry(Long id, String n) {
		if (id == null || id == EntryPort.NULL_INTEGER)
			throw new NullPointerException();
		this.id = id;
		this.name = n;
		if (entryMap.containsKey(id))
			System.out.printf("Duplicate category: %d, %s%n", id, entryMap.get(id).name);
		entryMap.put(id, this);
	}
	public CategoryEntry(Long id) {
		this(id, null);
	}
	
	public static CategoryEntry parseJSON(JSONObject cont) {
		Long id = cont.getLong("id");
		String n = Helper.parseValidString(cont, "name");
		CategoryEntry en = getCategory(id, n);
		return en != null ? en : new CategoryEntry(id, n);
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject(true);
		o.put("id", this.id);
		Helper.putValidString(o, "name", this.name);
		return o;
	}
	
}
