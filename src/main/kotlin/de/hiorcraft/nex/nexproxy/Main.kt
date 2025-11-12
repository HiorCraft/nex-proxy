package de.hiorcraft.nex.nexproxy;

import com.google.inject.Inject
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult.command
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.ChannelIdentifier
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import net.kyori.adventure.text.Component
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

    private val teamChatChannel: ChannelIdentifier = MinecraftChannelIdentifier.create("nex", "teamchat")

    @Subscribe
    fun onProxyInitialize(event: ProxyInitializeEvent) {
        logger.info("NexProxy wurde erfolgreich gestartet!")

        // Registrierung des Plugin-Messaging-Kanals
        server.channelRegistrar.register(teamChatChannel)

        // Brigadier-Command direkt in Main erstellen
        val sendLiteral = LiteralArgumentBuilder.literal<CommandSource>("send")
            .then(
                RequiredArgumentBuilder.argument<CommandSource, String>("player", StringArgumentType.word())
                    .then(
                        RequiredArgumentBuilder.argument<CommandSource, String>("server", StringArgumentType.word())
                            .executes { context ->
                                val playerName = StringArgumentType.getString(context, "player")
                                val serverName = StringArgumentType.getString(context, "server")

                                val player = server.getPlayer(playerName)
                                val targetServer = server.getServer(serverName)

                                if (player.isEmpty || targetServer.isEmpty) {
                                    context.source.sendMessage(Component.text("Spieler oder Server nicht gefunden."))
                                    return@executes 0
                                }

                                player.get().createConnectionRequest(targetServer.get()).connect()
                                context.source.sendMessage(Component.text("Sende $playerName zu $serverName..."))
                                1
                            }
                    )
            )

        val brigadierCommand = BrigadierCommand(sendLiteral)

        // CommandMeta erstellen und registrieren (API: register(CommandMeta, Command))
        val meta = server.commandManager.metaBuilder("send").build()
        server.commandManager.register(meta, brigadierCommand)
    }
}