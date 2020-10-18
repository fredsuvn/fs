package xyz.srclab.common.base

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * @see [StaticInvoker]
 * @see [VirtualInvoker]
 * @see [InvokerProvider]
 * @see [ReflectedInvokerProvider]
 * @see [MethodHandlerInvokerProvider]
 */
interface Invoker {

    fun <T> invoke(vararg args: Any?): T

    companion object {

        @JvmStatic
        fun staticInvoker(method: Method): StaticInvoker {
            return StaticInvoker.forMethod(method)
        }

        @JvmStatic
        fun staticInvoker(constructor: Constructor<*>): StaticInvoker {
            return StaticInvoker.forConstructor(constructor)
        }

        @JvmStatic
        fun virtualInvoker(method: Method): VirtualInvoker {
            return VirtualInvoker.forMethod(method)
        }
    }
}

interface StaticInvoker : Invoker {

    override fun <T> invoke(vararg args: Any?): T

    companion object {

        @JvmStatic
        fun forMethod(method: Method): StaticInvoker {
            return InvokerProvider.defaultProvider().staticInvoker(method)
        }

        @JvmStatic
        fun forConstructor(constructor: Constructor<*>): StaticInvoker {
            return InvokerProvider.defaultProvider().staticInvoker(constructor)
        }
    }
}

interface VirtualInvoker : Invoker {

    override fun <T> invoke(vararg args: Any?): T

    fun <T> invoke(owner: Any?, vararg args: Any?): T

    companion object {

        @JvmStatic
        fun forMethod(method: Method): VirtualInvoker {
            return InvokerProvider.defaultProvider().virtualInvoker(method)
        }
    }
}

fun staticInvoker(method: Method): StaticInvoker {
    return Invoker.staticInvoker(method)
}

fun staticInvoker(constructor: Constructor<*>): StaticInvoker {
    return Invoker.staticInvoker(constructor)
}

fun virtualInvoker(method: Method): VirtualInvoker {
    return Invoker.virtualInvoker(method)
}

interface InvokerProvider {

    fun staticInvoker(method: Method): StaticInvoker

    fun staticInvoker(constructor: Constructor<*>): StaticInvoker

    fun virtualInvoker(method: Method): VirtualInvoker

    companion object {

        @JvmStatic
        fun defaultProvider(): InvokerProvider {
            return ReflectedInvokerProvider
        }
    }
}

object ReflectedInvokerProvider : InvokerProvider {

    override fun staticInvoker(method: Method): StaticInvoker {
        return ReflectedStaticMethodInvoker(method)
    }

    override fun staticInvoker(constructor: Constructor<*>): StaticInvoker {
        return ReflectedConstructorInvoker(constructor)
    }

    override fun virtualInvoker(method: Method): VirtualInvoker {
        return ReflectedVirtualMethodInvoker(method)
    }
}

object MethodHandlerInvokerProvider : InvokerProvider {

    override fun staticInvoker(method: Method): StaticInvoker {
        return StaticMethodHandlerInvoker(method)
    }

    override fun staticInvoker(constructor: Constructor<*>): StaticInvoker {
        return StaticMethodHandlerInvoker(constructor)
    }

    override fun virtualInvoker(method: Method): VirtualInvoker {
        return VirtualMethodHandlerInvoker(method)
    }
}

private class ReflectedStaticMethodInvoker(private val method: Method) : StaticInvoker {

    override fun <T> invoke(vararg args: Any?): T {
        return method.invoke(null, *args).asAny()
    }
}

private class ReflectedVirtualMethodInvoker(private val method: Method) : VirtualInvoker {

    override fun <T> invoke(vararg args: Any?): T {
        return method.invoke(null, *args).asAny()
    }

    override fun <T> invoke(owner: Any?, vararg args: Any?): T {
        return method.invoke(owner, *args).asAny()
    }
}

private class ReflectedConstructorInvoker(private val constructor: Constructor<*>) : StaticInvoker {

