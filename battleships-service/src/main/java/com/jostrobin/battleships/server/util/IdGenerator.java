package com.jostrobin.battleships.server.util;

/**
 * Generates IDs.
 *
 * @author joscht
 */
public class IdGenerator
{
    private Long id = new Long(0);

    /**
     * Generates an id and returns it. Ids are zero based.
     * @return
     */
    public synchronized Long nextId()
    {
        return id++;
    }
}
