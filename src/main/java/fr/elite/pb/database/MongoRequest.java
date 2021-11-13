package fr.elite.pb.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import fr.elite.pb.database.model.Channel;
import fr.elite.pb.database.model.Statistic;
import fr.elite.pb.database.model.Task;
import fr.elite.pb.util.TaskState;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoRequest {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private MongoCollection<Task> taskCollection;
    private MongoCollection<Channel> channelCollection;

    public MongoRequest() throws ParseException {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).codecRegistry(codecRegistry).build();

        MongoClient mongoClient = MongoClients.create(clientSettings);
        taskCollection = mongoClient.getDatabase("PlanningDB").getCollection("Task", Task.class);
        channelCollection = mongoClient.getDatabase("PlanningDB").getCollection("Channel", Channel.class);
    }

    public void addTask(Task task) throws ParseException {
        taskCollection.insertOne(task);
    }

    public ArrayList<Task> getMembers() {
        return taskCollection.find().into(new ArrayList<>());
    }

    public boolean taskExist(Task task) {
        if(taskCollection.find().first() == null) return false;
        return true;
    }

    public void removeTask(Task task) {
        taskCollection.deleteOne(Filters.and(Filters.eq("title", task.getTitle()), Filters.eq("mainMemberName", task.getMainMemberName())));
    }

    public void editTask(Task task) {
        taskCollection.replaceOne(Filters.and(Filters.eq("title", task.getTitle()), Filters.eq("mainMemberName", task.getMainMemberName())), task);
    }

    public void doneTask(Task task) {
        task.setState(TaskState.DONE.name());
        taskCollection.updateOne(Filters.and(Filters.eq("title", task.getTitle()), Filters.eq("mainMemberName", task.getMainMemberName())), Updates.set("state", TaskState.DONE.name()));
    }

    public List<Date> getAlarmsByTask(Task task) {
        return taskCollection.find(Filters.and(Filters.eq("title", task.getTitle()), Filters.eq("mainMemberName", task.getMainMemberName()))).first().getAlarms();
    }

    public boolean linkChannel(Channel channel) {
        Bson checkGuildID = Filters.eq("guildID", channel.getGuildID());

        if(channelCollection.find(checkGuildID).first() == null) {
            channelCollection.insertOne(channel);
        } else if(!channelCollection.find(checkGuildID).first().getChannelID().equals(channel.getChannelID())){
            channelCollection.replaceOne(checkGuildID, channel);
            return false;
        } else {
            return true;
        }
        return true;
    }

    public String getChannel(String guildID) {
        return channelCollection.find(Filters.eq("guildID", guildID)).first().getChannelID();
    }

    public Set<Statistic> getStats() {
        Set<Statistic> stats = new LinkedHashSet<>();
        List<Task> tasks = taskCollection.find().into(new ArrayList<>());

        tasks.stream().filter(task -> task.getState().equals(TaskState.IN_PROGRESS.name())).collect(Collectors.groupingBy(Task::getMainMemberName))
                .forEach((k, v) -> {
                    Statistic statistic = new Statistic();
                    statistic.setPseudo(k); statistic.setTaskInProgress(v.size());
                    stats.add(statistic);
                });

        tasks.stream().filter(task -> task.getState().equals(TaskState.DONE.name())).collect(Collectors.groupingBy(Task::getMainMemberName))
                .forEach((k, v) -> {
                    Statistic statistic = new Statistic();
                    statistic.setPseudo(k); statistic.setTaskCompleted(v.size());
                    stats.stream().filter(s -> s.getPseudo().equalsIgnoreCase(k)).forEach(t -> t.setTaskCompleted(v.size()));
                    stats.add(statistic);
                });

        return stats;
    }

    public List<Task> getTasks() {
        return taskCollection.find().into(new ArrayList<>());
    }

}
