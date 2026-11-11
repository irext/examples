package net.irext.ircontrol.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import net.irext.ircontrol.bean.RemoteControl;

/**
 * Filename:       AppDatabase.java
 * Revised:        Date: 2026-11-11
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Room database holder for the application
 * <p>
 * Revision log:
 * 2026-11-11: created by strawmanbobi
 */
@Database(entities = {RemoteControl.class}, version = 1, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RemoteControlDao remoteControlDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            ctx.getApplicationContext(), AppDatabase.class, "irext-remote.db")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
