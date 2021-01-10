//Ensure start from //emumo.xiami.com/genre
var genreExp = new RegExp("(?<=genre/detail/gid/|genre/detail/gid/)[0-9]+");
var artistExp = new RegExp("(?<=artist/)[0-9A-Za-z]+"), albumExp = new RegExp("(?<=album/)[0-9A-Za-z]+"), songExp = new RegExp("(?<=song/)[0-9A-Za-z]+");
(function () {
	var cont = frame.contentDocument.getElementById("tree").getElementsByTagName("dl")[0];
	var eles = cont.getElementsByTagName("dt");
	for (var i = 0; i < eles.length; i++) {
		var an = eles[i].getElementsByTagName("a")[0];
		queue.push({ type: "genre", url: an.href, callback: captureGenre, data: data });
	}
	frame.task = queue.shift();
	frame.src = frame.task.url;
})();

function captureGenre(doc) {
	var cont = doc.getElementById("texts");
	var n = cont.getElementsByTagName("h3")[0].textContent.trim(),
		txt = cont.getElementsByClassName("content")[0].textContent.trim();
	var en = { type: this.type, id: location.href.match(genreExp)[0], name: n, description: txt };
	en.songs = [];
	cont = doc.getElementById("songs").getElementsByClassName("content")[0];
	var eles = cont.getElementsByClassName("info");
	for (var i = 0; i < eles.length; i++) {
		var sng = eles[i].getElementsByTagName("a")[0], art = eles[i].getElementsByTagName("a")[1];
		var sid = sng.href.match(songExp)[0], uid = art.href.match(artistExp)[0];
		en.songs.push({ id: sid, name: sng.textContent, artist: uid });
		if (!pool.songs.has(sid))
			pool.songs.set(sid, { id: sid, name: sng.textContent });
		if (!pool.artists.has(uid))
			pool.artists.set(uid, { id: uid, name: art.textContent });
	}
	en.albums = [];
	cont = doc.getElementById("albums").getElementsByClassName("content")[0];
	eles = cont.getElementsByClassName("info");
	for (var i = 0; i < eles.length; i++) {
		var alb = eles[i].getElementsByTagName("a")[0], art = eles[i].getElementsByTagName("a")[1];
		var aid = alb.href.match(albumExp)[0], uid = art.href.match(artistExp)[0];
		en.albums.push({ id: aid, name: alb.textContent, artist: uid });
		if (!pool.albums.has(aid))
			pool.albums.set(aid, { id: aid, name: alb.textContent });
		if (!pool.artists.has(uid))
			pool.artists.set(uid, { id: uid, name: art.textContent });
	}
	en.artists = [];
	cont = doc.getElementById("artists").getElementsByClassName("content")[0];
	eles = cont.getElementsByClassName("info");
	for (var i = 0; i < eles.length; i++) {
		var art = eles[i].getElementsByTagName("a")[0];
		var uid = art.href.match(artistExp)[0];
		en.artists.push({ id: uid, name: art.textContent });
		if (!pool.artists.has(uid))
			pool.artists.set(uid, { id: uid, name: art.textContent });
	}
	if (this.type == "genre") {
		en.styles = [];
		var cur = doc.getElementById("tree").getElementsByClassName("unfold current")[0],
			eles = cur.nextElementSibling.getElementsByTagName("li");
		for (var i = eles.length - 1; i >= 0; i--) {
			var an = eles[i].getElementsByTagName("a")[0];
			queue.unshift({ type: "style", url: an.href, callback: captureGenre, data: en.styles });
		}
	}
	this.data.push(en);
}
