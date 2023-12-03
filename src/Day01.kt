fun main() {
    val numberNames = arrayOf(0, "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
    val numberRegex = Regex("\\d")
    // the ?= part of the regex helps with overlapping values
    val numberOrLettersRegex = Regex("(?<number>\\d)|(?=(?<letters>one|two|three|four|five|six|seven|eight|nine))")

    /**
     * Expects a Regex match result that contains the name of a number. It will transform it into the equivalent number
     * as string value
     * @param matchGroup Match group whose value will be converted
     * @return String with the number cipher equivalent to the name of the number received
     * @throws IllegalArgumentException The match group given is <code>null</code>
     */
    fun convertLettersToNumber(matchGroup: MatchGroup?): String {
        if (matchGroup == null) {
            throw IllegalArgumentException("Expected a non-null group")
        }
        return numberNames.indexOf(matchGroup.value).toString()
    }

    fun getMatchValue(match: MatchResult): String {
        val numericValue = match.groups["number"]
        if (numericValue != null) {
            return numericValue.value
        }
        return convertLettersToNumber(match.groups["letters"])
    }

    fun extractNumber(text: String): Int {
        val matches = numberRegex.findAll(text)
        val valueSearched = matches.first().value + matches.last().value
        return valueSearched.toInt()
    }

    fun part1(input: List<String>): Int {
        var acc = 0
        input.forEach {
            val extractedNumber = extractNumber(it)
            acc += extractedNumber
        }
        return acc
    }

    fun extractNumberOrLetters(text: String): Int {
        val matches = numberOrLettersRegex.findAll(text)
        val valueSearched = getMatchValue(matches.first()) + getMatchValue(matches.last())
        return valueSearched.toInt()
    }

    fun part2(input: List<String>): Int {var acc = 0
        input.forEach {
            val extractedNumber = extractNumberOrLetters(it)
            acc += extractedNumber
        }
        return acc
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)
    val testInput2 = readInput("Day01_test2")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
