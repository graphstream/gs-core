/*
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */

/**
 * @since 2011-07-04
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.tlp;

import org.graphstream.util.parser.SimpleCharStream;
import org.graphstream.util.parser.Token;
import org.graphstream.util.parser.TokenMgrError;

/** Token Manager. */
public class TLPParserTokenManager implements TLPParserConstants {

	/** Debug output. */
	public java.io.PrintStream debugStream = System.out;

	/** Set debug output. */
	public void setDebugStream(java.io.PrintStream ds) {
		debugStream = ds;
	}

	private final int jjStopStringLiteralDfa_0(int pos, long active0) {
		switch (pos) {
		case 0:
			if ((active0 & 0x900000L) != 0L)
				return 35;
			if ((active0 & 0x240000L) != 0L)
				return 29;
			return -1;
		case 1:
			if ((active0 & 0x200000L) != 0L)
				return 28;
			return -1;
		default:
			return -1;
		}
	}

	private final int jjStartNfa_0(int pos, long active0) {
		return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
	}

	private int jjStopAtPos(int pos, int kind) {
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		return pos + 1;
	}

	private int jjMoveStringLiteralDfa0_0() {
		switch (curChar) {
		case 40:
			return jjStopAtPos(0, 10);
		case 41:
			return jjStopAtPos(0, 11);
		case 65:
		case 97:
			return jjMoveStringLiteralDfa1_0(0x80000L);
		case 67:
		case 99:
			return jjMoveStringLiteralDfa1_0(0x240000L);
		case 68:
		case 100:
			return jjMoveStringLiteralDfa1_0(0x900000L);
		case 69:
		case 101:
			return jjMoveStringLiteralDfa1_0(0x30000L);
		case 71:
		case 103:
			return jjMoveStringLiteralDfa1_0(0x2000L);
		case 78:
		case 110:
			return jjMoveStringLiteralDfa1_0(0xc000L);
		case 80:
		case 112:
			return jjMoveStringLiteralDfa1_0(0x400000L);
		case 84:
		case 116:
			return jjMoveStringLiteralDfa1_0(0x1000L);
		default:
			return jjMoveNfa_0(6, 0);
		}
	}

