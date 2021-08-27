package fr.elite.pb.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.time.temporal.TemporalAccessor;

public class Embed {

    private EmbedBuilder embed;

    public Embed(EmbedBuilder embed, String title, Color color, MessageEmbed.Field field, TemporalAccessor timestamp) {
        this.embed = embed;
        embed.setTitle(title).setColor(color).addField(field).setTimestamp(timestamp);
    }

    public void build(TextChannel channel) {
        channel.sendMessage(embed.build()).queue();
    }
}
