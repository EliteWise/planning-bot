package fr.elite.pb.database.model;

import java.util.Objects;

public class Channel {

    private String guildID;
    private String channelID;

    public String getGuildID() {
        return guildID;
    }

    public void setGuildID(String guildID) {
        this.guildID = guildID;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(guildID, channel.guildID) &&
                Objects.equals(channelID, channel.channelID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guildID, channelID);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "guildID='" + guildID + '\'' +
                ", channelID='" + channelID + '\'' +
                '}';
    }
}
