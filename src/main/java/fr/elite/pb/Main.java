package fr.elite.pb;

import fr.elite.pb.database.MongoRequest;
import fr.elite.pb.event.Message;
import fr.elite.pb.event.Reaction;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emoji;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

public class Main {

    private Main() throws LoginException, IOException {
        JDABuilder builder = JDABuilder.createDefault(getAccessToken());
        builder.addEventListeners(new Message(), new Reaction());
        builder.setActivity(Activity.of(Activity.ActivityType.WATCHING, ("les tâches " + Emoji.fromUnicode("\uD83D\uDCCB").toData().values().stream().findFirst().get())));
        builder.build();
    }

    public static void main(String[] args) throws LoginException, ParseException, IOException {
        new Main();
        new MongoRequest();
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
