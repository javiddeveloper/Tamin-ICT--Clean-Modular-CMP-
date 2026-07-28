# -*- coding: utf-8 -*-
"""Refresh the showcase video entity from Aparat's public video API."""
import io, json, re, urllib.request

VIDEO_HASH = "qqj045o"
API = "https://www.aparat.com/api/fa/v1/video/video/show/videohash/" + VIDEO_HASH
FIXTURE = ("core/core-network/src/commonMain/kotlin/com/tamin/taminhamrah/"
           "dataSource/agent/AgentFakeData.kt")

req = urllib.request.Request(API, headers={"User-Agent": "Mozilla/5.0"})
attrs = json.load(urllib.request.urlopen(req, timeout=25))["data"]["attributes"]

mp4 = next(q["urls"][0] for q in attrs["file_link_all"] if q["profile"] == "480p")
poster = attrs["big_poster"]
title = attrs["title"]
duration_ms = int(attrs["duration"]) * 1000

entity = json.dumps(
    {
        "type": "video",
        "video": mp4,
        "thumbnail": poster,
        "duration": str(duration_ms),
        "caption": title,
    },
    ensure_ascii=False,
)

src = io.open(FIXTURE, encoding="utf-8").read()
updated, count = re.subn(
    r'\{"type": "video".*?\}',
    lambda _m: entity,          # lambda avoids backslash-escape handling in the replacement
    src,
    count=1,
)
if count != 1:
    raise SystemExit("video entity not found in fixture")

io.open(FIXTURE, "w", encoding="utf-8").write(updated)
print("title :", title)
print("poster:", poster[:90])
print("mp4   :", mp4[:90])
print("dur   :", duration_ms, "ms")
