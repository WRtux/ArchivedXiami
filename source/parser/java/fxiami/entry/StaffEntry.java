package fxiami.entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class StaffEntry implements EntryPort.Entry {
	
	public static final String entryName = "staff";
	
	public final String type;
	public String name;
	
	public ReferenceEntry[] artists;
	
	public StaffEntry(String typ) {
		if (typ == null || typ == EntryPort.NULL_STRING)
			throw new NullPointerException();
		this.type = typ;
	}
	
	public static StaffEntry parseJSON(JSONObject cont) {
		StaffEntry en = new StaffEntry(cont.getString("type"));
		en.name = Helper.parseValidString(cont, "name");
		if (cont.containsKey("artists"))
			en.artists = EntryPort.parseJSONArray(ReferenceEntry.class, cont.getJSONArray("artists"));
		return en;
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject(true);
		o.put("type", this.type);
		Helper.putValidString(o, "name", this.name);
		if (this.artists != null) {
			JSONArray arr = null;
			if (this.artists != EntryPort.forNullEntry(ReferenceEntry[].class)) {
				arr = new JSONArray(this.artists.length);
				for (ReferenceEntry en : this.artists) {
					arr.add(en != null ? en.toJSON() : null);
				}
			}
			o.put("artists", arr);
		}
		return o;
	}
	
}
