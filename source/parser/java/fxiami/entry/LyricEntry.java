package fxiami.entry;

import com.alibaba.fastjson.JSONObject;

public class LyricEntry {
	
	public static final String entryName = "lyric";
	
	public final Long id;
	
	public Long update;
	
	public Long type;
	
	public Boolean official;
	
	public String contentURL;
	
	public LyricEntry(Long id, Long typ, String url) {
		this.id = (id != Entry.NULL_INTEGER ? id : null);
		this.type = typ;
		this.contentURL = url;
	}
	public LyricEntry(Long id) {
		this(id, null, null);
	}
	
	public static LyricEntry parseJSON(JSONObject cont) {
		LyricEntry en = new LyricEntry(cont.getLong("id"));
		en.update = Helper.parseValidInteger(cont, "update");
		en.type = Helper.parseValidInteger(cont, "type");
		en.official = Helper.parseValidBoolean(cont, "official");
		en.contentURL = Helper.parseValidString(cont, "contentURL");
		return en;
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject(true);
		o.put("id", this.id);
		Helper.putValidInteger(o, "update", this.update);
		Helper.putValidInteger(o, "type", this.type);
		Helper.putValidBoolean(o, "official", this.official);
		Helper.putValidString(o, "contentURL", this.contentURL);
		return o;
	}
	
}
