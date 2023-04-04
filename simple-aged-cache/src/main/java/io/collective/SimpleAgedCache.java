package io.collective;

import java.time.Clock;

public class SimpleAgedCache {

    Clock clock = Clock.systemDefaultZone();

    public SimpleAgedCache(Clock clock) {
        this.clock = clock;
    }

    ExpirableEntry head;

    public class ExpirableEntry {
        Object key;
        Object value;
        long expire_t;

        ExpirableEntry next;

        ExpirableEntry(Object key,Object value, int retention){
            this.key = key;
            this.value = value;
            /* time = clock.millis(); */
            this.expire_t = clock.millis() + retention;
            next = null;
        }
    }




    public SimpleAgedCache() {
    }

    public void put(Object key, Object value, int retentionInMillis) {
        ExpirableEntry new_entry = new ExpirableEntry(key, value, retentionInMillis);
        if (head == null) {
            head = new_entry;
        } else if (head.expire_t > new_entry.expire_t) {
            new_entry.next = head;
            head = new_entry;
        } else {
            ExpirableEntry curr = head;
            while (curr.next != null) {
                if (curr.next.expire_t > new_entry.expire_t){
                    new_entry.next = curr.next;
                    curr.next = null;
                } else {
                    curr =  curr.next;
                }
            }
            curr.next = new_entry;
        }
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        int len = 0;
        ExpirableEntry curr = head;
        while (curr != null){
            if (curr.expire_t < clock.millis()) {
                head = curr.next;
            } else {
                len += 1;
            }
            curr = curr.next;
        }
        return len;
    }

    public Object get(Object key) {
        ExpirableEntry curr = head;
        while (curr != null){
            if (curr.expire_t < clock.millis()) {
                head = curr.next;
                curr = curr.next;
            } else if (curr.key == key) {
                return curr.value;
            } else {
                curr = curr.next;
            }
        }
        return null;

    }
}