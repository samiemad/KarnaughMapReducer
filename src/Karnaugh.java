import java.util.ArrayList;
import java.util.Scanner;

public class Karnaugh {

	final static int TRUE = 1, FALSE = 0, X = 2;
	final static int gray[] = { 0, 1, 3, 2, 4, 5, 7, 6, 12, 13, 15, 14, 8, 9, 11, 10 };

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);

		System.out.print("Enter the number of variables: ");
		int n = input.nextInt();
		input.nextLine();
		if (n > 4) {
			System.out.println("ERROR: n is too large!");
			System.exit(n);
		}
		int nPow2 = (1 << n); // == 2^n

		System.out.println("Please enter your function of Ones (1) E.g. ABCD+AB'CD");
		String function = input.nextLine();
		System.out.println("Please enter your function of Don't care (X) E.g. A'BCD+A'B'CD");
		String Xfunction = input.nextLine();
		input.close();

		int map[] = new int[nPow2];

		if (Xfunction.contains("A")) {
			String[] unknowns = Xfunction.split("[+]");
			for (String term : unknowns) {
				int idx = getIdxForTerm(term);
				map[idx] = X;
			}
		}

		if (function.contains("A")) {
			String[] terms = function.split("[+]");
			for (String term : terms) {
				int idx = getIdxForTerm(term);
				map[idx] = TRUE;
			}
		}

		for (int i = 0; i < nPow2; ++i) {
			System.out.print(map[gray[i]] + " ");
			if (i % 4 == 3)
				System.out.println();
		}

		// result contains values for variables in the reduced function
		ArrayList<Integer> result = new ArrayList<>();
		// resultM indicates deleted variables in each term in the reduced
		// function.
		ArrayList<Integer> resultM = new ArrayList<>();
		// this loops over all possible rectangles to try to reduce...
		for (int mask = nPow2 - 1; mask >= 0; --mask) {
			// System.out.println("mask = " + mask);
			// tries all possible values for the variables witch will remain..
			for (int base = (nPow2 - 1) ^ mask; base >= 0; base = (base - 1) & ((nPow2 - 1) ^ mask)) {
				// can: true if there is no Zeros in the rectangle...
				// hasOne: true if the rectangle contains at least one one..
				boolean can = true;
				boolean hasOne = false;
				// System.out.println("base = " + base);
				// check if this rectangle only contains all 1's and X's...
				for (int rest = mask; rest >= 0; rest = (rest - 1) & mask) {
					// System.out.println("rest = " + rest + "\tmap[" + (base |
					// rest) + "] = " + map[base | rest]);
					if (map[base | rest] == FALSE) {
						can = false;
					} else if (map[base | rest] == TRUE) {
						hasOne = true;
					}
					if (rest == 0)
						break;
				}
				// System.out.println("can = " + can + ", hasOne = " + hasOne);
				// can reduce iff the rectangle has no Zeros and at least one 1.
				if (can && hasOne) {
					// add reduced term to the result
					result.add(base);
					resultM.add(mask);

					// change all 1's in the rectangle to X's
					for (int rest = mask; rest >= 0; rest = (rest - 1) & mask) {
						map[base | rest] = X;
						if (rest == 0)
							break;
					}
				}
				if (base == 0)
					break;
			}
			if (mask == 0)
				break;
		}

		System.out.println("result has " + result.size() + " terms");

		// loops over each term in the result:
		for (int i = 0; i < result.size(); ++i) {
			// print + between terms:
			if (i > 0)
				System.out.print(" + ");
			int base = result.get(i);
			int mask = resultM.get(i);

			// loops over my variables: (A, B, ..)
			for (int b = 0; b < n; ++b) {
				// if var is 1 in mask then it was deleted, if it is 0 then it
				// should appear in the resulting term
				if (((1 << b) & mask) == 0) {
					System.out.print(((char) ('A' + b)));
					// if var is 1 in base then it is negated, so print (')
					if (((1 << b) & base) != 0) {
						System.out.print("\'");
					}
				}
			}
		}

		System.out.println();
	}

	final static int[] val = new int[300];

	private static int getIdxForTerm(String term) {
		val['A'] = 1;
		val['B'] = 2;
		val['C'] = 4;
		val['D'] = 8;
		int idx = 0;
		for (int i = 0; i < term.length(); ++i) {
			if (term.charAt(i) >= 'A' && term.charAt(i) <= 'D') {
				if (i != term.length() - 1 && term.charAt(i + 1) == '\'') {
					idx += val[term.charAt(i)];
				}
			}
		}
		System.out.println("idx for " + term + " = " + idx);
		return idx;
	}
}