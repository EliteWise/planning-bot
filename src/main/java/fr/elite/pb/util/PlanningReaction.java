package fr.elite.pb.util;

import fr.elite.pb.database.MongoRequest;
import fr.elite.pb.event.Message;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

public enum PlanningReaction {

    DONE("✅") {
        public void process(MessageEmbed messageEmbed, TextChannel channel, String messageID) throws ParseException {
            MongoRequest mongoRequest = new MongoRequest();
            Embed embed = new Embed();

            mongoRequest.doneTask(embed.embedToTask(messageEmbed));

            Embed embed1 = new Embed(new EmbedBuilder(), messageEmbed.getTitle(), messageEmbed.getFields().get(0), messageEmbed.getTimestamp(), messageEmbed.getFooter().getText());
            channel.editMessageById(messageID, embed1.getBuilder().build()).queue();
        }
    },

    DELETE("❌") {
        public void process(MessageEmbed messageEmbed, TextChannel channel, String messageID) throws ParseException {
            MongoRequest mongoRequest = new MongoRequest();
            Embed embed = new Embed();
            mongoRequest.removeTask(embed.embedToTask(messageEmbed));
            channel.deleteMessageById(messageID).queue();
        }
    },

    ALARM("⏰") {
        public void process(MessageEmbed messageEmbed, TextChannel channel, String messageID) throws ParseException {
            MongoRequest mongoRequest = new MongoRequest();
            Embed embed = new Embed();

            channel.sendMessage("Type the day (Day/Month/Year)").queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
            Message.alarmTask = embed.embedToTask(messageEmbed);
        }
    };

    private String emote;

    PlanningReaction(String emote) {
        this.emote = emote;
    }

    public String getEmote() {
        return this.emote;
    }

    public static void executeByEmote(String emote, MessageEmbed embed, TextChannel channel, String messageID) throws ParseException {
        for(PlanningReaction planningReaction : values()) {
            if(planningReaction.getEmote().equals(emote)) {
                planningReaction.process(embed, channel, messageID);
            }
        }
    }

    public abstract void process(MessageEmbed embed, TextChannel channel, String messageID) throws ParseException;
}
