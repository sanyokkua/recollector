import log    from "loglevel";
import prefix from "loglevel-plugin-prefix";


const logLevel = import.meta.env.VITE_APP_LOG_LEVEL;
// Configure logging
prefix.reg(log);
log.enableAll();
log.setLevel(logLevel);

prefix.apply(log, {
    template: "[%t] %l (%n):",
    levelFormatter(level) {
        return level.toUpperCase();
    },
    nameFormatter(name) {
        return name ?? "[anonymous]";
    },
    timestampFormatter(date) {
        return date.toISOString();
    }
});

log.info("Initialized logger");

export const logger = log;

