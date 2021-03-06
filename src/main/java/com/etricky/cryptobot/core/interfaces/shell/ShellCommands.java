package com.etricky.cryptobot.core.interfaces.shell;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Shell;
import org.springframework.shell.result.ThrowableResultHandler;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.commands.Help;
import org.springframework.shell.standard.commands.Quit;

import com.etricky.cryptobot.core.exchanges.common.exceptions.ExchangeException;
import com.etricky.cryptobot.core.interfaces.Commands;

import lombok.extern.slf4j.Slf4j;

@ShellComponent
@Slf4j
public class ShellCommands implements Quit.Command {

	public enum ShellCommandsEnum {
		START("start", "s"), BACKTEST("backtest", "bt"), STOP("stop", "st"), LIST("list", "l"), QUIT("quit", "q"),
		HELP("help", null), STACKTRACE("stacktrace", null), RELOADFCONFIGS("reload", "rl"), WALLET("wallet", "w");

		public String command;
		public String shortCommand;

		public String getCommand() {
			return command;
		}

		public String getShortCommand() {
			return shortCommand;
		}

		ShellCommandsEnum(String command, String shortCommand) {
			this.command = command;
			this.shortCommand = shortCommand;
		}

	}

	@Autowired
	Help shellHelp;
	@Autowired
	private ThrowableResultHandler throwableResultHandler;
	@Autowired
	Shell shell;

	private Commands commands;

	public ShellCommands(Commands commands) {
		log.debug("start");

		this.commands = commands;

		log.debug("done");
	}

	public String executeCommand(String command, String source) {
		boolean execute = false;
		String txt = null;
		CharSequence cs;

		log.debug("start. command: {} source: {}", command, source);

		try {
			String[] arguments = command.split("\\s+");

			if (arguments[0].equalsIgnoreCase(ShellCommandsEnum.START.command)
					|| arguments[0].equalsIgnoreCase(ShellCommandsEnum.START.shortCommand)) {

				log.debug("command matched: {}", ShellCommandsEnum.START.command);
				execute = true;

			} else if (arguments[0].equalsIgnoreCase(ShellCommandsEnum.BACKTEST.command)
					|| arguments[0].equalsIgnoreCase(ShellCommandsEnum.BACKTEST.shortCommand)) {

				log.debug("command matched: {}", ShellCommandsEnum.BACKTEST.command);
				execute = true;

			} else if (arguments[0].equalsIgnoreCase(ShellCommandsEnum.STOP.command)
					|| arguments[0].equalsIgnoreCase(ShellCommandsEnum.STOP.shortCommand)) {

				log.debug("command matched: {}", ShellCommandsEnum.STOP.command);
				execute = true;

			} else if (arguments[0].equalsIgnoreCase(ShellCommandsEnum.LIST.command)
					|| arguments[0].equalsIgnoreCase(ShellCommandsEnum.LIST.shortCommand)) {

				log.debug("command matched: {}", ShellCommandsEnum.LIST.command);
				list(true);

			} else if (arguments[0].equalsIgnoreCase(ShellCommandsEnum.QUIT.command)
					|| arguments[0].equalsIgnoreCase(ShellCommandsEnum.QUIT.shortCommand)) {

				log.debug("command matched: {}", ShellCommandsEnum.QUIT.command);
				execute = true;

			} else if (arguments[0].equalsIgnoreCase(ShellCommandsEnum.RELOADFCONFIGS.command)
					|| arguments[0].equalsIgnoreCase(ShellCommandsEnum.RELOADFCONFIGS.shortCommand)) {

				log.debug("command matched: {}", ShellCommandsEnum.RELOADFCONFIGS.command);
				execute = true;

			} else if (arguments[0].equalsIgnoreCase(ShellCommandsEnum.WALLET.command)
					|| arguments[0].equalsIgnoreCase(ShellCommandsEnum.WALLET.shortCommand)) {

				log.debug("command matched: {}", ShellCommandsEnum.WALLET.command);
				execute = true;

			} else if (arguments[0].equalsIgnoreCase(ShellCommandsEnum.HELP.command)) {

				log.debug("command matched: {}", ShellCommandsEnum.HELP.command);

				try {
					// remove the command otherwise it would execute help help
					if (arguments.length > 1) {
						arguments = Arrays.copyOfRange(arguments, 1, arguments.length - 1);
						command = Arrays.toString(arguments);
					} else {
						command = null;
					}

					// executes shell help command of the command provided
					cs = shellHelp.help(command);
					txt = cs.toString();

				} catch (IOException e) {
					log.error("Exception: {}", e);
					throw new ExchangeException(e);
				}

			} else if (arguments[0].equalsIgnoreCase(ShellCommandsEnum.STACKTRACE.command)) {
				log.debug("command matched: {}", ShellCommandsEnum.STACKTRACE.command);

				if (throwableResultHandler.getLastError() != null) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					txt = throwableResultHandler.getLastError().getMessage().concat("\n");
					throwableResultHandler.getLastError().printStackTrace(pw);
					txt = txt.concat(sw.toString());
				}

			} else {
				log.debug("command didn't matched");
				txt = "Invalid command!!!\n\n";
				cs = shellHelp.help(null);
				txt = txt.concat(cs.toString());
			}

			if (execute) {
				ShellInputParser shellInputParser = new ShellInputParser(command);
				commands.sendMessage("command from " + command + " source: " + source, false);
				shell.run(shellInputParser);
			}
		} catch (Exception e) {
			commands.exceptionHandler(e);
		}

