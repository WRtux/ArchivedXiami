package fxiami.entry;

import com.alibaba.fastjson.JSONObject;

public class LyricEntry {
	
	public final Long id;
	
	public String content;
	
	public LyricEntry(Long id, String dat) {
		this.id = (id != Entry.NULL_INTEGER ? id : null);
		this.content = dat;
	}
	public LyricEntry(Long id) {
		this(id, null);
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject(true);
		o.put("id", this.id);
		Helper.putValidString(o, "content", this.content);
		return o;
	}
	
}
