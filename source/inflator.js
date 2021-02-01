"use strict";

function inflateArtists() {
	if (arguments[0]) {
		if (this.type != "albums") {
			let en = parseArtist(JSON.parse(arguments[0]).result.data.artistDetailVO);
			if (en) {
				en.commentSid = inflateComment("artist", this.sid) ? "artist-" + this.sid : null;
				pool.artists.inflate(en);
				queue.status.iterateCount++;
				let o = { artistId: this.sid, category: 0, pagingVO: { page: 1, pageSize: 10 } };
				(en.playCount >= 200000) && (o.pagingVO.pageSize = 30);
				(en.playCount >= 20000) && (o.pagingVO.pageSize = 20);
				queue[0].url = buildRequestURL("/api/album/getArtistAlbums", o);
			} else
				queue.shift();
		} else {
			let cont = JSON.parse(arguments[0]).result.data;
			if (!cont || !cont.pagingVO)
				return;
			let en = pool.artists.get(this.sid);
			en.albumCount = cont.pagingVO.count;
			en.albums = new Array();
			for (let alb of cont.albums) {
				en.albums.push({
					id: alb.albumId, sid: alb.albumStringId, name: alb.albumName,
					category: alb.categoryId, songCount: alb.songCount,
					playCount: alb.playCount, grade: Math.round(alb.grade * 10) || null
				});
				parseAlbum(alb, true);
			}
		}
		return;
	}
	for (let [k, v] of pool.artists) {
		if (v.weight && v.weight <= 10) {
			let func = () => buildRequestURL("/api/artist/getArtistDetail", { artistId: k });
			let ref = v.referrer || base.artist + k;
			queue.push({
				mode: "request", builder: func, referrer: ref, callback: inflateArtists,
				type: "artist", sid: k
			});
			queue.push({
				mode: "request", referrer: ref, callback: inflateArtists,
				type: "albums", sid: k
			});
		}
	}
	window.setTimeout(startQueue);
}

function inflateSongs() {
	if (arguments[0]) {
		let en = parseSong(JSON.parse(arguments[0]).result.data, false);
		if (en) {
			en.commentSid = inflateComment("song", this.sid) ? "song-" + this.sid : null;
			pool.songs.inflate(en);
		}
		queue.status.iterateCount++;
		return;
	}
	for (let [k, v] of pool.songs) {
		if (v.weight && v.weight <= 10) {
			let func = () => buildRequestURL("/api/song/initialize", { songId: k });
			let ref = v.referrer || base.song + k;
			queue.push({
				mode: "request", builder: func, referrer: ref, callback: inflateSongs,
				type: "song", sid: k
			});
		}
	}
	window.setTimeout(startQueue);
}

function inflateComment(typ, id, lim) {
	let en = {
		sid: typ + "-" + id, update: new Date().getTime(), count: undefined,
		recentList: null, hotList: null
	};
	let cont = fetchComment(typ, id, 20);
	if (!cont || !cont.pagingVO)
		return null;
	en.count = cont.pagingVO.count;
	en.recentList = parseComment(cont.commentList);
	if (!lim) {
		lim = 3;
		(en.count >= 1000) && (lim = 10);
		(en.count >= 600) && (lim = 8);
		(en.count >= 300) && (lim = 6);
		(en.count >= 100) && (lim = 4);
	}
	cont = fetchComment(typ, id, -Math.abs(lim)).hotList;
	en.hotList = parseComment(cont);
	pool.comments.inflate(en);
	queue.status.mergeCount += 2;
	queue.status.iterateCount++;
	return en;
}

function parseArtist(dat) {
	(typeof dat == "string") && (dat = JSON.parse(dat));
	if (!dat || !dat.artistId)
		return null;
	let en = {
		id: dat.artistId, sid: dat.artistStringId, update: new Date().getTime(), name: dat.artistName,
		subName: dat.alias || null, logo: dat.artistLogo, description: dat.description,
		category: dat.categoryId, area: dat.area || null, gender: dat.gender, birthday: dat.birthday,
		playCount: dat.playCount, collectCount: dat.countLikes, commentCount: dat.comments,
		roles: new Array(), styles: new Array()
	};
	for (let o of dat.roles || "")
		en.roles.push(o.name);
	for (let o of dat.styles || "")
		en.styles.push({ id: o.styleId, name: o.styleName });
	if (pool.artists.inflate(en))
		queue.status.mergeCount++;
	queue.status.queryCount++;
	return en;
}

function parseAlbum(dat, f) {
	(typeof dat == "string") && (dat = JSON.parse(dat));
	if (!dat || !dat.albumId)
		return null;
	let en = {
		id: dat.albumId, sid: dat.albumStringId, update: new Date().getTime(), name: dat.albumName,
		subName: dat.subName || null, logo: dat.albumLogo,
		category: dat.categoryId, grade: Math.round(dat.grade * 10) || null, language: dat.language,
		artist: null, company: { id: dat.companyId, name: dat.comapny },
		publishTime: dat.gmtPublish
	};
	let ref = base.album + en.sid;
	let o = (dat.artists || "")[0];
	o && (en.artist = { id: o.artistId, sid: o.artistStringId, name: o.artistName });
	o && pool.artists.weigh({ sid: o.artistStringId, referrer: ref, name: o.artistName, weight: 3 });
	if (!f) {
		Object.assign(en, {
			description: dat.description,
			songs: new Array()
		});
		//TODO
	}
	f && (en.weight = 10);
	if (pool.albums.inflate(en))
		queue.status.mergeCount++;
	queue.status.queryCount++;
	return en;
}

