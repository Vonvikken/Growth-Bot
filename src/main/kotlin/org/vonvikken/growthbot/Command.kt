package org.vonvikken.growthbot

internal typealias CommandCallback = () -> Unit

internal sealed class Command(val commandName: String, val description: String, val callback: CommandCallback)

// TODO Implement callbacks

internal object NewBaby : Command("new", "Add a new baby.", {})

internal object SwitchBaby : Command("switch", "Switch to another existing baby.", {})

internal object DeleteBaby : Command("delete", "Delete an existing baby.", {})

internal object Weight : Command("weight", "Add a weight measurement.", {})

internal object Length : Command("length", "Add a length measurement.", {})
