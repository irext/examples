package net.irext.ircontrol.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import net.irext.ircontrol.bean.RemoteControl;

import java.util.List;

/**
 * Filename:       RemoteControlDao.java
 * Revised:        Date: 2026-11-11
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Room data access object for RemoteControl entity
 * <p>
 * Revision log:
 * 2026-11-11: created by strawmanbobi
 */
@Dao
public interface RemoteControlDao {

    @Insert
    long insert(RemoteControl remoteControl);

    @Query("SELECT * FROM RemoteControl ORDER BY id DESC LIMIT :count OFFSET :from")
    List<RemoteControl> list(int from, int count);

    @Query("SELECT * FROM RemoteControl WHERE id = :id LIMIT 1")
    RemoteControl getById(long id);

    @Query("DELETE FROM RemoteControl WHERE id = :id")
    void deleteById(long id);
}
