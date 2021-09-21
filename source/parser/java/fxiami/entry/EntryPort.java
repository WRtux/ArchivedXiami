package fxiami.entry;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
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
	
	@SuppressWarnings("unchecked")
	static <T> T getClassConstant(Class<?> cls, String n, Class<T> typ) {
		try {
			Field var = cls.getDeclaredField(n);
			if (!Modifier.isStatic(var.getModifiers()) || !typ.isAssignableFrom(var.getType()))
				throw new ClassCastException("Malformed constant: " + var.toString());
			return (T)var.get(null);
		} catch (NoSuchFieldException ex) {
			return null;
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	static Method getClassMethod(Class<?> cls, String n, boolean typ, Class<?> ret, Class<?>... params) {
		Method meth;
		try {
			meth = typ ? cls.getDeclaredMethod(n, params) : cls.getMethod(n, params);
		} catch (NoSuchMethodException ex) {
			return null;
		}
		if (Modifier.isStatic(meth.getModifiers()) ^ typ || !ret.isAssignableFrom(meth.getReturnType()))
			throw new ClassCastException("Malformed method: " + meth.toString());
		return meth;
	}
	
	public static boolean registerEntryClass(String n, Class<? extends Entry> cls) {
		if (entryClassMap.containsValue(cls))
			return false;
		n = (n != null) ? n : getClassConstant(cls, "entryName", String.class);
		if (n == null)
			throw new NullPointerException();
		entryClassMap.put(n, cls);
		parseMehtodMap.put(cls, getClassMethod(cls, "parseJSON", true, cls, JSONObject.class));
		toJSONMethodMap.put(cls, getClassMethod(cls, "toJSON", false, JSONObject.class));
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
		} catch (ReflectiveOperationException ex) {
			throw new IllegalStateException(ex);
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
		} catch (ReflectiveOperationException ex) {
			throw new IllegalStateException(ex);
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