		log.debug("done");
		return txt;
	}

	@ShellMethod(value = "Starts processing a trade from an exchange", key = { "start", "s" })
	public void start(String exchange, String tradeName,
			@ShellOption(defaultValue = "0", help = "0 - All, 1 - History, 2 - Live") int tradeType) {

		log.debug("start. exchange: {} tradeName: {} tradeType: {}", exchange, tradeName, tradeType);

		commands.startExchangeTrade(exchange, tradeName, tradeType);

		log.debug("done");
	}

	@ShellMethod(value = "Backtests the strategy for a tradeName from an exchange. Parameters: History Days (Optional)", key = {
			"backtest", "bt" })
	public void backtest(String exchange, String tradeName,
			@ShellOption(defaultValue = "0", help = "0 - period defined by start and end date, > 0 - period from now minus history days") int historyDays,
			@ShellOption(defaultValue = "1970-01-01", help = "Start date with format yyyy-mm-dd") String startDate,
			@ShellOption(defaultValue = "1970-01-02", help = "End date with format yyyy-mm-dd") String endDate) {

		log.debug("start. exchange: {} tradeName: {} historyDays: {} startDate: {} endDate: {}", exchange, tradeName,
				historyDays, startDate, endDate);

		commands.backtest(exchange, tradeName, historyDays, startDate, endDate);

		log.debug("done");
	}

	@ShellMethod(value = "Stops processing a currency from an exchange", key = { "stop", "st" })
	public void stop(String exchange, String currency) {

		log.debug("start. exchange: {} currency: {}", exchange, currency);

		commands.stopExchangeTrade(exchange, currency);

		log.debug("done");
	}

	@ShellMethod(value = "List current running currencies for each exchange", key = { "list", "l" })
	public void list() {
		log.debug("start");

		list(false);

		log.debug("done");
	}

	@ShellMethod(value = "Reloads config files", key = { "reload", "rl" })
	public void reload() {
		log.debug("start");

		commands.reloadConfigs();

		log.debug("done");
	}

	@ShellMethod(value = "Ends CryptoBot.", key = { "quit", "exit", "shutdown", "q" })
	public void quit() {
		log.debug("start");

		commands.quitApplication();

		log.debug("done");
	}

	@ShellMethod(value = "Wallet info.", key = { "wallet", "w" })
	public void wallet(String exchange) {
		log.debug("start. exchange: {}");

		commands.printWalletInfo(exchange);

		log.debug("done");
	}

	private void list(boolean toExternalApp) {
		log.debug("start");

		commands.listExchangeTradingThreads(toExternalApp);

		log.debug("done");
	}
}
