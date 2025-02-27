package models;

public class Model2 {
    @Bind private int LL;
    @Bind private double[] twKI, twKS, twINW, twEKS, twIMP;
    @Bind private double[] KI, KS, INW, EKS, IMP, PKB;

    public Model2() {}

    public void run() {
        PKB = new double[LL];
        PKB[0] = (KI[0] + KS[0] + INW[0] + EKS[0] - IMP[0]) * 1.1;
        for (int t = 1; t < LL; t++) {
            KI[t] = twKI[t] * KI[t - 1] * 1.05;
            KS[t] = twKS[t] * KS[t - 1] * 0.95;
            INW[t] = twINW[t] * INW[t - 1] * 1.02;
            EKS[t] = twEKS[t] * EKS[t - 1] * 0.98;
            IMP[t] = twIMP[t] * IMP[t - 1] * 1.01;
            PKB[t] = KI[t] + KS[t] + INW[t] + EKS[t] - IMP[t];
        }
    }
}
