---
tags: [feature, ui]
---

# Stories — «تازه‌ها»

Instagram-shaped story browsing: a horizontal rail of publisher rings on the home page, and a
full-screen viewer that plays a channel's slides in order.

Module `:feature:stories`, package `…feature.stories`. Registered in `settings.gradle.kts`,
`shared/build.gradle.kts`, `sharedModules` (`storiesModule`) and the central `NavHost`.

> **No web service yet.** The feature is layered exactly as every other one — DN models,
> repository interface, implementation, use cases, PR models, mapper — but the implementation
> answers from a bundled catalogue instead of a remote data source. There is deliberately **no
> route, no DTO and no remote data source**: those are the only pieces the endpoint will add.
> See [[#When the service arrives]].

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

## The pieces, by layer

```
core-domain
  model/stories/StoryChannelDN.kt   StoryChannelDN / StoryItemDN / StoryMediaDN / StoryCtaDN
                                    / StoryEngagementDN — no colors, no assets, no formatting
  repository/stories/               StoryRepository (the interface the endpoint will satisfy)
  useCases/stories/                 GetStoryChannels, ObserveSeenStoryChannels,
                                    MarkStoryChannelSeen, ObserveStoryEngagement,
                                    ToggleStoryLike, ToggleStorySave

core-data
  data/repository/stories/
    StoryRepositoryImpl.kt          cache, in-flight guard, seen/liked/saved state
    StoryMockCatalog.kt             the bundled five channels — deleted whole when the API lands

feature/stories
  ui/model/StoryPR.kt               adds the palette and the icon the domain left out
  ui/mapper/StoryUiMapper.kt        DN → PR, plus the per-channel look table
  ui/theme/StoryTokens.kt           StoryDimens, StoryPalette, storyTextStyles(), the ink
  ui/rail/                          StoryRail + StoryRailViewModel + contract
  ui/viewer/                        StoryViewerScreen, StoryProgressBar, StoryVideoPlayer
```

`StoryRepositoryImpl` is a Koin `single`, and that is what makes the two screens agree: the viewer
marks a channel watched and the ring behind it on the home page goes grey, without either screen
knowing about the other. It is also why opening the viewer costs no second fetch — the guard in
`loadOnce` answers instead.

### Two decisions worth knowing

**The palette and the icon are not in the domain.** A `Color` and a `DrawableResource` are
presentation, so `StoryChannelDN` carries none. `StoryUiMapper` adds both from the channel `key`
through one lookup table, and that table **falls back** rather than failing — a channel published
after this build shipped renders in the default palette instead of crashing the rail.

**`StoryMediaDN.SampleImage` / `SampleVideo` are mock-only variants, not paths.** The mock could
not name a file in the feature's own asset bundle without core-data knowing about it, so it names
*what the slide is* and the mapper resolves it (`Res.getUri`, which is an ordinary function, not a
suspending one — so the mapper stays plain and nothing touches the asset bundle from a unit test).
Deleting those two variants when the service lands makes the compiler point at every site.

## The segment clock — the one design decision worth knowing

**There is no `progress: Float` in the viewer's state.** A bar that filled from state would push a
new state through the whole MVI pipeline every frame it moved.

Instead the state describes the *segment* — `segmentDurationMs`, `segmentElapsedMs`, `isBuffering`,
the two hold flags, and a `segmentToken` — and `StoryProgressBar` animates itself. The ViewModel
bumps the token when the segment itself changes: a new slide, a clip reporting its length, media
failing. Holds need no bump: the effect is also keyed on `isPlaying`, so a hold arriving cancels
the animation where it stands and leaving resumes it from that exact value.

### The Animatable is built per segment, in composition

```kotlin
val fill = remember(segmentToken) { Animatable(startFraction) }
```

Not one long-lived `Animatable` snapped back to zero from the effect. `segmentToken` and
`itemIndex` change together in the composition pass, but an effect body only runs *after* it — so
with a shared instance there was one frame in which the incoming segment was drawn with the
outgoing one's fill. It read as the next bar starting part-filled and instantly correcting itself,
and it was reported. Creating the value alongside the index it belongs to closes the window
instead of racing it.

The same change lets the remaining time be measured from `fill.value` rather than from the clock's
tally, so resuming carries on from the pixel that is actually on screen.

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

The three pieces that were deliberately skipped are exactly the three you add:

1. **Route + DTO + remote data source** in `core-network`, per [[Networking]].
2. Point `StoryRepositoryImpl` at that data source instead of `mockStoryChannels()`, and delete
   `StoryMockCatalog.kt`. The interface, the use cases, the mapper, the models and both ViewModels
   do not move.
3. Delete `StoryMediaDN.SampleImage` / `SampleVideo` and the bundled files under
   `composeResources/files/`; the compiler will point at the mapper branches to remove.

Then:

4. `StoryChannelPR.icon` can become a URL the service sends rather than a bundled drawable — the
   rail loads it the way the viewer already loads a slide's picture.
5. Likes, bookmarks and "seen" live in `StoryRepositoryImpl` in memory for the life of the process.
   That is where they stop being local; the interface above them does not change.
4. ⚠️ **Story media must not be fetched through the app's shared Coil loader.** `mainHttpClient`
   sets `sendWithoutRequest { true }`, so every image Coil fetches through it carries the user's
   access token — fine for the bundled files today, wrong the moment media comes off a CDN. Give
   story media its own unauthenticated `ImageLoader` then.
5. The comment field types and sends but goes nowhere — `CommentSubmitted` only clears the draft.
   Give it a destination there.

## Tests

`feature/stories/src/commonTest` — 41 tests, `kotlin.test` + Turbine + `runTest`, hand-written
fakes (`FakeStoryRepository` implements the domain interface), no Robolectric and no `getString` in either ViewModel (see [[Typography]] and the
`getstring-viewmodel-test-hazard` memory).

```powershell
.\gradlew.bat :feature:stories:testDebugUnitTest
```

Covered: list / empty / error / retry, duplicate-request prevention, opening the viewer, next,
previous, channel roll-over, close-at-the-end, auto-advance, pause, resume-from-remaining,
image duration, video buffering and reported duration, `onEnded`, media failure, the watchdog,
close, CTA, view tracking — and the two independent holds: the keyboard holding the story, a
finger lifting under it not restarting it, and vice versa.
