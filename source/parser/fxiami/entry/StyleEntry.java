package fxiami.entry;

import com.alibaba.fastjson.JSONObject;

public class StyleEntry {
	
	public final Long genre;
	public final Long id;
	
	public String name;
	
	public StyleEntry(Long gen, Long id, String n) {
		if (id == null || id == Entry.NULL_INTEGER)
			throw new NullPointerException();
		this.genre = (gen != Entry.NULL_INTEGER ? gen : null);
		this.id = id;
		this.name = n;
	}
	public StyleEntry(Long gen, Long id) {
		this(gen, id, null);
	}
	public StyleEntry(Long id) {
		this(null, id);
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject(true);
		if (this.genre != null)
			o.put("genre", this.genre);
		o.put("id", this.id);
		Helper.putValidString(o, "name", this.name);
		return o;
	}
	
}
