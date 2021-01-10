var frame = document.getElementById("interface"), download = document.getElementById("download");
frame.onload = function () {
	if (frame.task)
		frame.task.callback(frame.contentDocument);
	if (queue.length > 0) {
		frame.task = queue.shift();
		frame.src = frame.task.url;
	} else if (data.length > 0) {
		var blob = new Blob([JSON.stringify(data)], { type: "application/json" });
		download.href = URL.createObjectURL(blob);
		download.click();
		data = [];
		console.log("Capture complete.");
	}
};

var queue = [];
var data = [];
