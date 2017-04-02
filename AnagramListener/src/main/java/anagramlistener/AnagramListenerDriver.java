package anagramlistener;

import configuration.ApplicationConfiguration;
import configuration.ConfigurationProvider;
import configuration.TwitterApiConfiguration;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterStream;

public class AnagramListenerDriver {

    private final static Logger logger = LoggerFactory.getLogger(AnagramListenerDriver.class);

    private final TwitterStream twitterStream;
    private final DBI dbi;
    private final ProcessedTweetCountLogger processedTweetCountLogger;

    private AnagramListenerDriver(TwitterStream twitterStream, DBI dbi, ProcessedTweetCountLogger processedTweetCountLogger) {
        this.twitterStream = twitterStream;
        this.dbi = dbi;
        this.processedTweetCountLogger = processedTweetCountLogger;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Interrupt received, shutting down...");
            cleanUp();
        }));
    }

    private void cleanUp() {
        logger.info("Shutting down twitter stream...");
        twitterStream.cleanUp();
        twitterStream.shutdown();
        logger.info("Finished shutting down twitter stream.");

        logger.info("Logging processed counts...");
        processedTweetCountLogger.logProcessedCounts();
        logger.info("Finished logging processed counts.");
    }

    private void run() {
        AnagramMatchingStatusListener publishFilteredStatusListener = new AnagramMatchingStatusListener(dbi, processedTweetCountLogger);

        twitterStream.addListener(publishFilteredStatusListener);
        twitterStream.sample("en");

        while (!Thread.currentThread().isInterrupted()){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                cleanUp();
            }
        }
    }

    public static void main(String[] args) {
        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.FromFileOrResources();
        TwitterApiConfiguration twitterApiConfiguration = TwitterApiConfiguration.FromFileOrResources();

        TwitterStream twitterStream = ConfigurationProvider.buildTwitterStream(twitterApiConfiguration, applicationConfiguration);
        DBI dbi = ConfigurationProvider.configureDatabase(applicationConfiguration);
        ProcessedTweetCountLogger processedTweetCountLogger = new ProcessedTweetCountLogger(dbi, applicationConfiguration);

        AnagramListenerDriver anagramListenerDriver = new AnagramListenerDriver(twitterStream, dbi, processedTweetCountLogger);
        anagramListenerDriver.run();
    }
}