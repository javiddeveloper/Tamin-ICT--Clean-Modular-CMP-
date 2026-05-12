package com.tamin.taminhamrah.plugin.di

import com.tamin.taminhamrah.plugin.InMemoryPluginRegistry
import com.tamin.taminhamrah.plugin.PluginRegistry
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val pluginModule = module {
    singleOf(::InMemoryPluginRegistry) { bind<PluginRegistry>() }
}
