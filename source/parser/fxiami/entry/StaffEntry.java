package fxiami.entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class StaffEntry {
	
	public final String type;
	public String name;
	
	public ReferenceEntry[] artists;
	
	public StaffEntry(String typ) {
		if (typ == null || typ == Entry.NULL_STRING)
			throw new NullPointerException();
		this.type = typ;
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject(true);
		o.put("type", this.type);
		Helper.putValidString(o, "name", this.name);
		if (this.artists != null) {
			JSONArray arr = new JSONArray();
			for (ReferenceEntry en : this.artists) {
				if (en != null)
					arr.add(en.toJSON());
			}
			o.put("artists", arr);
		} else {
			o.put("artists", null);
		}
		return o;
	}
	
}
