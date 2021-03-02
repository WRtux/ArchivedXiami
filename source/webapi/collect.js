"use strict";

for (let id of JSON.parse(dataInput)) {
	let func = () => buildRequestURL("/api/collect/getCollectStaticUrl", { listId: id });
	queue.push({
		mode: "request", builder: func, referrer: "/list?type=collect", recook: true,
		callback: prepareCollect, type: "prepare", id: id
	});
}
data = new Array();
startQueue();

function prepareCollect(str) {
	let url = JSON.parse(str).result.data.data.data.url;
	url = buildRequestURL(url.substring(url.indexOf("//")), "");
	queue.unshift({
		mode: "request", url: url, referrer: "/list?type=collect",
		callback: captureCollect, type: "collect", id: this.id
	});
}

function captureCollect(str) {
	let dat = JSON.parse(str).resultObj;
	let en = dat && parseCollect(dat);
	if (en) {
		en.commentSid = inflateComment("collect", this.id) ? "collect-" + this.id : null;
		data.push(en);
		queue.status.mergeCount++;
	} else
		data.push({ id: this.id, update: new Date().getTime(), name: null });
	queue.status.iterateCount++;
}
