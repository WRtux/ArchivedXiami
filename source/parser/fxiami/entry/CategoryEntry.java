package fxiami.entry;

import com.alibaba.fastjson.JSONObject;

public class CategoryEntry {
	
	public final Long id;
	
	public String name;
	
	public CategoryEntry(Long id, String n) {
		if (id == null || id == Entry.NULL_INTEGER)
			throw new NullPointerException();
		this.id = id;
		this.name = n;
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
