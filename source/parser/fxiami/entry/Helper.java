package fxiami.entry;

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
			arr = o.getJSONArray(k).toArray();
		} catch (RuntimeException ex) {
			System.out.println("Not a valid array: " + String.valueOf(o.get(k)));
		}
		return arr != null ? arr : Entry.NULL_ARRAY;
	}
	
	public static <T> T[] parseValidArray(JSONObject o, String k, T[] dest) {
		if (!o.containsKey(k))
			return null;
		try {
			return o.getJSONArray(k).toArray(dest);
		} catch (RuntimeException ex) {
			System.out.println("Not a valid array: " + String.valueOf(o.get(k)));
			return null;
		}
	}
	
	public static boolean putValidInteger(Number num, JSONObject dest, String k) {
		if (num == null)
			return false;
		dest.put(k, num != Entry.NULL_INTEGER ? num.longValue() : null);
		return true;
	}
	
	public static boolean putValidFloat(Number num, JSONObject dest, String k) {
		if (num == null)
			return false;
		dest.put(k, num != Entry.NULL_FLOAT ? num.doubleValue() : null);
		return true;
	}
	
	public static boolean putValidString(String str, JSONObject dest, String k) {
		if (str == null)
			return false;
		dest.put(k, str != Entry.NULL_STRING ? str : null);
		return true;
	}
	
	public static boolean putValidArray(Object[] arr, JSONObject dest, String k) {
		if (arr == null)
			return false;
		dest.put(k, arr != Entry.NULL_ARRAY ? arr : null);
		return true;
	}
	
	public static boolean putNonNullArray(Object[] arr, JSONObject dest, String k) {
		if (arr != null) {
			dest.put(k, arr != Entry.NULL_ARRAY ? arr : null);
			return arr != Entry.NULL_ARRAY;
		} else {
			dest.put(k, null);
			return false;
		}
	}
	
	@Deprecated
	private Helper() {
		throw new IllegalStateException();
	}
	
}
