package com.tamin.common.network


import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val taminDispatcher: TaminDispatchers)

enum class TaminDispatchers {
    Default,
    IO,
}
