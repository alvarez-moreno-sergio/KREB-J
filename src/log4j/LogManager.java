package log4j;

import org.apache.log4j.Logger;

// LOG4J implementation
public class LogManager {
	private static Logger logger = Logger.getLogger(LogManager.class);

	public enum TypeLog4j {
		INFO, DEBUG, ERROR, WARN, FATAL, TRACE;
	}

	public static void writeInLog(TypeLog4j tipo, String message) {
		switch (tipo) {
		case INFO:
			logger.info(message);
			break;
		case DEBUG:
			logger.debug(message);
			break;
		case ERROR:
			logger.error(message);
			break;
		case WARN:
			logger.warn(message);
			break;
		case FATAL:
			logger.fatal(message);
			break;
		case TRACE:
			logger.trace(message);
			break;
		default:
			LogManager.writeInLog(LogManager.TypeLog4j.INFO, "--Error writting with log4j => " + message);
		}
	}
}
