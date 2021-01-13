//Standalone at //www.xiami.com/collect/* or //www.xiami.com/list*
"use strict";
var data = new Object();

var artistExp = new RegExp("(?<=artist/)[0-9A-Za-z]+"), albumExp = new RegExp("(?<=album/)[0-9A-Za-z]+"), songExp = new RegExp("(?<=song/)[0-9A-Za-z]+");
var userExp = new RegExp("(?<=user/)[0-9]+");
(function () {
	let an = document.createElement("a");
	an.id = "download";
	an.download = "导出歌单.json";
	document.body.appendChild(an);
	setTimeout(captureCollect, 1000);
})();

function captureCollect() {
	if (!data.songs) {
		let ele = document.getElementsByClassName("titleInfo-name")[0] || document.querySelector(".list-header> h2");
		data.title = ele && ele.textContent;
		ele = document.getElementsByClassName("collect-intro-text")[0];
		data.description = ele && ele.innerText;
		ele = document.querySelector(".collect-user-item> .name> a");
		if (ele) {
			data.userId = (ele.href.match(userExp) || "")[0];
			data.userName = ele.textContent;
		}
		data.songs = new Array();
	}
	let cont = document.getElementsByClassName("song-table")[0];
	let eles = cont.querySelectorAll("tbody> tr:not(.extended-row)");
	for (let ele of eles) {
		let sng = ele.querySelector(".song-name> a"), art = ele.querySelector(".singers> a"), alb = ele.querySelector(".album> a");
		let sid = sng.href.match(songExp)[0], aid = alb && (alb.href.match(albumExp) || "")[0], uid = art && (art.href.match(artistExp) || "")[0];
		data.songs.push({ id: sid, name: sng.textContent, albumId: aid, albumName: alb && alb.textContent, artistId: uid, artistName: art && art.textContent });
	}
	cont = document.getElementsByClassName("comment-list")[0];
	if (cont) {
		data.comments = new Array();
		let li = [];
		eles = cont.getElementsByClassName("comment");
		for (let ele of eles)
			li.push(ele);
		while (li.length > 0) {
			let ele = li.shift();
			cont = ele.querySelector(".comment-item> .comment-main");
			ele = cont.querySelector(".user-name> a");
			let uid = (ele.href.match(userExp) || "")[0], un = ele.textContent;
			let txt = cont.getElementsByClassName("comment-text")[0].innerText;
			ele = cont.querySelector(".comment-control> .count");
			//TODO
			data.comments.push({ userId: uid, userName: un, comment: txt });
			//cont = ele.getElementsByClassName("comment-reply-list")[0];
		}
	}
	
	let nxt = document.getElementsByClassName("rc-pagination-next")[0];
	if (nxt && !nxt.classList.contains("rc-pagination-disabled")) {
		nxt.click(captureCollect);
		setTimeout(captureCollect, 2000);
	} else {
		let blob = new Blob([JSON.stringify(data)], { type: "application/json" });
		let an = document.getElementById("download");
		an.href = URL.createObjectURL(blob);
		an.click();
		console.log("Capture complete.");
	}
}
