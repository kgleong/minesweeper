package com.orangemako.minesweeper.api;

import com.google.gson.annotations.SerializedName;

public class StockQuoteResponse {
    Query query;

    class Query {
        Results results;
    }

    class Results {
        Quote quote;
    }

    class Quote {
        @SerializedName("symbol") private String mSymbol;
        @SerializedName("Ask") private float mPrice;
    }
}
