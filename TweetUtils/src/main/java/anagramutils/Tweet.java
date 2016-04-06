package anagramutils;

import anagramutils.processing.Normalize;
import anagramutils.processing.ProcessedTweetText;
import twitter4j.Status;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.UUID;

public class Tweet {

    private UUID id;
    private long statusId;
    private Timestamp createdAt;
    private String tweetOriginalText;
    private String tweetStrippedText;
    private String tweetSortedStrippedText;
    private Long userId;
    private String userName;
    private boolean isMatched;

    private Tweet() {
    }

    public Tweet(UUID id, long statusId, Timestamp createdAt, String tweetOriginalText, String tweetStrippedText, String tweetSortedStrippedText, Long userId, String userName, boolean isMatched) {
        this.id = id;
        this.statusId = statusId;
        this.createdAt = createdAt;
        this.tweetOriginalText = tweetOriginalText;
        this.tweetStrippedText = tweetStrippedText;
        this.tweetSortedStrippedText = tweetSortedStrippedText;
        this.userId = userId;
        this.userName = userName;
        this.isMatched = isMatched;
    }

    public static ProcessedTweetText processTweetText(String originalText) {
        String strippedText = Normalize.normalize(originalText);

        char[] chars = strippedText.toCharArray();
        Arrays.sort(chars);
        String sortedStrippedText = new String(chars);

        return new ProcessedTweetText(originalText, strippedText, sortedStrippedText);
    }

    public static Tweet fromStatus(Status status) {
        ProcessedTweetText processedTweetText = processTweetText(status.getText());

        return new Tweet(UUID.randomUUID(), status.getId(), new java.sql.Timestamp(status.getCreatedAt().getTime()),
                status.getText(), processedTweetText.getStrippedText(),
                processedTweetText.getSortedStrippedText(), status.getUser().getId(),
                status.getUser().getScreenName(), false);
    }

    public UUID getId() {
        return id;
    }

    public long getStatusId() {
        return statusId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getTweetOriginalText() {
        return tweetOriginalText;
    }

    public String getTweetStrippedText() {
        return tweetStrippedText;
    }

    public String getTweetSortedStrippedText() {
        return tweetSortedStrippedText;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public boolean getIsMatched() {
        return isMatched;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "id=" + id +
                ", statusId=" + statusId +
                ", createdAt=" + createdAt +
                ", tweetOriginalText='" + tweetOriginalText + '\'' +
                ", tweetStrippedText='" + tweetStrippedText + '\'' +
                ", tweetSortedStrippedText='" + tweetSortedStrippedText + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", isMatched=" + isMatched +
                '}';
    }
}