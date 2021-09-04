package com.wiilink24.bot.commands.misc;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wiilink24.bot.Bot;
import com.wiilink24.bot.Database;
import io.sentry.Sentry;

import java.sql.*;

public class AFK extends Command {
    private final Database database;
    private final Bot bot;

    public AFK(Bot bot)  {
        this.name = "afk";
        this.database = new Database();
        this.bot = bot;
    }

    @Override
    protected void execute(CommandEvent event) {
        try (Connection con = DriverManager.getConnection(bot.db(), bot.dbUser(), bot.dbPass())) {
            String userID = event.getAuthor().getId();
            boolean exists = database.doesExist(con, userID);

            // The user is not in the database, add them
            if (!exists) {
                database.createUser(con, userID);
            }

            database.updateAFK(con, true, event.getArgs(), userID);

            event.reply("**" + event.getAuthor().getName() + "**#" + event.getAuthor().getDiscriminator() + " is now AFK.");
        } catch (SQLException e) {
            Sentry.captureException(e);
        }
    }
}