	private int jjMoveStringLiteralDfa1_0(long active0) {
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(0, active0);
			return 1;
		}
		switch (curChar) {
		case 65:
		case 97:
			return jjMoveStringLiteralDfa2_0(active0, 0x100000L);
		case 68:
		case 100:
			return jjMoveStringLiteralDfa2_0(active0, 0x30000L);
		case 69:
		case 101:
			return jjMoveStringLiteralDfa2_0(active0, 0x800000L);
		case 76:
		case 108:
			return jjMoveStringLiteralDfa2_0(active0, 0x41000L);
		case 79:
		case 111:
			return jjMoveStringLiteralDfa2_0(active0, 0x20c000L);
		case 82:
		case 114:
			return jjMoveStringLiteralDfa2_0(active0, 0x402000L);
		case 85:
		case 117:
			return jjMoveStringLiteralDfa2_0(active0, 0x80000L);
		default:
			break;
		}
		return jjStartNfa_0(0, active0);
	}

	private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjStartNfa_0(0, old0);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(1, active0);
			return 2;
		}
		switch (curChar) {
		case 65:
		case 97:
			return jjMoveStringLiteralDfa3_0(active0, 0x2000L);
		case 68:
		case 100:
			return jjMoveStringLiteralDfa3_0(active0, 0xc000L);
		case 70:
		case 102:
			return jjMoveStringLiteralDfa3_0(active0, 0x800000L);
		case 71:
		case 103:
			return jjMoveStringLiteralDfa3_0(active0, 0x30000L);
		case 77:
		case 109:
			return jjMoveStringLiteralDfa3_0(active0, 0x200000L);
		case 79:
		case 111:
			return jjMoveStringLiteralDfa3_0(active0, 0x400000L);
		case 80:
		case 112:
			if ((active0 & 0x1000L) != 0L)
				return jjStopAtPos(2, 12);
			break;
		case 84:
		case 116:
			return jjMoveStringLiteralDfa3_0(active0, 0x180000L);
		case 85:
		case 117:
			return jjMoveStringLiteralDfa3_0(active0, 0x40000L);
		default:
			break;
		}
		return jjStartNfa_0(1, active0);
	}

	private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjStartNfa_0(1, old0);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(2, active0);
			return 3;
		}
		switch (curChar) {
		case 65:
		case 97:
			return jjMoveStringLiteralDfa4_0(active0, 0x800000L);
		case 69:
		case 101:
			if ((active0 & 0x4000L) != 0L) {
				jjmatchedKind = 14;
				jjmatchedPos = 3;
			} else if ((active0 & 0x10000L) != 0L) {
				jjmatchedKind = 16;
				jjmatchedPos = 3;
			} else if ((active0 & 0x100000L) != 0L)
				return jjStopAtPos(3, 20);
			return jjMoveStringLiteralDfa4_0(active0, 0x28000L);
		case 72:
		case 104:
			return jjMoveStringLiteralDfa4_0(active0, 0x80000L);
		case 77:
		case 109:
			return jjMoveStringLiteralDfa4_0(active0, 0x200000L);
		case 80:
		case 112:
			return jjMoveStringLiteralDfa4_0(active0, 0x402000L);
		case 83:
		case 115:
			return jjMoveStringLiteralDfa4_0(active0, 0x40000L);
		default:
			break;
		}
		return jjStartNfa_0(2, active0);
	}

	private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjStartNfa_0(2, old0);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(3, active0);
			return 4;
		}
		switch (curChar) {
		case 69:
		case 101:
			return jjMoveStringLiteralDfa5_0(active0, 0x600000L);
		case 72:
		case 104:
			if ((active0 & 0x2000L) != 0L)
				return jjStopAtPos(4, 13);
			break;
		case 79:
		case 111:
			return jjMoveStringLiteralDfa5_0(active0, 0x80000L);
		case 83:
		case 115:
			if ((active0 & 0x8000L) != 0L)
				return jjStopAtPos(4, 15);
			else if ((active0 & 0x20000L) != 0L)
				return jjStopAtPos(4, 17);
			break;
		case 84:
		case 116:
			return jjMoveStringLiteralDfa5_0(active0, 0x40000L);
		case 85:
		case 117:
			return jjMoveStringLiteralDfa5_0(active0, 0x800000L);
		default:
			break;
		}
		return jjStartNfa_0(3, active0);
	}

	private int jjMoveStringLiteralDfa5_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjStartNfa_0(3, old0);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(4, active0);
			return 5;
		}
		switch (curChar) {
		case 69:
		case 101:
			return jjMoveStringLiteralDfa6_0(active0, 0x40000L);
		case 76:
		case 108:
			return jjMoveStringLiteralDfa6_0(active0, 0x800000L);
		case 78:
		case 110:
			return jjMoveStringLiteralDfa6_0(active0, 0x200000L);
		case 82:
		case 114:
			if ((active0 & 0x80000L) != 0L)
				return jjStopAtPos(5, 19);
			return jjMoveStringLiteralDfa6_0(active0, 0x400000L);
		default:
			break;
		}
		return jjStartNfa_0(4, active0);
	}

	private int jjMoveStringLiteralDfa6_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjStartNfa_0(4, old0);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(5, active0);
			return 6;
		}
		switch (curChar) {
		case 82:
		case 114:
			if ((active0 & 0x40000L) != 0L)
				return jjStopAtPos(6, 18);
			break;
		case 84:
		case 116:
			if ((active0 & 0x800000L) != 0L)
				return jjStopAtPos(6, 23);
			return jjMoveStringLiteralDfa7_0(active0, 0x600000L);
		default:
			break;
		}
		return jjStartNfa_0(5, active0);
	}

	private int jjMoveStringLiteralDfa7_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjStartNfa_0(5, old0);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(6, active0);
			return 7;
		}
		switch (curChar) {
		case 83:
		case 115:
			if ((active0 & 0x200000L) != 0L)
				return jjStopAtPos(7, 21);
			break;
		case 89:
		case 121:
			if ((active0 & 0x400000L) != 0L)
				return jjStopAtPos(7, 22);
			break;
		default:
			break;
		}
		return jjStartNfa_0(6, active0);
	}

	static final long[] jjbitVec0 = { 0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL };

	private int jjMoveNfa_0(int startState, int curPos) {
		int startsAt = 0;
		jjnewStateCnt = 55;
		int i = 1;
		jjstateSet[0] = startState;
		int kind = 0x7fffffff;
		for (;;) {
			if (++jjround == 0x7fffffff)
				ReInitRounds();
			if (curChar < 64) {
				long l = 1L << curChar;
				do {
					switch (jjstateSet[--i]) {
					case 6:
						if ((0x3ff000000000000L & l) != 0L) {
							if (kind > 24)
								kind = 24;
							jjCheckNAddStates(0, 2);
						} else if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(11);
						else if (curChar == 39)
							jjCheckNAddTwoStates(20, 21);
						else if (curChar == 34)
							jjCheckNAddStates(3, 5);
						else if (curChar == 59)
							jjCheckNAddTwoStates(8, 9);
						else if (curChar == 47)
							jjstateSet[jjnewStateCnt++] = 0;
						break;
					case 0:
						if (curChar == 42)
							jjCheckNAddStates(6, 8);
						break;
					case 1:
						if ((0xfffffbffffffffffL & l) != 0L)
							jjCheckNAddStates(6, 8);
						break;
					case 2:
						if (curChar == 42)
							jjstateSet[jjnewStateCnt++] = 3;
						break;
					case 3:
						if ((0xffff7fffffffffffL & l) != 0L)
							jjCheckNAddStates(6, 8);
						break;
					case 4:
						if (curChar == 47 && kind > 5)
							kind = 5;
						break;
					case 5:
						if (curChar == 42)
							jjstateSet[jjnewStateCnt++] = 4;
						break;
					case 7:
						if (curChar == 59)
							jjCheckNAddTwoStates(8, 9);
						break;
					case 8:
						if ((0xffffffffffffdbffL & l) != 0L)
							jjCheckNAddTwoStates(8, 9);
						break;
					case 9:
						if ((0x2400L & l) != 0L && kind > 6)
							kind = 6;
						break;
					case 10:
						if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(11);
						break;
					case 11:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 25)
							kind = 25;
						jjCheckNAddTwoStates(11, 12);
						break;
					case 12:
						if (curChar == 46)
							jjCheckNAdd(13);
						break;
					case 13:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 25)
							kind = 25;
						jjCheckNAdd(13);
						break;
					case 14:
					case 16:
						if (curChar == 34)
							jjCheckNAddStates(3, 5);
						break;
					case 15:
						if ((0xfffffffbffffffffL & l) != 0L)
							jjCheckNAddStates(3, 5);
						break;
					case 18:
						if (curChar == 34 && kind > 26)
							kind = 26;
						break;
					case 19:
						if (curChar == 39)
							jjCheckNAddTwoStates(20, 21);
						break;
					case 20:
						if ((0xffffff7fffffffffL & l) != 0L)
							jjCheckNAddTwoStates(20, 21);
						break;
					case 21:
						if (curChar == 39 && kind > 26)
							kind = 26;
						break;
					case 53:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 24)
							kind = 24;
						jjCheckNAddStates(0, 2);
						break;
					case 54:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 24)
							kind = 24;
						jjCheckNAdd(54);
						break;
					default:
						break;
					}
				} while (i != startsAt);
			} else if (curChar < 128) {
				long l = 1L << (curChar & 077);
				do {
					switch (jjstateSet[--i]) {
					case 6:
						if ((0x8000000080000L & l) != 0L)
							jjAddStates(9, 10);
						else if ((0x20000000200L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 43;
						else if ((0x100000001000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 41;
						else if ((0x1000000010L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 35;
						else if ((0x800000008L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 29;
						else if ((0x400000004L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 24;
						break;
					case 1:
					case 3:
						jjCheckNAddStates(6, 8);
						break;
					case 8:
						jjAddStates(11, 12);
						break;
					case 15:
						jjAddStates(3, 5);
						break;
					case 17:
						if (curChar == 92)
							jjstateSet[jjnewStateCnt++] = 16;
						break;
					case 20:
						jjAddStates(13, 14);
						break;
					case 22:
						if ((0x100000001000L & l) != 0L && kind > 27)
							kind = 27;
						break;
					case 23:
						if ((0x800000008000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 22;
						break;
					case 24:
						if ((0x800000008000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 23;
						break;
					case 25:
						if ((0x400000004L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 24;
						break;
					case 26:
						if ((0x4000000040000L & l) != 0L && kind > 27)
							kind = 27;
						break;
					case 27:
						if ((0x800000008000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 26;
						break;
					case 28:
						if ((0x100000001000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 27;
						break;
					case 29:
						if ((0x800000008000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 28;
						break;
					case 30:
						if ((0x800000008L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 29;
						break;
					case 31:
						if ((0x2000000020L & l) != 0L && kind > 27)
							kind = 27;
						break;
					case 32:
						if ((0x100000001000L & l) != 0L)
							jjCheckNAdd(31);
						break;
					case 33:
						if ((0x400000004L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 32;
						break;
					case 34:
						if ((0x20000000200000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 33;
						break;
					case 35:
						if ((0x800000008000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 34;
						break;
					case 36:
						if ((0x1000000010L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 35;
						break;
					case 37:
						if ((0x10000000100000L & l) != 0L && kind > 27)
							kind = 27;
						break;
					case 38:
						if ((0x20000000200000L & l) != 0L)
							jjCheckNAdd(37);
						break;
					case 39:
						if ((0x800000008000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 38;
						break;
					case 40:
						if ((0x200000002000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 39;
						break;
					case 41:
						if ((0x200000002L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 40;
						break;
					case 42:
						if ((0x100000001000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 41;
						break;
					case 43:
						if ((0x400000004000L & l) != 0L)
							jjCheckNAdd(37);
						break;
					case 44:
						if ((0x20000000200L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 43;
						break;
					case 45:
						if ((0x8000000080000L & l) != 0L)
							jjAddStates(9, 10);
						break;
					case 46:
						if ((0x400000004000000L & l) != 0L)
							jjCheckNAdd(31);
						break;
					case 47:
						if ((0x20000000200L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 46;
						break;
					case 48:
						if ((0x8000000080L & l) != 0L && kind > 27)
							kind = 27;
						break;
					case 49:
						if ((0x400000004000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 48;
						break;
					case 50:
						if ((0x20000000200L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 49;
						break;
					case 51:
						if ((0x4000000040000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 50;
						break;
					case 52:
						if ((0x10000000100000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 51;
						break;
					default:
						break;
					}
				} while (i != startsAt);
			} else {
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				do {
					switch (jjstateSet[--i]) {
					case 1:
					case 3:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjCheckNAddStates(6, 8);
						break;
					case 8:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjAddStates(11, 12);
						break;
					case 15:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjAddStates(3, 5);
						break;
					case 20:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjAddStates(13, 14);
						break;
					default:
						break;
					}
				} while (i != startsAt);
			}
			if (kind != 0x7fffffff) {
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 55 - (jjnewStateCnt = startsAt)))
				return curPos;
			try {
				curChar = input_stream.readChar();
			} catch (java.io.IOException e) {
				return curPos;
			}
		}
	}

	static final int[] jjnextStates = { 54, 11, 12, 15, 17, 18, 1, 2, 5, 47, 52, 8, 9, 20, 21, };

	/** Token literal values. */
	public static final String[] jjstrLiteralImages = { "", null, null, null, null, null, null, null, null, null, "\50",
			"\51", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, };

	/** Lexer state names. */
	public static final String[] lexStateNames = { "DEFAULT", };
	static final long[] jjtoToken = { 0xffffc01L, };
	static final long[] jjtoSkip = { 0x7eL, };
	protected SimpleCharStream input_stream;
	private final int[] jjrounds = new int[55];
	private final int[] jjstateSet = new int[110];
	protected char curChar;

	/** Constructor. */
	public TLPParserTokenManager(SimpleCharStream stream) {
		if (SimpleCharStream.staticFlag)
			throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
		input_stream = stream;
	}

	/** Constructor. */
	public TLPParserTokenManager(SimpleCharStream stream, int lexState) {
		this(stream);
		SwitchTo(lexState);
	}

	/** Reinitialise parser. */
	public void ReInit(SimpleCharStream stream) {
		jjmatchedPos = jjnewStateCnt = 0;
		curLexState = defaultLexState;
		input_stream = stream;
		ReInitRounds();
	}

	private void ReInitRounds() {
		int i;
		jjround = 0x80000001;
		for (i = 55; i-- > 0;)
			jjrounds[i] = 0x80000000;
	}

	/** Reinitialise parser. */
	public void ReInit(SimpleCharStream stream, int lexState) {
		ReInit(stream);
		SwitchTo(lexState);
	}

	/** Switch to specified lex state. */
	public void SwitchTo(int lexState) {
		if (lexState >= 1 || lexState < 0)
			throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.",
					TokenMgrError.INVALID_LEXICAL_STATE);
		else
			curLexState = lexState;
	}

	protected Token jjFillToken() {
		final Token t;
		final String curTokenImage;
		final int beginLine;
		final int endLine;
		final int beginColumn;
		final int endColumn;
		String im = jjstrLiteralImages[jjmatchedKind];
		curTokenImage = (im == null) ? input_stream.GetImage() : im;
		beginLine = input_stream.getBeginLine();
		beginColumn = input_stream.getBeginColumn();
		endLine = input_stream.getEndLine();
		endColumn = input_stream.getEndColumn();
		t = Token.newToken(jjmatchedKind, curTokenImage);

		t.beginLine = beginLine;
		t.endLine = endLine;
		t.beginColumn = beginColumn;
		t.endColumn = endColumn;

		return t;
	}

	int curLexState = 0;
	int defaultLexState = 0;
	int jjnewStateCnt;
	int jjround;
	int jjmatchedPos;
	int jjmatchedKind;

	/** Get the next Token. */
	public Token getNextToken() {
		Token matchedToken;
		int curPos = 0;

		EOFLoop: for (;;) {
			try {
				curChar = input_stream.BeginToken();
			} catch (java.io.IOException e) {
				jjmatchedKind = 0;
				matchedToken = jjFillToken();
				return matchedToken;
			}

			try {
				input_stream.backup(0);
				while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
					curChar = input_stream.BeginToken();
			} catch (java.io.IOException e1) {
				continue EOFLoop;
			}
			jjmatchedKind = 0x7fffffff;
			jjmatchedPos = 0;
			curPos = jjMoveStringLiteralDfa0_0();
			if (jjmatchedKind != 0x7fffffff) {
				if (jjmatchedPos + 1 < curPos)
					input_stream.backup(curPos - jjmatchedPos - 1);
				if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L) {
					matchedToken = jjFillToken();
					return matchedToken;
				} else {
					continue EOFLoop;
				}
			}
			int error_line = input_stream.getEndLine();
			int error_column = input_stream.getEndColumn();
			String error_after = null;
			boolean EOFSeen = false;
			try {
				input_stream.readChar();
				input_stream.backup(1);
			} catch (java.io.IOException e1) {
				EOFSeen = true;
				error_after = curPos <= 1 ? "" : input_stream.GetImage();
				if (curChar == '\n' || curChar == '\r') {
					error_line++;
					error_column = 0;
				} else
					error_column++;
			}
			if (!EOFSeen) {
				input_stream.backup(1);
				error_after = curPos <= 1 ? "" : input_stream.GetImage();
			}
			throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar,
					TokenMgrError.LEXICAL_ERROR);
		}
	}

	private void jjCheckNAdd(int state) {
		if (jjrounds[state] != jjround) {
			jjstateSet[jjnewStateCnt++] = state;
			jjrounds[state] = jjround;
		}
	}

	private void jjAddStates(int start, int end) {
		do {
			jjstateSet[jjnewStateCnt++] = jjnextStates[start];
		} while (start++ != end);
	}

	private void jjCheckNAddTwoStates(int state1, int state2) {
		jjCheckNAdd(state1);
		jjCheckNAdd(state2);
	}

	private void jjCheckNAddStates(int start, int end) {
		do {
			jjCheckNAdd(jjnextStates[start]);
		} while (start++ != end);
	}

}
