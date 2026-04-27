package com.example.russianroulette.logic

import kotlin.random.Random

class RussianRouletteEngine(
    val totalChambers: Int = 6,
    val initialBulletCount: Int = 1
) {
    private var chambers = BooleanArray(totalChambers)
    var currentRotationIndex = 0 // Conceptually, we treat the array as rotating
    
    init {
        reset()
    }

    fun reset() {
        chambers = BooleanArray(totalChambers)
        // Bullets must be clustered together
        // Start position for the cluster
        val startPos = Random.nextInt(totalChambers)
        for (i in 0 until initialBulletCount) {
            val pos = (startPos + i) % totalChambers
            chambers[pos] = true
        }
    }

    /**
     * Rotates the cylinder by a specific amount.
     * In this implementation, we rotate the actual array or shift our perspective.
     * Let's shift the array for simplicity in "check Index 0" logic.
     */
    fun rotate(steps: Int) {
        val newChambers = BooleanArray(totalChambers)
        for (i in 0 until totalChambers) {
            // Steps > 0 means clockwise/right, steps < 0 counter-clockwise/left
            val newPos = (i + steps).mod(totalChambers)
            newChambers[newPos] = chambers[i]
        }
        chambers = newChambers
    }

    /**
     * Randomly rotates the cylinder.
     */
    fun randomSpin() {
        rotate(Random.nextInt(totalChambers * 2, totalChambers * 5))
    }

    /**
     * Pulls the trigger. Checks Index 0.
     * @return true if there was a bullet, false otherwise.
     */
    fun fire(): Boolean {
        val hasBullet = chambers[0]
        if (hasBullet) {
            chambers[0] = false // Bullet spent
        }
        // Rotate to next chamber after firing? 
        // Usually, in a real revolver, firing moves the cylinder to the next one.
        rotate(-1) // Move to next (Shift Index 1 to Index 0)
        return hasBullet
    }

    fun getRemainingBulletsCount(): Int {
        return chambers.count { it }
    }

    fun getChamberStates(): List<Boolean> {
        return chambers.toList()
    }
}
