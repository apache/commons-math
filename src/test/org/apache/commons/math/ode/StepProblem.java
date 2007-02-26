package org.apache.commons.math.ode;

import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.SwitchingFunction;


public class StepProblem
  implements FirstOrderDifferentialEquations, SwitchingFunction {

  public StepProblem(double rateBefore, double rateAfter,
                     double switchTime) {
    this.rateAfter  = rateAfter;
    this.switchTime = switchTime;
    setRate(rateBefore);
  }

  public void computeDerivatives(double t, double[] y, double[] yDot) {
    yDot[0] = rate;
  }

  public int getDimension() {
    return 1;
  }

  public void setRate(double rate) {
    this.rate = rate;
  }

  public int eventOccurred(double t, double[] y) {
    setRate(rateAfter);
    return RESET_DERIVATIVES;
  }

  public double g(double t, double[] y) {
    return t - switchTime;
  }

  public void resetState(double t, double[] y) {
  }

  private double rate;
  private double rateAfter;
  private double switchTime;

  private static final long serialVersionUID = 7590601995477504318L;

}
