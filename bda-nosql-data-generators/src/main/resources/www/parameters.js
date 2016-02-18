function queryParams() {
    var qd = {};
    location.search.substr(1).split("&").forEach(function(item) {
        var s = item.split("="),
            k = s[0],
            v = s[1] && decodeURIComponent(s[1]);
        (k in qd) ? qd[k].push(v) : qd[k] = [v]
    });
    return qd;
}