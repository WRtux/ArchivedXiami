"use strict";

function fetchComment(typ, sid, m) {
	if (m == "hot")
		m = true;
	else if (m == "new")
		m = false;
	else if (typeof m == "undefined")
		m = true;
	let url = "/api/comment/" + (m ? "getHotCommentList" : "getCommentList");
	let param = JSON.stringify({ objectId: sid, objectType: typ, pagingVO: { page: 1, pageSize: m ? 10 : 20 } });
	let coki = document.cookie.match(/(?:^|;)\s*xm_sg_tk=([^;]*)/)[1];
	coki = coki.substring(0, coki.indexOf("_"));
	let hash = md5(coki + "_xmMain_" + url + "_" + param);
	let xhr = new XMLHttpRequest();
	xhr.open("GET", url + "?_q=" + encodeURIComponent(param) + "&_s=" + hash, false);
	xhr.send();
	if (xhr.status >= 200 && xhr.status < 300)
		return JSON.parse(xhr.responseText);
	else
		return null;
}

