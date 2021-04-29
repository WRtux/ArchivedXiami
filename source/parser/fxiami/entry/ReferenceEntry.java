package fxiami.entry;

public final class ReferenceEntry extends Entry {
	
	public ReferenceEntry(Long id, String sid) {
		super(id, sid, true);
	}
	public ReferenceEntry(Long id, String sid, String n) {
		this(id, sid);
		this.name = n;
	}
	
	public ArtistEntry matchArtistEntry() {
		return ArtistEntry.matchEntry(this.id, this.sid);
	}
	
	public AlbumEntry matchAlbumEntry() {
		return AlbumEntry.matchEntry(this.id, this.sid);
	}
	
	public SongEntry matchSongEntry() {
		return SongEntry.matchEntry(this.id, this.sid);
	}
	
}
