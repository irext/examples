package net.irext.ircontrol.compose.data

import com.activeandroid.ActiveAndroid
import com.activeandroid.query.Select
import net.irext.ircontrol.compose.bean.RemoteControl

/**
 * Filename:       RemoteControlRepository.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    Manages database operations for saved remote controls and ordering.
 *
 * Revision log:
 * 2026-07-09: created by shdmfire and strawmanbobi
 */

class RemoteControlRepository {
    fun list(from: Int, count: Int): List<RemoteControl> {
        // Backfill orderIndex for any existing records where orderIndex is 0
        val unindexed = Select()
            .from(RemoteControl::class.java)
            .where("OrderIndex = 0")
            .execute<RemoteControl>()
        if (!unindexed.isNullOrEmpty()) {
            ActiveAndroid.beginTransaction()
            try {
                for (rc in unindexed) {
                    rc.orderIndex = rc.id ?: 0L
                    rc.save()
                }
                ActiveAndroid.setTransactionSuccessful()
            } finally {
                ActiveAndroid.endTransaction()
            }
        }

        return Select()
            .from(RemoteControl::class.java)
            .orderBy("OrderIndex ASC, id ASC")
            .offset(from)
            .limit(count)
            .execute()
    }

    fun get(id: Long): RemoteControl? = Select()
        .from(RemoteControl::class.java)
        .where("id = ?", id)
        .execute<RemoteControl>()
        ?.firstOrNull()

    fun save(remote: RemoteControl): Long {
        if (remote.orderIndex == 0L) {
            remote.orderIndex = getMaxOrderIndex() + 1
        }
        return remote.save()
    }

    fun delete(id: Long) {
        get(id)?.delete()
    }

    fun getMaxOrderIndex(): Long {
        val last = Select()
            .from(RemoteControl::class.java)
            .orderBy("OrderIndex DESC")
            .execute<RemoteControl>()
            ?.firstOrNull()
        return last?.orderIndex ?: 0L
    }

    fun moveUp(id: Long) {
        val currentRemote = get(id) ?: return
        val currentIndex = currentRemote.orderIndex
        val prevRemote = Select()
            .from(RemoteControl::class.java)
            .where("OrderIndex < ?", currentIndex)
            .orderBy("OrderIndex DESC")
            .execute<RemoteControl>()
            ?.firstOrNull() ?: return
        
        val temp = currentRemote.orderIndex
        currentRemote.orderIndex = prevRemote.orderIndex
        prevRemote.orderIndex = temp
        
        currentRemote.save()
        prevRemote.save()
    }

    fun moveDown(id: Long) {
        val currentRemote = get(id) ?: return
        val currentIndex = currentRemote.orderIndex
        val nextRemote = Select()
            .from(RemoteControl::class.java)
            .where("OrderIndex > ?", currentIndex)
            .orderBy("OrderIndex ASC")
            .execute<RemoteControl>()
            ?.firstOrNull() ?: return
        
        val temp = currentRemote.orderIndex
        currentRemote.orderIndex = nextRemote.orderIndex
        nextRemote.orderIndex = temp
        
        currentRemote.save()
        nextRemote.save()
    }
}
