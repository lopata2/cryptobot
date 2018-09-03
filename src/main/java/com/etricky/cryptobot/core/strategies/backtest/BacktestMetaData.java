package com.etricky.cryptobot.core.strategies.backtest;

import java.math.BigDecimal;

import org.ta4j.core.Order.OrderType;

import com.etricky.cryptobot.core.strategies.TradingStrategy;
import com.etricky.cryptobot.core.strategies.TrailingStopLossStrategy;

import lombok.Getter;
import lombok.Setter;

public class BacktestMetaData {
	// order increased or decreased the balance
	private int balanceResult;
	private int amountResult;

	@Getter
	private BigDecimal firstOrderPrice = BigDecimal.ZERO, firstOrderAmount = BigDecimal.ZERO,
			firstOrderBalance = BigDecimal.ZERO, totalFees = BigDecimal.ZERO;

	@Getter
	@Setter
	private BigDecimal previousOrderPrice = BigDecimal.ZERO, previousOrderAmount = BigDecimal.ZERO,
			previousOrderBalance = BigDecimal.ZERO;
	@Getter
	private int posBalanceOrders = 0, negBalanceOrders = 0, posAmountOrders = 0, negAmountOrders = 0, totalOrders = 0,
			tradingBuys = 0, tradingSells = 0, stopLossBuys = 0, stopLossSells = 0;

	public void calculateMetaData(BacktestOrderInfo orderInfo) {
		String strategyBeanName;
		OrderType orderType;

		orderInfo.getStrategyResult().setBalanceAndAmount(previousOrderBalance, previousOrderAmount);

		if (firstOrderAmount == BigDecimal.ZERO) {
			firstOrderPrice = orderInfo.getStrategyResult().getClosePrice();
			firstOrderAmount = orderInfo.getStrategyResult().getAmount();
			firstOrderBalance = orderInfo.getStrategyResult().getBalance();
		}

		balanceResult = orderInfo.getStrategyResult().getBalance().compareTo(previousOrderBalance);
		amountResult = orderInfo.getStrategyResult().getAmount().compareTo(previousOrderAmount);
		strategyBeanName = orderInfo.getStrategyResult().getStrategyName();
		orderType = orderInfo.getStrategyResult().getLastEntry().getType();

		totalFees = totalFees.add(orderInfo.getStrategyResult().getFeeValue());

		if (balanceResult < 0) {
			negBalanceOrders++;
		} else {
			posBalanceOrders++;
		}

		if (amountResult < 0) {
			negAmountOrders++;
		} else {
			posAmountOrders++;
		}

		if (strategyBeanName.equals(TradingStrategy.STRATEGY_NAME)) {
			if (orderType.equals(OrderType.BUY)) {
				tradingBuys++;
			} else {
				tradingSells++;
			}
		} else if (strategyBeanName.equals(TrailingStopLossStrategy.STRATEGY_NAME)) {
			if (orderType.equals(OrderType.BUY)) {
				stopLossBuys++;
			} else {
				stopLossSells++;
			}
		}

		totalOrders++;
	}

}