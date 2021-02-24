@file:OptIn(ExperimentalContracts::class)
package com.example.coroutine_project

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.internal.*
import kotlinx.coroutines.intrinsics.*
import kotlin.contracts.*
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.*

interface CoroutineActivityScope {
    /**
     * The context of this scope.
     * Context is encapsulated by the scope and used for implementation of coroutine builders that are extensions on the scope.
     * Accessing this property in general code is not recommended for any purposes except accessing the [Job] instance for advanced usages.
     *
     * By convention, should contain an instance of a [job][Job] to enforce structured concurrency.
     */
    public val coroutineContext: CoroutineContext
}

/**
 * Adds the specified coroutine context to this scope, overriding existing elements in the current
 * scope's context with the corresponding keys.
 *
 * This is a shorthand for `CoroutineScope(thisScope + context)`.
 */
public operator fun CoroutineActivityScope.plus(context: CoroutineContext): CoroutineScope =
    CoroutineScope(coroutineContext + context)

/**
 * Creates the main [CoroutineScope] for UI components.
 *
 * Example of use:
 * ```
 * class MyAndroidActivity {
 *     private val scope = MainScope()
 *
 *     override fun onDestroy() {
 *         super.onDestroy()
 *         scope.cancel()
 *     }
 * }
 * ```
 *
 * The resulting scope has [SupervisorJob] and [Dispatchers.Main] context elements.
 * If you want to append additional elements to the main scope, use [CoroutineScope.plus] operator:
 * `val scope = MainScope() + CoroutineName("MyActivity")`.
 */
@Suppress("FunctionName")
public fun MainScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

/**
 * Returns `true` when the current [Job] is still active (has not completed and was not cancelled yet).
 *
 * Check this property in long-running computation loops to support cancellation:
 * ```
 * while (isActive) {
 *     // do some computation
 * }
 * ```
 *
 * This property is a shortcut for `coroutineContext.isActive` in the scope when
 * [CoroutineScope] is available.
 * See [coroutineContext][kotlin.coroutines.coroutineContext],
 * [isActive][kotlinx.coroutines.isActive] and [Job.isActive].
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public val CoroutineScope.isActive: Boolean
    get() = coroutineContext[Job]?.isActive ?: true

/**
 * A global [CoroutineScope] not bound to any job.
 *
 * Global scope is used to launch top-level coroutines which are operating on the whole application lifetime
 * and are not cancelled prematurely.
 * Another use of the global scope is operators running in [Dispatchers.Unconfined], which don't have any job associated with them.
 *
 * Application code usually should use an application-defined [CoroutineScope]. Using
 * [async][CoroutineScope.async] or [launch][CoroutineScope.launch]
 * on the instance of [GlobalScope] is highly discouraged.
 *
 * Usage of this interface may look like this:
 *
 * ```
 * fun ReceiveChannel<Int>.sqrt(): ReceiveChannel<Double> = GlobalScope.produce(Dispatchers.Unconfined) {
 *     for (number in this) {
 *         send(Math.sqrt(number))
 *     }
 * }
 * ```
 */
public object GlobalScope : CoroutineScope {
    /**
     * Returns [EmptyCoroutineContext].
     */
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}




