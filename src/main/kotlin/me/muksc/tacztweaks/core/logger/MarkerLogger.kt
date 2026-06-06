package me.muksc.tacztweaks.core.logger

import org.slf4j.Logger
import org.slf4j.Marker

//? if >=1.20.5 {
/*import org.slf4j.event.Level
import org.slf4j.spi.LoggingEventBuilder
*///?}

class MarkerLogger(
    val base: Logger,
    val marker: Marker
) : Logger by base {
    //? if >=1.20.5 {
    /*override fun makeLoggingEventBuilder(level: Level?): LoggingEventBuilder? = base.makeLoggingEventBuilder(level)

    override fun atLevel(level: Level?): LoggingEventBuilder? = base.atLevel(level)

    override fun isEnabledForLevel(level: Level?): Boolean = base.isEnabledForLevel(level)

    override fun atTrace(): LoggingEventBuilder? = base.atTrace()

    override fun atDebug(): LoggingEventBuilder? = base.atDebug()

    override fun atInfo(): LoggingEventBuilder? = base.atInfo()

    override fun atWarn(): LoggingEventBuilder? = base.atWarn()

    override fun atError(): LoggingEventBuilder? = base.atError()
    *///?}

    override fun isTraceEnabled(): Boolean = base.isTraceEnabled(marker)

    override fun trace(msg: String?) = base.trace(marker, msg)

    override fun trace(format: String?, arg: Any?) = base.trace(marker, format, arg)

    override fun trace(format: String?, arg1: Any?, arg2: Any?) = base.trace(marker, format, arg1, arg2)

    override fun trace(format: String?, vararg arguments: Any?) = base.trace(marker, format, *arguments)

    override fun trace(msg: String?, t: Throwable?) = base.trace(marker, msg, t)

    override fun isDebugEnabled(): Boolean = base.isDebugEnabled(marker)

    override fun debug(msg: String?) = base.debug(marker, msg)

    override fun debug(format: String?, arg: Any?) = base.debug(marker, format, arg)

    override fun debug(format: String?, arg1: Any?, arg2: Any?) = base.debug(marker, format, arg1, arg2)

    override fun debug(format: String?, vararg arguments: Any?) = base.debug(marker, format, *arguments)

    override fun debug(msg: String?, t: Throwable?) = base.debug(marker, msg, t)

    override fun isInfoEnabled(): Boolean = base.isInfoEnabled(marker)

    override fun info(msg: String?) = base.info(marker, msg)

    override fun info(format: String?, arg: Any?) = base.info(marker, format, arg)

    override fun info(format: String?, arg1: Any?, arg2: Any?) = base.info(marker, format, arg1, arg2)

    override fun info(format: String?, vararg arguments: Any?) = base.info(marker, format, *arguments)

    override fun info(msg: String?, t: Throwable?) = base.info(marker, msg, t)

    override fun isWarnEnabled(): Boolean = base.isWarnEnabled(marker)

    override fun warn(msg: String?) = base.warn(marker, msg)

    override fun warn(format: String?, arg: Any?) = base.warn(marker, format, arg)

    override fun warn(format: String?, arg1: Any?, arg2: Any?) = base.warn(marker, format, arg1, arg2)

    override fun warn(format: String?, vararg arguments: Any?) = base.warn(marker, format, *arguments)

    override fun warn(msg: String?, t: Throwable?) = base.warn(marker, msg, t)

    override fun isErrorEnabled(): Boolean = base.isErrorEnabled(marker)

    override fun error(msg: String?) = base.error(marker, msg)

    override fun error(format: String?, arg: Any?) = base.error(marker, format, arg)

    override fun error(format: String?, arg1: Any?, arg2: Any?) = base.error(marker, format, arg1, arg2)

    override fun error(format: String?, vararg arguments: Any?) = base.error(marker, format, *arguments)

    override fun error(msg: String?, t: Throwable?) = base.error(marker, msg, t)
}

fun Logger.withMarker(marker: Marker): MarkerLogger =
    MarkerLogger(if (this is MarkerLogger) base else this, marker)