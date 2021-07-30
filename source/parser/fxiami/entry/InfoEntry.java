package fxiami.entry;

import com.alibaba.fastjson.JSONObject;

public class InfoEntry {
	
	public String title;
	
	public String content;
	
	public InfoEntry(String tt, String dat) {
		this.title = tt;
		this.content = dat;
	}
	public InfoEntry() {
		this(null, null);
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject(true);
		Helper.putValidString(o, "title", this.title);
		Helper.putValidString(o, "content", this.content);
		return o;
	}
	
}
