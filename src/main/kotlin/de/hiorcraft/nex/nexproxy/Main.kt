package de.hiorcraft.nex.nexproxy;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.ChannelIdentifier
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.slf4j.Logger
import java.io.ByteArrayInputStream
import java.io.DataInput
import java.io.DataInputStream


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
    }

    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        if (event.identifier == teamChatChannel) {
            val data = DataInputStream(ByteArrayInputStream(event.data))
            val playerName = data.readUTF()
            val message = data.readUTF()

            server.allPlayers
                .filter { it.hasPermission("teamchat.use") }
                .forEach { player ->
                    player.sendMessage(
                        Component.text("(TEAM) $playerName: $message", NamedTextColor.DARK_AQUA)
                    )
                }
        }
    }
}