"use strict";

function inflateSongs() {
	let str = arguments[0];
	if (str) {
		let en = inflateSong(JSON.parse(str).result.data, false);
		if (en) {
			en.lyrics && inflateComments("song", this.sid);
			en.lyrics && (en.commentSid = "song-" + this.sid);
			pool.songs.inflate(en);
		}
		queue.status.iterateCount++;
	} else
		setTimeout(startQueue);
	for (let [k, v] of pool.songs) {
		if (v.weight && v.weight <= 10) {
			let url = buildRequestURL("/api/song/initialize", { songId: k });
			let ref = v.referrer || base.song + k;
			queue.push({
				mode: "request", url: url, referrer: ref, callback: inflateSongs,
				sid: k
			});
			break;
		}
	}
}

function inflateComments(typ, id, lim) {
	let en = {
		sid: typ + "-" + id, update: new Date().getTime(), count: undefined,
		recentList: new Array(), hotList: new Array()
	};
	let cont = fetchComment(typ, id, 20);
	en.count = cont.pagingVO.count;
	en.recentList = parseComment(cont.commentList);
	if (!lim) {
		lim = 3;
		(en.count > 1000) && (lim = 10);
		(en.count > 600) && (lim = 8);
		(en.count > 300) && (lim = 6);
		(en.count > 100) && (lim = 4);
	}
	cont = fetchComment(typ, id, -Math.abs(lim)).hotList;
	en.hotList = parseComment(cont);
	pool.comments.inflate(en);
	queue.status.mergeCount += 2;
	queue.status.iterateCount++;
	return en;
}

function inflateSong(dat, f) {
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
			info: new Array(), lyrics: undefined, styles: new Array(), tags: new Array()
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
	if (!dat.listId)
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
		inflateSong(sng, true);
	}
	queue.status.queryCount++;
	return en;
}

function parseComment(dat) {
	(typeof dat == "string") && (dat = JSON.parse(dat));
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
