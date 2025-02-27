package scripts

CONSUMPTION = new double[LL]
for (i = 0; i < LL; i++) {
    CONSUMPTION[i] = (KI[i] + KS[i])/PKB[i] * 100;
}