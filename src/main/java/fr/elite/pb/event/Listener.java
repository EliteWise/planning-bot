package fr.elite.pb.event;

import fr.elite.pb.database.MongoRequest;
import fr.elite.pb.util.Embed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.ParseException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.logging.Logger;

public class Listener extends ListenerAdapter {

    private int state = 0;
    static Logger log = Logger.getLogger(Listener.class.getName());

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        System.out.println("Planning Bot Ready!");
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent e) {
        Message message = e.getMessage();
        TextChannel channel = e.getChannel();

        if(message.getContentRaw().equalsIgnoreCase("!p help")) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Planning Help");
            eb.setColor(Color.CYAN);
            eb.addField("`!p board`", "*Display the board*", false);
            eb.addField("`!p board add <Pseudo>`", "*Add a member to the board*", false);
            eb.addField("`!p board remove <Pseudo>`", "*Remove a member to the board*", false);
            eb.setTimestamp(OffsetDateTime.now(Clock.systemUTC()));
            channel.sendMessage(eb.build()).queue();
        } else if(message.getContentRaw().contains("!p add") && state == 0 && !message.getAuthor().isBot()) {
            if(message.getContentRaw().split(" ").length >= 3) {
                channel.sendMessage("Type the description: ").queue((response) -> state++);
            } else {
                channel.sendMessage("You should specify the member name `!p add <MemberName>`").queue();
            }

        } else if(message.getContentRaw().equalsIgnoreCase("!p board")) {
            EmbedBuilder eb = new EmbedBuilder();
            MongoRequest mongoRequest = null;
            try {
                mongoRequest = new MongoRequest();
            } catch (ParseException ex) {
                ex.printStackTrace();
            }

            for(int i = 0; i < mongoRequest.getMembers().size(); i++) {
                MessageEmbed.Field field = new MessageEmbed.Field(mongoRequest.getMembers().get(i), "1", true);
                eb.clear();
                new Embed(eb, "Planning Board", Color.ORANGE, field, OffsetDateTime.now(Clock.systemUTC())).build(channel);
                log.info(mongoRequest.getMembers().get(i));
            }
        }

        if(!message.getAuthor().isBot()) {
            switch (state) {
                case 1:
                    channel.sendMessage("Type the deadline: ").queue();
                    state++;
                    break;
                case 2:
                    channel.sendMessage("Type time to complete: ").queue();
                    state++;
                    break;
            }
        }
    }

}
