"use strict";
var pool = { artists: new Map(), albums: new Map(), songs: new Map() };

for (let n in pool) {
	pool[n].weigh = function (k, v) {
		let w = (this.get(k) || "").weight;
		if (k && (!w || v.weight >= w)) {
			let en = new Object();
			Object.assign(en, v);
			en.sid = k;
			this.set(k, en);
		}
	};
}
var base = {
	artist: "/artist/", album: "/album/", song: "/song/",
	genre: "/genre/detail/gid/", style: "/genre/detail/sid/", collect: "/collect/",
	user: "/user/"
};
for (let n in base)
	base[n + "Exp"] = new RegExp(base[n] + "([0-9A-Za-z]+)");
base.genreExp = new RegExp("(?:" + base.genre + "|" + base.style + ")([0-9A-Za-z]+)");

function loadPool(dat) {
	//TODO
}

function savePool() {
	let dat = new Object();
	for (let n in pool) {
		dat[n] = new Array();
		for (let [k, v] of pool[n])
			dat[n].push(v);
	}
	let blob = new Blob([JSON.stringify(dat)], { type: "application/json" });
	download.href = URL.createObjectURL(blob);
	download.click();
}

function inflatePool() {
	//TODO
}
