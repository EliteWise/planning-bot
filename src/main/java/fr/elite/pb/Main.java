package fr.elite.pb;

import fr.elite.pb.database.MongoRequest;
import fr.elite.pb.event.Listener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emoji;

import javax.security.auth.login.LoginException;
import java.text.ParseException;

public class Main {

    private Main() throws LoginException {
        JDABuilder builder = JDABuilder.createDefault("ODc4NTg3ODU5NDI1MTk0MDI1.YSDWtQ.vV4PaK5WmbZlP9gdCl0_UbW9BC4");
        builder.addEventListeners(new Listener());
        builder.setActivity(Activity.of(Activity.ActivityType.WATCHING, ("les tâches " + Emoji.fromUnicode("\uD83D\uDCCB").toData().values().stream().findFirst().get())));
        builder.build();
    }

    public static void main(String[] args) throws LoginException, ParseException {
        new Main();
        new MongoRequest();
    }
}
