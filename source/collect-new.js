"use strict";

for (let id of data) {
	let func = () => buildRequestURL("/api/collect/getCollectStaticUrl", { listId: id }); //api/collect/initialize
	queue.push({
		mode: "request", builder: func, referrer: "/list?type=collect", callback: prepareCollect,
		type: "prepare"
	});
}
data = new Array();
startQueue();

function prepareCollect(str) {
	let url = JSON.parse(str).result.data.data.data.url;
	url = buildRequestURL(url.substring(url.indexOf("//")), "");
	queue.unshift({
		mode: "request", url: url, referrer: "/list?type=collect", callback: captureCollect,
		type: "collect"
	});
}

function captureCollect(str) {
	let cont = JSON.parse(str).resultObj;
	let en = {
		id: cont.listId, update: new Date().getTime(), name: cont.collectName,
		logo: cont.collectLogo, description: cont.description, tags: cont.tags,
		user: { id: cont.user.userId, name: cont.user.nickName, avatar: cont.user.avatar },
		createTime: cont.gmtCreate, modifyTime: cont.gmtModify,
		playCount: cont.playCount, collectCount: cont.collects, commentCount: cont.comments
	};
	en.songs = new Array();
	for (let i = 0; i < cont.songCount; i++) {
		en.songs.push({ id: cont.allSongs[i], sid: (cont.allSongStringIds || "")[i] });
//		pool.songs.weigh({ sid: cont.allSongStringIds[i], referrer: base.collect + cont.listId, weight: 6 });
	}
	
	if (cont.type || cont.help)
		console.warn(cont);
	
	let ref = base.song + cont.listId;
	cont = cont.songs;
	for (let sng of cont) {
		let o = en.songs.find((o) => (o.id == sng.songId));
		Object.assign(o, {
			sid: sng.songStringId, name: sng.songName,
			description: sng.description, length: sng.length,
			artistSid: sng.artistVOs || sng.artistVOs[0].artistStringId, artistName: sng.artistName,
			albumSid: sng.albumId, albumName: sng.albumName
		});
		let sngen = {
			id: sng.songId, sid: sng.songStringId, update: new Date().getTime(), name: sng.songName,
			subName: sng.newSubName || sng.subName || null, track: sng.track, length: sng.length,
			artist: null,
			album: { id: sng.albumId, sid: sng.albumStringId, name: sng.albumName },
			weight: 0
		};
		if (sng.artistVOs) {
			o = sng.artistVOs[0];
			sngen.artist = { id: o.artistId, sid: o.artistStringId, name: o.artistName };
			pool.artists.weigh({ sid: o.artistStringId, referrer: ref, name: o.artistName, weight: 3 });
		}
		pool.albums.weigh({ sid: sng.albumStringId, referrer: ref, name: sng.albumName, weight: 3 });
		pool.songs.inflate(sngen);
		
		if (sng.artistVOs.length > 1)
			console.warn(sng);
	}
	data.push(en);
	queue.status.iterateCount++;
	if (Math.random() < 0.1)
		clearCookie();
}
