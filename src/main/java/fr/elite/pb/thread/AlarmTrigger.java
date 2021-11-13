package fr.elite.pb.thread;

import fr.elite.pb.Main;
import fr.elite.pb.database.MongoRequest;
import fr.elite.pb.database.model.Task;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AlarmTrigger {

    private void task() {
        try {
            MongoRequest mongoRequest = new MongoRequest();

            mongoRequest.getTasks().forEach(System.out::println);

            for(Task task : mongoRequest.getTasks()) {
                String user = task.getMainMemberName();
                List<Date> alarms = task.getAlarms();

                if(alarms == null || task.getMainMemberId() == null) continue;

                alarms.forEach(alarm -> {
                    LocalDateTime ldt = alarm.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    if(ldt.getDayOfMonth() == LocalDateTime.now().getDayOfMonth() && ldt.getMonth().equals(LocalDateTime.now().getMonth())) {

                        Main.jda.retrieveUserById(task.getMainMemberId()).complete().openPrivateChannel().queue(privateChannel -> {
                            privateChannel.sendMessage("One of your task has to be finished soon.").queue();
                        });
                    }
                });
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::task,
                0,
                10,
                TimeUnit.SECONDS);
    }

}
