package fr.elite.pb.event;

import fr.elite.pb.database.MongoRequest;
import fr.elite.pb.util.PlanningReaction;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.text.ParseException;

public class Reaction extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent e) {
        MessageReaction.ReactionEmote reactionEmote = e.getReactionEmote();
        MessageReaction reaction = e.getReaction();
        User user = e.getUser();
        TextChannel channel = e.getChannel();
        String messageID = e.getMessageId();
        MessageEmbed embed = channel.retrieveMessageById(messageID).complete().getEmbeds().get(0);

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
