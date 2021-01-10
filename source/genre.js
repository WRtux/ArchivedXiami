//Ensure start from //emumo.xiami.com/genre
"use strict";

(function () {
	let cont = frame.contentDocument.getElementById("tree").getElementsByTagName("dl")[0];
	let eles = cont.getElementsByTagName("dt");
	for (let ele of eles) {
		let an = ele.getElementsByTagName("a")[0];
		queue.push({ type: "genre", url: an.href, callback: captureGenre, data: data });
	}
	frame.task = queue.shift();
	frame.src = frame.task.url;
})();

function captureGenre(doc) {
	let cont = doc.getElementById("texts");
	let n = cont.getElementsByTagName("h3")[0].textContent.trim(),
		txt = cont.getElementsByClassName("content")[0].textContent.trim();
	let en = { type: this.type, id: this.url.match(genreExp)[0], name: n, description: txt };
	en.songs = new Array();
	cont = doc.getElementById("songs").getElementsByClassName("content")[0];
	let eles = cont.getElementsByClassName("info");
	for (let ele of eles) {
		let sng = ele.getElementsByTagName("a")[0], art = ele.getElementsByTagName("a")[1];
		let sid = sng.href.match(songExp)[0], uid = art && art.href.match(artistExp) && art.href.match(artistExp)[0];
		en.songs.push({ id: sid, name: sng.textContent, artist: uid });
		if (!pool.songs.has(sid))
			pool.songs.set(sid, { id: sid, name: sng.textContent });
		if (uid && !pool.artists.has(uid))
			pool.artists.set(uid, { id: uid, name: art.textContent });
	}
	en.albums = new Array();
	cont = doc.getElementById("albums").getElementsByClassName("content")[0];
	eles = cont.getElementsByClassName("info");
	for (let ele of eles) {
		let alb = ele.getElementsByTagName("a")[0], art = ele.getElementsByTagName("a")[1];
		let aid = alb.href.match(albumExp)[0], uid = art && art.href.match(artistExp) && art.href.match(artistExp)[0];
		en.albums.push({ id: aid, name: alb.textContent, artist: uid });
		if (!pool.albums.has(aid))
			pool.albums.set(aid, { id: aid, name: alb.textContent });
		if (uid && !pool.artists.has(uid))
			pool.artists.set(uid, { id: uid, name: art.textContent });
	}
	en.artists = new Array();
	cont = doc.getElementById("artists").getElementsByClassName("content")[0];
	eles = cont.getElementsByClassName("info");
	for (let ele of eles) {
		let art = ele.getElementsByTagName("a")[0];
		let uid = art.href.match(artistExp)[0];
		en.artists.push({ id: uid, name: art.textContent });
		if (!pool.artists.has(uid))
			pool.artists.set(uid, { id: uid, name: art.textContent });
	}
	if (this.type == "genre") {
		en.styles = new Array();
		let cur = doc.getElementById("tree").getElementsByClassName("unfold current")[0],
			eles = cur.nextElementSibling.getElementsByTagName("li");
		for (let i = eles.length - 1; i >= 0; i--) {
			let an = eles[i].getElementsByTagName("a")[0];
			queue.unshift({ type: "style", url: an.href, callback: captureGenre, data: en.styles });
		}
	}
	this.data.push(en);
}
