package de.dvdb.domain.shared;

import java.util.Random;

public class PWDGenerator {

	Random prng = new Random();

	String[] vowels = { "a", "e", "i", "o", "u" };

	String[] dipthg = { "ao", "ea", "eo", "ia", "io", "oa", "ua", "ue", "ui" };

	String[] gencon = { "b", "c", "d", "f", "g", "j", "l", "m", "n", "p", "r",
			"s", "t", "v", "y", "z" };

	String[] fincon = { "b", "c", "d", "f", "l", "m", "n", "p", "r", "s", "t" };

	String[] dblcon = { "ch", "ll", "rr" };

	private static final byte mapNone = 0;

	private static final byte mapVowel = 1;

	private static final byte mapDipthg = 2;

	private static final byte mapGencon = 3;

	private static final byte mapFincon = 4;

	private static final byte mapDblcon = 5;

	byte[] mapFirst = { mapGencon, mapVowel };

	byte[] mapAftVow = { mapGencon, mapGencon, mapGencon, mapGencon, mapFincon,
			mapNone, mapNone, mapNone, mapNone, mapDblcon };

	byte[] mapAftGen = { mapVowel, mapVowel, mapVowel, mapVowel, mapVowel,
			mapVowel, mapDipthg };

	byte[] mapAftDip = { mapGencon, mapFincon, mapNone, mapNone };

	public String generate() {
		int i = 0, Dip = 0, Dbl = 0;
		int minWdLen = 5;
		int maxWdLen = minWdLen + prng.nextInt(4) + prng.nextInt(4)
				+ prng.nextInt(4);
		byte[] map = new byte[maxWdLen + 3];
		byte nextType = mapFirst[prng.nextInt(mapFirst.length)];

		i = 0;
		map[i++] = nextType;

		while (i < minWdLen) {
			byte temp = nextType;

			switch (nextType) {
			case mapVowel:
				nextType = mapAftVow[prng.nextInt(mapAftVow.length)];
				break;

			case mapDipthg:
				Dip = 1;
				nextType = mapAftDip[prng.nextInt(mapAftDip.length)];
				break;

			case mapFincon:
			case mapGencon:
				nextType = mapAftGen[prng.nextInt(mapAftGen.length)];
				break;

			case mapDblcon:
				Dbl = 1;
				nextType = mapVowel;
				break;
			}

			if (((Dip == 1) && (nextType == mapDipthg))
					|| ((Dbl == 1) && (nextType == mapDblcon))
					|| (nextType == mapNone)) {
				nextType = temp;
			} else {
				map[i++] = nextType;
			}
		}

		while ((i < maxWdLen) && (nextType != mapNone)) {
			byte temp = nextType;

			switch (nextType) {
			case mapVowel:
				nextType = mapAftVow[prng.nextInt(mapAftVow.length)];
				break;

			case mapDipthg:
				Dip = 1;
				nextType = mapAftDip[prng.nextInt(mapAftDip.length)];
				break;

			case mapGencon:
				nextType = mapAftGen[prng.nextInt(mapAftGen.length)];
				break;

			case mapDblcon:
				Dbl = 1;
				nextType = mapVowel;
				break;

			case mapFincon:
				nextType = mapNone;
			}

			if (((Dip == 1) && (nextType == mapDipthg))
					|| ((Dbl == 1) && (nextType == mapDblcon))) {
				nextType = temp;
			} else {
				map[i++] = nextType;
			}
		}

		while (nextType != mapNone) {
			switch (nextType) {
			case mapVowel:
				nextType = mapNone;
				break;

			case mapGencon:
				nextType = mapAftGen[prng.nextInt(mapAftGen.length)];
				break;

			case mapFincon:
				nextType = mapNone;
				break;

			case mapDipthg:
				nextType = mapNone;
				break;

			case mapDblcon:
				nextType = mapVowel;
				break;
			}

			map[i++] = nextType;
		}

		StringBuffer genWord = new StringBuffer();
		genWord.ensureCapacity(2 * i);

		for (i = 0; map[i] != mapNone; i++) {
			switch (map[i]) {
			case mapVowel:

				genWord.append(vowels[prng.nextInt(vowels.length)]);
				break;

			case mapGencon:

				genWord.append(gencon[prng.nextInt(gencon.length)]);
				break;

			case mapDipthg:

				genWord.append(dipthg[prng.nextInt(dipthg.length)]);
				break;

			case mapDblcon:

				genWord.append(dblcon[prng.nextInt(dblcon.length)]);
				break;

			case mapFincon:

				genWord.append(fincon[prng.nextInt(fincon.length)]);
				break;
			}
		}

		return genWord.toString();
	}

}
