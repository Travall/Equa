package com.travall.game.tools;

/**
 * K.jpg's original OpenSimplex Noise
 * DigitalShadow's optimized implementation, https://gist.github.com/digitalshadow/134a3a02b67cecd72181/
 * with updated gradient sets (Dec 2019, Feb 2020)
 */
public class OpenSimplex
{
	private static final double STRETCH_2D = -0.211324865405187;    // (1/Math.sqrt(2+1)-1)/2;
	private static final double STRETCH_3D = -1.0 / 6.0;            // (1/Math.sqrt(3+1)-1)/3;
	private static final double SQUISH_2D = 0.366025403784439;      // (Math.sqrt(2+1)-1)/2;
	private static final double SQUISH_3D = 1.0 / 3.0;              // (Math.sqrt(3+1)-1)/3;
	private static final int PSIZE = 2048;
	private static final int PMASK = 2047;

	private short[] perm;
	private Grad2[] permGrad2;
	private Grad3[] permGrad3;

	public OpenSimplex(long seed) {
		perm = new short[PSIZE];
		permGrad2 = new Grad2[PSIZE];
		permGrad3 = new Grad3[PSIZE];
		short[] source = new short[PSIZE];
		for (int i = 0; i < PSIZE; i++) {
			source[i] = (short)i;
		}
		for (int i = PSIZE - 1 ; i >= 0; i--) {
			seed = seed * 6364136223846793005L + 1442695040888963407L;
			int r = (int)((seed + 31) % (i + 1));
			if (r < 0) {
				r += (i + 1);
			}
			perm[i] = source[r];
			permGrad2[i] = GRADIENTS_2D[perm[i]];
			permGrad3[i] = GRADIENTS_3D[perm[i]];
			source[r] = source[i];
		}
	}

	public double eval(double x, double y) {
		double stretchOffset = (x + y) * STRETCH_2D;
		double xs = x + stretchOffset;
		double ys = y + stretchOffset;

		int xsb = fastFloor(xs);
		int ysb = fastFloor(ys);

		double xins = xs - xsb;
		double yins = ys - ysb;

		double inSum = xins + yins;

		double squishOffsetIns = inSum * SQUISH_2D;
		double dx0 = xins + squishOffsetIns;
		double dy0 = yins + squishOffsetIns;

		int hash =
		   (int)(xins - yins + 1) |
		   (int)(inSum) << 1 |
		   (int)(inSum + yins) << 2 |
		   (int)(inSum + xins) << 4;

		Contribution2 c = LOOKUP_2D[hash];

		double value = 0.0;
		while (c != null) {
			double dx = dx0 + c.dx;
			double dy = dy0 + c.dy;
			double attn = 2 - dx * dx - dy * dy;
			if (attn > 0) {
				int px = xsb + c.xsb;
				int py = ysb + c.ysb;

				Grad2 grad = permGrad2[perm[px & PMASK] ^ (py & PMASK)];
				double valuePart = grad.dx * dx + grad.dy * dy;

				attn *= attn;
				value += attn * attn * valuePart;
			}
			c = c.next;
		}
		return value;
	}
	
	public double eval(double x, double y, double z) {
		double stretchOffset = (x + y + z) * STRETCH_3D;
		double xs = x + stretchOffset;
		double ys = y + stretchOffset;
		double zs = z + stretchOffset;
		
		return eval3_Base(xs, ys, zs);
	}
	
