"use strict";
var pool = { artists: new Map(), albums: new Map(), songs: new Map() };

var genreExp = new RegExp("(?<=genre/detail/gid/|genre/detail/sid/)[0-9]+");
var artistExp = new RegExp("(?<=artist/)[0-9A-Za-z]+"), albumExp = new RegExp("(?<=album/)[0-9A-Za-z]+"), songExp = new RegExp("(?<=song/)[0-9A-Za-z]+");

function loadPool(dat) {
	//TODO
}

function savePool() {
	let dat = new Object();
	for (let en in pool) {
		dat[en] = new Array();
		for (let [k, v] of pool[en])
			dat[en].push(v);
	}
	let blob = new Blob([JSON.stringify(dat)], { type: "application/json" });
	download.href = URL.createObjectURL(blob);
	download.click();
}
