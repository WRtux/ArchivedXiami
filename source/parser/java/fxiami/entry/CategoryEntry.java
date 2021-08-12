package fxiami.entry;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class CategoryEntry {
	
	protected static final Map<Long, CategoryEntry> entryMap = new HashMap<>();
	
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
	
	public final Long id;
	
	public String name;
	
	public CategoryEntry(Long id, String n) {
		if (id == null || id == Entry.NULL_INTEGER)
			throw new NullPointerException();
		this.id = id;
		this.name = n;
		if (entryMap.containsKey(id))
			System.out.printf("Duplicate category: %d, %s.%n", id, entryMap.get(id).name);
		entryMap.put(id, this);
	}
	public CategoryEntry(Long id) {
		this(id, null);
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject(true);
		o.put("id", this.id);
		Helper.putValidString(o, "name", this.name);
		return o;
	}
	
}