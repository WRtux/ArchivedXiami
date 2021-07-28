package fxiami.entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public final class Helper {
	
	public static boolean isNullFloat(double num) {
		return Double.doubleToRawLongBits(num) == Double.doubleToRawLongBits(Entry.NULL_FLOAT);
	}
	
	public static Long parseValidInteger(JSONObject o, String k) {
		if (!o.containsKey(k))
			return null;
		Long num = null;
		try {
			num = o.getLong(k);
		} catch (RuntimeException ex) {
			System.out.println("Not an integer: " + String.valueOf(o.get(k)));
		}
		return num != null ? num : Entry.NULL_INTEGER;
	}
	
	public static Double parseValidFloat(JSONObject o, String k) {
		if (!o.containsKey(k))
			return null;
		Double num = null;
		try {
			num = o.getDouble(k);
		} catch (RuntimeException ex) {
			System.out.println("Not a float: " + String.valueOf(o.get(k)));
		}
		return num != null ? num : Entry.NULL_FLOAT;
	}
	
	public static String parseValidString(JSONObject o, String k) {
		if (!o.containsKey(k))
			return null;
		String str = null;
		try {
			str = o.getString(k);
		} catch (RuntimeException ex) {
			System.out.println("Not a string: " + String.valueOf(o.get(k)));
		}
		return str != null ? str : Entry.NULL_STRING;
	}
	
	public static Object[] parseValidArray(JSONObject o, String k) {
		if (!o.containsKey(k))
			return null;
		Object[] arr = null;
		try {
			JSONArray cont = o.getJSONArray(k);
			if (cont != null)
				arr = cont.toArray();
		} catch (RuntimeException ex) {
			System.out.println("Not a valid array: " + String.valueOf(o.get(k)));
		}
		return arr != null ? arr : Entry.NULL_OBJECT_ARRAY;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] parseValidArray(JSONObject o, String k, T[] dest) {
		if (!o.containsKey(k))
			return null;
		T[] arr = null;
		try {
			JSONArray cont = o.getJSONArray(k);
			if (cont != null)
				arr = cont.toArray(dest);
		} catch (RuntimeException ex) {
			System.out.println("Not a valid array: " + String.valueOf(o.get(k)));
		}
		return arr != null ? arr : (T[])Entry.forNullEntry(dest.getClass());
	}
	
	public static ReferenceEntry parseValidEntry(JSONObject o, String idk, String sidk) {
		ReferenceEntry ren = null;
		try {
			Long id = o.getLong(idk);
			String sid = o.getString(sidk);
			if (id != null || sid != null)
				ren = new ReferenceEntry(id, sid);
		} catch (RuntimeException ex) {
			System.out.printf("Not a valid entry: %s, %s%n",
				String.valueOf(o.get(idk)), String.valueOf(o.get(sidk)));
		}
		return ren;
	}
	
	public static ReferenceEntry parseValidEntry(JSONObject o, String idk, String sidk, String nk) {
		ReferenceEntry ren = null;
		try {
			Long id = o.getLong(idk);
			String sid = o.getString(sidk);
			String n = parseValidString(o, nk);
			if (id != null || sid != null || (n != null && n != Entry.NULL_STRING))
				ren = new ReferenceEntry(id, sid, n);
		} catch (RuntimeException ex) {
			System.out.printf("Not a valid entry: %s, %s, %s%n",
				String.valueOf(o.get(idk)), String.valueOf(o.get(sidk)), String.valueOf(o.get(nk)));
		}
		return ren;
	}
	
	public static boolean putValidInteger(JSONObject dest, String k, Number num) {
		if (num == null)
			return false;
		dest.put(k, num != Entry.NULL_INTEGER ? num.longValue() : null);
		return true;
	}
	
	public static boolean putValidFloat(JSONObject dest, String k, Number num) {
		if (num == null)
			return false;
		dest.put(k, num != Entry.NULL_FLOAT ? num.doubleValue() : null);
		return true;
	}
	
	public static boolean putValidString(JSONObject dest, String k, String str) {
		if (str == null)
			return false;
		dest.put(k, str != Entry.NULL_STRING ? str : null);
		return true;
	}
	
	public static boolean putValidArray(JSONObject dest, String k, Object[] arr) {
		if (arr == null)
			return false;
		dest.put(k, arr != Entry.nullEntryMap.get(arr.getClass()) ? arr : null);
		return true;
	}
	
	public static boolean putValidEntry(JSONObject dest, String idk, String sidk, Entry en) {
		if (en == null)
			return false;
		dest.put(idk, en.id);
		dest.put(sidk, en.sid);
		return true;
	}
	public static boolean putValidEntry(JSONObject dest, Entry en) {
		return putValidEntry(dest, "id", "sid", en);
	}
	
	public static JSONObject getValidEntry(String idk, String sidk, Entry en) {
		if (en == null)
			return null;
		JSONObject o = new JSONObject(true);
		o.put(idk, en.id);
		o.put(sidk, en.sid);
		return o;
	}
	public static JSONObject getValidEntry(Entry en) {
		return getValidEntry("id", "sid", en);
	}
	
	@Deprecated
	private Helper() {
		throw new IllegalStateException();
	}
	
}
