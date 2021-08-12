package fxiami.entry;

public final class ReferenceEntry extends Entry {
	
	public ReferenceEntry(Long id, String sid) {
		super(id, sid, true);
	}
	public ReferenceEntry(Long id, String sid, String n) {
		this(id, sid);
		this.name = n;
	}
	
	public ArtistEntry getArtistEntry() {
		return ArtistEntry.getEntry(this.id, this.sid);
	}
	public ArtistEntry matchArtistEntry() {
		return ArtistEntry.matchEntry(this.id, this.sid);
	}
	
	public AlbumEntry getAlbumEntry() {
		return AlbumEntry.getEntry(this.id, this.sid);
	}
	public AlbumEntry matchAlbumEntry() {
		return AlbumEntry.matchEntry(this.id, this.sid);
	}
	
	public SongEntry getSongEntry() {
		return SongEntry.getEntry(this.id, this.sid);
	}
	public SongEntry matchSongEntry() {
		return SongEntry.matchEntry(this.id, this.sid);
	}
	
	public Entry getEntry(String typ) {
		switch (typ) {
		case "artist":
			return this.getArtistEntry();
		case "album":
			return this.getAlbumEntry();
		case "song":
			return this.getSongEntry();
		default:
			throw new IllegalArgumentException();
		}
	}
	
}
