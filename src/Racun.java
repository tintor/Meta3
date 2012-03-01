public class Racun {
	public static void main(final String[] args) {
		for (int i = 5; i <= 20; i += 1) {
			final double r = renta(i * 12, 9);
			System.out.printf("godina %s\trenta %s\tukupno %s\n", i, 25000 * r, 25000 * r * i * 12);
		}
	}

	static double renta(final int m, final double kamata) {
		final double p = (Math.pow(1 + kamata / 100, 1.0 / 12.0) - 1) * 0.8 + 1;
		return Math.pow(p, m) * (p - 1) / (Math.pow(p, m) - 1);
	}

	public static void main2(final String[] args) {
		final double k = 72.94;
		final double kp = k / 80.0 * 100.0;
		System.out.println(kp + 3000);
		System.out.println(3000 * Math.sqrt(1.062));
		System.out.println(3000 * (1 + 0.062 / 2));
		System.out.println(Math.exp(Math.log(1.05) / 365));

		for (int i = 1; i <= 30; i++)
			System.out.printf("%s\t%s\n", i, 1000 * Math.pow(1.05, i));
	}
}