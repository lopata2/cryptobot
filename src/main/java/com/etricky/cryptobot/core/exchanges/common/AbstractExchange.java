package com.etricky.cryptobot.core.exchanges.common;

import java.lang.Thread.UncaughtExceptionHandler;

import com.etricky.cryptobot.core.common.threads.ThreadInfo;
import com.etricky.cryptobot.core.exchanges.common.enums.ExchangeEnum;
import com.etricky.cryptobot.core.exchanges.common.threads.ExchangeThreads;
import com.etricky.cryptobot.core.interfaces.Commands;
import com.etricky.cryptobot.core.interfaces.jsonFiles.JsonFiles;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractExchange implements UncaughtExceptionHandler {

	@Getter
	protected ThreadInfo threadInfo;
	protected ExchangeThreads exchangeThreads;
	protected Commands commands;
	protected JsonFiles jsonFiles;
	@Getter
	protected ExchangeEnum exchangeEnum;

	public AbstractExchange(ExchangeThreads exchangeThreads, Commands commands, JsonFiles jsonFiles) {
		this.exchangeThreads = exchangeThreads;
		this.commands = commands;
		this.jsonFiles = jsonFiles;
	}

	protected abstract void exchangeDisconnect();

	protected void setThreadInfoData() {
		log.debug("start");

		log.debug("thread: {}", Thread.currentThread().getId());
		threadInfo.setThread(Thread.currentThread(), this);

		log.debug("done");
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		log.error("start. exception on thread:{}", t.getName());
		log.error("exception: {}", e);

		commands.sendMessage("Exception occurred on " + t.getName() + ". Stopping thread", true);
		// sends the interrupt to itself
		if (t.isAlive() || !t.isInterrupted()) {
			log.debug("sending interrupt");
			t.interrupt();
		}

		// in case the interrupt hasn't stopped the thread
		exchangeDisconnect();

		log.debug("done");
	}

}
