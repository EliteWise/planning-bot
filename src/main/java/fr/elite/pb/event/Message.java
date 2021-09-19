package fr.elite.pb.event;

import fr.elite.pb.database.MongoRequest;
import fr.elite.pb.database.model.Channel;
import fr.elite.pb.database.model.Statistic;
import fr.elite.pb.database.model.Task;
import fr.elite.pb.util.Embed;
import fr.elite.pb.util.TaskState;
import net.dv8tion.jda.api.EmbedBuilder;
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
import java.util.List;
import java.util.*;
import java.util.logging.Logger;

public class Message extends ListenerAdapter {

    public static Task alarmTask;
    private int state = 0;
    private net.dv8tion.jda.api.entities.Message previousBotMessage;
    static Logger log = Logger.getLogger(Message.class.getName());

    private Task task;

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        System.out.println("Planning Bot Ready!");
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent e) {
        net.dv8tion.jda.api.entities.Message message = e.getMessage();
        TextChannel channel = e.getChannel();

        if(previousBotMessage != null && state > 0) {
            previousBotMessage.delete().queue();
            message.delete().queue();
        }
        previousBotMessage = message.getAuthor().isBot() ? message : null;

        if(message.getContentRaw().equalsIgnoreCase("!p help")) {
            EmbedBuilder eb = new EmbedBuilder();
            List<MessageEmbed.Field> fields = new ArrayList<>(
                    Arrays.asList(new MessageEmbed.Field("`!p board`", "*Display the board*", false),
                                  new MessageEmbed.Field("`!p board add <Pseudo>`", "*Add a member to the board*", false),
                                  new MessageEmbed.Field("`!p board remove <Pseudo>`", "*Remove a member to the board*", false))
            );
            new Embed(eb, "Planning Help", Color.CYAN, fields, OffsetDateTime.now(Clock.systemUTC())).build(channel);
        } else if(message.getContentRaw().contains("!p add") && state == 0 && !message.getAuthor().isBot()) {
            if(message.getContentRaw().split(" ").length >= 3) {

                message.delete().queue();

                task = new Task();
                task.setMainMemberName(message.getContentRaw().split(" ")[2]);

                channel.sendMessage("Type the title: ").queue((response) ->
                    state++
                );

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
                channel.sendMessage("Error.").queue();
            }

            if(!mongoRequest.taskExist(task)) channel.sendMessage("No task registered.").queue();

            for(int i = 0; i < mongoRequest.getMembers().size(); i++) {
                Random rand = new Random();

                float r = rand.nextFloat();
                float g = rand.nextFloat();
                float b = rand.nextFloat();
                Color randomColor = new Color(r, g, b);

                Task task = mongoRequest.getMembers().get(i);

                MessageEmbed.Field field = new MessageEmbed.Field(task.getTitle(), task.getDescription(), true);
                eb.clear();

                new Embed(eb, task.getMainMemberName(), field, task.getDeadline(), task.getTimeToComplete(), task.getState()).build(channel);
                log.info(mongoRequest.getMembers().get(i) + "");
            }
        } else if(message.getContentRaw().equalsIgnoreCase("!p link")) {
            try {
                MongoRequest mongoRequest = new MongoRequest();
                Channel linkedChannel = new Channel();
                linkedChannel.setGuildID(channel.getGuild().getId());
                linkedChannel.setChannelID(channel.getId());
                boolean isLinked = mongoRequest.linkChannel(linkedChannel);
                if(isLinked) channel.sendMessage("Channel already linked.").queue();

            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        } else if(message.getContentRaw().equalsIgnoreCase("!p stats")) {
            try {
                MongoRequest mongoRequest = new MongoRequest();
                List<Statistic> stats = mongoRequest.getStats();

                List<MessageEmbed.Field> fields = new ArrayList<>();

                for(Statistic statistic : stats) {
                    fields.add(new MessageEmbed.Field(statistic.getPseudo(),
                            "Task in progress: " + statistic.getTaskInProgress() + " | Task completed: " + statistic.getTaskCompleted(), false));
                }

                new Embed(new EmbedBuilder(), "Statistics", Color.LIGHT_GRAY, fields, null).build(channel);

            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }

        if(!message.getAuthor().isBot()) {
            switch (state) {
                case 1:
                    task.setTitle(message.getContentRaw());

                    channel.sendMessage("Type the other members assigned to this task (0 if not): ").queue();

                    state++;
                    break;
                case 2:
                    task.setOtherMembers(message.getContentRaw().equals("0") ? null : Collections.singletonList(message.getContentRaw()));

                    channel.sendMessage("Type the description: ").queue();
                    state++;
                    break;
                case 3:
                    task.setDescription(message.getContentRaw());

                    channel.sendMessage("Type the deadline (Day/Month/Year): ").queue();
                    state++;
                    break;
                case 4:
                    String msg = message.getContentRaw(); Date date = new GregorianCalendar(Integer.parseInt(msg.split("/")[2]), Integer.parseInt(msg.split("/")[1]) -1, Integer.parseInt(msg.split("/")[0])).getTime(); task.setDeadline(date);

                    channel.sendMessage("Type time to complete: (0.2 = 20 mins | 1.2 = 1:20)").queue();
                    state++;
                    break;
                case 5:
                    task.setTimeToComplete(Double.parseDouble(message.getContentRaw()));
                    task.setState(TaskState.IN_PROGRESS.name().replace("_", " "));

                    try {
                        MongoRequest mongoRequest = new MongoRequest();
                        mongoRequest.addTask(task);

                        new Embed(new EmbedBuilder(), task.getMainMemberName(), new MessageEmbed.Field(task.getTitle(), task.getDescription(), true), task.getDeadline(), task.getTimeToComplete(), task.getState())
                                .build(channel.getGuild().getTextChannelById(mongoRequest.getChannel(channel.getGuild().getId())));

                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }

                    state = 0;

                    break;
            }
        }

        if(alarmTask != null && !message.getAuthor().isBot()) {
            try {
                MongoRequest mongoRequest = new MongoRequest();

                List<Date> alarms = mongoRequest.getAlarmsByTask(alarmTask);
                String msg = message.getContentRaw();
                message.delete().queue();

                Date date = new GregorianCalendar(Integer.parseInt(msg.split("/")[2]), Integer.parseInt(msg.split("/")[1]) -1, Integer.parseInt(msg.split("/")[0])).getTime();
                alarms.add(date);

                alarmTask.setAlarms(alarms);
                mongoRequest.editTask(alarmTask);

            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
    }

}
