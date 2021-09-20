package fxiami.entry;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public final class EntryPort {
	
	public static interface Entry {}
	
	static final Map<Class<?>, Object> nullEntryMap = new HashMap<>();
	
	public static final Long NULL_INTEGER = new Long(0x80000000_00000000L);
	public static final Double NULL_FLOAT = Double.longBitsToDouble(0xFFF80000_00000000L);
	public static final Boolean NULL_BOOLEAN = new Boolean(false);
	public static final String NULL_STRING = new String(new char[] {0});
	public static final Object[] NULL_OBJECT_ARRAY = new Object[0];
	
	static final Map<String, Class<? extends Entry>> entryClassMap = new HashMap<>();
	
	private static final Map<Class<? extends Entry>, Method> parseMehtodMap = new HashMap<>();
	private static final Map<Class<? extends Entry>, Method> toJSONMethodMap = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	private static final Class<? extends Entry>[] defaultEntryClasses = new Class[] {
		ArtistEntry.class, AlbumEntry.class, SongEntry.class, ReferenceEntry.class,
		CategoryEntry.class, StyleEntry.class, InfoEntry.class, StaffEntry.class, LyricEntry.class};
	static {
		nullEntryMap.put(Number.class, NULL_INTEGER);
		nullEntryMap.put(Long.class, NULL_INTEGER);
		nullEntryMap.put(Double.class, NULL_FLOAT);
		nullEntryMap.put(Boolean.class, NULL_BOOLEAN);
		nullEntryMap.put(String.class, NULL_STRING);
		nullEntryMap.put(Object[].class, NULL_OBJECT_ARRAY);
		for (Class<? extends Entry> cls : defaultEntryClasses) {
			registerEntryClass(cls);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T forNullEntry(Class<T> cls) {
		T o = (T)nullEntryMap.get(cls);
		if (o == null && cls.isArray()) {
			o = (T)Array.newInstance(cls.getComponentType(), 0);
			nullEntryMap.put(cls, o);
		}
		return o;
	}
	
	public static boolean registerEntryClass(String n, Class<? extends Entry> cls) {
		if (entryClassMap.containsKey(cls))
			return false;
		Method smth, meth;
		try {
			if (n == null)
				n = (String)cls.getDeclaredField("entryName").get(null);
			smth = cls.getDeclaredMethod("parseJSON", JSONObject.class);
			meth = cls.getMethod("toJSON");
		} catch (ReflectiveOperationException ex) {
			throw new ClassCastException();
		}
		if (!cls.isAssignableFrom(smth.getReturnType()) || !Modifier.isStatic(smth.getModifiers())
				|| meth.getReturnType() != JSONObject.class)
			throw new ClassCastException();
		entryClassMap.put(n, cls);
		parseMehtodMap.put(cls, smth);
		toJSONMethodMap.put(cls, meth);
		return true;
	}
	public static boolean registerEntryClass(Class<? extends Entry> cls) {
		return registerEntryClass(null, cls);
	}
	
	public static Class<? extends Entry> getEntryClass(String n) {
		return entryClassMap.get(n);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Entry> T parseJSON(Class<T> cls, JSONObject cont) {
		if (cont == null)
			return null;
		try {
			return (T)parseMehtodMap.get(cls).invoke(null, cont);
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException();
		} catch (InvocationTargetException ex) {
			throw (RuntimeException)ex.getCause();
		}
	}
	public static Object parseJSON(String typ, JSONObject cont) {
		return parseJSON(entryClassMap.get(typ), cont);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Entry> T[] parseJSONArray(Class<T> cls, JSONArray arr) {
		T[] ens = null;
		if (arr != null) {
			ens = (T[])Array.newInstance(cls, arr.size());
			for (int i = 0; i < ens.length; i++) {
				ens[i] = parseJSON(cls, arr.getJSONObject(i));
			}
		}
		return ens != null ? ens : (T[])forNullEntry(Array.newInstance(cls, 0).getClass());
	}
	public static Object[] parseJSONArray(String typ, JSONArray arr) {
		return parseJSONArray(entryClassMap.get(typ), arr);
	}
	
	public static JSONObject toJSON(Entry en) {
		if (en == null)
			return null;
		try {
			return (JSONObject)toJSONMethodMap.get(en.getClass()).invoke(en);
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException();
		} catch (InvocationTargetException ex) {
			throw (RuntimeException)ex.getCause();
		}
	}
	
	public static JSONArray toJSONArray(Entry[] ens) {
		if (ens == null || Helper.isNullArray(ens))
			return null;
		JSONArray arr = new JSONArray(ens.length);
		for (Entry en : ens) {
			arr.add(toJSON(en));
		}
		return arr;
	}
	
	@Deprecated
	private EntryPort() {
		throw new IllegalStateException();
	}
	
}
