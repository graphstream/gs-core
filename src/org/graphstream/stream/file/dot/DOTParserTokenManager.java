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
 * @since 2011-07-21
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.dot;

import org.graphstream.util.parser.SimpleCharStream;
import org.graphstream.util.parser.Token;
import org.graphstream.util.parser.TokenMgrError;

/** Token Manager. */
public class DOTParserTokenManager implements DOTParserConstants {

	/** Debug output. */
	public java.io.PrintStream debugStream = System.out;

	/** Set debug output. */
	public void setDebugStream(java.io.PrintStream ds) {
		debugStream = ds;
	}

	private final int jjStopStringLiteralDfa_0(int pos, long active0) {
		switch (pos) {
		case 0:
			if ((active0 & 0x3ff07e0000L) != 0L) {
				jjmatchedKind = 26;
				return 16;
			}
			return -1;
		case 1:
			if ((active0 & 0xaa07e0000L) != 0L) {
				jjmatchedKind = 26;
				jjmatchedPos = 1;
				return 16;
			}
			return -1;
		case 2:
			if ((active0 & 0x7e0000L) != 0L) {
				jjmatchedKind = 26;
				jjmatchedPos = 2;
				return 16;
			}
			return -1;
		case 3:
			if ((active0 & 0x300000L) != 0L)
				return 16;
			if ((active0 & 0x4e0000L) != 0L) {
				jjmatchedKind = 26;
				jjmatchedPos = 3;
				return 16;
			}
			return -1;
		case 4:
			if ((active0 & 0x20000L) != 0L)
				return 16;
			if ((active0 & 0x4c0000L) != 0L) {
				jjmatchedKind = 26;
				jjmatchedPos = 4;
				return 16;
			}
			return -1;
		case 5:
			if ((active0 & 0xc0000L) != 0L) {
				jjmatchedKind = 26;
				jjmatchedPos = 5;
				return 16;
			}
			if ((active0 & 0x400000L) != 0L)
				return 16;
			return -1;
		case 6:
			if ((active0 & 0x40000L) != 0L)
				return 16;
			if ((active0 & 0x80000L) != 0L) {
				jjmatchedKind = 26;
				jjmatchedPos = 6;
				return 16;
			}
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
		case 44:
			return jjStopAtPos(0, 15);
		case 58:
			return jjStopAtPos(0, 14);
		case 59:
			return jjStopAtPos(0, 27);
		case 61:
			return jjStopAtPos(0, 16);
		case 91:
			return jjStopAtPos(0, 10);
		case 93:
			return jjStopAtPos(0, 11);
		case 95:
			return jjStartNfaWithStates_0(0, 26, 16);
		case 67:
		case 99:
			return jjStartNfaWithStates_0(0, 26, 16);
		case 68:
		case 100:
			return jjMoveStringLiteralDfa1_0(0x40000L);
		case 69:
		case 101:
			jjmatchedKind = 26;
			return jjMoveStringLiteralDfa1_0(0x200000L);
		case 71:
		case 103:
			return jjMoveStringLiteralDfa1_0(0x20000L);
		case 78:
		case 110:
			jjmatchedKind = 26;
			return jjMoveStringLiteralDfa1_0(0x820100000L);
		case 83:
		case 115:
			jjmatchedKind = 26;
			return jjMoveStringLiteralDfa1_0(0x280480000L);
		case 87:
		case 119:
			return jjStartNfaWithStates_0(0, 26, 16);
		case 123:
			return jjStopAtPos(0, 12);
		case 125:
			return jjStopAtPos(0, 13);
		default:
			return jjMoveNfa_0(0, 0);
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
		case 68:
		case 100:
			return jjMoveStringLiteralDfa2_0(active0, 0x200000L);
		case 69:
		case 101:
			if ((active0 & 0x20000000L) != 0L)
				return jjStartNfaWithStates_0(1, 26, 16);
			else if ((active0 & 0x80000000L) != 0L)
				return jjStartNfaWithStates_0(1, 26, 16);
			break;
		case 73:
		case 105:
			return jjMoveStringLiteralDfa2_0(active0, 0x40000L);
		case 79:
		case 111:
			return jjMoveStringLiteralDfa2_0(active0, 0x100000L);
		case 82:
		case 114:
			return jjMoveStringLiteralDfa2_0(active0, 0x20000L);
		case 84:
		case 116:
			return jjMoveStringLiteralDfa2_0(active0, 0x400000L);
		case 85:
		case 117:
			return jjMoveStringLiteralDfa2_0(active0, 0x80000L);
		case 87:
		case 119:
			if ((active0 & 0x200000000L) != 0L)
				return jjStartNfaWithStates_0(1, 26, 16);
			else if ((active0 & 0x800000000L) != 0L)
				return jjStartNfaWithStates_0(1, 26, 16);
			break;
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
			return jjMoveStringLiteralDfa3_0(active0, 0x20000L);
		case 66:
		case 98:
			return jjMoveStringLiteralDfa3_0(active0, 0x80000L);
		case 68:
		case 100:
			return jjMoveStringLiteralDfa3_0(active0, 0x100000L);
		case 71:
		case 103:
			return jjMoveStringLiteralDfa3_0(active0, 0x240000L);
		case 82:
		case 114:
			return jjMoveStringLiteralDfa3_0(active0, 0x400000L);
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
		case 69:
		case 101:
			if ((active0 & 0x100000L) != 0L)
				return jjStartNfaWithStates_0(3, 20, 16);
			else if ((active0 & 0x200000L) != 0L)
				return jjStartNfaWithStates_0(3, 21, 16);
			break;
		case 71:
		case 103:
			return jjMoveStringLiteralDfa4_0(active0, 0x80000L);
		case 73:
		case 105:
			return jjMoveStringLiteralDfa4_0(active0, 0x400000L);
		case 80:
		case 112:
			return jjMoveStringLiteralDfa4_0(active0, 0x20000L);
		case 82:
		case 114:
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
		case 65:
		case 97:
			return jjMoveStringLiteralDfa5_0(active0, 0x40000L);
		case 67:
		case 99:
			return jjMoveStringLiteralDfa5_0(active0, 0x400000L);
		case 72:
		case 104:
			if ((active0 & 0x20000L) != 0L)
				return jjStartNfaWithStates_0(4, 17, 16);
			break;
		case 82:
		case 114:
			return jjMoveStringLiteralDfa5_0(active0, 0x80000L);
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
		case 65:
		case 97:
			return jjMoveStringLiteralDfa6_0(active0, 0x80000L);
		case 80:
		case 112:
			return jjMoveStringLiteralDfa6_0(active0, 0x40000L);
		case 84:
		case 116:
			if ((active0 & 0x400000L) != 0L)
				return jjStartNfaWithStates_0(5, 22, 16);
			break;
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
		case 72:
		case 104:
			if ((active0 & 0x40000L) != 0L)
				return jjStartNfaWithStates_0(6, 18, 16);
			break;
		case 80:
		case 112:
			return jjMoveStringLiteralDfa7_0(active0, 0x80000L);
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
		case 72:
		case 104:
			if ((active0 & 0x80000L) != 0L)
				return jjStartNfaWithStates_0(7, 19, 16);
			break;
		default:
			break;
		}
		return jjStartNfa_0(6, active0);
	}

