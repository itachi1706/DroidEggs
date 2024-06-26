/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itachi1706.droideggs.eggs.upside_down_cake.easter_egg.landroid

import android.content.res.Resources
import com.itachi1706.droideggs.R
import kotlin.random.Random

const val SUFFIX_PROB = 0.75f
const val LETTER_PROB = 0.3f
const val NUMBER_PROB = 0.3f
const val RARE_PROB = 0.05f

class Namer(resources: Resources) {
    private val planetDescriptors = Bag(resources.getStringArray(R.array.planet_descriptors))
    private val lifeDescriptors = Bag(resources.getStringArray(R.array.life_descriptors))
    private val anyDescriptors = Bag(resources.getStringArray(R.array.any_descriptors))
    private val atmoDescriptors = Bag(resources.getStringArray(R.array.atmo_descriptors))

    private val planetTypes = Bag(resources.getStringArray(R.array.planet_types))
    private val constellations = Bag(resources.getStringArray(R.array.constellations))
    private val constellationsRare = Bag(resources.getStringArray(R.array.constellations_rare))
    private val suffixes = Bag(resources.getStringArray(R.array.star_suffixes))
    private val suffixesRare = Bag(resources.getStringArray(R.array.star_suffixes_rare))

    private val planetTable = RandomTable(0.75f to planetDescriptors, 0.25f to anyDescriptors)

    private var lifeTable = RandomTable(0.75f to lifeDescriptors, 0.25f to anyDescriptors)

    private var constellationsTable =
        RandomTable(RARE_PROB to constellationsRare, 1f - RARE_PROB to constellations)

    private var suffixesTable = RandomTable(RARE_PROB to suffixesRare, 1f - RARE_PROB to suffixes)

    private var atmoTable = RandomTable(0.75f to atmoDescriptors, 0.25f to anyDescriptors)

    private var delimiterTable =
        RandomTable(
            15f to " ",
            3f to "-",
            1f to "_",
            1f to "/",
            1f to ".",
            1f to "*",
            1f to "^",
            1f to "#",
            0.1f to "(^*!%@##!!"
        )

    fun describePlanet(rng: Random): String {
        return planetTable.roll(rng).pull(rng) + " " + planetTypes.pull(rng)
    }

    fun describeLife(rng: Random): String {
        return lifeTable.roll(rng).pull(rng)
    }

    fun nameSystem(rng: Random): String {
        val parts = StringBuilder()
        parts.append(constellationsTable.roll(rng).pull(rng))
        if (rng.nextFloat() <= SUFFIX_PROB) {
            parts.append(delimiterTable.roll(rng))
            parts.append(suffixesTable.roll(rng).pull(rng))
            if (rng.nextFloat() <= RARE_PROB) parts.append(' ').append(suffixesRare.pull(rng))
        }
        if (rng.nextFloat() <= LETTER_PROB) {
            parts.append(delimiterTable.roll(rng))
            parts.append('A' + rng.nextInt(0, 26))
            if (rng.nextFloat() <= RARE_PROB) parts.append(delimiterTable.roll(rng))
        }
        if (rng.nextFloat() <= NUMBER_PROB) {
            parts.append(delimiterTable.roll(rng))
            parts.append(rng.nextInt(2, 5039))
        }
        return parts.toString()
    }

    fun describeAtmo(rng: Random): String {
        return atmoTable.roll(rng).pull(rng)
    }
}