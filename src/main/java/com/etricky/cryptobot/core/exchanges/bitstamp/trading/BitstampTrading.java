package com.etricky.cryptobot.core.exchanges.bitstamp.trading;

import org.springframework.stereotype.Component;

import com.etricky.cryptobot.core.exchanges.common.AbstractExchangeTrading;
import com.etricky.cryptobot.core.exchanges.common.threads.ExchangeThreads;
import com.etricky.cryptobot.core.interfaces.Commands;
import com.etricky.cryptobot.core.interfaces.jsonFiles.JsonFiles;
import com.etricky.cryptobot.core.strategies.common.TimeSeriesHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("bitstampTradingBean")
public class BitstampTrading extends AbstractExchangeTrading {

	public BitstampTrading(ExchangeThreads exchangeThreads, Commands commands, JsonFiles jsonFiles,
			TimeSeriesHelper timeSeriesHelper) {
		super(exchangeThreads, commands, jsonFiles, timeSeriesHelper);
	}

	private void startTrade() {
		log.debug("start");

		log.debug("done");
	}

	@Override
	public void run() {
		log.debug("start");

		startTrade();

		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			log.debug("thread interrupted");

			commands.sendMessage("Thread " + getThreadInfo().getThreadName() + " interrupted", true);
		}

		log.debug("done");
	}
}
