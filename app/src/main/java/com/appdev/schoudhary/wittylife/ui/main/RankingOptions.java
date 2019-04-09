package com.appdev.schoudhary.wittylife.ui.main;

public enum RankingOptions {
    QOL("Quality of life"), COST("Cost of Living"), TRAFFIC("Traffic");
    private String rankingOption;

    RankingOptions(String rankingOption) {
        this.rankingOption = rankingOption;
    }

    public String getRankingOption() {
        return rankingOption;
    }
}