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
 * @since 2009-02-19
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph.stylesheet.parser;

import org.graphstream.util.parser.SimpleCharStream;
import org.graphstream.util.parser.Token;
import org.graphstream.util.parser.TokenMgrError;

/** Token Manager. */
public class StyleSheetParserTokenManager implements StyleSheetParserConstants {

	private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1, long active2) {
		switch (pos) {
		case 0:
			if ((active0 & 0xffffffffff8c0000L) != 0L || (active1 & 0xffffffffffffffffL) != 0L
					|| (active2 & 0x3fffL) != 0L) {
				jjmatchedKind = 142;
				return 63;
			}
			if ((active0 & 0x4000L) != 0L)
				return 1;
			return -1;
		case 1:
			if ((active2 & 0x200L) != 0L)
				return 63;
			if ((active0 & 0x10000000000000L) != 0L || (active1 & 0x8000000000000000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 142;
					jjmatchedPos = 0;
				}
				return -1;
			}
			if ((active0 & 0xffefffffff8c0000L) != 0L || (active1 & 0x7fffffffffffffffL) != 0L
					|| (active2 & 0x3dffL) != 0L) {
				jjmatchedKind = 142;
				jjmatchedPos = 1;
				return 63;
			}
			return -1;
		case 2:
			if ((active0 & 0xffefffffff000000L) != 0L || (active1 & 0x7ffefffcf77fffffL) != 0L
					|| (active2 & 0x3dffL) != 0L) {
				if (jjmatchedPos != 2) {
					jjmatchedKind = 142;
					jjmatchedPos = 2;
				}
				return 63;
			}
			if ((active0 & 0x8c0000L) != 0L || (active1 & 0x1000008000000L) != 0L)
				return 63;
			if ((active1 & 0x300800000L) != 0L) {
				if (jjmatchedPos < 1) {
					jjmatchedKind = 142;
					jjmatchedPos = 1;
				}
				return -1;
			}
			if ((active0 & 0x10000000000000L) != 0L || (active1 & 0x8000000000000000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 142;
					jjmatchedPos = 0;
				}
				return -1;
			}
			return -1;
		case 3:
			if ((active0 & 0x306000006040000L) != 0L || (active1 & 0x2100000830080080L) != 0L
					|| (active2 & 0x588L) != 0L)
				return 63;
			if ((active1 & 0x38L) != 0L || (active2 & 0x10L) != 0L) {
				if (jjmatchedPos < 2) {
					jjmatchedKind = 142;
					jjmatchedPos = 2;
				}
				return -1;
			}
			if ((active0 & 0xfce9fffff9000000L) != 0L || (active1 & 0x5efefff4c777ff47L) != 0L
					|| (active2 & 0x3867L) != 0L) {
				if (jjmatchedPos != 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return 63;
			}
			if ((active1 & 0x300800000L) != 0L) {
				if (jjmatchedPos < 1) {
					jjmatchedKind = 142;
					jjmatchedPos = 1;
				}
				return -1;
			}
			if ((active0 & 0x10000000000000L) != 0L || (active1 & 0x8000000000000000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 142;
					jjmatchedPos = 0;
				}
				return -1;
			}
			return -1;
		case 4:
			if ((active0 & 0xe480000001000000L) != 0L || (active1 & 0x884007085078004L) != 0L
					|| (active2 & 0x564L) != 0L)
				return 63;
			if ((active1 & 0x38L) != 0L || (active2 & 0x10L) != 0L) {
				if (jjmatchedPos < 2) {
					jjmatchedKind = 142;
					jjmatchedPos = 2;
				}
				return -1;
			}
			if ((active0 & 0x801ffc070000000L) != 0L || (active1 & 0x40f80002400000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			if ((active1 & 0x300800000L) != 0L) {
				if (jjmatchedPos < 1) {
					jjmatchedKind = 142;
					jjmatchedPos = 1;
				}
				return -1;
			}
			if ((active0 & 0x10000000000000L) != 0L || (active1 & 0x8000000000000000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 142;
					jjmatchedPos = 0;
				}
				return -1;
			}
			if ((active0 & 0x1068003f88000000L) != 0L || (active1 & 0x563a078440307f43L) != 0L
					|| (active2 & 0x3803L) != 0L) {
				if (jjmatchedPos != 4) {
					jjmatchedKind = 142;
					jjmatchedPos = 4;
				}
				return 63;
			}
			return -1;
		case 5:
			if ((active0 & 0x1068003f80000000L) != 0L || (active1 & 0x461a068000203e02L) != 0L
					|| (active2 & 0x3803L) != 0L) {
				if (jjmatchedPos != 5) {
					jjmatchedKind = 142;
					jjmatchedPos = 5;
				}
				return 63;
			}
			if ((active0 & 0x8000000L) != 0L || (active1 & 0x20010440104141L) != 0L || (active2 & 0x40L) != 0L)
				return 63;
			if ((active1 & 0x1000000000000000L) != 0L) {
				if (jjmatchedPos < 4) {
					jjmatchedKind = 142;
					jjmatchedPos = 4;
				}
				return -1;
			}
			if ((active1 & 0x38L) != 0L || (active2 & 0x10L) != 0L) {
				if (jjmatchedPos < 2) {
					jjmatchedKind = 142;
					jjmatchedPos = 2;
				}
				return -1;
			}
			if ((active0 & 0x801ffc070000000L) != 0L || (active1 & 0x40f80002400000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			if ((active1 & 0x300800000L) != 0L) {
				if (jjmatchedPos < 1) {
					jjmatchedKind = 142;
					jjmatchedPos = 1;
				}
				return -1;
			}
			if ((active0 & 0x10000000000000L) != 0L || (active1 & 0x8000000000000000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 142;
					jjmatchedPos = 0;
				}
				return -1;
			}
			return -1;
		case 6:
			if ((active0 & 0x8000000000000L) != 0L || (active1 & 0x18008000000000L) != 0L || (active2 & 0x1000L) != 0L)
				return 63;
			if ((active0 & 0x1060000000000000L) != 0L || (active1 & 0x602060000203e00L) != 0L
					|| (active2 & 0x2803L) != 0L) {
				jjmatchedKind = 142;
				jjmatchedPos = 6;
				return 63;
			}
			if ((active1 & 0x1000000000000000L) != 0L) {
				if (jjmatchedPos < 4) {
					jjmatchedKind = 142;
					jjmatchedPos = 4;
				}
				return -1;
			}
			if ((active1 & 0x38L) != 0L || (active2 & 0x10L) != 0L) {
				if (jjmatchedPos < 2) {
					jjmatchedKind = 142;
					jjmatchedPos = 2;
				}
				return -1;
			}
			if ((active0 & 0x3f80000000L) != 0L || (active1 & 0x4000000000000002L) != 0L) {
				if (jjmatchedPos < 5) {
					jjmatchedKind = 142;
					jjmatchedPos = 5;
				}
				return -1;
			}
			if ((active0 & 0x801ffc070000000L) != 0L || (active1 & 0x40f80002400000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			if ((active1 & 0x300800000L) != 0L) {
				if (jjmatchedPos < 1) {
					jjmatchedKind = 142;
					jjmatchedPos = 1;
				}
				return -1;
			}
			if ((active0 & 0x10000000000000L) != 0L || (active1 & 0x8000000000000000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 142;
					jjmatchedPos = 0;
				}
				return -1;
			}
			return -1;
		case 7:
			if ((active1 & 0x1000000000000000L) != 0L) {
				if (jjmatchedPos < 4) {
					jjmatchedKind = 142;
					jjmatchedPos = 4;
				}
				return -1;
			}
			if ((active0 & 0x801ffc070000000L) != 0L || (active1 & 0x40f80002400000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			if ((active1 & 0x600020000000000L) != 0L || (active2 & 0x2000L) != 0L)
				return 63;
			if ((active1 & 0x38L) != 0L || (active2 & 0x10L) != 0L) {
				if (jjmatchedPos < 2) {
					jjmatchedKind = 142;
					jjmatchedPos = 2;
				}
				return -1;
			}
			if ((active1 & 0x200000000L) != 0L) {
				if (jjmatchedPos < 1) {
					jjmatchedKind = 142;
					jjmatchedPos = 1;
				}
				return -1;
			}
			if ((active1 & 0x2000000000000L) != 0L) {
				if (jjmatchedPos < 6) {
					jjmatchedKind = 142;
					jjmatchedPos = 6;
				}
				return -1;
			}
			if ((active1 & 0x8000000000000000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 142;
					jjmatchedPos = 0;
				}
				return -1;
			}
			if ((active0 & 0x3f80000000L) != 0L || (active1 & 0x4000000000000002L) != 0L) {
				if (jjmatchedPos < 5) {
					jjmatchedKind = 142;
					jjmatchedPos = 5;
				}
				return -1;
			}
			if ((active0 & 0x1060000000000000L) != 0L || (active1 & 0x40000203e00L) != 0L || (active2 & 0x803L) != 0L) {
				if (jjmatchedPos != 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return 63;
			}
			return -1;
		case 8:
			if ((active1 & 0x1000000000000000L) != 0L) {
				if (jjmatchedPos < 4) {
					jjmatchedKind = 142;
					jjmatchedPos = 4;
				}
				return -1;
			}
			if ((active0 & 0x801ffc070000000L) != 0L || (active1 & 0x40f00002400000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			if ((active1 & 0x40000200000L) != 0L)
				return 63;
			if ((active1 & 0x8L) != 0L || (active2 & 0x10L) != 0L) {
				if (jjmatchedPos < 2) {
					jjmatchedKind = 142;
					jjmatchedPos = 2;
				}
				return -1;
			}
			if ((active1 & 0x3e00L) != 0L || (active2 & 0x2L) != 0L) {
				if (jjmatchedPos < 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return -1;
			}
			if ((active0 & 0x1060000000000000L) != 0L || (active2 & 0x801L) != 0L) {
				jjmatchedKind = 142;
				jjmatchedPos = 8;
				return 63;
			}
			if ((active1 & 0x2000000000000L) != 0L) {
				if (jjmatchedPos < 6) {
					jjmatchedKind = 142;
					jjmatchedPos = 6;
				}
				return -1;
			}
			if ((active1 & 0x8000000000000000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 142;
					jjmatchedPos = 0;
				}
				return -1;
			}
			if ((active0 & 0x3f80000000L) != 0L || (active1 & 0x4000000000000002L) != 0L) {
				if (jjmatchedPos < 5) {
					jjmatchedKind = 142;
					jjmatchedPos = 5;
				}
				return -1;
			}
			return -1;
		case 9:
			if ((active0 & 0x1060000000000000L) != 0L || (active2 & 0x800L) != 0L)
				return 63;
			if ((active1 & 0x3e00L) != 0L || (active2 & 0x2L) != 0L) {
				if (jjmatchedPos < 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return -1;
			}
			if ((active2 & 0x1L) != 0L) {
				if (jjmatchedPos != 9) {
					jjmatchedKind = 142;
					jjmatchedPos = 9;
				}
				return 63;
			}
			if ((active1 & 0x1000000000000000L) != 0L) {
				if (jjmatchedPos < 4) {
					jjmatchedKind = 142;
					jjmatchedPos = 4;
				}
				return -1;
			}
			if ((active1 & 0x2000000000000L) != 0L) {
				if (jjmatchedPos < 6) {
					jjmatchedKind = 142;
					jjmatchedPos = 6;
				}
				return -1;
			}
			if ((active0 & 0x3f80000000L) != 0L || (active1 & 0x4000000000000002L) != 0L) {
				if (jjmatchedPos < 5) {
					jjmatchedKind = 142;
					jjmatchedPos = 5;
				}
				return -1;
			}
			if ((active0 & 0x801f98060000000L) != 0L || (active1 & 0x40f00000400000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			if ((active1 & 0x8000000000000000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 142;
					jjmatchedPos = 0;
				}
				return -1;
			}
			return -1;
		case 10:
			if ((active2 & 0x1L) != 0L) {
				if (jjmatchedPos < 9) {
					jjmatchedKind = 142;
					jjmatchedPos = 9;
				}
				return -1;
			}
			if ((active1 & 0x3e00L) != 0L || (active2 & 0x2L) != 0L) {
				if (jjmatchedPos < 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return -1;
			}
			if ((active1 & 0x1000000000000000L) != 0L) {
				if (jjmatchedPos < 4) {
					jjmatchedKind = 142;
					jjmatchedPos = 4;
				}
				return -1;
			}
			if ((active1 & 0x2000000000000L) != 0L) {
				if (jjmatchedPos < 6) {
					jjmatchedKind = 142;
					jjmatchedPos = 6;
				}
				return -1;
			}
			if ((active0 & 0x801f80000000000L) != 0L || (active1 & 0xf00000000000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			if ((active0 & 0x3f80000000L) != 0L || (active1 & 0x4000000000000002L) != 0L) {
				if (jjmatchedPos < 5) {
					jjmatchedKind = 142;
					jjmatchedPos = 5;
				}
				return -1;
			}
			if ((active1 & 0x8000000000000000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 142;
					jjmatchedPos = 0;
				}
				return -1;
			}
			return -1;
		case 11:
			if ((active2 & 0x1L) != 0L) {
				if (jjmatchedPos < 9) {
					jjmatchedKind = 142;
					jjmatchedPos = 9;
				}
				return -1;
			}
			if ((active1 & 0x3e00L) != 0L || (active2 & 0x2L) != 0L) {
				if (jjmatchedPos < 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return -1;
			}
			if ((active0 & 0x801780000000000L) != 0L || (active1 & 0xd00000000000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			if ((active0 & 0x3b00000000L) != 0L || (active1 & 0x2L) != 0L) {
				if (jjmatchedPos < 5) {
					jjmatchedKind = 142;
					jjmatchedPos = 5;
				}
				return -1;
			}
			if ((active1 & 0x8000000000000000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 142;
					jjmatchedPos = 0;
				}
				return -1;
			}
			return -1;
		case 12:
			if ((active2 & 0x1L) != 0L) {
				if (jjmatchedPos < 9) {
					jjmatchedKind = 142;
					jjmatchedPos = 9;
				}
				return -1;
			}
			if ((active1 & 0x3e00L) != 0L || (active2 & 0x2L) != 0L) {
				if (jjmatchedPos < 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return -1;
			}
			if ((active0 & 0x800780000000000L) != 0L || (active1 & 0x900000000000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			if ((active0 & 0x2000000000L) != 0L) {
				if (jjmatchedPos < 5) {
					jjmatchedKind = 142;
					jjmatchedPos = 5;
				}
				return -1;
			}
			if ((active1 & 0x8000000000000000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 142;
					jjmatchedPos = 0;
				}
				return -1;
			}
			return -1;
		case 13:
			if ((active2 & 0x1L) != 0L) {
				if (jjmatchedPos < 9) {
					jjmatchedKind = 142;
					jjmatchedPos = 9;
				}
				return -1;
			}
			if ((active1 & 0x3e00L) != 0L || (active2 & 0x2L) != 0L) {
				if (jjmatchedPos < 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return -1;
			}
			if ((active0 & 0x800780000000000L) != 0L || (active1 & 0x900000000000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			return -1;
		case 14:
			if ((active2 & 0x1L) != 0L) {
				if (jjmatchedPos < 9) {
					jjmatchedKind = 142;
					jjmatchedPos = 9;
				}
				return -1;
			}
			if ((active1 & 0x3e00L) != 0L || (active2 & 0x2L) != 0L) {
				if (jjmatchedPos < 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return -1;
			}
			if ((active0 & 0x780000000000L) != 0L || (active1 & 0x100000000000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			return -1;
		case 15:
			if ((active2 & 0x1L) != 0L) {
				if (jjmatchedPos < 9) {
					jjmatchedKind = 142;
					jjmatchedPos = 9;
				}
				return -1;
			}
			if ((active1 & 0x2e00L) != 0L || (active2 & 0x2L) != 0L) {
				if (jjmatchedPos < 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return -1;
			}
			if ((active0 & 0x680000000000L) != 0L || (active1 & 0x100000000000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			return -1;
		case 16:
			if ((active2 & 0x1L) != 0L) {
				if (jjmatchedPos < 9) {
					jjmatchedKind = 142;
					jjmatchedPos = 9;
				}
				return -1;
			}
			if ((active1 & 0x2e00L) != 0L || (active2 & 0x2L) != 0L) {
				if (jjmatchedPos < 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return -1;
			}
			if ((active0 & 0x680000000000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			return -1;
		case 17:
			if ((active2 & 0x1L) != 0L) {
				if (jjmatchedPos < 9) {
					jjmatchedKind = 142;
					jjmatchedPos = 9;
				}
				return -1;
			}
			if ((active1 & 0xe00L) != 0L || (active2 & 0x2L) != 0L) {
				if (jjmatchedPos < 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return -1;
			}
			if ((active0 & 0x680000000000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			return -1;
		case 18:
			if ((active2 & 0x1L) != 0L) {
				if (jjmatchedPos < 9) {
					jjmatchedKind = 142;
					jjmatchedPos = 9;
				}
				return -1;
			}
			if ((active1 & 0x800L) != 0L || (active2 & 0x2L) != 0L) {
				if (jjmatchedPos < 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return -1;
			}
			if ((active0 & 0x680000000000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			return -1;
		case 19:
			if ((active2 & 0x2L) != 0L) {
				if (jjmatchedPos < 7) {
					jjmatchedKind = 142;
					jjmatchedPos = 7;
				}
				return -1;
			}
			if ((active2 & 0x1L) != 0L) {
				if (jjmatchedPos < 9) {
					jjmatchedKind = 142;
					jjmatchedPos = 9;
				}
				return -1;
			}
			if ((active0 & 0x680000000000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			return -1;
		case 20:
			if ((active2 & 0x1L) != 0L) {
				if (jjmatchedPos < 9) {
					jjmatchedKind = 142;
					jjmatchedPos = 9;
				}
				return -1;
			}
			if ((active0 & 0x400000000000L) != 0L) {
				if (jjmatchedPos < 3) {
					jjmatchedKind = 142;
					jjmatchedPos = 3;
				}
				return -1;
			}
			return -1;
		default:
			return -1;
		}
	}

	private final int jjStartNfa_0(int pos, long active0, long active1, long active2) {
		return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0, active1, active2), pos + 1);
	}

	private int jjStopAtPos(int pos, int kind) {
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		return pos + 1;
	}

	private int jjMoveStringLiteralDfa0_0() {
		switch (curChar) {
		case 35:
			return jjStartNfaWithStates_0(0, 14, 1);
		case 40:
			return jjStopAtPos(0, 12);
		case 41:
			return jjStopAtPos(0, 13);
		case 44:
			return jjStopAtPos(0, 17);
		case 46:
			return jjStopAtPos(0, 9);
		case 58:
			return jjStopAtPos(0, 15);
		case 59:
			return jjStopAtPos(0, 16);
		case 76:
			return jjMoveStringLiteralDfa1_0(0x0L, 0x8000000000000000L, 0x0L);
		case 97:
			return jjMoveStringLiteralDfa1_0(0xe000000000000000L, 0x800004380800000L, 0x4L);
		case 98:
			return jjMoveStringLiteralDfa1_0(0x0L, 0x2021000030000000L, 0x0L);
		case 99:
			return jjMoveStringLiteralDfa1_0(0x0L, 0x1004010400000002L, 0x1000L);
		case 100:
			return jjMoveStringLiteralDfa1_0(0x0L, 0x80000000001f8L, 0x0L);
		case 101:
			return jjMoveStringLiteralDfa1_0(0x2000000L, 0x0L, 0x0L);
		case 102:
			return jjMoveStringLiteralDfa1_0(0x70000000L, 0x40008000000L, 0x88L);
		case 103:
			return jjMoveStringLiteralDfa1_0(0x1000000L, 0x3e00L, 0x0L);
		case 104:
			return jjMoveStringLiteralDfa1_0(0x0L, 0x4000L, 0x1L);
		case 105:
			return jjMoveStringLiteralDfa1_0(0x6000000000000L, 0x40078000L, 0x60L);
		case 106:
			return jjMoveStringLiteralDfa1_0(0x1000000000000000L, 0x8000000000L, 0x0L);
		case 108:
			return jjMoveStringLiteralDfa1_0(0x0L, 0x100000800000000L, 0x0L);
		case 110:
			return jjMoveStringLiteralDfa1_0(0x4000000L, 0x180000L, 0x500L);
		case 111:
			return jjMoveStringLiteralDfa1_0(0x0L, 0x2000000L, 0x0L);
		case 112:
			return jjMoveStringLiteralDfa1_0(0x8000000000000L, 0x690000000000004L, 0x810L);
		case 114:
			return jjMoveStringLiteralDfa1_0(0xc0000L, 0x2001000000000L, 0x0L);
		case 115:
			return jjMoveStringLiteralDfa1_0(0x780003f88000000L, 0x4000000000000001L, 0x2000L);
		case 116:
			return jjMoveStringLiteralDfa1_0(0x801ffc000000000L, 0x40fa0000200000L, 0x200L);
		case 117:
			return jjMoveStringLiteralDfa1_0(0x800000L, 0x2001000000L, 0x0L);
		case 118:
			return jjMoveStringLiteralDfa1_0(0x60000000000000L, 0x0L, 0x2L);
		case 122:
			return jjMoveStringLiteralDfa1_0(0x10000000000000L, 0x4400000L, 0x0L);
		case 123:
			return jjStopAtPos(0, 10);
		case 125:
			return jjStopAtPos(0, 11);
		default:
			return jjMoveNfa_0(0, 0);
		}
	}

	private int jjMoveStringLiteralDfa1_0(long active0, long active1, long active2) {
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(0, active0, active1, active2);
			return 1;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa2_0(active0, 0x10000000000000L, active1, 0x8000000000000000L, active2, 0L);
		case 97:
			return jjMoveStringLiteralDfa2_0(active0, 0x8000000000000L, active1, 0x80000000000042L, active2, 0L);
		case 98:
			return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x4000000000L, active2, 0L);
		case 99:
			return jjMoveStringLiteralDfa2_0(active0, 0x1006000000000000L, active1, 0L, active2, 0L);
		case 100:
			return jjMoveStringLiteralDfa2_0(active0, 0x2000000L, active1, 0L, active2, 0L);
		case 101:
			return jjMoveStringLiteralDfa2_0(active0, 0x801ffc000000000L, active1, 0x40f80c00000000L, active2, 0x2002L);
		case 103:
			return jjMoveStringLiteralDfa2_0(active0, 0xc0000L, active1, 0L, active2, 0L);
		case 104:
			return jjMoveStringLiteralDfa2_0(active0, 0x480003c00000000L, active1, 0L, active2, 0L);
		case 105:
			return jjMoveStringLiteralDfa2_0(active0, 0x360000070000000L, active1, 0x108011008004000L, active2, 0x10L);
		case 108:
			return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x2000000080000004L, active2, 0x1008L);
		case 109:
			return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x78000L, active2, 0x60L);
		case 110:
			return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x800002001000000L, active2, 0L);
		case 111:
			if ((active2 & 0x200L) != 0L)
				return jjStartNfaWithStates_0(1, 137, 63);
			return jjMoveStringLiteralDfa2_0(active0, 0x4000000L, active1, 0x613000034580180L, active2, 0x501L);
		case 112:
			return jjMoveStringLiteralDfa2_0(active0, 0x8000000L, active1, 0x1L, active2, 0L);
		case 113:
			return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x4000000000000000L, active2, 0L);
		case 114:
			return jjMoveStringLiteralDfa2_0(active0, 0xe000000001800000L, active1, 0x4060000203e00L, active2, 0x884L);
		case 116:
			return jjMoveStringLiteralDfa2_0(active0, 0x380000000L, active1, 0x340800000L, active2, 0L);
		case 117:
			return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x1020008000000000L, active2, 0L);
		case 118:
			return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x2000000L, active2, 0L);
		case 121:
			return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x38L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(0, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(0, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(1, active0, active1, active2);
			return 2;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x300800000L, active2, 0L);
		case 97:
			return jjMoveStringLiteralDfa3_0(active0, 0x480003c01000000L, active1, 0x800004007be04L, active2, 0x60L);
		case 98:
			if ((active0 & 0x80000L) != 0L) {
				jjmatchedKind = 19;
				jjmatchedPos = 2;
			}
			return jjMoveStringLiteralDfa3_0(active0, 0x40000L, active1, 0x1000000000000000L, active2, 0L);
		case 100:
			return jjMoveStringLiteralDfa3_0(active0, 0x8000004000000L, active1, 0x2001004000L, active2, 0x500L);
		case 101:
			return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x40002000000L, active2, 0x10L);
		case 102:
			return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x800000000L, active2, 0L);
		case 103:
			return jjMoveStringLiteralDfa3_0(active0, 0x2000000L, active1, 0x800001000000000L, active2, 0L);
		case 105:
			return jjMoveStringLiteralDfa3_0(active0, 0x10000000000000L, active1, 0x20000000000L, active2, 0x1000L);
		case 108:
			if ((active0 & 0x800000L) != 0L)
				return jjStartNfaWithStates_0(2, 23, 63);
			return jjMoveStringLiteralDfa3_0(active0, 0x70000000L, active1, 0x610000030000000L, active2, 0x2000L);
		case 110:
			return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x18000040008003aL, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa3_0(active0, 0x1006000000000000L, active1, 0x2004004084400000L, active2,
					0x888L);
		case 114:
			return jjMoveStringLiteralDfa3_0(active0, 0xe000000388000000L, active1, 0x10000100001L, active2, 0x7L);
		case 115:
			return jjMoveStringLiteralDfa3_0(active0, 0x60000000000000L, active1, 0x8000008000000040L, active2, 0L);
		case 116:
			if ((active1 & 0x8000000L) != 0L)
				return jjStartNfaWithStates_0(2, 91, 63);
			return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x20000000000080L, active2, 0L);
		case 117:
			return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x4002000000200100L, active2, 0L);
		case 120:
			if ((active1 & 0x1000000000000L) != 0L)
				return jjStartNfaWithStates_0(2, 112, 63);
			return jjMoveStringLiteralDfa3_0(active0, 0x801ffc000000000L, active1, 0x40f80000000000L, active2, 0L);
		case 122:
			return jjMoveStringLiteralDfa3_0(active0, 0x300000000000000L, active1, 0L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(1, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa3_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(1, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(2, active0, active1, active2);
			return 3;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x38L, active2, 0x10L);
		case 97:
			if ((active0 & 0x40000L) != 0L)
				return jjStartNfaWithStates_0(3, 18, 63);
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x4000020000000000L, active2, 0L);
		case 98:
			if ((active1 & 0x2000000000000000L) != 0L)
				return jjStartNfaWithStates_0(3, 125, 63);
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x100L, active2, 0L);
		case 99:
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x10000000000L, active2, 0x1000L);
		case 100:
			if ((active1 & 0x10000000L) != 0L) {
				jjmatchedKind = 92;
				jjmatchedPos = 3;
			}
			return jjMoveStringLiteralDfa4_0(active0, 0x8003c00000000L, active1, 0x20007e00L, active2, 0L);
		case 101:
			if ((active0 & 0x2000000L) != 0L)
				return jjStartNfaWithStates_0(3, 25, 63);
			else if ((active0 & 0x4000000L) != 0L) {
				jjmatchedKind = 26;
				jjmatchedPos = 3;
			} else if ((active0 & 0x100000000000000L) != 0L) {
				jjmatchedKind = 56;
				jjmatchedPos = 3;
			} else if ((active1 & 0x80000L) != 0L)
				return jjStartNfaWithStates_0(3, 83, 63);
			else if ((active1 & 0x100000000000000L) != 0L)
				return jjStartNfaWithStates_0(3, 120, 63);
			return jjMoveStringLiteralDfa4_0(active0, 0x200000000000000L, active1, 0x80042001000000L, active2, 0x2500L);
		case 103:
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x78000L, active2, 0x60L);
		case 104:
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x1000000040L, active2, 0L);
		case 105:
			return jjMoveStringLiteralDfa4_0(active0, 0x60000008000000L, active1, 0x1000000000000005L, active2, 0x1L);
		case 106:
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 0x800L);
		case 108:
			return jjMoveStringLiteralDfa4_0(active0, 0x70000000L, active1, 0x800000140000000L, active2, 0L);
		case 109:
			if ((active2 & 0x80L) != 0L)
				return jjStartNfaWithStates_0(3, 135, 63);
			return jjMoveStringLiteralDfa4_0(active0, 0x1000000000000000L, active1, 0x8000004500000L, active2, 0L);
		case 110:
			if ((active0 & 0x4000000000000L) != 0L) {
				jjmatchedKind = 50;
				jjmatchedPos = 3;
			}
			return jjMoveStringLiteralDfa4_0(active0, 0x12000000000000L, active1, 0x2000080200000L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa4_0(active0, 0xe000000380000000L, active1, 0L, active2, 0x4L);
		case 112:
			return jjMoveStringLiteralDfa4_0(active0, 0x480000001000000L, active1, 0L, active2, 0L);
		case 113:
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x8000000000000000L, active2, 0L);
		case 114:
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x202000000L, active2, 0L);
		case 115:
			if ((active1 & 0x80L) != 0L)
				return jjStartNfaWithStates_0(3, 71, 63);
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x4000000000000L, active2, 0L);
		case 116:
			if ((active1 & 0x800000000L) != 0L)
				return jjStartNfaWithStates_0(3, 99, 63);
			return jjMoveStringLiteralDfa4_0(active0, 0x801ffc000000000L, active1, 0x60f88400000000L, active2, 0x2L);
		case 118:
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x4000000002L, active2, 0L);
		case 119:
			if ((active2 & 0x8L) != 0L)
				return jjStartNfaWithStates_0(3, 131, 63);
			break;
		case 121:
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x610000000000000L, active2, 0L);
		case 122:
			return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x800000L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(2, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa4_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(2, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(3, active0, active1, active2);
			return 4;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa5_0(active0, 0xa03ffc070000000L, active1, 0x40f80022400000L, active2, 0L);
		case 48:
			if ((active2 & 0x100L) != 0L)
				return jjStartNfaWithStates_0(4, 136, 63);
			break;
		case 49:
			if ((active2 & 0x400L) != 0L)
				return jjStartNfaWithStates_0(4, 138, 63);
			break;
		case 97:
			return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x100002L, active2, 0L);
		case 98:
			return jjMoveStringLiteralDfa5_0(active0, 0x60000000000000L, active1, 0L, active2, 0L);
		case 99:
			return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x1000000000200000L, active2, 0x2010L);
		case 100:
			return jjMoveStringLiteralDfa5_0(active0, 0x10000000000000L, active1, 0x2000000000000L, active2, 0L);
		case 101:
			if ((active0 & 0x80000000000000L) != 0L) {
				jjmatchedKind = 55;
				jjmatchedPos = 4;
			} else if ((active1 & 0x4000000000L) != 0L)
				return jjStartNfaWithStates_0(4, 102, 63);
			else if ((active1 & 0x800000000000000L) != 0L)
				return jjStartNfaWithStates_0(4, 123, 63);
			else if ((active2 & 0x20L) != 0L) {
				jjmatchedKind = 133;
				jjmatchedPos = 4;
			}
			return jjMoveStringLiteralDfa5_0(active0, 0x400000000000000L, active1, 0x50007c040L, active2, 0x840L);
		case 103:
			if ((active1 & 0x80000000L) != 0L)
				return jjStartNfaWithStates_0(4, 95, 63);
			return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x10000000000000L, active2, 0L);
		case 104:
			if ((active0 & 0x1000000L) != 0L)
				return jjStartNfaWithStates_0(4, 24, 63);
			break;
		case 105:
			return jjMoveStringLiteralDfa5_0(active0, 0x8000000000000L, active1, 0x8240003e20L, active2, 0x2L);
		case 107:
			return jjMoveStringLiteralDfa5_0(active0, 0x380000000L, active1, 0L, active2, 0x1000L);
		case 108:
			if ((active1 & 0x80000000000000L) != 0L)
				return jjStartNfaWithStates_0(4, 119, 63);
			return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x600010000000100L, active2, 0L);
		case 110:
			if ((active1 & 0x4L) != 0L)
				return jjStartNfaWithStates_0(4, 66, 63);
			return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x20000000000L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa5_0(active0, 0x3c00000000L, active1, 0x28000000800000L, active2, 0L);
		case 112:
			return jjMoveStringLiteralDfa5_0(active0, 0x1000000000000000L, active1, 0x40000000008L, active2, 0L);
		case 114:
			if ((active1 & 0x2000000000L) != 0L) {
				jjmatchedKind = 101;
				jjmatchedPos = 4;
			}
			return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x4000000001000000L, active2, 0L);
		case 115:
			if ((active1 & 0x4000000L) != 0L)
				return jjStartNfaWithStates_0(4, 90, 63);
			else if ((active1 & 0x4000000000000L) != 0L)
				return jjStartNfaWithStates_0(4, 114, 63);
			return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x10L, active2, 0L);
		case 116:
			if ((active1 & 0x1000000000L) != 0L)
				return jjStartNfaWithStates_0(4, 100, 63);
			return jjMoveStringLiteralDfa5_0(active0, 0x8000000L, active1, 0x1L, active2, 0L);
		case 117:
			return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x8000000000000000L, active2, 0L);
		case 119:
			if ((active2 & 0x4L) != 0L) {
				jjmatchedKind = 130;
				jjmatchedPos = 4;
			}
			return jjMoveStringLiteralDfa5_0(active0, 0xe000000000000000L, active1, 0L, active2, 0L);
		case 122:
			return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 0x1L);
		default:
			break;
		}
		return jjStartNfa_0(3, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa5_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(3, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(4, active0, active1, active2);
			return 5;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa6_0(active0, 0xe400000000000000L, active1, 0x1000000001078000L, active2, 0L);
		case 97:
			return jjMoveStringLiteralDfa6_0(active0, 0x800000000000000L, active1, 0x8000000000200000L, active2, 0L);
		case 98:
			return jjMoveStringLiteralDfa6_0(active0, 0x600000000000L, active1, 0x80000000000L, active2, 0L);
		case 99:
			if ((active1 & 0x40000000L) != 0L)
				return jjStartNfaWithStates_0(5, 94, 63);
			return jjMoveStringLiteralDfa6_0(active0, 0x8020000000L, active1, 0x200000000020L, active2, 0x802L);
		case 100:
			return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x400000000000L, active2, 0L);
		case 101:
			if ((active0 & 0x8000000L) != 0L) {
				jjmatchedKind = 27;
				jjmatchedPos = 5;
			} else if ((active1 & 0x100L) != 0L)
				return jjStartNfaWithStates_0(5, 72, 63);
			else if ((active1 & 0x10000000000L) != 0L)
				return jjStartNfaWithStates_0(5, 104, 63);
			return jjMoveStringLiteralDfa6_0(active0, 0x10000380000000L, active1, 0x4002000000003e01L, active2,
					0x1000L);
		case 102:
			return jjMoveStringLiteralDfa6_0(active0, 0x20000000000L, active1, 0x40008100000000L, active2, 0L);
		case 103:
			return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x20200000000L, active2, 0L);
		case 104:
			return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 0x10L);
		case 105:
			return jjMoveStringLiteralDfa6_0(active0, 0x60000040000000L, active1, 0x600000020000010L, active2, 0L);
		case 108:
			if ((active1 & 0x100000L) != 0L)
				return jjStartNfaWithStates_0(5, 84, 63);
			return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x40000000008L, active2, 0L);
		case 109:
			return jjMoveStringLiteralDfa6_0(active0, 0x202004010000000L, active1, 0L, active2, 0L);
		case 110:
			if ((active1 & 0x4000L) != 0L)
				return jjStartNfaWithStates_0(5, 78, 63);
			else if ((active1 & 0x20000000000000L) != 0L)
				return jjStartNfaWithStates_0(5, 117, 63);
			return jjMoveStringLiteralDfa6_0(active0, 0x8000000000000L, active1, 0x8000000000000L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa6_0(active0, 0x1000800000000000L, active1, 0x10000000800000L, active2, 0x1L);
		case 112:
			return jjMoveStringLiteralDfa6_0(active0, 0x1000000000000L, active1, 0x800000000000L, active2, 0L);
		case 114:
			if ((active1 & 0x400000000L) != 0L)
				return jjStartNfaWithStates_0(5, 98, 63);
			return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x100000400000L, active2, 0L);
		case 115:
			if ((active1 & 0x40L) != 0L)
				return jjStartNfaWithStates_0(5, 70, 63);
			else if ((active2 & 0x40L) != 0L)
				return jjStartNfaWithStates_0(5, 134, 63);
			return jjMoveStringLiteralDfa6_0(active0, 0x50000000000L, active1, 0x2L, active2, 0L);
		case 116:
			return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 0x2000L);
		case 118:
			return jjMoveStringLiteralDfa6_0(active0, 0x180000000000L, active1, 0L, active2, 0L);
		case 119:
			return jjMoveStringLiteralDfa6_0(active0, 0x3c00000000L, active1, 0L, active2, 0L);
		case 122:
			return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x2000000L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(4, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa6_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(4, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(5, active0, active1, active2);
			return 6;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa7_0(active0, 0x3f80000000L, active1, 0x4000000000000003L, active2, 0L);
		case 97:
			return jjMoveStringLiteralDfa7_0(active0, 0x1600000000000L, active1, 0x840000400008L, active2, 0x12L);
		case 99:
			return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x1000000000000000L, active2, 0L);
		case 100:
			if ((active1 & 0x8000000000000L) != 0L)
				return jjStartNfaWithStates_0(6, 115, 63);
			else if ((active2 & 0x1000L) != 0L)
				return jjStartNfaWithStates_0(6, 140, 63);
			return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x2000000000000L, active2, 0L);
		case 101:
			return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0x2000L);
		case 102:
			return jjMoveStringLiteralDfa7_0(active0, 0x800000000000L, active1, 0L, active2, 0L);
		case 103:
			if ((active0 & 0x8000000000000L) != 0L)
				return jjStartNfaWithStates_0(6, 51, 63);
			break;
		case 104:
			return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x200000000L, active2, 0L);
		case 105:
			return jjMoveStringLiteralDfa7_0(active0, 0x20001c0000000000L, active1, 0x40600000000000L, active2, 0L);
		case 108:
			return jjMoveStringLiteralDfa7_0(active0, 0x860000000000000L, active1, 0x20000000000L, active2, 0L);
		case 109:
			if ((active1 & 0x800000L) != 0L)
				return jjStopAtPos(6, 87);
			return jjMoveStringLiteralDfa7_0(active0, 0x40000000L, active1, 0L, active2, 0L);
		case 110:
			if ((active1 & 0x10000000000000L) != 0L)
				return jjStartNfaWithStates_0(6, 116, 63);
			return jjMoveStringLiteralDfa7_0(active0, 0x1000000000000000L, active1, 0x600000000003e00L, active2, 0x1L);
		case 111:
			return jjMoveStringLiteralDfa7_0(active0, 0x20202c030000000L, active1, 0x180002000020L, active2, 0L);
		case 112:
			return jjMoveStringLiteralDfa7_0(active0, 0x400000000000000L, active1, 0L, active2, 0L);
		case 114:
			return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x8000000000000000L, active2, 0L);
		case 115:
			return jjMoveStringLiteralDfa7_0(active0, 0xc000000000000000L, active1, 0x70000L, active2, 0L);
		case 116:
			if ((active1 & 0x100000000L) != 0L)
				return jjStopAtPos(6, 96);
			return jjMoveStringLiteralDfa7_0(active0, 0x10000000000L, active1, 0x20208000L, active2, 0x800L);
		case 120:
			if ((active0 & 0x10000000000000L) != 0L)
				return jjStopAtPos(6, 52);
			break;
		case 121:
			if ((active1 & 0x8000000000L) != 0L)
				return jjStartNfaWithStates_0(6, 103, 63);
			break;
		case 122:
			return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x1000010L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(5, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa7_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(5, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(6, active0, active1, active2);
			return 7;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x2000000000000L, active2, 0L);
		case 97:
			return jjMoveStringLiteralDfa8_0(active0, 0x40000000L, active1, 0x400020000000L, active2, 0L);
		case 99:
			return jjMoveStringLiteralDfa8_0(active0, 0x600900000000L, active1, 0x70002L, active2, 0L);
		case 100:
			if ((active2 & 0x2000L) != 0L)
				return jjStartNfaWithStates_0(7, 141, 63);
			return jjMoveStringLiteralDfa8_0(active0, 0x203004010000000L, active1, 0L, active2, 0L);
		case 101:
			if ((active1 & 0x10L) != 0L)
				return jjStopAtPos(7, 68);
			else if ((active1 & 0x20000000000L) != 0L)
				return jjStartNfaWithStates_0(7, 105, 63);
			else if ((active1 & 0x200000000000000L) != 0L) {
				jjmatchedKind = 121;
				jjmatchedPos = 7;
			}
			return jjMoveStringLiteralDfa8_0(active0, 0x1000000000000000L, active1, 0x8440000000200000L, active2, 0L);
		case 102:
			return jjMoveStringLiteralDfa8_0(active0, 0x800000000000L, active1, 0L, active2, 0L);
		case 104:
			return jjMoveStringLiteralDfa8_0(active0, 0x8000000000000000L, active1, 0L, active2, 0L);
		case 105:
			return jjMoveStringLiteralDfa8_0(active0, 0x4860000000000000L, active1, 0x8008L, active2, 0x800L);
		case 108:
			return jjMoveStringLiteralDfa8_0(active0, 0x8020000000L, active1, 0x4000000000000000L, active2, 0x2L);
		case 109:
			return jjMoveStringLiteralDfa8_0(active0, 0x2000000480000000L, active1, 0L, active2, 0L);
		case 110:
			if ((active1 & 0x20L) != 0L)
				return jjStopAtPos(7, 69);
			return jjMoveStringLiteralDfa8_0(active0, 0x20000000000L, active1, 0x40000400000L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa8_0(active0, 0x400002000000000L, active1, 0x3000001L, active2, 0L);
		case 114:
			return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0xa00000000000L, active2, 0x10L);
		case 115:
			return jjMoveStringLiteralDfa8_0(active0, 0x180000000000L, active1, 0L, active2, 0L);
		case 116:
			if ((active1 & 0x200000000L) != 0L)
				return jjStopAtPos(7, 97);
			return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x3e00L, active2, 0x1L);
		case 117:
			return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x1000100000000000L, active2, 0L);
		case 119:
			return jjMoveStringLiteralDfa8_0(active0, 0x1200000000L, active1, 0L, active2, 0L);
		case 120:
			if ((active1 & 0x80000000000L) != 0L)
				return jjStopAtPos(7, 107);
			break;
		case 121:
			return jjMoveStringLiteralDfa8_0(active0, 0x10000000000L, active1, 0L, active2, 0L);
		case 122:
			return jjMoveStringLiteralDfa8_0(active0, 0x40000000000L, active1, 0L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(6, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa8_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(6, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(7, active0, active1, active2);
			return 8;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0x8400000000003e00L, active2, 0x2L);
		case 97:
			return jjMoveStringLiteralDfa9_0(active0, 0xa000000000000000L, active1, 0x800000070000L, active2, 0x1L);
		case 98:
			return jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0x2000000000000L, active2, 0L);
		case 99:
			return jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0x200000000000L, active2, 0L);
		case 100:
			if ((active1 & 0x200000L) != 0L)
				return jjStartNfaWithStates_0(8, 85, 63);
			return jjMoveStringLiteralDfa9_0(active0, 0x1000000000000L, active1, 0L, active2, 0L);
		case 101:
			if ((active0 & 0x10000000L) != 0L)
				return jjStopAtPos(8, 28);
			else if ((active0 & 0x4000000000L) != 0L)
				return jjStopAtPos(8, 38);
			else if ((active0 & 0x40000000000L) != 0L)
				return jjStopAtPos(8, 42);
			else if ((active0 & 0x2000000000000L) != 0L)
				return jjStopAtPos(8, 49);
			else if ((active0 & 0x200000000000000L) != 0L)
				return jjStopAtPos(8, 57);
			else if ((active1 & 0x40000000000L) != 0L)
				return jjStartNfaWithStates_0(8, 106, 63);
			break;
		case 102:
			return jjMoveStringLiteralDfa9_0(active0, 0x2000000000L, active1, 0L, active2, 0L);
		case 103:
			return jjMoveStringLiteralDfa9_0(active0, 0x800000040000000L, active1, 0x400000L, active2, 0L);
		case 105:
			return jjMoveStringLiteralDfa9_0(active0, 0x400181200000000L, active1, 0x4000000000000000L, active2, 0L);
		case 107:
			return jjMoveStringLiteralDfa9_0(active0, 0x600000000000L, active1, 0L, active2, 0L);
		case 108:
			return jjMoveStringLiteralDfa9_0(active0, 0x10000000000L, active1, 0x40000020008000L, active2, 0L);
		case 109:
			if ((active1 & 0x2000000L) != 0L)
				return jjStopAtPos(8, 89);
			return jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0x400000000000L, active2, 0L);
		case 110:
			if ((active1 & 0x8L) != 0L)
				return jjStopAtPos(8, 67);
			return jjMoveStringLiteralDfa9_0(active0, 0x1000000000000000L, active1, 0x100000000000L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa9_0(active0, 0x8da0000000L, active1, 0x1000002L, active2, 0x800L);
		case 114:
			return jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0x1000000000000001L, active2, 0L);
		case 115:
			return jjMoveStringLiteralDfa9_0(active0, 0x800000000000L, active1, 0L, active2, 0L);
		case 116:
			if ((active0 & 0x20000000000L) != 0L)
				return jjStopAtPos(8, 41);
			else if ((active2 & 0x10L) != 0L)
				return jjStopAtPos(8, 132);
			return jjMoveStringLiteralDfa9_0(active0, 0x60000000000000L, active1, 0L, active2, 0L);
		case 122:
			return jjMoveStringLiteralDfa9_0(active0, 0x4000000000000000L, active1, 0L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(7, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa9_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(7, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(8, active0, active1, active2);
			return 9;
		}
		switch (curChar) {
		case 98:
			return jjMoveStringLiteralDfa10_0(active0, 0x180000000000L, active1, 0L, active2, 0L);
		case 100:
			if ((active1 & 0x40000000000000L) != 0L)
				return jjStopAtPos(9, 118);
			return jjMoveStringLiteralDfa10_0(active0, 0x1680000000L, active1, 0x100000000600L, active2, 0L);
		case 101:
			if ((active0 & 0x40000000L) != 0L)
				return jjStopAtPos(9, 30);
			else if ((active0 & 0x10000000000L) != 0L)
				return jjStopAtPos(9, 40);
			else if ((active0 & 0x4000000000000000L) != 0L)
				return jjStopAtPos(9, 62);
			else if ((active1 & 0x400000L) != 0L)
				return jjStopAtPos(9, 86);
			return jjMoveStringLiteralDfa10_0(active0, 0x800000000000L, active1, 0x8000L, active2, 0L);
		case 102:
			return jjMoveStringLiteralDfa10_0(active0, 0x2000000000L, active1, 0L, active2, 0L);
		case 103:
			return jjMoveStringLiteralDfa10_0(active0, 0x2000600000000000L, active1, 0x800000000000L, active2, 0L);
		case 104:
			return jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0x800L, active2, 0L);
		case 105:
			return jjMoveStringLiteralDfa10_0(active0, 0x1000000000000L, active1, 0x20000001L, active2, 0L);
		case 108:
			return jjMoveStringLiteralDfa10_0(active0, 0x900000000L, active1, 0x8000200000070002L, active2, 0x1L);
		case 109:
			if ((active1 & 0x1000000L) != 0L)
				return jjStopAtPos(9, 88);
			break;
		case 110:
			if ((active2 & 0x800L) != 0L)
				return jjStartNfaWithStates_0(9, 139, 63);
			return jjMoveStringLiteralDfa10_0(active0, 0xc00000000000000L, active1, 0x4000000000000000L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0x2400000000000L, active2, 0L);
		case 112:
			return jjMoveStringLiteralDfa10_0(active0, 0x8000000000000000L, active1, 0L, active2, 0L);
		case 114:
			if ((active0 & 0x20000000L) != 0L)
				return jjStopAtPos(9, 29);
			else if ((active0 & 0x8000000000L) != 0L)
				return jjStopAtPos(9, 39);
			return jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0x1000L, active2, 0L);
		case 115:
			return jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0x400000000000000L, active2, 0x2L);
		case 116:
			if ((active0 & 0x1000000000000000L) != 0L)
				return jjStartNfaWithStates_0(9, 60, 63);
			break;
		case 118:
			return jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0x1000000000002000L, active2, 0L);
		case 121:
			if ((active0 & 0x40000000000000L) != 0L) {
				jjmatchedKind = 54;
				jjmatchedPos = 9;
			}
			return jjMoveStringLiteralDfa10_0(active0, 0x20000000000000L, active1, 0L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(8, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa10_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(8, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(9, active0, active1, active2);
			return 10;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa11_0(active0, 0x20000000000000L, active1, 0L, active2, 0x1L);
		case 97:
			return jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0x1000L, active2, 0L);
		case 99:
			if ((active1 & 0x20000000L) != 0L)
				return jjStopAtPos(10, 93);
			return jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0x400000000000000L, active2, 0L);
		case 100:
			if ((active1 & 0x8000L) != 0L)
				return jjStopAtPos(10, 79);
			break;
		case 101:
			if ((active0 & 0x80000000L) != 0L)
				return jjStopAtPos(10, 31);
			else if ((active0 & 0x400000000L) != 0L)
				return jjStopAtPos(10, 34);
			else if ((active0 & 0x2000000000000000L) != 0L)
				return jjStopAtPos(10, 61);
			else if ((active0 & 0x8000000000000000L) != 0L)
				return jjStopAtPos(10, 63);
			else if ((active1 & 0x200000000000L) != 0L)
				return jjStopAtPos(10, 109);
			else if ((active1 & 0x1000000000000000L) != 0L)
				return jjStopAtPos(10, 124);
			else if ((active1 & 0x4000000000000000L) != 0L)
				return jjStopAtPos(10, 126);
			return jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0x100000072001L, active2, 0L);
		case 105:
			return jjMoveStringLiteralDfa11_0(active0, 0x180000000000L, active1, 0x8000000000000600L, active2, 0L);
		case 109:
			return jjMoveStringLiteralDfa11_0(active0, 0x800000000000000L, active1, 0L, active2, 0L);
		case 110:
			return jjMoveStringLiteralDfa11_0(active0, 0x1000000000000L, active1, 0x400000000000L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa11_0(active0, 0x900000000L, active1, 0x802L, active2, 0L);
		case 113:
			return jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0x2L);
		case 114:
			return jjMoveStringLiteralDfa11_0(active0, 0x600000000000L, active1, 0x800000000000L, active2, 0L);
		case 115:
			return jjMoveStringLiteralDfa11_0(active0, 0x2000000000L, active1, 0L, active2, 0L);
		case 116:
			if ((active0 & 0x800000000000L) != 0L)
				return jjStopAtPos(10, 47);
			return jjMoveStringLiteralDfa11_0(active0, 0x400001200000000L, active1, 0L, active2, 0L);
		case 120:
			if ((active1 & 0x2000000000000L) != 0L)
				return jjStopAtPos(10, 113);
			break;
		default:
			break;
		}
		return jjStartNfa_0(9, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa11_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(9, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(10, active0, active1, active2);
			return 11;
		}
		switch (curChar) {
		case 97:
			return jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0x400800000000600L, active2, 0L);
		case 100:
			if ((active1 & 0x10000L) != 0L) {
				jjmatchedKind = 80;
				jjmatchedPos = 11;
			} else if ((active1 & 0x400000000000L) != 0L)
				return jjStopAtPos(11, 110);
			return jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0x100000061000L, active2, 0L);
		case 101:
			return jjMoveStringLiteralDfa12_0(active0, 0x800002000000000L, active1, 0L, active2, 0L);
		case 103:
			if ((active0 & 0x1000000000000L) != 0L)
				return jjStopAtPos(11, 48);
			break;
		case 104:
			if ((active0 & 0x200000000L) != 0L)
				return jjStopAtPos(11, 33);
			else if ((active0 & 0x1000000000L) != 0L)
				return jjStopAtPos(11, 36);
			break;
		case 108:
			return jjMoveStringLiteralDfa12_0(active0, 0x180000000000L, active1, 0L, active2, 0L);
		case 109:
			return jjMoveStringLiteralDfa12_0(active0, 0x20000000000000L, active1, 0L, active2, 0L);
		case 110:
			return jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0x8000000000000001L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa12_0(active0, 0x600000000000L, active1, 0L, active2, 0L);
		case 114:
			if ((active0 & 0x100000000L) != 0L)
				return jjStopAtPos(11, 32);
			else if ((active0 & 0x800000000L) != 0L)
				return jjStopAtPos(11, 35);
			else if ((active1 & 0x2L) != 0L)
				return jjStopAtPos(11, 65);
			return jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0x2800L, active2, 0L);
		case 115:
			if ((active0 & 0x400000000000000L) != 0L)
				return jjStopAtPos(11, 58);
			return jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0x1L);
		case 117:
			return jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0x2L);
		default:
			break;
		}
		return jjStartNfa_0(10, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa12_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(10, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(11, active0, active1, active2);
			return 12;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0x100000060000L, active2, 0L);
		case 97:
			return jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0x2L);
		case 101:
			if ((active1 & 0x8000000000000000L) != 0L)
				return jjStopAtPos(12, 127);
			break;
		case 103:
			return jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0x600L, active2, 0L);
		case 105:
			return jjMoveStringLiteralDfa13_0(active0, 0x180000000000L, active1, 0x1800L, active2, 0L);
		case 108:
			return jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0x400000000000000L, active2, 0L);
		case 110:
			return jjMoveStringLiteralDfa13_0(active0, 0x800000000000000L, active1, 0L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa13_0(active0, 0x20000000000000L, active1, 0L, active2, 0L);
		case 112:
			return jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0x800000000000L, active2, 0L);
		case 113:
			return jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0x1L);
		case 116:
			if ((active0 & 0x2000000000L) != 0L)
				return jjStopAtPos(12, 37);
			return jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0x2001L, active2, 0L);
		case 117:
			return jjMoveStringLiteralDfa13_0(active0, 0x600000000000L, active1, 0L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(11, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa13_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(11, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(12, active0, active1, active2);
			return 13;
		}
		switch (curChar) {
		case 97:
			return jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0x1001L, active2, 0L);
		case 98:
			return jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0x100000000000L, active2, 0L);
		case 100:
			return jjMoveStringLiteralDfa14_0(active0, 0x20000000000000L, active1, 0L, active2, 0L);
		case 101:
			return jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0x400000000000000L, active2, 0L);
		case 104:
			if ((active1 & 0x800000000000L) != 0L)
				return jjStopAtPos(13, 111);
			break;
		case 105:
			return jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0x2000L, active2, 0L);
		case 110:
			return jjMoveStringLiteralDfa14_0(active0, 0x600000000000L, active1, 0L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0x600L, active2, 0L);
		case 114:
			return jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0x60000L, active2, 0x2L);
		case 116:
			if ((active0 & 0x800000000000000L) != 0L)
				return jjStopAtPos(13, 59);
			return jjMoveStringLiteralDfa14_0(active0, 0x180000000000L, active1, 0L, active2, 0L);
		case 117:
			return jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0L, active2, 0x1L);
		case 122:
			return jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0x800L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(12, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa14_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(12, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(13, active0, active1, active2);
			return 14;
		}
		switch (curChar) {
		case 97:
			return jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0x60000L, active2, 0x1L);
		case 99:
			return jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0x2000L, active2, 0L);
		case 100:
			if ((active1 & 0x400000000000000L) != 0L)
				return jjStopAtPos(14, 122);
			return jjMoveStringLiteralDfa15_0(active0, 0x600000000000L, active1, 0L, active2, 0L);
		case 101:
			if ((active0 & 0x20000000000000L) != 0L)
				return jjStopAtPos(14, 53);
			return jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0L, active2, 0x2L);
		case 108:
			if ((active1 & 0x1000L) != 0L)
				return jjStopAtPos(14, 76);
			break;
		case 110:
			return jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0x600L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0x100000000800L, active2, 0L);
		case 116:
			return jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0x1L, active2, 0L);
		case 121:
			if ((active0 & 0x100000000000L) != 0L) {
				jjmatchedKind = 44;
				jjmatchedPos = 14;
			}
			return jjMoveStringLiteralDfa15_0(active0, 0x80000000000L, active1, 0L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(13, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa15_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(13, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(14, active0, active1, active2);
			return 15;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa16_0(active0, 0x680000000000L, active1, 0L, active2, 0x2L);
		case 97:
			return jjMoveStringLiteralDfa16_0(active0, 0L, active1, 0x2600L, active2, 0L);
		case 105:
			return jjMoveStringLiteralDfa16_0(active0, 0L, active1, 0x1L, active2, 0L);
		case 110:
			return jjMoveStringLiteralDfa16_0(active0, 0L, active1, 0x800L, active2, 0L);
		case 114:
			return jjMoveStringLiteralDfa16_0(active0, 0L, active1, 0L, active2, 0x1L);
		case 116:
			return jjMoveStringLiteralDfa16_0(active0, 0L, active1, 0x60000L, active2, 0L);
		case 120:
			if ((active1 & 0x100000000000L) != 0L)
				return jjStopAtPos(15, 108);
			break;
		default:
			break;
		}
		return jjStartNfa_0(14, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa16_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(14, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(15, active0, active1, active2);
			return 16;
		}
		switch (curChar) {
		case 99:
			return jjMoveStringLiteralDfa17_0(active0, 0x400000000000L, active1, 0L, active2, 0L);
		case 101:
			return jjMoveStringLiteralDfa17_0(active0, 0L, active1, 0L, active2, 0x1L);
		case 105:
			return jjMoveStringLiteralDfa17_0(active0, 0L, active1, 0x60000L, active2, 0L);
		case 108:
			if ((active1 & 0x2000L) != 0L)
				return jjStopAtPos(16, 77);
			return jjMoveStringLiteralDfa17_0(active0, 0L, active1, 0x600L, active2, 0x2L);
		case 109:
			return jjMoveStringLiteralDfa17_0(active0, 0x280000000000L, active1, 0L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa17_0(active0, 0L, active1, 0x1L, active2, 0L);
		case 116:
			return jjMoveStringLiteralDfa17_0(active0, 0L, active1, 0x800L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(15, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa17_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(15, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(16, active0, active1, active2);
			return 17;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa18_0(active0, 0L, active1, 0L, active2, 0x1L);
		case 49:
			if ((active1 & 0x200L) != 0L)
				return jjStopAtPos(17, 73);
			break;
		case 50:
			if ((active1 & 0x400L) != 0L)
				return jjStopAtPos(17, 74);
			break;
		case 97:
			return jjMoveStringLiteralDfa18_0(active0, 0L, active1, 0x800L, active2, 0L);
		case 105:
			return jjMoveStringLiteralDfa18_0(active0, 0L, active1, 0L, active2, 0x2L);
		case 110:
			if ((active1 & 0x1L) != 0L)
				return jjStopAtPos(17, 64);
			break;
		case 111:
			return jjMoveStringLiteralDfa18_0(active0, 0x680000000000L, active1, 0x60000L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(16, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa18_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(16, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(17, active0, active1, active2);
			return 18;
		}
		switch (curChar) {
		case 45:
			return jjMoveStringLiteralDfa19_0(active0, 0L, active1, 0x60000L, active2, 0L);
		case 100:
			return jjMoveStringLiteralDfa19_0(active0, 0x280000000000L, active1, 0L, active2, 0L);
		case 108:
			if ((active1 & 0x800L) != 0L)
				return jjStopAtPos(18, 75);
			return jjMoveStringLiteralDfa19_0(active0, 0x400000000000L, active1, 0L, active2, 0x1L);
		case 110:
			return jjMoveStringLiteralDfa19_0(active0, 0L, active1, 0L, active2, 0x2L);
		default:
			break;
		}
		return jjStartNfa_0(17, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa19_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(17, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(18, active0, active1, active2);
			return 19;
		}
		switch (curChar) {
		case 101:
			if ((active0 & 0x80000000000L) != 0L)
				return jjStopAtPos(19, 43);
			else if ((active0 & 0x200000000000L) != 0L)
				return jjStopAtPos(19, 45);
			else if ((active2 & 0x2L) != 0L)
				return jjStopAtPos(19, 129);
			break;
		case 105:
			return jjMoveStringLiteralDfa20_0(active0, 0L, active1, 0L, active2, 0x1L);
		case 109:
			return jjMoveStringLiteralDfa20_0(active0, 0L, active1, 0x60000L, active2, 0L);
		case 111:
			return jjMoveStringLiteralDfa20_0(active0, 0x400000000000L, active1, 0L, active2, 0L);
		default:
			break;
		}
		return jjStartNfa_0(18, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa20_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(18, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(19, active0, active1, active2);
			return 20;
		}
		switch (curChar) {
		case 97:
			return jjMoveStringLiteralDfa21_0(active0, 0L, active1, 0x20000L, active2, 0L);
		case 105:
			return jjMoveStringLiteralDfa21_0(active0, 0L, active1, 0x40000L, active2, 0L);
		case 110:
			return jjMoveStringLiteralDfa21_0(active0, 0L, active1, 0L, active2, 0x1L);
		case 114:
			if ((active0 & 0x400000000000L) != 0L)
				return jjStopAtPos(20, 46);
			break;
		default:
			break;
		}
		return jjStartNfa_0(19, active0, active1, active2);
	}

	private int jjMoveStringLiteralDfa21_0(long old0, long active0, long old1, long active1, long old2, long active2) {
		if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L)
			return jjStartNfa_0(19, old0, old1, old2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(20, 0L, active1, active2);
			return 21;
		}
		switch (curChar) {
		case 101:
			if ((active2 & 0x1L) != 0L)
				return jjStopAtPos(21, 128);
			break;
		case 110:
			if ((active1 & 0x40000L) != 0L)
				return jjStopAtPos(21, 82);
			break;
		case 120:
			if ((active1 & 0x20000L) != 0L)
				return jjStopAtPos(21, 81);
			break;
		default:
			break;
		}
		return jjStartNfa_0(20, 0L, active1, active2);
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
		jjnewStateCnt = 72;
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
							if (kind > 21)
								kind = 21;
							jjCheckNAddStates(0, 4);
						} else if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(48);
						else if (curChar == 47)
							jjAddStates(5, 6);
						else if (curChar == 39)
							jjCheckNAddTwoStates(60, 61);
						else if (curChar == 34)
							jjCheckNAddTwoStates(57, 58);
						else if (curChar == 35)
							jjstateSet[jjnewStateCnt++] = 1;
						break;
					case 1:
						if ((0x3ff000000000000L & l) != 0L)
							jjAddStates(7, 13);
						break;
					case 2:
					case 5:
					case 9:
					case 15:
					case 22:
					case 32:
					case 46:
						if ((0x3ff000000000000L & l) != 0L)
							jjCheckNAdd(3);
						break;
					case 3:
						if ((0x3ff000000000000L & l) != 0L && kind > 20)
							kind = 20;
						break;
					case 4:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 5;
						break;
					case 6:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 7;
						break;
					case 7:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 8;
						break;
					case 8:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 9;
						break;
					case 10:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 11;
						break;
					case 11:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 12;
						break;
					case 12:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 13;
						break;
					case 13:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 14;
						break;
					case 14:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 15;
						break;
					case 16:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 17;
						break;
					case 17:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 18;
						break;
					case 18:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 19;
						break;
					case 19:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 20;
						break;
					case 20:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 21;
						break;
					case 21:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 22;
						break;
					case 23:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 24;
						break;
					case 24:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 25;
						break;
					case 25:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 26;
						break;
					case 26:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 27;
						break;
					case 27:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 28;
						break;
					case 28:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 29;
						break;
					case 29:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 30;
						break;
					case 30:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 31;
						break;
					case 31:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 32;
						break;
					case 33:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 34;
						break;
					case 34:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 35;
						break;
					case 35:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 36;
						break;
					case 36:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 37;
						break;
					case 37:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 38;
						break;
					case 38:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 39;
						break;
					case 39:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 40;
						break;
					case 40:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 41;
						break;
					case 41:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 42;
						break;
					case 42:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 43;
						break;
					case 43:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 44;
						break;
					case 44:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 45;
						break;
					case 45:
						if ((0x3ff000000000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 46;
						break;
					case 47:
						if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(48);
						break;
					case 48:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAddStates(0, 4);
						break;
					case 49:
						if (curChar == 46)
							jjCheckNAdd(50);
						break;
					case 50:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAddStates(14, 17);
						break;
					case 55:
						if (curChar == 37 && kind > 21)
							kind = 21;
						break;
					case 56:
						if (curChar == 34)
							jjCheckNAddTwoStates(57, 58);
						break;
					case 57:
						if ((0xfffffffbffffffffL & l) != 0L)
							jjCheckNAddTwoStates(57, 58);
						break;
					case 58:
						if (curChar == 34 && kind > 22)
							kind = 22;
						break;
					case 59:
						if (curChar == 39)
							jjCheckNAddTwoStates(60, 61);
						break;
					case 60:
						if ((0xffffff7fffffffffL & l) != 0L)
							jjCheckNAddTwoStates(60, 61);
						break;
					case 61:
						if (curChar == 39 && kind > 22)
							kind = 22;
						break;
					case 63:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 142)
							kind = 142;
						jjstateSet[jjnewStateCnt++] = 63;
						break;
					case 64:
						if (curChar == 47)
							jjAddStates(5, 6);
						break;
					case 65:
						if (curChar == 42)
							jjCheckNAddTwoStates(66, 68);
						break;
					case 66:
						if ((0xfffffbffffffffffL & l) != 0L)
							jjCheckNAddTwoStates(66, 68);
						break;
					case 67:
						if (curChar == 47 && kind > 143)
							kind = 143;
						break;
					case 68:
						if (curChar == 42)
							jjstateSet[jjnewStateCnt++] = 67;
						break;
					case 69:
						if (curChar == 47)
							jjCheckNAddTwoStates(70, 71);
						break;
					case 70:
						if ((0xffffffffffffdbffL & l) != 0L)
							jjCheckNAddTwoStates(70, 71);
						break;
					case 71:
						if ((0x2400L & l) != 0L && kind > 143)
							kind = 143;
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
						if ((0x7fffffe07fffffeL & l) == 0L)
							break;
						if (kind > 142)
							kind = 142;
						jjCheckNAdd(63);
						break;
					case 1:
						if ((0x7e0000007eL & l) != 0L)
							jjAddStates(7, 13);
						break;
					case 2:
					case 5:
					case 9:
					case 15:
					case 22:
					case 32:
					case 46:
						if ((0x7e0000007eL & l) != 0L)
							jjCheckNAdd(3);
						break;
					case 3:
						if ((0x7e0000007eL & l) != 0L && kind > 20)
							kind = 20;
						break;
					case 4:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 5;
						break;
					case 6:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 7;
						break;
					case 7:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 8;
						break;
					case 8:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 9;
						break;
					case 10:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 11;
						break;
					case 11:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 12;
						break;
					case 12:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 13;
						break;
					case 13:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 14;
						break;
					case 14:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 15;
						break;
					case 16:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 17;
						break;
					case 17:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 18;
						break;
					case 18:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 19;
						break;
					case 19:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 20;
						break;
					case 20:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 21;
						break;
					case 21:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 22;
						break;
					case 23:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 24;
						break;
					case 24:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 25;
						break;
					case 25:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 26;
						break;
					case 26:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 27;
						break;
					case 27:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 28;
						break;
					case 28:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 29;
						break;
					case 29:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 30;
						break;
					case 30:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 31;
						break;
					case 31:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 32;
						break;
					case 33:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 34;
						break;
					case 34:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 35;
						break;
					case 35:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 36;
						break;
					case 36:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 37;
						break;
					case 37:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 38;
						break;
					case 38:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 39;
						break;
					case 39:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 40;
						break;
					case 40:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 41;
						break;
					case 41:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 42;
						break;
					case 42:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 43;
						break;
					case 43:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 44;
						break;
					case 44:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 45;
						break;
					case 45:
						if ((0x7e0000007eL & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 46;
						break;
					case 51:
						if (curChar == 117 && kind > 21)
							kind = 21;
						break;
					case 52:
						if (curChar == 103)
							jjstateSet[jjnewStateCnt++] = 51;
						break;
					case 53:
						if (curChar == 120 && kind > 21)
							kind = 21;
						break;
					case 54:
						if (curChar == 112)
							jjstateSet[jjnewStateCnt++] = 53;
						break;
					case 57:
						jjAddStates(18, 19);
						break;
					case 60:
						jjAddStates(20, 21);
						break;
					case 63:
						if ((0x7fffffe87fffffeL & l) == 0L)
							break;
						if (kind > 142)
							kind = 142;
						jjCheckNAdd(63);
						break;
					case 66:
						jjAddStates(22, 23);
						break;
					case 70:
						jjAddStates(24, 25);
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
					case 57:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjAddStates(18, 19);
						break;
					case 60:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjAddStates(20, 21);
						break;
					case 66:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjAddStates(22, 23);
						break;
					case 70:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjAddStates(24, 25);
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
			if ((i = jjnewStateCnt) == (startsAt = 72 - (jjnewStateCnt = startsAt)))
				return curPos;
			try {
				curChar = input_stream.readChar();
			} catch (java.io.IOException e) {
				return curPos;
			}
		}
	}

	static final int[] jjnextStates = { 48, 49, 52, 54, 55, 65, 69, 2, 4, 6, 10, 16, 23, 33, 50, 52, 54, 55, 57, 58, 60,
			61, 66, 68, 70, 71, };

	/** Token literal values. */
	public static final String[] jjstrLiteralImages = { "", null, null, null, null, null, null, null, null, "\56",
			"\173", "\175", "\50", "\51", "\43", "\72", "\73", "\54", "\162\147\142\141", "\162\147\142", null, null,
			null, "\165\162\154", "\147\162\141\160\150", "\145\144\147\145", "\156\157\144\145",
			"\163\160\162\151\164\145", "\146\151\154\154\55\155\157\144\145",
			"\146\151\154\154\55\143\157\154\157\162", "\146\151\154\154\55\151\155\141\147\145",
			"\163\164\162\157\153\145\55\155\157\144\145", "\163\164\162\157\153\145\55\143\157\154\157\162",
			"\163\164\162\157\153\145\55\167\151\144\164\150", "\163\150\141\144\157\167\55\155\157\144\145",
			"\163\150\141\144\157\167\55\143\157\154\157\162", "\163\150\141\144\157\167\55\167\151\144\164\150",
			"\163\150\141\144\157\167\55\157\146\146\163\145\164", "\164\145\170\164\55\155\157\144\145",
			"\164\145\170\164\55\143\157\154\157\162", "\164\145\170\164\55\163\164\171\154\145",
			"\164\145\170\164\55\146\157\156\164", "\164\145\170\164\55\163\151\172\145",
			"\164\145\170\164\55\166\151\163\151\142\151\154\151\164\171\55\155\157\144\145",
			"\164\145\170\164\55\166\151\163\151\142\151\154\151\164\171",
			"\164\145\170\164\55\142\141\143\153\147\162\157\165\156\144\55\155\157\144\145",
			"\164\145\170\164\55\142\141\143\153\147\162\157\165\156\144\55\143\157\154\157\162",
			"\164\145\170\164\55\157\146\146\163\145\164", "\164\145\170\164\55\160\141\144\144\151\156\147",
			"\151\143\157\156\55\155\157\144\145", "\151\143\157\156", "\160\141\144\144\151\156\147",
			"\172\55\151\156\144\145\170", "\166\151\163\151\142\151\154\151\164\171\55\155\157\144\145",
			"\166\151\163\151\142\151\154\151\164\171", "\163\150\141\160\145", "\163\151\172\145",
			"\163\151\172\145\55\155\157\144\145", "\163\150\141\160\145\55\160\157\151\156\164\163",
			"\164\145\170\164\55\141\154\151\147\156\155\145\156\164", "\152\143\157\155\160\157\156\145\156\164",
			"\141\162\162\157\167\55\151\155\141\147\145", "\141\162\162\157\167\55\163\151\172\145",
			"\141\162\162\157\167\55\163\150\141\160\145",
			"\163\160\162\151\164\145\55\157\162\151\145\156\164\141\164\151\157\156",
			"\143\141\156\166\141\163\55\143\157\154\157\162", "\160\154\141\151\156",
			"\144\171\156\55\160\154\141\151\156", "\144\171\156\55\163\151\172\145", "\144\171\156\55\151\143\157\156",
			"\144\141\163\150\145\163", "\144\157\164\163", "\144\157\165\142\154\145",
			"\147\162\141\144\151\145\156\164\55\144\151\141\147\157\156\141\154\61",
			"\147\162\141\144\151\145\156\164\55\144\151\141\147\157\156\141\154\62",
			"\147\162\141\144\151\145\156\164\55\150\157\162\151\172\157\156\164\141\154",
			"\147\162\141\144\151\145\156\164\55\162\141\144\151\141\154",
			"\147\162\141\144\151\145\156\164\55\166\145\162\164\151\143\141\154", "\150\151\144\144\145\156",
			"\151\155\141\147\145\55\164\151\154\145\144", "\151\155\141\147\145\55\163\143\141\154\145\144",
			"\151\155\141\147\145\55\163\143\141\154\145\144\55\162\141\164\151\157\55\155\141\170",
			"\151\155\141\147\145\55\163\143\141\154\145\144\55\162\141\164\151\157\55\155\151\156", "\156\157\156\145",
			"\156\157\162\155\141\154", "\164\162\165\156\143\141\164\145\144",
			"\172\157\157\155\55\162\141\156\147\145", "\141\164\55\172\157\157\155",
			"\165\156\144\145\162\55\172\157\157\155", "\157\166\145\162\55\172\157\157\155", "\172\157\157\155\163",
			"\146\151\164", "\142\157\154\144", "\142\157\154\144\55\151\164\141\154\151\143",
			"\151\164\141\154\151\143", "\141\154\157\156\147", "\141\164\55\154\145\146\164",
			"\141\164\55\162\151\147\150\164", "\143\145\156\164\145\162", "\154\145\146\164", "\162\151\147\150\164",
			"\165\156\144\145\162", "\141\142\157\166\145", "\152\165\163\164\151\146\171", "\143\151\162\143\154\145",
			"\164\162\151\141\156\147\154\145", "\146\162\145\145\160\154\141\156\145",
			"\164\145\170\164\55\142\157\170", "\164\145\170\164\55\162\157\165\156\144\145\144\55\142\157\170",
			"\164\145\170\164\55\143\151\162\143\154\145", "\164\145\170\164\55\144\151\141\155\157\156\144",
			"\164\145\170\164\55\160\141\162\141\147\162\141\160\150", "\142\157\170",
			"\162\157\165\156\144\145\144\55\142\157\170", "\143\162\157\163\163", "\144\151\141\155\157\156\144",
			"\160\157\154\171\147\157\156", "\142\165\164\164\157\156", "\164\145\170\164\55\146\151\145\154\144",
			"\160\141\156\145\154", "\154\151\156\145", "\160\157\154\171\154\151\156\145",
			"\160\157\154\171\154\151\156\145\55\163\143\141\154\145\144", "\141\156\147\154\145",
			"\143\165\142\151\143\55\143\165\162\166\145", "\142\154\157\142",
			"\163\161\165\141\162\145\55\154\151\156\145", "\114\55\163\161\165\141\162\145\55\154\151\156\145",
			"\150\157\162\151\172\157\156\164\141\154\55\163\161\165\141\162\145\55\154\151\156\145",
			"\166\145\162\164\151\143\141\154\55\163\161\165\141\162\145\55\154\151\156\145", "\141\162\162\157\167",
			"\146\154\157\167", "\160\151\145\55\143\150\141\162\164", "\151\155\141\147\145",
			"\151\155\141\147\145\163", "\146\162\157\155", "\156\157\144\145\60", "\164\157", "\156\157\144\145\61",
			"\160\162\157\152\145\143\164\151\157\156", "\143\154\151\143\153\145\144",
			"\163\145\154\145\143\164\145\144", null, null, };

	/** Lexer state names. */
	public static final String[] lexStateNames = { "DEFAULT", };
	static final long[] jjtoToken = { 0xfffffffffffffe01L, 0xffffffffffffffffL, 0xffffL, };
	static final long[] jjtoSkip = { 0x1eL, 0x0L, 0x0L, };
	protected SimpleCharStream input_stream;
	private final int[] jjrounds = new int[72];
	private final int[] jjstateSet = new int[144];
	protected char curChar;

	/** Constructor. */
	public StyleSheetParserTokenManager(SimpleCharStream stream) {
		if (SimpleCharStream.staticFlag)
			throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
		input_stream = stream;
	}

	/** Constructor. */
	public StyleSheetParserTokenManager(SimpleCharStream stream, int lexState) {
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
		for (i = 72; i-- > 0;)
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
