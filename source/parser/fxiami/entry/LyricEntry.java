package fxiami.entry;

import com.alibaba.fastjson.JSONObject;

public class LyricEntry {
	
	public final Long id;
	
	public Long update;
	
	public Long type;
	
	public Boolean official;
	
	public String content;
	
	public LyricEntry(Long id, Long typ, String dat) {
		this.id = (id != Entry.NULL_INTEGER ? id : null);
		this.type = typ;
		this.content = dat;
	}
	public LyricEntry(Long id) {
		this(id, null, null);
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject(true);
		o.put("id", this.id);
		Helper.putValidInteger(o, "update", this.update);
		Helper.putValidInteger(o, "type", this.type);
		Helper.putValidBoolean(o, "official", this.official);
		Helper.putValidString(o, "content", this.content);
		return o;
	}
	
}
