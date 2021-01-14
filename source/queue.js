"use strict";
var queue = [];
queue.status = { start: null, update: null, iterateCount: 0, queryCount: 0, mergeCount: 0 };
var data = undefined;

var frame = document.getElementById("interface"), download = document.getElementById("download");
frame.loadCount = 0;
frame.onload = function () {
	this.loadCount++;
	if (this.task) {
		this.task.callback(this.contentDocument);
		this.task = null;
		queue.status.update = new Date();
		proceedQueue();
	}
};

function startQueue(tsk) {
	queue.status.start = new Date();
	if (tsk)
		queue.unshift(tsk);
	proceedQueue();
}

function pauseQueue(f) {
	if (f && frame.task) {
		queue.unshift(frame.task);
		frame.task = null;
	}
	queue.status.start = null;
}

function proceedQueue() {
	if (!queue.status.start)
		return;
	if (queue.length > 0) {
		let tsk = queue.shift();
		if (tsk.mode == "frame") {
			frame.task = tsk;
			frame.src = tsk.url;
			frame.style.pointerEvents = "none";
		} else {
			let xhr = new XMLHttpRequest();
			xhr.open("GET", tsk.url, true);
			xhr.responseType = "document";
			xhr.send();
			xhr.onload = function () {
				tsk.callback(this.responseXML);
				queue.status.update = new Date();
				proceedQueue();
			};
			xhr.onerror = function () {
				this.open("GET", tsk.url, true);
				this.send();
			};
		}
	} else {
		if (data && Object.keys(data).length > 0) {
			let blob = new Blob([JSON.stringify(data)], { type: "application/json" });
			download.href = URL.createObjectURL(blob);
			download.click();
			data = new Array();
			console.log("Capture complete.");
		}
		queue.status.start = null;
		frame.style.pointerEvents = "auto";
	}
}