    override fun <T> invoke(vararg args: Any?): T {
        return constructor.newInstance(*args).asAny()
    }
}

private class StaticMethodHandlerInvoker private constructor() : StaticInvoker {

    private lateinit var methodHandle: MethodHandle

    constructor(method: Method) : this() {
        methodHandle = findStaticMethodHandle(method)
    }

    constructor(constructor: Constructor<*>) : this() {
        methodHandle = findStaticMethodHandle(constructor)
    }

    override fun <T> invoke(vararg args: Any?): T {
        return when (args.size) {
            0 -> methodHandle.invoke()
            1 -> methodHandle.invoke(args[0])
            2 -> methodHandle.invoke(args[0], args[1])
            3 -> methodHandle.invoke(args[0], args[1], args[2])
            4 -> methodHandle.invoke(args[0], args[1], args[2], args[3])
            5 -> methodHandle.invoke(args[0], args[1], args[2], args[3], args[4])
            6 -> methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5])
            7 -> methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6])
            8 -> methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7])
            else -> methodHandle.invokeWithArguments(*args)
        }.asAny()
    }

    private fun findStaticMethodHandle(method: Method): MethodHandle {
        val methodType: MethodType = when (method.parameterCount) {
            0 -> MethodType.methodType(method.returnType)
            1 -> MethodType.methodType(method.returnType, method.parameterTypes[0])
            else -> MethodType.methodType(method.returnType, method.parameterTypes)
        }
        return MethodHandles.lookup().findStatic(method.declaringClass, method.name, methodType)
    }

    private fun findStaticMethodHandle(constructor: Constructor<*>): MethodHandle {
        val methodType: MethodType = when (constructor.parameterCount) {
            0 -> MethodType.methodType(Void.TYPE)
            1 -> MethodType.methodType(Void.TYPE, constructor.parameterTypes[0])
            else -> MethodType.methodType(Void.TYPE, constructor.parameterTypes)
        }
        return MethodHandles.lookup().findConstructor(constructor.declaringClass, methodType)
    }
}

private class VirtualMethodHandlerInvoker(method: Method) : VirtualInvoker {

    private var methodHandle = findMethodHandle(method)

    override fun <T> invoke(vararg args: Any?): T {
        checkArgument(args.isNotEmpty(), "Arguments of invoking cannot be empty.")
        val owner = args[0]
        val arguments = args.copyOfRange(1, args.size)
        return invoke0(owner, *arguments)
    }

    override fun <T> invoke(owner: Any?, vararg args: Any?): T {
        return invoke0(owner, *args)
    }

    private fun <T> invoke0(owner: Any?, vararg args: Any?): T {
        return when (args.size) {
            0 -> methodHandle.invoke(owner)
            1 -> methodHandle.invoke(owner, args[0])
            2 -> methodHandle.invoke(owner, args[0], args[1])
            3 -> methodHandle.invoke(owner, args[0], args[1], args[2])
            4 -> methodHandle.invoke(owner, args[0], args[1], args[2], args[3])
            5 -> methodHandle.invoke(owner, args[0], args[1], args[2], args[3], args[4])
            6 -> methodHandle.invoke(owner, args[0], args[1], args[2], args[3], args[4], args[5])
            7 -> methodHandle.invoke(owner, args[0], args[1], args[2], args[3], args[4], args[5], args[6])
            8 -> methodHandle.invoke(owner, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7])
            else -> methodHandle.invokeWithArguments(owner, *args)
        }.asAny()
    }

    private fun findMethodHandle(method: Method): MethodHandle {
        val methodType: MethodType = when (method.parameterCount) {
            0 -> MethodType.methodType(method.returnType)
            1 -> MethodType.methodType(method.returnType, method.parameterTypes[0])
            else -> MethodType.methodType(method.returnType, method.parameterTypes)
        }
        return MethodHandles.lookup().findVirtual(method.declaringClass, method.name, methodType)
    }
}