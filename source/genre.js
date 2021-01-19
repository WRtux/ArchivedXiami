"use strict";

data = new Array();
startQueue({ mode: "frame", url: "/genre", callback: initGenre });

function initGenre(doc) {
	let cont = doc.querySelector("#tree> dl");
	let eles = cont.getElementsByTagName("dt");
	for (let ele of eles) {
		let an = ele.getElementsByTagName("a")[0];
		queue.push({ mode: "frame", type: "genre", url: an.href, callback: captureGenre, data: data });
	}
}

function captureGenre(doc) {
	let cont = doc.getElementById("texts");
	let n = cont.getElementsByTagName("h3")[0].textContent.trim(),
		txt = cont.getElementsByClassName("content")[0].textContent.trim();
	let en = {
		type: this.type, id: this.url.match(base.genreExp)[1], update: new Date().getTime(),
		name: n, description: txt
	};
	en.songs = new Array();
	cont = doc.querySelector("#songs> .content");
	let eles = cont.getElementsByClassName("info");
	for (let ele of eles) {
		let sng = ele.getElementsByTagName("a")[0], art = ele.getElementsByTagName("a")[1];
		let sid = sng.href.match(base.songExp)[1], uid = art && (art.href.match(base.artistExp) || "")[1];
		en.songs.push({ sid: sid, name: sng.textContent, artistSid: uid, artistName: art.textContent });
		pool.songs.weigh({ sid: sid, referrer: doc.location.pathname, name: sng.textContent, weight: 8 });
		pool.artists.weigh({ sid: uid, referrer: doc.location.pathname, name: art.textContent, weight: 5 });
	}
	en.albums = new Array();
	cont = doc.querySelector("#albums> .content");
	eles = cont.getElementsByClassName("info");
	for (let ele of eles) {
		let alb = ele.getElementsByTagName("a")[0], art = ele.getElementsByTagName("a")[1];
		let aid = alb.href.match(base.albumExp)[1], uid = art && (art.href.match(base.artistExp) || "")[1];
		en.albums.push({ sid: aid, name: alb.textContent, artistSid: uid, artistName: art.textContent });
		pool.albums.weigh({ sid: aid, referrer: doc.location.pathname, name: alb.textContent, weight: 7 });
		pool.artists.weigh({ sid: uid, referrer: doc.location.pathname, name: art.textContent, weight: 4 });
	}
	en.artists = new Array();
	cont = doc.querySelector("#artists> .content");
	eles = cont.getElementsByClassName("info");
	for (let ele of eles) {
		let art = ele.getElementsByTagName("a")[0];
		let uid = art.href.match(base.artistExp)[1];
		en.artists.push({ sid: uid, name: art.textContent });
		pool.artists.weigh({ sid: uid, referrer: doc.location.pathname, name: art.textContent, weight: 7 });
	}
	if (this.type == "genre") {
		en.styles = new Array();
		let cur = doc.querySelector("#tree dt.current"),
			eles = cur.nextElementSibling.getElementsByTagName("li");
		for (let i = eles.length - 1; i >= 0; i--) {
			let an = eles[i].getElementsByTagName("a")[0];
			queue.unshift({ mode: "frame", type: "style", url: an.href, referrer: this.url, callback: captureGenre, data: en.styles });
		}
	}
	this.data.push(en);
}
