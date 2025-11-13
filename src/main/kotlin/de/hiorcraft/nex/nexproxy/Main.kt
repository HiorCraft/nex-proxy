package de.hiorcraft.nex.nexproxy

import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.util.MinecraftChannelIdentifier
import com.velocitypowered.api.util.ChannelIdentifier
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.slf4j.Logger

@Plugin(
    id = "nexproxy",
    name = "NexProxy",
    version = "1.3.0",
    authors = ["HiorCraft"]
)
class Main @Inject constructor(
    private val logger: Logger,
    private val server: ProxyServer
) {


    @Subscribe
    fun onProxyInitialize(event: ProxyInitializeEvent) {
        logger.info("NexProxy wurde erfolgreich gestartet!")

        val sendCommand = LiteralArgumentBuilder.literal<CommandSource>("send")
            .then(
                RequiredArgumentBuilder.argument<CommandSource, String>("player", StringArgumentType.word())
                    .then(
                        RequiredArgumentBuilder.argument<CommandSource, String>("server", StringArgumentType.word())
                            .executes { context ->
                                val source = context.source


                                if (!source.hasPermission("nexproxy.send")) {
                                    source.sendMessage(Component.text("Keine Berechtigung für diesen Befehl.").color(NamedTextColor.RED))
                                    return@executes 0
                                }
                                val playerName = StringArgumentType.getString(context, "player")
                                val serverName = StringArgumentType.getString(context, "server")

                                val player = server.getPlayer(playerName)
                                val targetServer = server.getServer(serverName)

                                if (player.isEmpty) {
                                    source.sendMessage(Component.text("Spieler '$playerName' nicht gefunden.").color(NamedTextColor.RED))
                                    return@executes 0
                                }
                                if (targetServer.isEmpty) {
                                    source.sendMessage(Component.text("Server '$serverName' nicht gefunden.").color(NamedTextColor.RED))
                                    return@executes 0
                                }

                                player.get().createConnectionRequest(targetServer.get()).connect()

                                source.sendMessage(
                                    Component.text("✔ Spieler ")
                                        .append(Component.text(playerName).color(NamedTextColor.GREEN))
                                        .append(Component.text(" wurde zu "))
                                        .append(Component.text(serverName).color(NamedTextColor.AQUA))
                                        .append(Component.text(" gesendet."))
                                )
                                1
                            }
                    )
            )

        server.commandManager.register(BrigadierCommand(sendCommand))
    }
}
