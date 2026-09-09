package com.tamin.taminhamrah.feature.stories.di

import com.tamin.taminhamrah.feature.stories.data.MockStorySource
import com.tamin.taminhamrah.feature.stories.data.StoryCatalog
import com.tamin.taminhamrah.feature.stories.data.StorySource
import com.tamin.taminhamrah.feature.stories.ui.rail.StoryRailViewModel
import com.tamin.taminhamrah.feature.stories.ui.viewer.StoryViewerViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val storiesModule = module {
    // A single, and that is the point: the rail on the home page and the viewer over it share one
    // catalogue, so opening a channel costs no second fetch and closing it greys the ring behind.
    singleOf(::StoryCatalog)
    // The one binding to swap when a stories service exists. Constructed by hand rather than with
    // singleOf, whose constructor reference would try to resolve the simulated latency as a
    // dependency instead of letting it default.
    single<StorySource> { MockStorySource() }

    viewModelOf(::StoryRailViewModel)
    viewModelOf(::StoryViewerViewModel)
}
