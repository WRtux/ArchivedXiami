//Disable cookies, use dynamic proxy, and init a separate pool for raw data!
"use strict";

for (let sid of data.artists) {
	queue.push({
		mode: "ajax", url: base.artist + sid, referrer: "/",
		type: "artist", callback: captureRaw, sid: sid
	});
}
for (let sid of data.albums) {
	queue.push({
		mode: "ajax", url: base.album + sid, referrer: "/",
		type: "album", callback: captureRaw, sid: sid
	});
}
for (let sid of data.songs) {
	queue.push({
		mode: "ajax", url: base.song + sid, referrer: "/list",
		type: "song", callback: captureRaw, sid: sid
	});
}
startQueue();

function captureRaw(doc) {
	let dat = getPreloadedData(doc);
	dat && (dat = dat[this.type + "Data"]);
	dat && pool[this.type + "s"].set(this.sid, dat);
	queue.status.iterateCount++;
}