	private double eval3_Base(double xs, double ys, double zs) {
		int xsb = fastFloor(xs);
		int ysb = fastFloor(ys);
		int zsb = fastFloor(zs);

		double xins = xs - xsb;
		double yins = ys - ysb;
		double zins = zs - zsb;

		double inSum = xins + yins + zins;

		double squishOffsetIns = inSum * SQUISH_3D;
		double dx0 = xins + squishOffsetIns;
		double dy0 = yins + squishOffsetIns;
		double dz0 = zins + squishOffsetIns;

		int hash =
		   (int)(yins - zins + 1) |
		   (int)(xins - yins + 1) << 1 |
		   (int)(xins - zins + 1) << 2 |
		   (int)inSum << 3 |
		   (int)(inSum + zins) << 5 |
		   (int)(inSum + yins) << 7 |
		   (int)(inSum + xins) << 9;

		Contribution3 c = LOOKUP_3D[hash];

		double value = 0.0;
		while (c != null) {
			double dx = dx0 + c.dx;
			double dy = dy0 + c.dy;
			double dz = dz0 + c.dz;
			double attn = 2 - dx * dx - dy * dy - dz * dz;
			if (attn > 0) {
				int px = xsb + c.xsb;
				int py = ysb + c.ysb;
				int pz = zsb + c.zsb;

				Grad3 grad = permGrad3[perm[perm[px & PMASK] ^ (py & PMASK)] ^ (pz & PMASK)];
				double valuePart = grad.dx * dx + grad.dy * dy + grad.dz * dz;

				attn *= attn;
				value += attn * attn * valuePart;
			}

			c = c.next;
		}
		return value;
	}
	
	private static class Contribution2 {
		double dx, dy;
		int xsb, ysb;
		Contribution2 next;

		public Contribution2(double multiplier, int xsb, int ysb) {
			dx = -xsb - multiplier * SQUISH_2D;
			dy = -ysb - multiplier * SQUISH_2D;
			this.xsb = xsb;
			this.ysb = ysb;
		}
	}

	private static class Contribution3 {
		double dx, dy, dz;
		int xsb, ysb, zsb;
		Contribution3 next;

		public Contribution3(double multiplier, int xsb, int ysb, int zsb) {
			dx = -xsb - multiplier * SQUISH_3D;
			dy = -ysb - multiplier * SQUISH_3D;
			dz = -zsb - multiplier * SQUISH_3D;
			this.xsb = xsb;
			this.ysb = ysb;
			this.zsb = zsb;
		}
	}

	public static class Grad2 {
		double dx, dy;
		public Grad2(double dx, double dy) {
			this.dx = dx;
			this.dy = dy;
		}
	}

	public static class Grad3 {
		double dx, dy, dz;
		public Grad3(double dx, double dy, double dz) {
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;
		}
	}

	
	
	/*
	 * Utility
	 */

	private static int fastFloor(double x) {
		int xi = (int)x;
		return x < xi ? xi - 1 : xi;
	}
	
	/*
	 * Gradients and lookup tables
	 */

	private static final Grad2[] GRADIENTS_2D;
	private static final Grad3[] GRADIENTS_3D;
	private static Contribution2[] LOOKUP_2D;
	private static Contribution3[] LOOKUP_3D;
	private static final double N2 = 7.69084574549313;
	private static final double N3 = 26.92263139946168;

