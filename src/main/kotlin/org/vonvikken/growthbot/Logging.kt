package org.vonvikken.growthbot

import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal fun logger(instance: () -> Unit): Lazy<Logger> = lazy { LoggerFactory.getLogger(instance.javaClass) }
