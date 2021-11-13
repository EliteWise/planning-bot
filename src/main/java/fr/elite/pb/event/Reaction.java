package fr.elite.pb.event;

import fr.elite.pb.database.MongoRequest;
import fr.elite.pb.util.PlanningReaction;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.util.List;

public class Reaction extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent e) {
        MessageReaction.ReactionEmote reactionEmote = e.getReactionEmote();
        MessageReaction reaction = e.getReaction();
        User user = e.getUser();
        TextChannel channel = e.getChannel();
        String messageID = e.getMessageId();
        Message message = channel.retrieveMessageById(messageID).complete();
        MessageEmbed embed = message.getEmbeds().get(0);
        List<MessageReaction> reactions = message.getReactions();

        if(reactions.size() == 1 && !e.getUser().isBot()) {
            reaction.removeReaction(user).complete();
            message.delete().queue();
            return;
        }

        try {
            MongoRequest mongoRequest = new MongoRequest();
            if(channel.getId().equals(mongoRequest.getChannel(channel.getGuild().getId()))) {
                if(e.getUser().isBot()) return;

                reaction.removeReaction(user).complete();

                PlanningReaction.executeByEmote(reactionEmote.getName(), embed, channel, messageID);
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        // Check if the reacted embed task is registered //

    }
}
