---
tags: [feature, ui]
---

# Stories — «تازه‌ها»

Instagram-shaped story browsing: a horizontal rail of publisher rings on the home page, and a
full-screen viewer that plays a channel's slides in order.

Module `:feature:stories`, package `…feature.stories`. Registered in `settings.gradle.kts`,
`shared/build.gradle.kts`, `sharedModules` (`storiesModule`) and the central `NavHost`.

> **Front-end only, on purpose.** There is no stories web service. The catalogue is bundled
> (`MockStorySource`), and so is the sample media. Nothing here touches `core-network`,
> `core-domain` or Room — see [[#When the service arrives]].

## Where it lives on screen

The rail sits between the role picker and [[Theme|CampaignCarousel]] in `HomeScreen`
(`shared/.../TaminHamrahNavGraph.kt`), full-bleed through the same
`ignoreHorizontalPadding(HomeContentPadding)` the carousel uses — a scrolling row has to be able
to run a ring off the screen edge.

The viewer is its own route (`StoryViewerRoute(channelIndex)`), not an overlay. That is what makes
the system back button close it and what takes the floating navigation bar off the screen: the host
graph hides that bar on every route outside the four tabs (`BottomTab.OTHER`).

## The design reference

Everything — the five channels, three slides each, the ring and backdrop gradients, the 6.2 s
slide, the 34%/66% tap columns — is read off the user's design bundle
(`Tamin Man8-9-2026-v2 (1).html` in Downloads, see the `reference-design-bundle-html` memory).
`StoryDimens` and `StoryPalette` reproduce those numbers rather than normalizing them.

## The pieces

```
data/StorySource.kt      fun interface — the one seam a real service replaces
data/MockStorySource.kt  the bundled catalogue; Res.getUri() for the sample media
data/StoryCatalog.kt     Koin single: cache, in-flight guard, seen/liked/saved sets
model/StoryModels.kt     StoryChannel / StoryItem / StoryMedia / StoryCta
ui/theme/StoryTokens.kt  StoryDimens, StoryPalette, storyTextStyles(), the ink constants
ui/rail/                 StoryRail + StoryRailViewModel + contract
ui/viewer/               StoryViewerScreen, StoryProgressBar, StoryVideoPlayer (expect/actual)
```

`StoryCatalog` being a `single` is what makes the two screens agree: the viewer marks a channel
watched and the ring behind it on the home page goes grey, without either screen knowing about the
other. It is also why opening the viewer costs no second fetch.

## The segment clock — the one design decision worth knowing

**There is no `progress: Float` in the viewer's state.** A bar that filled from state would push a
new state through the whole MVI pipeline every frame it moved.

Instead the state describes the *segment* — `segmentDurationMs`, `segmentElapsedMs`, `isBuffering`,
the two hold flags, and a `segmentToken` — and `StoryProgressBar` animates itself with an
`Animatable` keyed on `(segmentToken, isPlaying)`. The ViewModel bumps the token when the segment
itself changes: a new slide, a clip reporting its length, media failing. Holds need no bump —
`isPlaying` is the other key, so a hold arriving or leaving restarts the animation on its own,
from the `segmentElapsedMs` published alongside it.

The ViewModel's own clock (`segmentJob`) accumulates in 50 ms ticks rather than sleeping out the
whole duration in one `delay`, which is what lets a pause report how far the slide already got —
without ever reading a wall clock. That is why every timing rule is a plain unit-test assertion
under `advanceTimeBy` rather than something only a screenshot could show.

Consequences:

- A swipe through a channel costs a handful of state emissions, not several hundred.
- Exactly one clock runs at a time; every path that changes the slide cancels it first, and a tick
  already in flight is discarded by the `segmentSerial` check instead of advancing twice.
- `watchdogJob` is the only other timer and is mutually exclusive with the clock — it runs while a
  clip buffers, which is exactly when the clock does not.

## Navigation rules

| Gesture | Result |
|---|---|
| Tap the wide column (left under RTL) | next slide |
| Tap the narrow column (right under RTL) | previous slide |
| Hold anywhere | pause; releasing resumes from where it stopped, and **never** moves the story |
| Next past a channel's last slide | next channel, and the finished one is marked seen |
| Next past the last channel | viewer closes |
| Previous on the very first slide | replays it (the design does nothing; "مناسب UX" was the requirement) |
| Close, or taking a call to action | marks the channel seen, then leaves |

The tap columns use `Alignment.TopStart`/`TopEnd`, not left/right. Under this app's RTL layout that
puts "next" on the left as required, and it stays correct rather than inverted if an LTR layout is
ever added.

### Tap vs. hold — three traps, all already sprung

`StoryTapZone` looks over-commented; it isn't. All three of these were real, reported bugs:

1. **`onLongPress` must be supplied, even empty.** Without it `detectTapGestures` reports a long
   hold-then-release as a *tap*, so resting a finger on the screen jumped to the next slide when
   you lifted it. Its mere presence suppresses that; the pause itself is done in `onPress`, which
   is the only callback that can await the release.
2. **`pointerInput(Unit)`, with the callbacks read through `rememberUpdatedState`.** Keying
   `pointerInput` on the lambdas — new instances every recomposition — tore the gesture down
   mid-press: pausing recomposes the viewer, which restarted `pointerInput`, which cancelled the
   `tryAwaitRelease()` that was going to resume. **The story then stayed paused forever.**
3. **Pause on touch-down, with nothing waited out first.** Gating the pause behind the long-press
   threshold as well left the bar visibly running for half a second under a resting thumb. The
   threshold decides only whether *lifting* the finger moves the story — it has no say in when the
   story stops.

## Holding the story — two independent reasons

A finger (`isTouchHeld`) and the comment keyboard (`isComposingComment`) both stop the same clock,
so neither may start or stop it alone. `StoryViewerViewModel.setHold()` is the only place either
is applied: the clock stops when the first hold arrives and starts again only when the last one
leaves. `isPaused` is derived from the two. Lifting a finger while the keyboard is up therefore
leaves the story exactly where it is.

## The comment field

Real field, no service: the keyboard opens, the story holds while it is up, `ImeAction.Send` and
the send button both clear it, and nothing is sent anywhere. Focus is the single signal — gaining
it holds the story, losing it (by sending, by tapping the picture, or by system back) releases it —
so no caller has to pair a "start" with a matching "stop".

While it has focus the field takes the whole action bar; the like and bookmark chips are dropped
rather than squeezed. The bottom column pads against `navigationBars.union(ime)`, which needs the
`adjustResize` already set on `MainActivity` in the manifest.

## Media

`StoryMedia.None` is the design's default — the channel gradient and the words, nothing else. It is
not a missing value.

An image slide runs the default 6.2 s; a clip's slide has **no** duration until the player reports
one (`isBuffering`, bar held at zero). Failure — reported, or the 8 s watchdog — falls back to the
default duration so a broken video can never strand the reader.

`StoryVideoPlayer` is a new `expect/actual`, deliberately **not** `feature/agent`'s `VideoPlayer`:
feature modules cannot depend on each other, and that one shows platform controls and reports only
`isPlaying`, where a story needs no controls and needs `onReady(durationMs)` / `onEnded` /
`onFailed`.

⚠️ The iOS actual (AVPlayer, polled readiness, `AVPlayerItemDidPlayToEndTimeNotification`) has
never been compiled — iOS does not build on Windows. Verify it on a Mac before trusting it.

## When the service arrives

1. Implement `StorySource` against the real endpoint and swap the one binding in `StoriesModule`.
   Nothing else in the feature has to move.
2. `StoryChannel.icon` becomes the URL the service sends instead of a bundled `DrawableResource`.
3. Likes, bookmarks and "seen" currently live in `StoryCatalog` in memory for the life of the
   process. They move out from there.
4. ⚠️ **Story media must not be fetched through the app's shared Coil loader.** `mainHttpClient`
   sets `sendWithoutRequest { true }`, so every image Coil fetches through it carries the user's
   access token — fine for the bundled files today, wrong the moment media comes off a CDN. Give
   story media its own unauthenticated `ImageLoader` then.
5. The comment field types and sends but goes nowhere — `CommentSubmitted` only clears the draft.
   Give it a destination there.

## A deliberate deviation

`.claude/rules/architecture.md` says feature modules have no local `data/` package. This one does,
because there is no backend to put a repository in front of and the brief was explicit about not
inventing one. `StorySource` is the seam that makes undoing it cheap.

## Tests

`feature/stories/src/commonTest` — 41 tests, `kotlin.test` + Turbine + `runTest`, hand-written
fakes, no Robolectric and no `getString` in either ViewModel (see [[Typography]] and the
`getstring-viewmodel-test-hazard` memory).

```powershell
.\gradlew.bat :feature:stories:testDebugUnitTest
```

Covered: list / empty / error / retry, duplicate-request prevention, opening the viewer, next,
previous, channel roll-over, close-at-the-end, auto-advance, pause, resume-from-remaining,
image duration, video buffering and reported duration, `onEnded`, media failure, the watchdog,
close, CTA, view tracking — and the two independent holds: the keyboard holding the story, a
finger lifting under it not restarting it, and vice versa.
