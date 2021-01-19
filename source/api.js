"use strict";

var frame = document.getElementById("interface"), download = document.getElementById("download");
var base = {
	artist: "/artist/", album: "/album/", song: "/song/",
	genre: "/genre/detail/gid/", style: "/genre/detail/sid/", collect: "/collect/",
	user: "/user/"
};
for (let n in base)
	base[n + "Exp"] = new RegExp(base[n] + "([0-9A-Za-z]+)");
base.genreExp = new RegExp("(?:" + base.genre + "|" + base.style + ")([0-9A-Za-z]+)");

function clearCookie() {
	let li = document.cookie.match(/[^=;\s]+(?==)/g);
	for (let k of li)
		document.cookie = k + "=; expires=" + new Date(0).toUTCString();
}

function downloadJSON(o) {
	let blob = new Blob([JSON.stringify(o)], { type: "application/json" });
	download.href = URL.createObjectURL(blob);
	download.click();
}

function getPreloadedData(doc) {
	let eles = doc.body.getElementsByTagName("script");
	for (let ele of eles) {
		let scr = ele.textContent;
		if (scr.startsWith("window.__PRELOADED_STATE__")) {
			let str = Base64.decode(scr.substring(scr.indexOf('"') + 1, scr.lastIndexOf('"')));
			return JSON.parse(str);
		}
	}
	return null;
}

function fetchComment(typ, sid, m) {
	if (m == "hot")
		m = true;
	else if (m == "new")
		m = false;
	else if (typeof m == "undefined")
		m = true;
	let url = "/api/comment/" + (m ? "getHotCommentList" : "getCommentList");
	let param = JSON.stringify({ objectId: sid, objectType: typ, pagingVO: { page: 1, pageSize: m ? 10 : 20 } });
	let coki = document.cookie.match(/(?:^|;)\s*xm_sg_tk=([^;]*)/)[1];
	coki = coki.substring(0, coki.indexOf("_"));
	let hash = md5(coki + "_xmMain_" + url + "_" + param);
	let xhr = new XMLHttpRequest();
	xhr.open("GET", url + "?_q=" + encodeURIComponent(param) + "&_s=" + hash, false);
	xhr.send();
	if (xhr.status >= 200 && xhr.status < 300)
		return JSON.parse(xhr.responseText);
	else
		return null;
}
