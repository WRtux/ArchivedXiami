"use strict";
var pool = { artists: new Map(), albums: new Map(), songs: new Map(), comments: new Map() };

for (let n in pool) {
	pool[n].weigh = function (o) {
		if (!o || (!o.sid && !o.id))
			return;
		let w = (this.get(o.sid || o.id) || { weight: -1 }).weight || 11;
		if (o.weight >= w) {
			let en = new Object();
			Object.assign(en, o);
			this.set(o.sid || o.id, en);
			return true;
		}
		return false;
	};
	pool[n].inflate = function (o) {
		if (!o || (!o.sid && !o.id))
			return;
		let w = (this.get(o.sid || o.id) || { weight: -1 }).weight || 11;
		if (!o.weight || o.weight >= w) {
			let en = this.get(o.sid || o.id) || new Object();
			delete en.referrer;
			delete en.weight;
			Object.assign(en, o);
			this.set(o.sid || o.id, en);
			return true;
		}
		return false;
	};
}

function loadPool(dat) {
	(typeof dat == "string") && (dat = JSON.parse(dat));
	for (let n in dat) {
		pool[n].clear();
		for (let en of dat[n])
			pool[n].set(en.sid || en.id, en);
	}
}

function mergePool(dat) {
	(typeof dat == "string") && (dat = JSON.parse(dat));
	for (let n in dat) {
		for (let en of dat[n])
			pool[n].inflate(en);
	}
}

function savePool() {
	let dat = new Object();
	for (let n in pool) {
		dat[n] = new Array();
		for (let [k, v] of pool[n])
			dat[n].push(v);
	}
	downloadJSON(dat);
}
