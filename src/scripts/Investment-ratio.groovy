package scripts

// Investment-ratio.groovy - Investment ratio
INWR = new double[LL]
for (i = 0; i < LL; i++) {
    INWR[i] = INW[i]/PKB[i] * 100;
}