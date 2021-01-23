"use strict";

var frame = document.getElementById("interface"), download = document.getElementById("download");
var base = {
	artist: "/artist/", album: "/album/", song: "/song/",
	genre: "/genre/gid/", style: "/genre/sid/", collect: "/collect/",
	user: "/user/"
};
for (let n in base)
	base[n + "Exp"] = new RegExp(base[n] + "([0-9A-Za-z]+)");
base.oldGenreExp = new RegExp("/genre/detail/(?:gid/|sid/)" + "([0-9A-Za-z]+)");

function clearCookie() {
	let li = document.cookie.match(/[^;\s]+(?==)/g);
	for (let k of li)
		document.cookie = k + "=; expires=" + new Date(0).toUTCString();
}

function downloadJSON(o) {
	let blob = new Blob([JSON.stringify(o, null, 1)], { type: "application/json" });
	download.href = URL.createObjectURL(blob);
	download.click();
}

function buildRequestURL(api, param) {
	let coki = document.cookie.match(/(?:^|[;\s])xm_sg_tk=([^;]*)/)[1];
	coki = coki.substring(0, coki.indexOf("_"));
	if (typeof param != "string")
		param = JSON.stringify(param);
	let hash = md5(coki + "_xmMain_" + api + "_" + param);
	return api +
		((api.indexOf("?") == -1) ? "?_q=" + encodeURIComponent(param) : "") +
		"&_s=" + hash;
}

function fetchComment(typ, sid, lim) {
	if (lim == 0)
		return null;
	let url = "/api/comment/" + (lim > 0 ? "getCommentList" : "getHotCommentList");
	let param = { objectId: sid, objectType: typ, pagingVO: { page: 1, pageSize: Math.abs(lim) } };
	url = buildRequestURL(url, param);
	let xhr = new XMLHttpRequest();
	xhr.open("GET", url, false);
	xhr.send();
	if (xhr.status >= 200 && xhr.status < 300)
		return JSON.parse(xhr.responseText);
	else
		return null;
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
