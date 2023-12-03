import java.lang.IllegalStateException

fun main() {

    fun part1(input: List<String>): Int {
        val gameConfiguration = GameConfiguration(12, 14, 13)
        val gameFactory = GameFactory(gameConfiguration)
        val games = input.map { gameFactory.create(it) }
        var result = 0
        for (game in games) {
            if (game.isValid()) {
                result += game.getId()
            }
        }
        return result
    }

    fun part2(input: List<String>): Int {
        val gameConfiguration = GameConfiguration(0, 0, 0)
        val gameFactory = GameFactory(gameConfiguration)
        val games = input.map { gameFactory.create(it) }
        var result = 0
        for (game in games) {
            result += game.getPower()
        }
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

class GameConfiguration(val maxRed: Int, val maxBlue: Int, val maxGreen: Int)

class GameRound {
    private var green: Int = 0
    private var blue: Int = 0
    private var red: Int = 0
    fun parseRound(text: String) {
        val extractions = text.split(',')
        for (extraction in extractions) {
            parseExtraction(extraction)
        }
    }

    private fun parseExtraction(extraction: String) {
        val match = cubeAmountRegex.find(extraction) ?: throw IllegalArgumentException("Invalid round extraction value: $extraction")
        val count = match.groups["count"]!!.value.toInt()
        val color = match.groups["color"]!!.value
        when (color) {
            "red" -> {
                red = count
            }
            "blue" -> {
                blue = count
            }
            "green" -> {
                green = count
            }
            else -> {
                throw IllegalArgumentException("Invalid round extraction value: $extraction. Unknown color")
            }
        }
    }

    /**
     * Gets the number of green cubes extracted by the elf in this round
     */
    fun getGreen(): Int {
        return green
    }

    /**
     * Gets the number of blue cubes extracted by the elf in this round
     */
    fun getBlue(): Int {
        return blue
    }

    /**
     * Gets the number of red cubes extracted by the elf in this round
     */
    fun getRed(): Int {
        return red
    }

    companion object {
        val cubeAmountRegex = Regex("(?<count>\\d+)\\s+(?<color>\\w+)")
    }
}

/**
 * Factory to create the game rounds from the raw input received for a game
 */
class GameRoundFactory {
    /**
     * Creates the list of rounds of a game based on the input received. The input must be a set of rounds information
     * split by semicolons.
     * @param input Raw input of the rounds for a game
     * @exception IllegalArgumentException The game rounds input received does not contain any valid round
     */
    fun create(input: String): List<GameRound> {
        val rounds = input.split(';')
        if (rounds.isEmpty()) {
            throw IllegalArgumentException("The games input received does not have any round: $input")
        }
        return rounds.map {
            val round = GameRound()
            round.parseRound(it)
            round
        }
    }
}

class Game(private val validConfiguration: GameConfiguration, private val roundFactory: GameRoundFactory) {
    private var gameId: Int? = null
    private val rounds: MutableList<GameRound> = mutableListOf()
    fun parseGameInput(input: String) {
        val idAndRounds = input.split(':')
        if (idAndRounds.size < 2) {
            throw IllegalArgumentException("A game input must contain the game name and the rounds information at least")
        }
        gameId = parseGameId(idAndRounds[0])
        rounds.clear()
        rounds.addAll(roundFactory.create(idAndRounds[1].trim()))
    }
    fun isValid(): Boolean {
        return rounds.all {
            it.getBlue() <= validConfiguration.maxBlue
                    && it.getGreen() <= validConfiguration.maxGreen
                    && it.getRed() <= validConfiguration.maxRed
        }
    }

    /**
     * Calculates the minimum amount of cubes of each color to make this game valid and returns the product of those
     * values.
     */
    fun getPower(): Int {
        var minGreen = 0
        var minBlue = 0
        var minRed = 0
        for (round in rounds) {
            if (round.getGreen() > minGreen) {
                minGreen = round.getGreen()
            }
            if (round.getRed() > minRed) {
                minRed = round.getRed()
            }
            if (round.getBlue() > minBlue) {
                minBlue = round.getBlue()
            }
        }
        return minGreen * minBlue * minRed
    }

    fun getId(): Int {
        return gameId ?: throw IllegalStateException("The game has not been initialized yet. Call parseGameInput first")
    }

    private fun parseGameId(text: String): Int {
        val match = gameIdRegex.find(text) ?: throw IllegalArgumentException("Invalid game id string $text")
        val idGroup = match.groups["id"] ?: throw IllegalArgumentException("Invalid game id string $text. The value does not contain numbers")
        return idGroup.value.toInt()
    }

    companion object {
        val gameIdRegex = Regex("Game\\s+(?<id>\\d+)")
    }
}

/**
 * Factory to create Game instances from their raw input definition
 * @param validConfiguration Configuration that the determines if a game is valid or not.
 */
class GameFactory(private val validConfiguration: GameConfiguration) {
    private val gameRoundFactory = GameRoundFactory()

    /**
     * Creates a new game instance and initializes it with the game input information given
     * @param input Raw input with the information of a game and its rounds
     * @throws IllegalArgumentException The game information given is not valid
     */
    fun create(input: String): Game {
        val game = Game(validConfiguration, gameRoundFactory)
        game.parseGameInput(input)
        return game
    }
}
