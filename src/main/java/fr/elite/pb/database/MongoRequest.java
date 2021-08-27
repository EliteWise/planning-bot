package fr.elite.pb.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import fr.elite.pb.database.model.Task;
import fr.elite.pb.util.TaskState;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoRequest {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private MongoCollection<Task> taskCollection;

    public MongoRequest() throws ParseException {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).codecRegistry(codecRegistry).build();

        MongoClient mongoClient = MongoClients.create(clientSettings);
        taskCollection = mongoClient.getDatabase("NewDB").getCollection("CollectionTest", Task.class);

        Task fakeTask = new Task();
        fakeTask.setMainMemberName("e");
        fakeTask.setOtherMembers(new ArrayList<>(Arrays.asList("M1", "M2", "M3")));
        fakeTask.setDescription("e");
        fakeTask.setCreationDate(Calendar.getInstance().getTime());
        fakeTask.setDeadline(sdf.parse("27/08/2021"));
        fakeTask.setTimeToComplete(1.5);
        fakeTask.setState(TaskState.IN_PROGRESS.name());
        //addTask(fakeTask);

        FindIterable<Task> cursor = taskCollection.find();
        System.out.println(cursor.first().getDescription());

    }

    public void addTask(Task task) throws ParseException {
        taskCollection.insertOne(task);
    }

    public void removeTask(int taskID) {
        taskCollection.deleteOne(Filters.eq("description", "Desc"));
    }

    public ArrayList<String> getMembers() {
        return taskCollection.distinct("memberName", String.class).into(new ArrayList<>());
    }

}
