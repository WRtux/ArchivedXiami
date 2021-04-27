package fxiami.entry;

public abstract class Entry {
	
	public static final Long NULL_INTEGER = new Long(0x80000000_00000000L);
	public static final Double NULL_FLOAT = Double.longBitsToDouble(0xFFF80000_00000000L);
	public static final String NULL_STRING = new String(new char[] {0});
	
	public static final Object[] NULL_ARRAY = new Void[0];
	
	public static Class<? extends Entry> getEntryClass(String typ) {
		switch (typ) {
		case "artist":
			return ArtistEntry.class;
		case "album":
			return AlbumEntry.class;
		case "song":
			return SongEntry.class;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public final Long id;
	public final String sid;
	
	public String name;
	
	public Entry(Long id, String sid) {
		this.id = id;
		this.sid = sid;
	}
	
}