	static {
		GRADIENTS_2D = new Grad2[PSIZE];
		Grad2[] grad2 = {
			new Grad2( 0.130526192220052,  0.99144486137381),
			new Grad2( 0.38268343236509,   0.923879532511287),
			new Grad2( 0.608761429008721,  0.793353340291235),
			new Grad2( 0.793353340291235,  0.608761429008721),
			new Grad2( 0.923879532511287,  0.38268343236509),
			new Grad2( 0.99144486137381,   0.130526192220051),
			new Grad2( 0.99144486137381,  -0.130526192220051),
			new Grad2( 0.923879532511287, -0.38268343236509),
			new Grad2( 0.793353340291235, -0.60876142900872),
			new Grad2( 0.608761429008721, -0.793353340291235),
			new Grad2( 0.38268343236509,  -0.923879532511287),
			new Grad2( 0.130526192220052, -0.99144486137381),
			new Grad2(-0.130526192220052, -0.99144486137381),
			new Grad2(-0.38268343236509,  -0.923879532511287),
			new Grad2(-0.608761429008721, -0.793353340291235),
			new Grad2(-0.793353340291235, -0.608761429008721),
			new Grad2(-0.923879532511287, -0.38268343236509),
			new Grad2(-0.99144486137381,  -0.130526192220052),
			new Grad2(-0.99144486137381,   0.130526192220051),
			new Grad2(-0.923879532511287,  0.38268343236509),
			new Grad2(-0.793353340291235,  0.608761429008721),
			new Grad2(-0.608761429008721,  0.793353340291235),
			new Grad2(-0.38268343236509,   0.923879532511287),
			new Grad2(-0.130526192220052,  0.99144486137381)
		};
		for (int i = 0; i < grad2.length; i++) {
			grad2[i].dx /= N2; grad2[i].dy /= N2;
		}
		for (int i = 0; i < PSIZE; i++) {
			GRADIENTS_2D[i] = grad2[i % grad2.length];
		}

		GRADIENTS_3D = new Grad3[PSIZE];
		Grad3[] grad3 = {
			new Grad3(-1.4082482904633333,    -1.4082482904633333,    -2.6329931618533333),
			new Grad3(-0.07491495712999985,   -0.07491495712999985,   -3.29965982852),
			new Grad3( 0.24732126143473554,   -1.6667938651159684,    -2.838945207362466),
			new Grad3(-1.6667938651159684,     0.24732126143473554,   -2.838945207362466),
			new Grad3(-1.4082482904633333,    -2.6329931618533333,    -1.4082482904633333),
			new Grad3(-0.07491495712999985,   -3.29965982852,         -0.07491495712999985),
			new Grad3(-1.6667938651159684,    -2.838945207362466,      0.24732126143473554),
			new Grad3( 0.24732126143473554,   -2.838945207362466,     -1.6667938651159684),
			new Grad3( 1.5580782047233335,     0.33333333333333337,   -2.8914115380566665),
			new Grad3( 2.8914115380566665,    -0.33333333333333337,   -1.5580782047233335),
			new Grad3( 1.8101897177633992,    -1.2760767510338025,    -2.4482280932803),
			new Grad3( 2.4482280932803,        1.2760767510338025,    -1.8101897177633992),
			new Grad3( 1.5580782047233335,    -2.8914115380566665,     0.33333333333333337),
			new Grad3( 2.8914115380566665,    -1.5580782047233335,    -0.33333333333333337),
			new Grad3( 2.4482280932803,       -1.8101897177633992,     1.2760767510338025),
			new Grad3( 1.8101897177633992,    -2.4482280932803,       -1.2760767510338025),
			new Grad3(-2.6329931618533333,    -1.4082482904633333,    -1.4082482904633333),
			new Grad3(-3.29965982852,         -0.07491495712999985,   -0.07491495712999985),
			new Grad3(-2.838945207362466,      0.24732126143473554,   -1.6667938651159684),
			new Grad3(-2.838945207362466,     -1.6667938651159684,     0.24732126143473554),
			new Grad3( 0.33333333333333337,    1.5580782047233335,    -2.8914115380566665),
			new Grad3(-0.33333333333333337,    2.8914115380566665,    -1.5580782047233335),
			new Grad3( 1.2760767510338025,     2.4482280932803,       -1.8101897177633992),
			new Grad3(-1.2760767510338025,     1.8101897177633992,    -2.4482280932803),
			new Grad3( 0.33333333333333337,   -2.8914115380566665,     1.5580782047233335),
			new Grad3(-0.33333333333333337,   -1.5580782047233335,     2.8914115380566665),
			new Grad3(-1.2760767510338025,    -2.4482280932803,        1.8101897177633992),
			new Grad3( 1.2760767510338025,    -1.8101897177633992,     2.4482280932803),
			new Grad3( 3.29965982852,          0.07491495712999985,    0.07491495712999985),
			new Grad3( 2.6329931618533333,     1.4082482904633333,     1.4082482904633333),
			new Grad3( 2.838945207362466,     -0.24732126143473554,    1.6667938651159684),
			new Grad3( 2.838945207362466,      1.6667938651159684,    -0.24732126143473554),
			new Grad3(-2.8914115380566665,     1.5580782047233335,     0.33333333333333337),
			new Grad3(-1.5580782047233335,     2.8914115380566665,    -0.33333333333333337),
			new Grad3(-2.4482280932803,        1.8101897177633992,    -1.2760767510338025),
			new Grad3(-1.8101897177633992,     2.4482280932803,        1.2760767510338025),
			new Grad3(-2.8914115380566665,     0.33333333333333337,    1.5580782047233335),
			new Grad3(-1.5580782047233335,    -0.33333333333333337,    2.8914115380566665),
			new Grad3(-1.8101897177633992,     1.2760767510338025,     2.4482280932803),
			new Grad3(-2.4482280932803,       -1.2760767510338025,     1.8101897177633992),
			new Grad3( 0.07491495712999985,    3.29965982852,          0.07491495712999985),
			new Grad3( 1.4082482904633333,     2.6329931618533333,     1.4082482904633333),
			new Grad3( 1.6667938651159684,     2.838945207362466,     -0.24732126143473554),
			new Grad3(-0.24732126143473554,    2.838945207362466,      1.6667938651159684),
			new Grad3( 0.07491495712999985,    0.07491495712999985,    3.29965982852),
			new Grad3( 1.4082482904633333,     1.4082482904633333,     2.6329931618533333),
			new Grad3(-0.24732126143473554,    1.6667938651159684,     2.838945207362466),
			new Grad3( 1.6667938651159684,    -0.24732126143473554,    2.838945207362466)
		};
		for (int i = 0; i < grad3.length; i++) {
			grad3[i].dx /= N3; grad3[i].dy /= N3; grad3[i].dz /= N3;
		}
		for (int i = 0; i < PSIZE; i++) {
			GRADIENTS_3D[i] = grad3[i % grad3.length];
		}

		int[][] base2D = new int[][] {
			new int[] { 1, 1, 0, 1, 0, 1, 0, 0, 0 },
			new int[] { 1, 1, 0, 1, 0, 1, 2, 1, 1 }
		};
		int[] p2D = new int[] { 0, 0, 1, -1, 0, 0, -1, 1, 0, 2, 1, 1, 1, 2, 2, 0, 1, 2, 0, 2, 1, 0, 0, 0 };
		int[] lookupPairs2D = new int[] { 0, 1, 1, 0, 4, 1, 17, 0, 20, 2, 21, 2, 22, 5, 23, 5, 26, 4, 39, 3, 42, 4, 43, 3 };

		Contribution2[] contributions2D = new Contribution2[p2D.length / 4];
		for (int i = 0; i < p2D.length; i += 4) {
			int[] baseSet = base2D[p2D[i]];
			Contribution2 previous = null, current = null;
			for (int k = 0; k < baseSet.length; k += 3) {
				current = new Contribution2(baseSet[k], baseSet[k + 1], baseSet[k + 2]);
				if (previous == null) {
					contributions2D[i / 4] = current;
				}
				else {
					previous.next = current;
				}
				previous = current;
			}
			current.next = new Contribution2(p2D[i + 1], p2D[i + 2], p2D[i + 3]);
		}

		LOOKUP_2D = new Contribution2[64];
		for (int i = 0; i < lookupPairs2D.length; i += 2) {
			LOOKUP_2D[lookupPairs2D[i]] = contributions2D[lookupPairs2D[i + 1]];
		}
		
		int[][] base3D = new int[][] {
			new int[] { 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1 },
			new int[] { 2, 1, 1, 0, 2, 1, 0, 1, 2, 0, 1, 1, 3, 1, 1, 1 },
			new int[] { 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 2, 1, 1, 0, 2, 1, 0, 1, 2, 0, 1, 1 }
		};
		int[] p3D = new int[] { 0, 0, 1, -1, 0, 0, 1, 0, -1, 0, 0, -1, 1, 0, 0, 0, 1, -1, 0, 0, -1, 0, 1, 0, 0, -1, 1, 0, 2, 1, 1, 0, 1, 1, 1, -1, 0, 2, 1, 0, 1, 1, 1, -1, 1, 0, 2, 0, 1, 1, 1, -1, 1, 1, 1, 3, 2, 1, 0, 3, 1, 2, 0, 1, 3, 2, 0, 1, 3, 1, 0, 2, 1, 3, 0, 2, 1, 3, 0, 1, 2, 1, 1, 1, 0, 0, 2, 2, 0, 0, 1, 1, 0, 1, 0, 2, 0, 2, 0, 1, 1, 0, 0, 1, 2, 0, 0, 2, 2, 0, 0, 0, 0, 1, 1, -1, 1, 2, 0, 0, 0, 0, 1, -1, 1, 1, 2, 0, 0, 0, 0, 1, 1, 1, -1, 2, 3, 1, 1, 1, 2, 0, 0, 2, 2, 3, 1, 1, 1, 2, 2, 0, 0, 2, 3, 1, 1, 1, 2, 0, 2, 0, 2, 1, 1, -1, 1, 2, 0, 0, 2, 2, 1, 1, -1, 1, 2, 2, 0, 0, 2, 1, -1, 1, 1, 2, 0, 0, 2, 2, 1, -1, 1, 1, 2, 0, 2, 0, 2, 1, 1, 1, -1, 2, 2, 0, 0, 2, 1, 1, 1, -1, 2, 0, 2, 0 };
		int[] lookupPairs3D = new int[] { 0, 2, 1, 1, 2, 2, 5, 1, 6, 0, 7, 0, 32, 2, 34, 2, 129, 1, 133, 1, 160, 5, 161, 5, 518, 0, 519, 0, 546, 4, 550, 4, 645, 3, 647, 3, 672, 5, 673, 5, 674, 4, 677, 3, 678, 4, 679, 3, 680, 13, 681, 13, 682, 12, 685, 14, 686, 12, 687, 14, 712, 20, 714, 18, 809, 21, 813, 23, 840, 20, 841, 21, 1198, 19, 1199, 22, 1226, 18, 1230, 19, 1325, 23, 1327, 22, 1352, 15, 1353, 17, 1354, 15, 1357, 17, 1358, 16, 1359, 16, 1360, 11, 1361, 10, 1362, 11, 1365, 10, 1366, 9, 1367, 9, 1392, 11, 1394, 11, 1489, 10, 1493, 10, 1520, 8, 1521, 8, 1878, 9, 1879, 9, 1906, 7, 1910, 7, 2005, 6, 2007, 6, 2032, 8, 2033, 8, 2034, 7, 2037, 6, 2038, 7, 2039, 6 };

		Contribution3[] contributions3D = new Contribution3[p3D.length / 9];
		for (int i = 0; i < p3D.length; i += 9) {
			int[] baseSet = base3D[p3D[i]];
			Contribution3 previous = null, current = null;
			for (int k = 0; k < baseSet.length; k += 4) {
				current = new Contribution3(baseSet[k], baseSet[k + 1], baseSet[k + 2], baseSet[k + 3]);
				if (previous == null) {
					contributions3D[i / 9] = current;
				}
				else {
					previous.next = current;
				}
				previous = current;
			}
			current.next = new Contribution3(p3D[i + 1], p3D[i + 2], p3D[i + 3], p3D[i + 4]);
			current.next.next = new Contribution3(p3D[i + 5], p3D[i + 6], p3D[i + 7], p3D[i + 8]);
		}

		LOOKUP_3D = new Contribution3[2048];
		for (int i = 0; i < lookupPairs3D.length; i += 2) {
			LOOKUP_3D[lookupPairs3D[i]] = contributions3D[lookupPairs3D[i + 1]];
		}
	}
}