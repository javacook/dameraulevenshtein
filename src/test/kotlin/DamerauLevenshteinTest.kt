import de.kotlincook.textmining.DamerauLevenshtein
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


internal class DamerauLevenshteinTest {

    @Test
    fun equals() {
        val algorithm = DamerauLevenshtein(1, 1, 1, 1)
        val actual = algorithm.execute("KotlinCook", "KotlinCook")
        assertEquals(0, actual)
    }

    @Test
    fun prefix() {
        val algorithm = DamerauLevenshtein(2, 1, 1, 2)
        val actual = algorithm.execute("KotlinCook", "Kotlin")
        assertEquals(8, actual)
    }

    @Test
    fun suffix() {
        val algorithm = DamerauLevenshtein(2, 1, 1, 2)
        val actual = algorithm.execute("KotlinCook", "Cook")
        assertEquals(12, actual)
    }

    @Test
    fun stadard1() {
        val algorithm = DamerauLevenshtein(1, 1, 2, 1)
        val actual = algorithm.execute("CotlinKook", "KotlinCook")
        assertEquals(4, actual)
    }

}