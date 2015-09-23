package com.orangemako.minesweeper.api;

import retrofit.Callback;
import retrofit.http.GET;

public interface FinanceService {
    @GET("/?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol=\"nke\"&env=store://datatables.org/alltableswithkeys&format=json")
    void getStockQuote(Callback<StockQuoteResponse> callback);
}
