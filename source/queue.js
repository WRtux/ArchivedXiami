"use strict";
var queue = [];
queue.status = { start: null, update: null, iterateCount: 0, queryCount: 0, mergeCount: 0 };
var data = undefined;

function startQueue(tsk) {
	queue.status.start = new Date();
	if (tsk)
		queue.unshift(tsk);
	proceedQueue();
}

function pauseQueue(f) {
	if (f)
		frame.onload = null;
	queue.status.start = null;
}

function proceedQueue() {
	if (!queue.status.start)
		return;
	if (queue.length > 0) {
		let tsk = queue.shift();
		fakeNavigate(tsk.mode, tsk.url, tsk.referrer, tsk.callback.bind(tsk));
	} else {
		if (data && Object.keys(data).length > 0) {
			downloadJSON(data);
			data = undefined;
			console.log("Capture complete.");
		}
		queue.status.start = null;
	}
}

function fakeNavigate(m, url, ref, func) {
	let wnd = window;
	if (ref && frame.contentDocument) {
		wnd = frame.contentWindow;
		wnd.history.pushState(null, null, ref);
	}
	if (m == "frame") {
		if (wnd != window)
			wnd.location.assign(url);
		else
			frame.src = url;
		frame.onload = function () {
			func(this.contentDocument);
			queue.status.update = new Date();
			setTimeout(proceedQueue, 1000);
			this.onload = null;
		};
	} else {
		let xhr = new wnd.XMLHttpRequest();
		xhr.open("GET", url, true);
		xhr.responseType = "document";
		xhr.send();
		xhr.onload = function () {
			func(this.response);
			queue.status.update = new Date();
			setTimeout(proceedQueue, 1000);
		};
		xhr.onerror = function () {
			this.open("GET", url, true);
			this.send();
		};
	}
}
