"use strict";

data = new Array();
for (let id of li)
	queue.push({ mode: "ajax", url: base.collect + id, callback: captureCollect });
startQueue();

function captureCollect(doc) {
	let dat = getPreloadedData(doc).collectData, cont = dat.collectDetail;
	let en = {
		id: cont.listId, name: cont.collectName,
		logo: cont.collectLogo, description: cont.description, tag: cont.tags,
		userId: cont.userId, userName: cont.userName, userAvatar: cont.authorAvatar,
		createTime: cont.gmtCreate, modifyTime: cont.gmtModify,
		viewCount: cont.views, playCount: cont.playCount, collectCount: cont.collects
	};
	en.songs = new Array();
	for (let i = 0; i < cont.songCount; i++) {
		en.songs.push({ id: cont.allSongs[i], sid: cont.allSongStringIds[i] });
		pool.songs.weigh(cont.allSongStringIds[i], { weigh: 6 });
	}
	
	if (cont.type || cont.recommend || cont.help)
		console.warn(cont);
	cont = dat.collectSongs;
	en.detailedSongs = new Array();
	for (let sng of cont) {
		en.detailedSongs.push({
			id: sng.songId, sid: sng.songStringId, name: sng.songName, subName: sng.subName,
			artistId: sng.artistVOs[0].artistId, artistSid: sng.artistVOs[0].artistStringId, artistName: sng.artistVOs[0].artistName,
			singerId: sng.singerVOs[0].artistId, singerSid: sng.singerVOs[0].artistStringId, singerName: sng.singerVOs[0].artistName,
			albumId: sng.albumId, albumSid: sng.albumStringId, albumName: sng.albumName,
			description: sng.description
		});
	}
	
	data.push(en);
	if (Math.random() < 0.4)
		clearCookie();
}