function parseSong(dat, f) {
	(typeof dat == "string") && (dat = JSON.parse(dat));
	let cont = f ? dat : dat.songDetail;
	if (!cont || !cont.songId)
		return null;
	let en = {
		id: cont.songId, sid: cont.songStringId, update: new Date().getTime(), name: cont.songName,
		subName: cont.newSubName || cont.subName || null, track: cont.track, length: cont.length,
		artist: null, album: null
	};
	let ref = base.song + en.sid;
	let o = (cont.artistVOs || "")[0];
	o && (en.artist = { id: o.artistId, sid: o.artistStringId, name: o.artistName });
	o && pool.artists.weigh({ sid: o.artistStringId, referrer: ref, name: o.artistName, weight: 3 });
	en.album = { id: cont.albumId, sid: cont.albumStringId, name: cont.albumName };
	pool.albums.weigh({ sid: cont.albumStringId, referrer: ref, name: cont.albumName, weight: 3 });
	if (!f) {
		o && (en.artist.logo = cont.artistLogo);
		en.album.logo = cont.albumLogo;
		Object.assign(en, {
			singers: new Array(), staff: new Array(),
			playCount: cont.playCount, collectCount: cont.favCount, commentCount: undefined,
			info: new Array(), styles: new Array(), tags: new Array(), lyrics: undefined
		});
		let f = (en.playCount >= 20000 || en.favCount >= 100);
		cont = dat.songExt;
		en.commentCount = cont.commentCount;
		for (o of cont.singerVOs || "") {
			en.singers.push({ id: o.artistId, sid: o.artistStringId, name: o.artistName });
			pool.artists.weigh({ sid: o.artistStringId, referrer: ref, name: o.artistName, weight: 2 });
		}
		for (let typ of cont.behindStaffs || "") {
			let typen = { type: typ.type, staff: new Array() };
			for (o of typ.staffs || "")
				typen.staff.push({ id: o.id, name: o.name });
			en.staff.push(typen);
		}
		for (o of cont.songDescs || "")
			en.info.push({ name: o.title, description: o.desc });
		for (o of cont.styles || "")
			en.styles.push({ id: o.styleId, name: o.styleName });
		for (o of cont.songTag.tags || "")
			en.tags.push({ sid: o.id, name: o.name });
		if (f) {
			en.lyrics = new Array();
			for (o of dat.songLyric || "")
				en.lyrics.push({ id: o.id, content: o.content });
		}
	}
	f && (en.weight = 10);
	if (pool.songs.inflate(en))
		queue.status.mergeCount++;
	queue.status.queryCount++;
	return en;
}

function parseCollect(dat) {
	(typeof dat == "string") && (dat = JSON.parse(dat));
	if (!dat || !dat.listId)
		return null;
	let en = {
		id: dat.listId, update: new Date().getTime(), name: dat.collectName,
		logo: dat.collectLogo, description: dat.description, tags: dat.tags,
		user: null,
		createTime: dat.gmtCreate, modifyTime: dat.gmtModify,
		playCount: dat.playCount, collectCount: dat.collects, commentCount: dat.comments,
		songs: new Array(), commentSid: null
	};
	dat.user && (en.user = { id: dat.user.userId, name: dat.user.nickName, avatar: dat.user.avatar });
	for (let i = 0; i < dat.songCount; i++) {
		en.songs.push({ id: dat.allSongs[i], sid: (dat.allSongStringIds || "")[i] });
//		pool.songs.weigh({ sid: dat.allSongStringIds[i], referrer: base.collect + en.id, weight: 6 });
	}
	for (let sng of dat.songs) {
		let o = en.songs.find((o) => (o.id == sng.songId));
		Object.assign(o, {
			sid: sng.songStringId, name: sng.songName,
			description: sng.description, length: sng.length,
			artistSid: sng.artistVOs && sng.artistVOs[0].artistStringId, artistName: sng.artistName,
			albumSid: sng.albumStringId, albumName: sng.albumName
		});
		parseSong(sng, true);
	}
	queue.status.queryCount++;
	return en;
}

function parseComment(dat) {
	(typeof dat == "string") && (dat = JSON.parse(dat));
	if (!dat)
		return null;
	let cont = new Array();
	for (let cmnt of dat) {
		let en = {
			id: cmnt.commentId,
			content: cmnt.message, createTime: cmnt.gmtCreate,
			user: { id: cmnt.userId, name: cmnt.nickName, avatar: cmnt.avatar },
			likeCount: cmnt.likes, replyCount: cmnt.replyNum || 0, replies: new Array()
		};
		for (let rep of cmnt.replyData || "") {
			en.replies.push({
				id: rep.commentId,
				content: rep.message, createTime: rep.gmtCreate,
				user: { id: rep.userId, name: rep.nickName, avatar: rep.avatar },
				likeCount: rep.likes
			});
		}
		cont.push(en);
	}
	queue.status.queryCount++;
	return cont;
}
