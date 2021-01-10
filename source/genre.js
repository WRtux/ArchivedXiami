var frame = document.getElementById("interface"), an = document.getElementById("download");
var url = "//emumo.xiami.com/genre/detail/gid/", gid = 1;
frame.onload = function () {
	var des = captureGenreDescription(gid, frame.contentDocument);
	if (des)
		data.push(des);
	if (gid < 25)
		frame.src = url + (gid += 1);
	else {
		var blob = new Blob([JSON.stringify(data)], { type: "application/json" });
		an.href = URL.createObjectURL(blob);
		an.click();
	}
};
var data = [];
frame.src = url + gid;

function captureGenreDescription(gid, doc) {
	var cont = doc.getElementById("texts");
	var n = cont.getElementsByTagName("h3")[0].textContent.trim(),
		txt = cont.getElementsByClassName("content")[0].textContent.trim();
	if (n.length > 0)
		return { id: gid, name: n, description: txt };
}
