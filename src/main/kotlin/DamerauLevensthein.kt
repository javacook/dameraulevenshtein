package de.kotlincook.textmining

import java.lang.Math.max
import java.util.HashMap

/**
 * The Damerau-Levenshtein Algorithm is an extension to the Levenshtein
 * Algorithm which solves the edit distance problem between a source string and
 * a target string with the following operations:
 *
 * Character Insertion
 * Character Deletion
 * Character Replacement
 * Adjacent Character Swap
 *
 * Note that the adjacent character swap operation is an edit that may be
 * applied when two adjacent characters in the source string match two adjacent
 * characters in the target string, but in reverse order, rather than a
 * general allowance for adjacent character swaps.
 *
 * This implementation allows the client to specify the costs of the various
 * edit operations with the restriction that the cost of two swap operations
 * must not be less than the cost of a delete operation followed by an insert
 * operation. This restriction is required to preclude two swaps involving the
 * same character being required for optimality which, in turn, enables a fast
 * dynamic programming solution.
 *
 * The running time of the Damerau-Levenshtein algorithm is O(n*m) where n
 * is the length of the source string and m is the length of the target
 * string. This implementation consumes O(n*m) space.
 *
 * @author Kevin L. and Joerg Vollmer (JavaCook & KotlinCook)
 * @param deleteCost the cost of deleting a character.
 * @param insertCost the cost of inserting a character.
 * @param replaceCost the cost of replacing a character.
 * @param swapCost the cost of swapping two adjacent characters.
 */
class DamerauLevenshtein(private val deleteCost: Int,
                         private val insertCost: Int,
                         private val replaceCost: Int,
                         private val swapCost: Int) {
    init {
        check(deleteCost >= 0)
        check(insertCost >= 0)
        check(replaceCost >= 0)
        check(swapCost >= 0)
        check(2 * swapCost >= insertCost + deleteCost)
    }

    /**
     * Compute the Damerau-Levenshtein distance between the specified source
     * string and the specified target string.
     */
    fun compute(source: String, target: String): Int {
        if (source.isEmpty()) {
            return target.length * insertCost
        }
        if (target.isEmpty()) {
            return source.length * deleteCost
        }
        val table = Array(source.length) { IntArray(target.length) }
        val sourceIndexByCharacter = HashMap<Char, Int>()
        if (source[0] != target[0]) {
            table[0][0] = Math.min(replaceCost, deleteCost + insertCost)
        }
        sourceIndexByCharacter.put(source[0], 0)
        for (i in 1 until source.length) {
            val deleteDistance = table[i - 1][0] + deleteCost
            val insertDistance = (i + 1) * deleteCost + insertCost
            val matchDistance  = i * deleteCost + if (source[i] == target[0]) 0 else replaceCost
            table[i][0] = intArrayOf(deleteDistance, insertDistance, matchDistance).min()!!
        }
        for (j in 1 until target.length) {
            val deleteDistance = table[0][j - 1] + insertCost
            val insertDistance = (j + 1) * insertCost + deleteCost
            val matchDistance = j * insertCost + if (source[0] == target[j]) 0 else replaceCost
            table[0][j] = intArrayOf(deleteDistance, insertDistance, matchDistance).min()!!
        }
        for (i in 1 until source.length) {
            var maxSourceLetterMatchIndex = if (source[i] == target[0]) 0 else -1
            for (j in 1 until target.length) {
                val candidateSwapIndex:Int? = sourceIndexByCharacter[target[j]]
                val jSwap = maxSourceLetterMatchIndex
                val deleteDistance = table[i - 1][j] + deleteCost
                val insertDistance = table[i][j - 1] + insertCost
                var matchDistance = table[i - 1][j - 1]
                if (source[i] != target[j]) {
                    matchDistance += replaceCost
                } else {
                    maxSourceLetterMatchIndex = j
                }
                var swapDistance =  Integer.MAX_VALUE;
                if (candidateSwapIndex != null && jSwap != -1) {
                    swapDistance = 0
                    if (candidateSwapIndex > 0 || jSwap > 0) {
                        swapDistance = table[max(0, candidateSwapIndex - 1)][max(0, jSwap - 1)]
                    }
                    swapDistance += (i - candidateSwapIndex - 1) * deleteCost
                    swapDistance += (j - jSwap - 1) * insertCost + swapCost
                }
                table[i][j] = intArrayOf(deleteDistance, insertDistance, matchDistance, swapDistance).min()!!
            }
            sourceIndexByCharacter.put(source[i], i)
        }
        return table[source.length - 1][target.length - 1]
    }
}