package com.tamin.taminhamrah.feature.orotezprotez.test

import org.jetbrains.compose.resources.DensityQualifier
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.LanguageQualifier
import org.jetbrains.compose.resources.RegionQualifier
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.ThemeQualifier

/**
 * `getString(Res.string...)` resolves its [ResourceEnvironment] on the Android target via
 * `Resources.getSystem()`, which throws "not mocked" under plain JVM unit tests (this repo has no
 * Robolectric). Compose Multiplatform Resources ships an `internal var getResourceEnvironment`
 * function reference in `ResourceEnvironmentKt` explicitly commented "will be overridden for
 * tests" — but `internal` blocks a normal Kotlin call from a different Gradle module, so this
 * reaches it via reflection, the same way calling a public-in-bytecode member from Java would.
 *
 * The property is declared as `internal var getResourceEnvironment = ::getSystemEnvironment`, so
 * its inferred type — and the setter's erased parameter type — is `kotlin.reflect.KFunction`, not
 * `kotlin.jvm.functions.Function0`. The replacement must likewise be a real function reference
 * ([buildEnvironment]), not a lambda, or the assigned value won't be a `KFunction` either.
 */
@OptIn(InternalResourceApi::class)
internal object FakeComposeResourceEnvironment {

    private var installed = false

    @Synchronized
    fun install() {
        if (installed) return

        val holder = Class.forName("org.jetbrains.compose.resources.ResourceEnvironmentKt")
        val setter = holder.getDeclaredMethod(
            "setGetResourceEnvironment",
            kotlin.reflect.KFunction::class.java,
        ).apply { isAccessible = true }
        setter.invoke(null, ::buildEnvironment)

        installed = true
    }

    private fun buildEnvironment(): ResourceEnvironment {
        val constructor = ResourceEnvironment::class.java.getDeclaredConstructor(
            LanguageQualifier::class.java,
            RegionQualifier::class.java,
            ThemeQualifier::class.java,
            DensityQualifier::class.java,
        ).apply { isAccessible = true }
        return constructor.newInstance(
            LanguageQualifier("fa"),
            RegionQualifier("IR"),
            ThemeQualifier.LIGHT,
            DensityQualifier.MDPI,
        )
    }
}
