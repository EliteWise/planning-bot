package fr.elite.pb.util;

import fr.elite.pb.database.model.Task;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;

public class Embed {

    private EmbedBuilder embed;

    public Embed() {

    }

    public Embed(EmbedBuilder embed, String title, Color color, List<MessageEmbed.Field> fields, TemporalAccessor timestamp) {
        this.embed = embed;
        embed.setTitle(title).setColor(color).setTimestamp(timestamp);
        for(MessageEmbed.Field field : fields) {
            embed.addField(field);
        }
    }

    public Embed(EmbedBuilder embed, String title, MessageEmbed.Field field, Date deadline, double estimatedTime, String state) {
        this.embed = embed;
        embed.setTitle(title).setColor(state.equalsIgnoreCase(TaskState.IN_PROGRESS.name().replace("_", " ")) ? Color.GREEN : Color.RED).addField(field).setTimestamp(deadline.toInstant()).setFooter("Estimated: " + estimatedTime);
    }

    public Embed(EmbedBuilder embed, String title, MessageEmbed.Field field, OffsetDateTime timestamp, String footer) {
        this.embed = embed;
        embed.setTitle(title).setColor(Color.RED).addField(field).setTimestamp(timestamp).setFooter(footer);
    }

    public Embed(EmbedBuilder embed, String title, Color color, MessageEmbed.Field field) {
        this.embed = embed;
        embed.setTitle(title).setColor(color).addField(field);
    }

    public void build(TextChannel channel) {
        channel.sendMessage(embed.build()).queue(msg -> {
            msg.addReaction("✅").queue(); msg.addReaction("❌").queue(); msg.addReaction("⏰").queue();
        });
    }

    public EmbedBuilder getBuilder() {
        return embed;
    }

    public Task embedToTask(MessageEmbed embed) {
        Task task = new Task();
        task.setTitle(embed.getFields().get(0).getName());
        task.setMainMemberName(embed.getTitle());
        return task;
    }
}
