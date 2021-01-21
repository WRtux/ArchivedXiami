//Work on //emumo.xiami.com
"use strict";

data = new Array();
startQueue({ mode: "frame", url: "/genre", callback: initGenre });

function initGenre(doc) {
	for (let ele of doc.querySelectorAll("#tree dt")) {
		let an = ele.getElementsByTagName("a")[0];
		queue.push({
			mode: "frame", url: an.href, callback: captureGenre,
			type: "genre", id: Number(an.href.match(base.oldGenreExp)[1]), data: data
		});
	}
}

function captureGenre(doc) {
	let cont = doc.getElementById("texts");
	let n = cont.getElementsByTagName("h3")[0].textContent.trim(),
		txt = cont.getElementsByClassName("content")[0].textContent.trim();
	let en = {
		type: this.type, id: this.id, update: new Date().getTime(), name: n,
		description: txt
	};
	queue.status.queryCount++;
	en.songs = new Array();
	cont = doc.querySelector("#songs> .content");
	let ref = base[this.type] + this.id;
	for (let ele of cont.getElementsByClassName("song")) {
		let [sng, art] = ele.querySelectorAll(".info a");
		let sid = sng.href.match(base.songExp)[1],
			uid = art && (art.href.match(base.artistExp) || "")[1];
		en.songs.push({
			sid: sid, name: sng.textContent,
			artistSid: uid, artistName: art.textContent
		});
		pool.songs.weigh({ sid: sid, referrer: ref, name: sng.textContent, weight: 8 });
		pool.artists.weigh({ sid: uid, referrer: ref, name: art && art.textContent, weight: 5 });
		queue.status.queryCount++, queue.status.mergeCount++;
	}
	en.albums = new Array();
	cont = doc.querySelector("#albums> .content");
	for (let ele of cont.getElementsByClassName("album")) {
		let [alb, art] = ele.querySelectorAll(".info a");
		let aid = alb.href.match(base.albumExp)[1],
			uid = art && (art.href.match(base.artistExp) || "")[1];
		en.albums.push({
			sid: aid, name: alb.textContent,
			artistSid: uid, artistName: art.textContent
		});
		pool.albums.weigh({ sid: aid, referrer: ref, name: alb.textContent, weight: 7 });
		pool.artists.weigh({ sid: uid, referrer: ref, name: art && art.textContent, weight: 4 });
		queue.status.queryCount++, queue.status.mergeCount++;
	}
	en.artists = new Array();
	cont = doc.querySelector("#artists> .content");
	for (let ele of cont.getElementsByClassName("artist")) {
		let img = ele.getElementsByTagName("img")[0], art = ele.querySelector(".info a");
		let uid = art.href.match(base.artistExp)[1];
		en.artists.push({ sid: uid, name: art.textContent, logoSmall: img && img.src });
		pool.artists.weigh({ sid: uid, referrer: ref, name: art.textContent, weight: 7 });
		queue.status.queryCount++, queue.status.mergeCount++;
	}
	if (this.type == "genre") {
		en.styles = new Array();
		let eles = doc.querySelectorAll("#tree dt.current+ dd li");
		for (let i = eles.length - 1; i >= 0; i--) {
			let an = eles[i].getElementsByTagName("a")[0];
			queue.unshift({
				mode: "frame", url: an.href, referrer: this.url, callback: captureGenre,
				type: "style", id: Number(an.href.match(base.oldGenreExp)[1]), data: en.styles
			});
		}
	}
	queue.status.iterateCount++;
	this.data.push(en);
}
