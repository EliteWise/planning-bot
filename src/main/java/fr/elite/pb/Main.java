package fr.elite.pb;

import fr.elite.pb.database.MongoRequest;
import fr.elite.pb.event.Message;
import fr.elite.pb.event.Reaction;
import fr.elite.pb.thread.AlarmTrigger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDateTime;

public class Main {

    public static JDA jda;

    private Main() throws LoginException, IOException {
        JDABuilder builder = JDABuilder.createDefault(getAccessToken()).
                setChunkingFilter(ChunkingFilter.ALL).setMemberCachePolicy(MemberCachePolicy.ALL).enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.addEventListeners(new Message(), new Reaction());
        builder.setActivity(Activity.of(Activity.ActivityType.WATCHING, ("les tâches " + Emoji.fromUnicode("\uD83D\uDCCB").toData().values().stream().findFirst().get())));
        jda = builder.build();
    }

    public static void main(String[] args) throws LoginException, ParseException, IOException {
        new Main();
        new MongoRequest();
        AlarmTrigger alarmTrigger = new AlarmTrigger();
        alarmTrigger.start();
        System.out.println(LocalDateTime.now().getMonth());
    }

    public String getAccessToken() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("config.json");
        StringBuilder textBuilder = new StringBuilder();
        int i;

        // read until the end of the stream
        while((i = is.read()) != -1) {
            textBuilder.append((char)i);
        }

        String fileContent = textBuilder.toString();
        return fileContent.substring(fileContent.indexOf("[") + 1, fileContent.indexOf("]"));
    }

}
