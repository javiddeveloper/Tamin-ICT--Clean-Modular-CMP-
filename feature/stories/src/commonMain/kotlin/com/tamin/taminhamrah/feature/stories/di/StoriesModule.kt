package com.tamin.taminhamrah.feature.stories.di

import com.tamin.taminhamrah.feature.stories.ui.rail.StoryRailViewModel
import com.tamin.taminhamrah.feature.stories.ui.viewer.StoryViewerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Only the two ViewModels: the catalogue itself is a repository, registered as a `single` in
 * `dataKoinModule`, and the use cases in front of it come from `domainModule`.
 *
 * That the repository is a single is what makes the rail and the viewer agree — closing a story
 * greys the ring behind it — and it is why opening the viewer costs no second fetch.
 */
val storiesModule = module {
    viewModelOf(::StoryRailViewModel)
    viewModelOf(::StoryViewerViewModel)
}