	private int jjStartNfaWithStates_0(int pos, int kind, int state) {
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return pos + 1;
		}
		return jjMoveNfa_0(state, pos + 1);
	}

	static final long[] jjbitVec0 = { 0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL };

	private int jjMoveNfa_0(int startState, int curPos) {
		int startsAt = 0;
		jjnewStateCnt = 28;
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
					case 0:
						if ((0x3ff000000000000L & l) != 0L) {
							if (kind > 24)
								kind = 24;
							jjCheckNAddTwoStates(4, 5);
						} else if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(4);
						else if (curChar == 47)
							jjAddStates(0, 1);
						else if (curChar == 39)
							jjCheckNAddTwoStates(13, 14);
						else if (curChar == 34)
							jjCheckNAddStates(2, 4);
						else if (curChar == 35)
							jjCheckNAddTwoStates(1, 2);
						if (curChar == 45)
							jjAddStates(5, 6);
						break;
					case 1:
						if ((0xffffffffffffdbffL & l) != 0L)
							jjCheckNAddTwoStates(1, 2);
						break;
					case 2:
						if ((0x2400L & l) != 0L && kind > 6)
							kind = 6;
						break;
					case 3:
						if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(4);
						break;
					case 4:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 24)
							kind = 24;
						jjCheckNAddTwoStates(4, 5);
						break;
					case 5:
						if (curChar == 46)
							jjCheckNAdd(6);
						break;
					case 6:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 24)
							kind = 24;
						jjCheckNAdd(6);
						break;
					case 7:
					case 9:
						if (curChar == 34)
							jjCheckNAddStates(2, 4);
						break;
					case 8:
						if ((0xfffffffbffffffffL & l) != 0L)
							jjCheckNAddStates(2, 4);
						break;
					case 11:
						if (curChar == 34 && kind > 25)
							kind = 25;
						break;
					case 12:
						if (curChar == 39)
							jjCheckNAddTwoStates(13, 14);
						break;
					case 13:
						if ((0xffffff7fffffffffL & l) != 0L)
							jjCheckNAddTwoStates(13, 14);
						break;
					case 14:
						if (curChar == 39 && kind > 25)
							kind = 25;
						break;
					case 16:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 26)
							kind = 26;
						jjstateSet[jjnewStateCnt++] = 16;
						break;
					case 17:
						if (curChar == 45)
							jjAddStates(5, 6);
						break;
					case 18:
						if (curChar == 45 && kind > 23)
							kind = 23;
						break;
					case 19:
						if (curChar == 62 && kind > 23)
							kind = 23;
						break;
					case 20:
						if (curChar == 47)
							jjAddStates(0, 1);
						break;
					case 21:
						if (curChar == 42)
							jjCheckNAddStates(7, 9);
						break;
					case 22:
						if ((0xfffffbffffffffffL & l) != 0L)
							jjCheckNAddStates(7, 9);
						break;
					case 23:
						if (curChar == 42)
							jjstateSet[jjnewStateCnt++] = 24;
						break;
					case 24:
						if ((0xffff7fffffffffffL & l) != 0L)
							jjCheckNAddStates(7, 9);
						break;
					case 25:
						if (curChar == 47 && kind > 5)
							kind = 5;
						break;
					case 26:
						if (curChar == 42)
							jjstateSet[jjnewStateCnt++] = 25;
						break;
					case 27:
						if (curChar == 47)
							jjCheckNAddTwoStates(1, 2);
						break;
					default:
						break;
					}
				} while (i != startsAt);
			} else if (curChar < 128) {
				long l = 1L << (curChar & 077);
				do {
					switch (jjstateSet[--i]) {
					case 0:
					case 16:
						if ((0x7fffffe87fffffeL & l) == 0L)
							break;
						if (kind > 26)
							kind = 26;
						jjCheckNAdd(16);
						break;
					case 1:
						jjAddStates(10, 11);
						break;
					case 8:
						jjAddStates(2, 4);
						break;
					case 10:
						if (curChar == 92)
							jjstateSet[jjnewStateCnt++] = 9;
						break;
					case 13:
						jjAddStates(12, 13);
						break;
					case 22:
					case 24:
						jjCheckNAddStates(7, 9);
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
					case 0:
					case 16:
						if ((jjbitVec0[i2] & l2) == 0L)
							break;
						if (kind > 26)
							kind = 26;
						jjCheckNAdd(16);
						break;
					case 1:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjAddStates(10, 11);
						break;
					case 8:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjAddStates(2, 4);
						break;
					case 13:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjAddStates(12, 13);
						break;
					case 22:
					case 24:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjCheckNAddStates(7, 9);
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
			if ((i = jjnewStateCnt) == (startsAt = 28 - (jjnewStateCnt = startsAt)))
				return curPos;
			try {
				curChar = input_stream.readChar();
			} catch (java.io.IOException e) {
				return curPos;
			}
		}
	}

	static final int[] jjnextStates = { 21, 27, 8, 10, 11, 18, 19, 22, 23, 26, 1, 2, 13, 14, };

	/** Token literal values. */
	public static final String[] jjstrLiteralImages = { "", null, null, null, null, null, null, null, null, null,
			"\133", "\135", "\173", "\175", "\72", "\54", "\75", null, null, null, null, null, null, null, null, null,
			null, "\73", null, null, null, null, null, null, null, null, null, "\137", };

	/** Lexer state names. */
	public static final String[] lexStateNames = { "DEFAULT", };
	static final long[] jjtoToken = { 0x3ffffffc01L, };
	static final long[] jjtoSkip = { 0x7eL, };
	protected SimpleCharStream input_stream;
	private final int[] jjrounds = new int[28];
	private final int[] jjstateSet = new int[56];
	protected char curChar;

	/** Constructor. */
	public DOTParserTokenManager(SimpleCharStream stream) {
		if (SimpleCharStream.staticFlag)
			throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
		input_stream = stream;
	}

	/** Constructor. */
	public DOTParserTokenManager(SimpleCharStream stream, int lexState) {
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
		for (i = 28; i-- > 0;)
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
