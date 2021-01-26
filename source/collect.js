"use strict";

for (let id of JSON.parse(dataInput)) {
	let func = () => buildRequestURL("/api/collect/getCollectStaticUrl", { listId: id }); //api/collect/initialize
	queue.push({
		mode: "request", builder: func, referrer: "/list?type=collect", callback: prepareCollect,
		type: "prepare", id: id
	});
}
data = new Array();
startQueue();

function prepareCollect(str) {
	let url = JSON.parse(str).result.data.data.data.url;
	url = buildRequestURL(url.substring(url.indexOf("//")), "");
	queue.unshift({
		mode: "request", url: url, referrer: "/list?type=collect", callback: captureCollect,
		type: "collect", id: this.id
	});
}

function captureCollect(str) {
	let cont = JSON.parse(str).resultObj;
	if (!cont) {
		data.push({ id: this.id, update: new Date().getTime(), name: null });
		queue.status.iterateCount++;
		return;
	}
	let en = {
		id: cont.listId, update: new Date().getTime(), name: cont.collectName,
		logo: cont.collectLogo, description: cont.description, tags: cont.tags,
		user: null,
		createTime: cont.gmtCreate, modifyTime: cont.gmtModify,
		playCount: cont.playCount, collectCount: cont.collects, commentCount: cont.comments,
		songs: new Array(), commentSid: null
	};
	cont.user && (en.user = { id: cont.user.userId, name: cont.user.nickName, avatar: cont.user.avatar });
	for (let i = 0; i < cont.songCount; i++) {
		en.songs.push({ id: cont.allSongs[i], sid: (cont.allSongStringIds || "")[i] });
//		pool.songs.weigh({ sid: cont.allSongStringIds[i], referrer: base.collect + this.id, weight: 6 });
	}
	let cmnto = {
		sid: "collect-" + this.id, update: new Date().getTime(),
		recentList: new Array(), hotList: new Array()
	};
	en.commentSid = cmnto.sid;
	let o = fetchComment("collect", this.id, 20).result.data.commentList;
	for (let cmnt of o) {
		let cmnten = {
			id: cmnt.commentId,
			content: cmnt.message, createTime: cmnt.gmtCreate,
			user: { id: cmnt.userId, name: cmnt.nickName, avatar: cmnt.avatar },
			likeCount: cmnt.likes, replyCount: cmnt.replyNum || 0, replies: new Array()
		};
		for (let rep of cmnt.replyData || "") {
			cmnten.replies.push({
				id: rep.commentId,
				content: rep.message, createTime: rep.gmtCreate,
				user: { id: rep.userId, name: rep.nickName, avatar: rep.avatar },
				likeCount: rep.likes
			});
		}
		cmnto.recentList.push(cmnten);
	}
	let cnt = 3;
	(cont.comments > 1000) && (cnt = 10);
	(cont.comments > 600) && (cnt = 8);
	(cont.comments > 300) && (cnt = 6);
	(cont.comments > 100) && (cnt = 4);
	o = fetchComment("collect", this.id, -cnt).result.data.hotList;
	for (let cmnt of o) {
		let cmnten = {
			id: cmnt.commentId,
			content: cmnt.message, createTime: cmnt.gmtCreate,
			user: { id: cmnt.userId, name: cmnt.nickName, avatar: cmnt.avatar },
			likeCount: cmnt.likes, replyCount: cmnt.replyNum || 0, replies: new Array()
		};
		for (let rep of cmnt.replyData || "") {
			cmnten.replies.push({
				id: rep.commentId,
				content: rep.message, createTime: rep.gmtCreate,
				user: { id: rep.userId, name: rep.nickName, avatar: rep.avatar },
				likeCount: rep.likes
			});
		}
		cmnto.hotList.push(cmnten);
	}
	pool.comments.inflate(cmnto);
	cont = cont.songs;
	for (let sng of cont) {
		let o = en.songs.find((o) => (o.id == sng.songId));
		Object.assign(o, {
			sid: sng.songStringId, name: sng.songName,
			description: sng.description, length: sng.length,
			artistSid: sng.artistVOs && sng.artistVOs[0].artistStringId, artistName: sng.artistName,
			albumSid: sng.albumStringId, albumName: sng.albumName
		});
		let sngen = {
			id: sng.songId, sid: sng.songStringId, update: new Date().getTime(), name: sng.songName,
			subName: sng.newSubName || sng.subName || null, track: sng.track, length: sng.length,
			artist: null,
			album: { id: sng.albumId, sid: sng.albumStringId, name: sng.albumName },
			weight: 0
		};
		let ref = base.song + sng.songStringId;
		o = (sng.artistVOs || "")[0];
		o && (sngen.artist = { id: o.artistId, sid: o.artistStringId, name: o.artistName });
		o && pool.artists.weigh({ sid: o.artistStringId, referrer: ref, name: o.artistName, weight: 3 });
		pool.albums.weigh({ sid: sng.albumStringId, referrer: ref, name: sng.albumName, weight: 3 });
		pool.songs.inflate(sngen);
	}
	data.push(en);
	queue.status.iterateCount++;
	if (Math.random() < 0.1)
		clearCookie();
}
