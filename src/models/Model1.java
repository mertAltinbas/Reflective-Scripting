package models;

public class Model1 {
    @Bind private int LL; // number of years
    @Bind private double[] twKI, twKS, twINW, twEKS, twIMP; // growth rates
    @Bind private double[] KI, KS, INW, EKS, IMP, PKB; // variables

    public Model1() {}

    public void run() {
        PKB = new double[LL];
        PKB[0] = KI[0] + KS[0] + INW[0] + EKS[0] - IMP[0];
        for (int t = 1; t < LL; t++) {
            KI[t] = twKI[t] * KI[t - 1];
            KS[t] = twKS[t] * KS[t - 1];
            INW[t] = twINW[t] * INW[t - 1];
            EKS[t] = twEKS[t] * EKS[t - 1];
            IMP[t] = twIMP[t] * IMP[t - 1];
            PKB[t] = KI[t] + KS[t] + INW[t] + EKS[t] - IMP[t];
        }
    }
}
