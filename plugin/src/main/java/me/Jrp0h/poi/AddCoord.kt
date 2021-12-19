package me.Jrp0h.poi

import me.Jrp0h.poi.utils.Compare

class AddCoord {
    companion object {
        public fun worldFromString(world: String): World? {
            if (Compare.ignoreCase(world, "overworld") || Compare.ignoreCase(world, "world"))
                    return World.Overworld
            if (Compare.ignoreCase(world, "nether") || Compare.ignoreCase(world, "world_nether"))
                    return World.Nether
            if (Compare.ignoreCase(world, "end") || Compare.ignoreCase(world, "world_the_end"))
                    return World.End

            return null
        }

        public fun categoryToString(category: Category): String {
            when (category) {
                Category.DesertTemple -> return "Desert Temple"
                Category.JungleTemple -> return "Jungle Temple"
                Category.GuardianTemple -> return "Guardian Temple"
                Category.OceanRuins -> return "Ocean Ruins"
                Category.PillagerOutpost -> return "Pillager Outpost"
                Category.EndPortal -> return "End Portal"
                Category.NetherPortal -> return "Nether Portal"
                Category.MushroomIsland -> return "Mushroom Island"
                Category.NetherFortress -> return "Nether Fortress"
                else -> return category.toString()
            }
        }
    }
    public enum class Step {
        Name,
        Description,
        Category,
        Done,
        Failed
    }

    public enum class World {
        Overworld,
        Nether,
        End
    }

    public enum class Category {
        Home,
        Spawner,
        Farm,
        Village,
        Stronghold,
        DesertTemple,
        JungleTemple,
        GuardianTemple,
        OceanRuins,
        Mineshaft,
        PillagerOutpost,
        Shipwreck,
        Biome,
        EndPortal,
        NetherPortal,
        Bastion,
        NetherFortress,
        Mansion,
        MushroomIsland,
        Misc
    }

    var x: Int = 0
    var y: Int = 0
    var z: Int = 0

    var name: String = ""
        get() = field
        set(value) {
            field = value
        }

    var description: String = ""
    var category: Category = Category.Home
    var username: String = ""
    var world: World = World.Overworld

    var looted: Boolean = false

    public var currentStep: Step = Step.Name
        get() = field

    constructor(x: Int, y: Int, z: Int, username: String, world: World) {
        this.x = x
        this.y = y
        this.z = z

        this.username = username
        this.world = world

        this.currentStep = Step.Name
    }

    fun incrementStep() {
        when (currentStep) {
            Step.Name -> currentStep = Step.Description
            Step.Description -> currentStep = Step.Category
            Step.Category -> currentStep = Step.Done
            Step.Done -> currentStep = Step.Failed
            Step.Failed -> return
        }
    }

    fun setFailed() {
        currentStep = Step.Failed
    }

    fun setCategoryFromIndex(i: Int): Boolean {
        when (i) {
            0 -> category = Category.Home
            1 -> category = Category.Spawner
            2 -> category = Category.Farm
            3 -> category = Category.Village
            4 -> category = Category.Stronghold
            5 -> category = Category.DesertTemple
            6 -> category = Category.JungleTemple
            7 -> category = Category.GuardianTemple
            8 -> category = Category.OceanRuins
            9 -> category = Category.Mineshaft
            10 -> category = Category.PillagerOutpost
            11 -> category = Category.Shipwreck
            12 -> category = Category.Biome
            13 -> category = Category.EndPortal
            14 -> category = Category.NetherPortal
            15 -> category = Category.Bastion
            16 -> category = Category.NetherFortress
            17 -> category = Category.Mansion
            18 -> category = Category.MushroomIsland
            19 -> category = Category.Misc
            else -> {
                return false
            }
        }

        return true
    }

    fun save(): Boolean {
        return true
    }
}
