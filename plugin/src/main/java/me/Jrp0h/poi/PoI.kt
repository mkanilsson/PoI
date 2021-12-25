package me.Jrp0h.poi

import java.util.UUID
import me.Jrp0h.poi.AddCoord.Category
import me.Jrp0h.poi.AddCoord.Step
import me.Jrp0h.poi.AddCoord.World
import me.Jrp0h.poi.database.DatabaseManager
import me.Jrp0h.poi.models.PointsOfInterest
import me.Jrp0h.poi.utils.Compare
import me.Jrp0h.poi.utils.Logger
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin

class PoI : JavaPlugin(), Listener {

    val addCoords = HashMap<UUID, AddCoord>()

    val categories = AddCoord.Category.Misc

    override fun onLoad() {
        Config.load(this)
        DatabaseManager.connect(this)
    }

    override fun onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this)
        Logger.info("Enabled")
    }

    override fun onCommand(
            sender: CommandSender,
            cmd: Command,
            label: String,
            args: Array<String>
    ): Boolean {
        if (Compare.ignoreCase(label, "poihelp")) return cmdHelp(sender)
        else if (Compare.ignoreCase(label, "poilist")) {
            if (sender is Player) return cmdList(sender)
            return true
        }
        if (Compare.ignoreCase(label, "poiadd")) {
            if (sender is Player) return cmdAdd(sender)
            return true
        }
        if (Compare.ignoreCase(label, "poi")) {
            if (sender is Player) return cmdPoi(sender, cmd, label, args)
            return true
        }

        return false
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.getPlayer()

        if (addCoords.containsKey(player.uniqueId)) {
            val coord = addCoords.get(player.uniqueId)

            if (coord == null) {
                Logger.error("Unreachable")
                return
            }

            val message = event.getMessage()

            when (coord.currentStep) {
                Step.Name -> {
                    if (message.length < 3) {
                        Logger.info("Name was too short")
                        val builder = StringBuilder()
                        builder.append(ChatColor.RED)
                                .append("")
                                .append("Name must be over 3 characters\n")
                                .append(ChatColor.BLUE)
                                .append("Please enter a name!")

                        Logger.info(builder.toString())
                        player.sendMessage(builder.toString())
                        event.setCancelled(true)
                        return
                    } else {
                        coord.incrementStep()
                        coord.name = event.getMessage()

                        val builder = StringBuilder()
                        builder.append(ChatColor.GREEN)
                                .append("Name: ")
                                .append(ChatColor.YELLOW)
                                .append(message)
                                .append(ChatColor.GREEN)
                                .append(" has been set!")

                        player.sendMessage(builder.toString())
                        builder.clear()
                        builder.append(ChatColor.BLUE).append("Please enter a descrption!")
                        player.sendMessage(builder.toString())
                        event.setCancelled(true)
                    }
                }
                Step.Description -> {
                    if (message.length < 3) {
                        Logger.info("Description was too short")
                        val builder = StringBuilder()
                        builder.append(ChatColor.RED)
                                .append("")
                                .append("Description must be over 3 characters\n")
                                .append(ChatColor.BLUE)
                                .append("Please enter a description!")

                        Logger.info(builder.toString())
                        player.sendMessage(builder.toString())
                        event.setCancelled(true)
                        return
                    } else {
                        coord.incrementStep()
                        coord.description = event.getMessage()

                        val builder = StringBuilder()
                        builder.append(ChatColor.GREEN)
                                .append("Description: ")
                                .append(ChatColor.YELLOW)
                                .append(message)
                                .append(ChatColor.GREEN)
                                .append(" has been set!")

                        player.sendMessage(builder.toString())
                        builder.clear()
                        builder.append(ChatColor.BLUE).append("Please enter a category!")
                        cmdList(player)
                        player.sendMessage(builder.toString())
                        event.setCancelled(true)
                    }
                }
                Step.Category -> {
                    val categoryAsArray = enumValues<Category>().asList()
                    try {
                        val id = event.getMessage().toInt()

                        if (id < 1 || id > categoryAsArray.size)
                                throw IllegalArgumentException("Id too low or too high")
                        else {
                            coord.incrementStep()
                            coord.setCategoryFromIndex(id - 1)

                            val builder = StringBuilder()
                            builder.append(ChatColor.GREEN)
                                    .append("Category: ")
                                    .append(ChatColor.YELLOW)
                                    .append(categoryAsArray[id - 1])
                                    .append(ChatColor.GREEN)
                                    .append(" has been set!")

                            player.sendMessage(builder.toString())
                            builder.clear()
                            builder.append(ChatColor.GRAY).append("All done, trying to save!")
                            player.sendMessage(builder.toString())

                            if (coord.save()) {
                                addCoords.remove(player.uniqueId)
                                player.setLevel(player.getLevel() + 2)
                                builder.clear()
                                builder.append(ChatColor.GREEN)
                                        .append("Coordinates has been saved!")
                                player.sendMessage(builder.toString())
                            } else {
                                builder.clear()
                                builder.append(ChatColor.RED)
                                        .append(
                                                "Something went wrong, type something in chat to try again. You can type cancel to cancel."
                                        )
                                player.sendMessage(builder.toString())
                                coord.setFailed()
                            }

                            event.setCancelled(true)
                        }
                    } catch (e: Exception) {
                        cmdList(player)
                        val builder = StringBuilder()
                        builder.append(ChatColor.RED).append("Invalid ID, please select a number!")
                        player.sendMessage(builder.toString())
                        event.setCancelled(true)
                        return
                    }
                }
                Step.Failed -> {
                    if (Compare.ignoreCase(message, "cancel")) {
                        val builder = StringBuilder()
                        builder.append(ChatColor.GRAY).append("Canceling adding the coordinates")
                        player.sendMessage(builder.toString())
                    } else {
                        if (coord.save()) {
                            addCoords.remove(player.uniqueId)
                            player.setLevel(player.getLevel() + 2)
                        } else {
                            val builder = StringBuilder()
                            builder.clear()
                            builder.append(ChatColor.RED)
                                    .append(
                                            "Something went wrong, type something in chat to try again. You can type cancel to cancel."
                                    )
                            coord.setFailed()
                        }
                    }
                    event.setCancelled(true)
                }
                else -> {
                    Logger.info("Else in when-block")
                }
            }
        } else {
            Logger.info("Player not found")
        }
    }

    override fun onDisable() {
        Logger.info("Disabled")
    }

    fun cmdHelp(sender: CommandSender): Boolean {
        sender.sendMessage("poihelp: Displays this message")
        sender.sendMessage("poi: Gets the five closest points of interest closest to you")
        sender.sendMessage("poiadd: Adds a point of interest from the coords you're currently on")
        sender.sendMessage("poilist: List all categories")
        return true
    }

    fun cmdList(p: Player): Boolean {
        enumValues<Category>().forEachIndexed { i, category ->
            val builder = StringBuilder()
            builder.append(ChatColor.LIGHT_PURPLE)
                    .append("")
                    .append(i + 1)
                    .append(" - ")
                    .append(AddCoord.categoryToString(category))

            p.sendMessage(builder.toString())
        }

        return true
    }

    fun cmdAdd(player: Player): Boolean {

        val location = player.getLocation()

        val x: Int = location.getX().toInt()
        val y: Int = location.getY().toInt()
        val z: Int = location.getZ().toInt()

        var world: World

        val minecraftWorld = location.getWorld()
        if (minecraftWorld != null) {
            val wfs = AddCoord.worldFromString(minecraftWorld.getName())
            if (wfs == null) {
                val builder = StringBuilder()
                builder.append(ChatColor.RED)
                        .append("")
                        .append("Something went wrong: Cound not find world from string")

                player.sendMessage(builder.toString())

                Logger.error("Could not find world from string")
                return true
            } else world = wfs
        } else {
            val builder = StringBuilder()
            builder.append(ChatColor.RED)
                    .append("")
                    .append("Something went wrong: Cound not find world")

            player.sendMessage(builder.toString())

            Logger.error("Could not find minecraft world")
            return true
        }

        addCoords.put(player.uniqueId, AddCoord(x, y, z, player.getDisplayName(), world))
        Logger.info("Player added")

        val builder = StringBuilder()
        builder.append(ChatColor.BLUE).append("").append("Please enter a name!")
        player.sendMessage(builder.toString())
        return true
    }

    fun cmdPoi(player: Player, cmd: Command, label: String, args: Array<String>): Boolean {
        val location = player.getLocation()

        val x: Int = location.getX().toInt()
        val y: Int = location.getY().toInt()
        val z: Int = location.getZ().toInt()

        var category: Category? = null
        var page = 0

        var world: World

        val minecraftWorld = location.getWorld()
        if (minecraftWorld != null) {
            val wfs = AddCoord.worldFromString(minecraftWorld.getName())
            if (wfs == null) {
                val builder = StringBuilder()
                builder.append(ChatColor.RED)
                        .append("")
                        .append("Something went wrong: Cound not find world from string")

                player.sendMessage(builder.toString())

                Logger.error("Could not find world from string")
                return true
            } else world = wfs
        } else {
            val builder = StringBuilder()
            builder.append(ChatColor.RED)
                    .append("")
                    .append("Something went wrong: Cound not find world")

            player.sendMessage(builder.toString())

            Logger.error("Could not find minecraft world")
            return true
        }

        val rx = Regex("^[A-Za-z]+=[A-Za-z0-9]+$")
        args.forEach { arg ->
            if (rx.matches(arg)) {
                val option_value = arg.split("=")
                val option = option_value[0]
                val value = option_value[1]

                if (Compare.ignoreCase(option, "category") || Compare.ignoreCase(option, "c")) {
                    val v = value.toIntOrNull()
                    if (v == null) {
                        val builder = StringBuilder()
                        builder.append(ChatColor.RED)
                                .append("")
                                .append("Category must be a number. See /poilist")
                        player.sendMessage(builder.toString())
                        return true
                    }
                    category = AddCoord.categoryFromInt(v)
                    Logger.info("Category:", category.toString())
                } else if (Compare.ignoreCase(option, "page") || Compare.ignoreCase(option, "p")) {
                    val v = value.toIntOrNull()
                    if (v == null) {
                        val builder = StringBuilder()
                        builder.append(ChatColor.RED).append("").append("Page must be a number.")
                        player.sendMessage(builder.toString())
                        return true
                    }
                    page = v
                } else {
                    Logger.warning("Unknown command:", option, ",", value)
                }
            }
        }

        val result = PointsOfInterest.getPointsOfInterest(x, y, z, world, page, category)

        val pois = result.second

        pois.forEachIndexed { i, poi ->
            val builder = StringBuilder()
            builder.append(ChatColor.YELLOW)
                    .append(i + 1)
                    .append(" - ")
                    .append(poi.name)
                    .append("\n")
            builder.append(ChatColor.AQUA).append(poi.distance).append(" blocks away").append("\n")
            builder.append(ChatColor.GRAY)
                    .append(AddCoord.categoryToString(poi.category))
                    .append(", X: ")
                    .append(poi.x)
                    .append(", Y:")
                    .append(poi.y)
                    .append(", Z:")
                    .append(poi.z)
                    .append("\n")
            player.sendMessage(builder.toString())
        }

        if (result.first) {
            val builder = StringBuilder()
            builder.append(ChatColor.GRAY)
                    .append(
                            "\nTheir are more coordinate that meet your criteria use page=<number> to go to another page\nExample: /poi page=2"
                    )
            player.sendMessage(builder.toString())
        }

        return true
    }
}
