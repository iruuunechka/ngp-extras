package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.runners;

/**
 * @author Arkadii Rost
 */
public interface RunConfig {
	double getMinSplitPercent();
	double getParameterSplitResolution();
	double getObservableSplitResolution();
	int getSplitLimit();
	double getEps();
	double getAlpha();
	double getGamma();
	double getActionScale();

	RunConfig DEFAULT = new RunConfig() {
		@Override
		public double getMinSplitPercent() {
			return 0.2;
		}

		@Override
		public double getParameterSplitResolution() {
			return 1e-3;
		}

		@Override
		public double getObservableSplitResolution() {
			return 1e-3;
		}

		@Override
		public int getSplitLimit() {
			return 1000;
		}

		@Override
		public double getEps() {
			return 0.1;
		}

		@Override
		public double getAlpha() {
			return 0.9;
		}

		@Override
		public double getGamma() {
			return 0.8;
		}

		@Override
		public double getActionScale() {
			return 0.2;
		}
	} ;
}
