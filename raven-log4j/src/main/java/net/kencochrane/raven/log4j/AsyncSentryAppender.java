package net.kencochrane.raven.log4j;

import net.kencochrane.raven.SentryDsn;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A Log4J appender that sends events asynchronously to Sentry.
 * <p>
 * This appender extends Log4J's {@link AsyncAppender}. If you use a log4j.xml file to configure Log4J, you can use that
 * class directly to wrap a {@link SentryAppender}. If you use a log4j.properties file -- which doesn't provide a way to
 * configure the {@link AsyncAppender} -- to configure Log4J and you need to send events asynchronously, you can use
 * this class instead.
 * </p>
 */
public class AsyncSentryAppender extends AsyncAppender {

    private String sentryDsn;
    private SentryAppender appender;

    public String getSentryDsn() {
        return sentryDsn;
    }

    public void setSentryDsn(String sentryDsn) {
        System.out.println("Oh come on!");
        this.sentryDsn = sentryDsn;
        if (appender != null) {
            removeAppender(appender);
        }
        SentryAppender appender = new SentryAppender();
        appender.setSentryDsn(sentryDsn);
        appender.setErrorHandler(this.getErrorHandler());
        appender.setLayout(this.getLayout());
        appender.setName(this.getName());
        appender.setThreshold(this.getThreshold());
        this.appender = appender;
        addAppender(appender);
    }

    @Override
    public void append(LoggingEvent event) {
        if (appender == null) {
            synchronized (this) {
                setSentryDsn(SentryDsn.build().toString());
            }
        }
        super.append(event);
    }

}
