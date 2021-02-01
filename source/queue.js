"use strict";
var queue = [];
queue.status = {
	start: null, update: null, task: null,
	iterateCount: 0, queryCount: 0, mergeCount: 0, cook: 0
};
queue.config = {
	interval: 1000, recookMod: 2,
	autoRestart: false
};
var data = undefined;

function startQueue(tsk) {
	queue.status.start = new Date();
	tsk && queue.unshift(tsk);
	proceedQueue();
	console.log("Queue started.");
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
	queue.config.autoRestart && setTimeout(startQueue, queue.config.interval * 3);
}

function proceedQueue() {
	if (!queue.status.start)
		return;
	if (queue.length > 0) {
		let tsk = queue.shift();
		queue.status.task = tsk;
		if (tsk.recook) {
			queue.status.cook++;
			(queue.status.cook % queue.config.recookMod == 0) && recook();
		}
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
			setTimeout(proceedQueue, queue.config.interval);
		}, pauseQueue.bind(undefined, true));
	} else {
		if (data && Object.keys(data).length > 0) {
			downloadJSON(data);
//			data = undefined;
			console.log("Capture complete.");
		}
		queue.status.start = null;
	}
}

function recook() {
	clearCookie();
	document.cookie = "xmgid=" + Math.floor(Math.random() * 10000) + "; domain=xiami.com; path=/";
	let xhr = new XMLHttpRequest();
	xhr.open("GET", "/api/comment/getCommentList", false);
	xhr.send();
}

function fakeNavigate(m, url, ref, hndl, err) {
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
			this.onload = this.onerror = null;
			hndl(this.contentDocument);
		};
		frame.onerror = function () {
			this.onload = this.onerror = null;
			err();
		};
	} else {
		let xhr = new wnd.XMLHttpRequest();
		xhr.open("GET", url, true);
		if (m == "ajax")
			xhr.responseType = "document";
		xhr.send();
		xhr.onload = () => hndl(xhr.response);
		xhr.onerror = function () {
			this.open("GET", url, true);
			setTimeout(this.send.bind(this), 1000);
			this.onerror = err;
		};
	}
}
