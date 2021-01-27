"use strict";
var queue = [];
queue.status = { start: null, update: null, task: null, iterateCount: 0, queryCount: 0, mergeCount: 0 };
var data = undefined;

function startQueue(tsk) {
	queue.status.start = new Date();
	tsk && queue.unshift(tsk);
	proceedQueue();
}

function pauseQueue(f) {
	if (f) {
		frame.contentDocument && frame.contentWindow.stop();
		frame.onload = null;
		if (queue.status.task) {
			queue.unshift(queue.status.task);
			queue.status.task = null;
		}
	}
	queue.status.start = null;
}

function proceedQueue() {
	if (!queue.status.start)
		return;
	if (queue.length > 0) {
		let tsk = queue.shift();
		queue.status.task = tsk;
		tsk.builder && (tsk.url = tsk.builder());
		fakeNavigate(tsk.mode, tsk.url, tsk.referrer, function (doc) {
			try {
				queue.status.task.callback(doc);
			} catch (err) {
				pauseQueue(true);
				console.error(err);
				return;
			}
			queue.status.task = null;
			queue.status.update = new Date();
			setTimeout(proceedQueue, 1000);
		});
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
			this.onload = null;
		};
	} else {
		let xhr = new wnd.XMLHttpRequest();
		xhr.open("GET", url, true);
		if (m == "ajax")
			xhr.responseType = "document";
		xhr.send();
		xhr.onload = () => func(xhr.response);
		xhr.onerror = function () {
			this.open("GET", url, true);
			setTimeout(this.send.bind(this), 1000);
		};
	}
}
